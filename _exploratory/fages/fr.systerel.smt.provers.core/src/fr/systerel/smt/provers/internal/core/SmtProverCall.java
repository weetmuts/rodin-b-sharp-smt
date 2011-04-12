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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.SMTThroughPP;
import br.ufrn.smt.solver.translation.SMTThroughVeriT;
import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.ast.SMTBenchmark;

/**
 * 
 * Each instance of this class represents a call to an external SMT prover.
 * 
 */
public class SmtProverCall extends XProverCall {
	private static final String RES = "res";
	private static String SMT_LIB_FILE_EXTENSION = ".smt";
	private static String VERIT_TEMP_FILE = "_prep";
	private static String VERIT_SIMPLIFY_ARGUMENT_STRING = "--print-simp-and-exit";
	private static String VERIT_DISABLE_BANNER = "--disable-banner";
	private static String TRANSLATION_PATH = System.getProperty("user.home")
			+ File.separatorChar + "rodin_smtlib_tmp_files";

	private static String PREFS_ID = "fr.systerel.smt.provers.ui";

	/**
	 * Name of the called external SMT prover
	 */
	private final String proverName;

	private final IPreferencesService preferencesService = Platform
			.getPreferencesService();

	private SMTPreferences smtUiPreferences;

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
	private static final String POST_PROCESSED_FILE_POSTFIX = "_pop.";

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

		String string1 = preferencesService.getString(PREFS_ID,
				"solverpreferences", null, null);
		int int2 = preferencesService.getInt(PREFS_ID, "solverindex", -1, null);
		boolean bool3 = preferencesService.getBoolean(PREFS_ID, "usingprepro",
				false, null);
		String string4 = preferencesService.getString(PREFS_ID, "prepropath",
				null, null);

