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
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTSignature;

/**
 * 
 * Each instance of this class represents a call to an external SMT prover.
 * 
 */
public abstract class SMTProverCall extends XProverCall {
	protected static final String RES = "res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	protected static final String TRANSLATION_PATH = System
			.getProperty("user.home")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";

	protected static boolean CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF = true;

	protected String translationFolder = null;

	protected final List<Process> activeProcesses = new ArrayList<Process>();

	/**
	 * Name of the called external SMT prover
	 */
	protected final String proverName;

	/**
	 * The UI preferences of the SMT plugin TODO remove this dependance to the
	 * UI
	 */
	protected final SMTPreferences smtUiPreferences;

	/**
	 * Name of the lemma to prove
	 */
	protected final String lemmaName;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	protected volatile boolean valid;

	/**
	 * Solver output at the end of the call
	 */
	protected String resultOfSolver;

	/**
	 * Access to these files must be synchronized. iFile contains the sequent to
	 * discharge translated into SMT-LIB language, oFile contains the result of
	 * the solver
	 */
	protected File iFile;
	protected File oFile;

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
	protected SMTProverCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm);
		smtUiPreferences = preferences;
		if (SMTSignature.getReservedSymbolsAndKeywords().contains(lemmaName)) {
			this.lemmaName = lemmaName + "_";
		} else {
			this.lemmaName = lemmaName;
		}
		proverName = preferences.getSolver().getId();
	}

	private String smtFilePath(final String fileName) {
		return translationFolder + File.separatorChar + fileName
				+ SMT_LIB_FILE_EXTENSION;
	}

	/**
	 * Delete the file and all its children (if it is a folder)
	 * 
	 * @param file
	 *            the file to be deleted
	 */
	public static void deleteFile(final File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			final File[] childFiles = file.listFiles();
			for (final File childFile : childFiles) {
				deleteFile(childFile);
			}
			file.delete();
		}
	}

	/**
	 * This method cleans the output folder of the SMT-LIB, that is, deletes all
	 * children of the SMT folder.
	 * 
	 * @param smtFolder
	 *            the SMT Folder
	 */
	public static void cleanSMTFolder(final File smtFolder) {
		if (smtFolder.exists()) {
			if (smtFolder.isDirectory()) {
				final File[] children = smtFolder.listFiles();
				for (final File child : children) {
					deleteFile(child);
				}
			}
		}
	}

	/**
	 * makes the output folder for SMT files.
	 * 
	 * @param cleanSmtFolder
	 *            If the folder already exists and it has content inside, this
	 *            boolean then is used to check if the content must be deleted
	 *            or not before new proof.
	 * 
	 * @return the path string of the created directory
	 */
	public static String mkTranslationDir(final boolean cleanSmtFolder) {
		final String returnString;
		File f = new File(TRANSLATION_PATH);
		if (!f.mkdir()) {
			if (f.isDirectory()) {
				returnString = f.getPath();
			} else {
				for (int i = 0;; i++) {
					f = new File(TRANSLATION_PATH + i);
					if (!f.mkdir()) {
						if (f.isDirectory()) {
							returnString = f.getPath();
							break;
						} else {
							continue;
						}
					} else {
						returnString = f.getPath();
						break;
					}
				}
			}
		} else {
			returnString = f.getPath();
		}
		if (cleanSmtFolder) {
			cleanSMTFolder(f);
		}
		return returnString;
	}

	/**
	 * Create a new PrintWriter given the file.
	 * 
	 * @param smtFile
	 *            the SMT file which will be the output of the translation
	 * @return the PrintWriter that points to the SMT file.
	 */
	protected static PrintWriter openSMTFileWriter(final File smtFile) {
		try {
			final PrintWriter smtFileWriter = new PrintWriter(
					new BufferedWriter(new FileWriter(smtFile)));

			return smtFileWriter;

		} catch (final IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		} catch (final SecurityException se) {
			se.printStackTrace();
			se.getMessage();
			return null;
		}
	}

	public abstract List<String> smtTranslation() throws IOException;

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
				proofMonitor.setTask("Translating Event-B proof obligation");
				final List<String> translatedPOs = smtTranslation();
				callProver(translatedPOs);

			} else if (smtUiPreferences.getSolver().getsmtV2_0()) {
				/**
				 * SMT lib v2.0
				 */
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Executes the SMT-Solver process and returns the output of it.
	 * 
	 * @param args
	 *            The arguments to build and execute the process
	 * @return the output of the process
	 * @throws IOException
	 */
	protected String execProcess(final List<String> args) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		final Process p = pb.start();
		activeProcesses.add(p);
		final ProcessMonitor pm = new ProcessMonitor(null, p, this);
		final String resultString = new String(pm.output());
		return resultString;
	}

	private File writeSMTFile(final SMTBenchmark benchmark,
			final String filePathName) {
		final File smtFile = new File(filePathName);
		try {
			smtFile.createNewFile();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		}
		final PrintWriter smtFileWriter = openSMTFileWriter(smtFile);
		benchmark.print(smtFileWriter);
		smtFileWriter.close();
		return smtFile;
	}

	// TODO Re-do this comment
	/**
	 * Performs Rodin PO to SMT translation: First, translate to predicate
	 * calculus, then translate to SMT with macros, eventually pre-processing
	 * macros.
	 * 
	 * @throws IOException
	 */
	protected List<String> smtTranslation(final SMTBenchmark benchmark)
			throws IOException {
		if (translationFolder == null) {
			translationFolder = mkTranslationDir(!CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF);
		}
		final String smtFileName = smtFilePath(benchmark.getName());

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
		proofMonitor.setTask("Running SMT-Solver");

		resultOfSolver = execProcess(args);

		proofMonitor.setTask("Processing result file from SMT-Solver");

		/**
		 * Set up result file
		 **/
		final File resultFile = new File(iFile.getParent() + File.separatorChar
				+ lemmaName + ".res");
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}
		final FileWriter fileWriter = new FileWriter(resultFile);
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
	protected List<String> setSolverArgs(final String lemmaFilePath) {
		final List<String> args = new ArrayList<String>();
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
			final String[] argumentsString = smtUiPreferences.getSolver()
					.getArgs().split(" "); //$NON-NLS-1$
			for (final String argString : argumentsString) {
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
	private boolean checkResult(final String solverResult) {
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
		for (final Process p : activeProcesses) {
			p.destroy();
		}
	}
}
