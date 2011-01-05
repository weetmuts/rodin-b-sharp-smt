/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *     Systerel (YFT) - Code refactoring
 *     Systerel (YFT) - Separate UI and Core as much as possible
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.SmtThroughPp;
import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * 
 * Each instance of this class represents a call to an external SMT prover.
 * 
 */
public class SmtProverCall extends XProverCall {
	private static String SMT_LIB_FILE_EXTENSION = ".smt";
	private static String TRANSLATION_PATH = System.getProperty("user.home")
			+ File.separatorChar + "rodin_smtlib_tmp_files";

	/**
	 * Name of the called external SMT prover
	 */
	private final String proverName;

	/**
	 * SMT UI preferences
	 */
	private UIPreferences smtUiPreferences;

	/**
	 * Name of the lemma to prove
	 */
	private final String lemmaName;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	private volatile boolean valid;

	/**
	 * Solver output at the end of the call
	 */
	private String resultOfSolver;

	/**
	 * Access to these files must be synchronized. iFile contains the sequent to
	 * discharge translated into SMT-LIB language, oFile contains the result of
	 * the solver
	 */
	protected File iFile;
	protected File oFile;

	private static String smtFilePath(final String fileName) {
		return TRANSLATION_PATH + File.separatorChar + fileName
				+ SMT_LIB_FILE_EXTENSION;
	}

	private static void mkTranslationDir() {
		new File(TRANSLATION_PATH).mkdir();
	}

	private static PrintWriter openSMTFileWriter(File smtFile,
			final String fileName) {
		try {
			final PrintWriter smtFileWriter = new PrintWriter(
					new BufferedWriter(new FileWriter(smtFile)));

			return smtFileWriter;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		} catch (SecurityException se) {
			se.printStackTrace();
			se.getMessage();
			return null;
		}
	}

	private static void closeSMTFileWriter(PrintWriter smtFileWriter) {
		smtFileWriter.close();
	}

	private static File writeSMTFile(final SMTBenchmark benchmark) {
		mkTranslationDir();
		File smtFile = new File(smtFilePath(benchmark.getName()));
		try {
			smtFile.createNewFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		}
		final PrintWriter smtFileWriter = openSMTFileWriter(smtFile,
				benchmark.getName());
		benchmark.print(smtFileWriter);
		closeSMTFileWriter(smtFileWriter);
		return smtFile;
	}