		smtUiPreferences = new SMTPreferences(string1, int2, bool3, string4);

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
			throw new IllegalArgumentException(
					Messages.SmtProversCall_Check_Smt_Preferences);
		}

		try {
			/**
			 * Translate and apply smt solver
			 */
			if (smtUiPreferences.getSolver().getsmtV1_2()) {
				/**
				 * SMT lib v1.2 TODO: Add option here and in the preferences to
				 * set the pre-processor: veriT or pptrans.
				 */
				final List<String> translatedPOs = smtTranslationThroughPP();
				callProver(translatedPOs);

			} else if (smtUiPreferences.getSolver().getsmtV2_0()) {
				/**
				 * SMT lib v2.0
				 */
			}
		} catch (TranslationException t) {
			throw new IllegalArgumentException(t);
			// FIXME
			// UIUtils.showError(t.getMessage());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
			// FIXME
			// UIUtils.showError(e.getMessage());
			// } catch (IllegalArgumentException iae) {
			// // FIXME
			// // UIUtils.showError(iae.getMessage());
			// return;
		}
	}

	public List<String> smtTranslationThroughPP() throws TranslationException,
			PreProcessingException, IOException {
		SMTBenchmark benchmark = SMTThroughPP.translateToSmtLibBenchmark(
				lemmaName, hypotheses, goal);
		return smtTranslation(benchmark);
	}

	public List<String> smtTranslationThroughVeriT()
			throws TranslationException, PreProcessingException, IOException {
		SMTBenchmark benchmark = SMTThroughVeriT.translateToSmtLibBenchmark(
				lemmaName, hypotheses, goal);

		/**
		 * The name of the SMT file with macros.
		 */
		String veriTPreProcessingFileName = smtVeriTPreProcessFilePath(benchmark
				.getName());

		/**
		 * First, write the SMT file with macros
		 */
		File preprocessedFile = writeVeritPreprocessedSMTFile(benchmark,
				veriTPreProcessingFileName);

		if (!preprocessedFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Then, call veriT, which produces a version of the SMT file without
		 * macros
		 */
		callVeriT(preprocessedFile);

		final List<String> args = setSolverArgs(iFile.getPath());

		return args;
	}

	/**
	 * This method should: call the veriT, produce a simplified version of the
	 * SMT file without macros, and verify if there is any input error
	 * 
	 * @param preprocessedFile
	 * @throws IOException
	 */
	private void callVeriT(File preprocessedFile) throws IOException {
		List<String> args = new ArrayList<String>();

		if (smtUiPreferences.getPreproPath().isEmpty()
				|| smtUiPreferences.getPreproPath() == null) {
			throw new IllegalArgumentException(
					Messages.SmtProversCall_preprocessor_path_not_defined);
		}

		args.add(smtUiPreferences.getPreproPath());
		args.add(VERIT_SIMPLIFY_ARGUMENT_STRING);
		args.add(VERIT_DISABLE_BANNER);
		args.add(preprocessedFile.getPath());

		resultOfSolver = Exec
				.execProgram(args.toArray(new String[args.size()]));

		/**
		 * Set up temporary result file
		 */
		checkPreProcessingResult(preprocessedFile.getParent());
	}

	private void createPostProcessedFile(String parentFolder, String extension)
			throws IOException {
		iFile = new File(parentFolder + File.separatorChar + lemmaName
				+ POST_PROCESSED_FILE_POSTFIX + extension);
		if (!iFile.exists()) {
			iFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(iFile);
		fileWriter.write(resultOfSolver);
		fileWriter.close();
	}

	private void checkPreProcessingResult(String parentFolder)
			throws IOException {
		if (resultOfSolver.contains("(benchmark")) {
			createPostProcessedFile(parentFolder, "smt");
			return;
		} else {
			createPostProcessedFile(parentFolder, RES);
			if (resultOfSolver.contains("syntax error")
					|| resultOfSolver.contains("parse error")
					|| resultOfSolver.contains("Lexical_error")) {
				throw new IllegalArgumentException(proverName
						+ " could not pre-process " + lemmaName
						+ ".smt with VeriT. See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + " for more details.");
			} else {
				throw new IllegalArgumentException("Unexpected response of "
						+ proverName + ". See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + RES
						+ " for more details.");
			}
		}
	}

	private String smtVeriTPreProcessFilePath(String fileName) {
		return TRANSLATION_PATH + File.separatorChar + fileName
				+ VERIT_TEMP_FILE + SMT_LIB_FILE_EXTENSION;
	}

	private File writeVeritPreprocessedSMTFile(SMTBenchmark benchmark,
			String veriTPreProcessingFileName) {
		mkTranslationDir();
		File preProcessedSMTFile = new File(veriTPreProcessingFileName);
		try {
			preProcessedSMTFile.createNewFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		}
		final PrintWriter smtFileWriter = openSMTFileWriter(
				preProcessedSMTFile, benchmark.getName());
		benchmark.print(smtFileWriter);
		closeSMTFileWriter(smtFileWriter);
		return preProcessedSMTFile;

	}

	private static File writeSMTFile(final SMTBenchmark benchmark,
			final String filePathName) {
		mkTranslationDir();
		File smtFile = new File(filePathName);
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
	 * Performs Rodin PO to SMT translation: First, translate to predicate
	 * calculus, then translate to SMT with macros, eventually pre-processing
	 * macros.
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 * @throws TranslationException
	 */
	private List<String> smtTranslation(SMTBenchmark benchmark)
			throws PreProcessingException, IOException, TranslationException {

		String smtFileName = smtFilePath(benchmark.getName());

		/**
		 * Parse Rodin PO to create Smt file
		 */
		iFile = writeSMTFile(benchmark, smtFileName);

		/**
		 * Get back translated smt file
		 */
		if (!iFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Set up arguments
		 */
		final List<String> args = setSolverArgs(smtFileName);

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
		resultOfSolver = Exec
				.execProgram(args.toArray(new String[args.size()]));

		/**
		 * Set up result file
		 */
		File resultFile = new File(iFile.getParent() + File.separatorChar
				+ lemmaName + ".res");
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(resultFile);
		fileWriter.write(resultOfSolver);
		fileWriter.close();
		oFile = resultFile;

		/**
		 * Check Solver Result
		 */
		checkResult(resultOfSolver);
	}

	/**
	 * Set up input arguments for solver.
	 * 
	 */
	private List<String> setSolverArgs(final String lemmaFilePath) {
		List<String> args = new ArrayList<String>();
		args.add(smtUiPreferences.getSolver().getPath());

		/**
		 * If solver is V1.2 the smt input is added in arguments
		 */
		if (smtUiPreferences.getSolver().getsmtV1_2()) {
			args.add(lemmaFilePath);
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
			throw new IllegalArgumentException(proverName + " could not parse "
					+ lemmaName + ".smt. See " + lemmaName
					+ ".res for more details.");
		} else if (solverResult.contains("unsat")) {
			valid = true;
		} else if (solverResult.contains("sat")) {
			valid = false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ proverName + ". See " + lemmaName
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
