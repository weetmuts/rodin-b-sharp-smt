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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.ast.commands.SMTCheckSatCommand;
import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * 
 * Each instance of this class represents a call to an external SMT prover.
 * 
 */
public class SmtProverCall extends XProverCall {

	/**
	 * Name of the called external SMT prover
	 */
	protected final String proverName;

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
	 * Access to these files must be synchronized. iFile contains the sequent to
	 * discharge translated into SMT-LIB language, oFile contains the result of
	 * the solver
	 */
	protected File iFile;
	protected File oFile;

	/**
	 * Solver output at the end of the call
	 */
	private String resultOfSolver;
	/**
	 * Temporary file. Contains the sequent to discharge translated into SMT-LIB
	 * language with macros (will be deleted).
	 */
	private File smtFile;

	/**
	 * SMT UI preferences
	 */
	private UIPreferences smtUiPreferences;

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
	 * Set up input arguments for solver.
	 * 
	 */
	private List<String> setSolverArgs() {
		List<String> args = new ArrayList<String>();
		args.add(smtUiPreferences.getSolver().getPath());

		/**
		 * If solver is V1.2 the smt input is added in arguments
		 */
		if (smtUiPreferences.getSolver().getsmtV1_2()) {
			args.add(smtFile.getPath());
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
	/*	if (iFile != null) {
			if (iFile.delete()) {
				iFile = null;
			} else {
				System.out
						.println(Messages.SmtProversCall_file_could_not_be_deleted);
			}
		}
		if (oFile != null) {
			if (oFile.delete()) {
				oFile = null;
			} else {
				System.out
						.println(Messages.SmtProversCall_file_could_not_be_deleted);
			}
		}*/
	}

	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to predicate calculus
		 */
		ppProof.translate();

		return ppProof;
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
		final PPProof ppProof = ppTranslation(this.hypotheses, this.goal);
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		/**
		 * Parse Rodin PO to create Smt file
		 */
		final RodinToSMTPredicateParser rp = new RodinToSMTPredicateParser(
				this.lemmaName, ppTranslatedHypotheses, ppTranslatedGoal);
		rp.writeSMTFile();

		/**
		 * Get back translated smt file
		 */
		this.smtFile = rp.getSMTFile();
		if (!this.smtFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Set up arguments
		 */
		final List<String> args = setSolverArgs();

		if (this.smtUiPreferences.getUsingPrepro()) {
			/**
			 * Launch preprocessing
			 */
			// smtTranslationPreprocessing(args);
		}

		this.iFile = this.smtFile;

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
	 * Method to call a SMT solver in interactive mode.
	 * 
	 * @param args
	 *            Arguments to pass for the call
	 * @throws IOException
	 */
	protected void callProverInteractive(final List<String> args)
			throws IOException {
		boolean solverRes = true;
		/**
		 * Create the new process
		 */
		Process pr = Runtime.getRuntime().exec(
				args.toArray(new String[args.size()]));

		// Set up buffers to communicate with the process
		BufferedWriter processInput = new BufferedWriter(
				new OutputStreamWriter(pr.getOutputStream()));

		BufferedReader processOutput = new BufferedReader(
				new InputStreamReader(pr.getInputStream()));
		BufferedReader processOutputError = new BufferedReader(
				new InputStreamReader(pr.getErrorStream()));

		// Set up result file
		File resultFile = new File(iFile.getParent() + File.separatorChar
				+ "res_" + this.lemmaName);
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(resultFile);

		try {
			// command line parameter
			FileInputStream fstream = new FileInputStream(smtFile);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = bufr.readLine()) != null) {
				// Send the content
				processInput.write(strLine);
				processInput.flush();
				System.out.println(strLine);
				// Get answer
				String ans = processOutput.readLine();
				System.out.println(ans);

				if (ans == null || !ans.equals("success")) {
					solverRes = false;
					break;
				} else {
					fileWriter.write(ans);
				}
			}

			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		// Check if all commands have been sent correctly
		if (solverRes) {
			// Send Check Sat Command
			SMTCheckSatCommand satCommand = new SMTCheckSatCommand();
			processInput.write(satCommand.toString());
			processInput.flush();
			System.out.println(satCommand.toString());
			// Get answer
			String ans = processOutput.readLine();
			System.out.println(ans);
			if (ans == null || !ans.equals("unsat")) {
				solverRes = false;
			} else {
				fileWriter.write(ans);
			}
		}

		// Close input and output streams
		processInput.close();
		processOutput.close();
		processOutputError.close();

		// Get back solver result
		valid = solverRes;

		// Close the output file
		fileWriter.close();
		oFile = resultFile;
	}
}