	/**
	 * Creates an instance of this class. Additional computations are: prover
	 * name and preferences settings.
	 * 
	 * @param hypotheses
	 *            hypotheses of the sequent to discharge
	 * @param goal
	 *            goal of the sequent to discharge
	 * @param pm
	 *            proof monitor used for cancellation
	 */
	public SmtProverCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm, String lemmaName) {
		super(hypotheses, goal, pm);

		/**
		 * Get back preferences from UI
		 */
		smtUiPreferences = new UIPreferences(SmtProversCore.getDefault()
				.getPreferenceStore().getString("solverpreferences"),//$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getInt("solverindex"), //$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getBoolean("usingprepro"), //$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getString("prepropath"));//$NON-NLS-1$

		this.proverName = smtUiPreferences.getSolver().getId();
		this.lemmaName = lemmaName;
	}

	/**
	 * Run the external SMT prover on the sequent given at instance creation.
	 */
	@Override
	public void run() {
		/**
		 * Test the SMT solver path
		 */
		if (smtUiPreferences.getSolver() == null) {
			/**
			 * Message popup displayed when there is no defined solver path
			 */
			UIUtils.showError(Messages.SmtProversCall_Check_Smt_Preferences);
			return;
		}

		try {
			/**
			 * Translate and apply smt solver
			 */
			if (smtUiPreferences.getSolver().getsmtV1_2()) {
				/**
				 * SMT lib v1.2
				 */
				final List<String> translatedPOs = smtTranslation();
				callProver(translatedPOs);

			} else if (smtUiPreferences.getSolver().getsmtV2_0()) {
				/**
				 * SMT lib v2.0
				 */
			}
		} catch (TranslationException t) {
			UIUtils.showError(t.getMessage());
			return;
		} catch (IOException e) {
			UIUtils.showError(e.getMessage());
			return;
		} catch (IllegalArgumentException iae) {
			UIUtils.showError(iae.getMessage());
			return;
		}
	}

	/**
	 * Performs Rodin PO to SMT translation: First, translate to predicate
	 * calculus, then translate to SMT with macros, eventually pre-processing
	 * macros.
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 * @throws TranslationException
	 */
	public List<String> smtTranslation() throws PreProcessingException,
			IOException, TranslationException {

		/**
		 * Parse Rodin PO to create Smt file
		 */
		final SMTBenchmark benchmark = SmtThroughPp.translateToSmtLibBenchmark(
				this.lemmaName, this.hypotheses, this.goal);
		this.iFile = writeSMTFile(benchmark);

		/**
		 * Set up arguments
		 */
		final List<String> args = setSolverArgs(benchmark.getName());

		/**
		 * Get back translated smt file
		 */
		if (!this.iFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		return args;
	}

	/**
	 * Method to call a SMT solver and get results back from it.
	 * 
	 * @param args
	 *            Arguments list needed by the prover to be called
	 * @throws IOException
	 * 
	 *             FIXME must be refactored: this method should not do anything
	 *             else than calling the prover. No file manipulation should be
	 *             visible at this level.
	 */
	public void callProver(final List<String> args) throws IOException,
			IllegalArgumentException {
		/**
		 * Launch solver and get back solver result
		 */
		this.resultOfSolver = Exec.execProgram(args.toArray(new String[args
				.size()]));

		/**
		 * Set up result file
		 */
		File resultFile = new File(iFile.getParent() + File.separatorChar
				+ this.lemmaName + ".res");
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(resultFile);
		fileWriter.write(this.resultOfSolver);
		fileWriter.close();
		oFile = resultFile;

		/**
		 * Check Solver Result
		 */
		checkResult(this.resultOfSolver);
	}

	/**
	 * Set up input arguments for solver.
	 * 
	 */
	private List<String> setSolverArgs(final String lemmaName) {
		List<String> args = new ArrayList<String>();
		args.add(smtUiPreferences.getSolver().getPath());

		/**
		 * If solver is V1.2 the smt input is added in arguments
		 */
		if (smtUiPreferences.getSolver().getsmtV1_2()) {
			args.add(smtFilePath(lemmaName));
		}

		/**
		 * Get back solver arguments
		 */
		if (!smtUiPreferences.getSolver().getArgs().isEmpty()) {
			/**
			 * Split arguments and add them in the list
			 */
			String[] argumentsString = smtUiPreferences.getSolver().getArgs()
					.split(" "); //$NON-NLS-1$
			for (String argString : argumentsString) {
				args.add(argString);
			}
		}

		return args;
	}

	/**
	 * Check if the result provided by the solver contains "unsat" string.
	 * "A formula is valid in a theory exactly when its negation is not satisfiable in this theory"
	 * So is set and returned "valid" attribut.
	 * 
	 * @param solverResult
	 *            The string result from the SMT solver.
	 */
	private boolean checkResult(String solverResult) {
		if (solverResult.contains("syntax error")
				|| solverResult.contains("parse error")
				|| solverResult.contains("Lexical_error")) {
			throw new IllegalArgumentException(this.proverName
					+ " could not parse " + this.lemmaName + ".smt. See "
					+ this.lemmaName + ".res for more details.");
		} else if (solverResult.contains("unsat")) {
			valid = true;
		} else if (solverResult.contains("sat")) {
			valid = false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ this.proverName + ". See " + this.lemmaName
					+ ".res for more details.");
		}
		return valid;
	}

	/**
	 * Tells whether the sequent has been proved valid by the external SMT
	 * prover.
	 **/
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * Human-readable message to be displayed for this proof
	 */
	@Override
	public String displayMessage() {
		return "SMT";
	}

	/**
	 * Cleans up this prover call: delete temporary files.
	 */
	@Override
	public synchronized void cleanup() {
		/*
		 * if (iFile != null) { if (iFile.delete()) { iFile = null; } else {
		 * System.out
		 * .println(Messages.SmtProversCall_file_could_not_be_deleted); } } if
		 * (oFile != null) { if (oFile.delete()) { oFile = null; } else {
		 * System.out
		 * .println(Messages.SmtProversCall_file_could_not_be_deleted); } }
		 */
	}
}
