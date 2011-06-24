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

/**
 * 
 * Each instance of this class represents a call to an external SMT solver.
 * 
 */
public abstract class SMTProverCall extends XProverCall {
	private static final String TRANSLATION_PATH = System
			.getProperty("user.home")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";

	protected static final String RES = "res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	protected static boolean CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF = true;

	private final List<Process> activeProcesses = new ArrayList<Process>();

	/**
	 * Name of the called external SMT solver
	 */
	protected final String solverName;

	/**
	 * The UI preferences of the SMT plugin
	 */
	protected final SMTPreferences smtPreferences;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	private volatile boolean valid;

	protected String translationFolder = null;

	/**
	 * Name of the lemma to prove
	 */
	protected String lemmaName;

	/**
	 * Solver output at the end of the call
	 */
	protected String solverResult;

	/**
	 * Access to these files must be synchronized. smtBenchmarkFile contains the
	 * sequent to discharge translated into SMT-LIB language, smtResultFile
	 * contains the result of the solver
	 */
	protected File smtBenchmarkFile;
	protected File smtResultFile;

	/**
	 * Creates an instance of this class. Additional computations are: solver
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
		smtPreferences = preferences;
		this.lemmaName = lemmaName;
		solverName = preferences.getSolver().getId();
	}

	/**
	 * Delete the file and all its children (if it is a folder)
	 * 
	 * @param file
	 *            the file to be deleted
	 */
	private static void deleteFile(final File file) {
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
	private static void cleanSMTFolder(final File smtFolder) {
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
	 * Set up input arguments for solver.
	 */
	private List<String> solverCommandLine() {
		final List<String> commandLine = new ArrayList<String>();

		/**
		 * Selected solver binary path
		 */
		commandLine.add(smtPreferences.getSolver().getPath());
		/**
		 * Benchmark file produced by translating the Event-B sequent
		 */
		commandLine.add(smtBenchmarkFile.getAbsolutePath());
		/**
		 * Selected solver parameters
		 */
		if (!smtPreferences.getSolver().getArgs().isEmpty()) {
			final String[] argumentsString = smtPreferences.getSolver()
					.getArgs().split(" ");
			for (final String argString : argumentsString) {
				commandLine.add(argString);
			}
		}

		return commandLine;
	}

	private void makeSMTResultFile() throws IOException {
		proofMonitor.setTask("Processing result file from SMT solver");
		smtResultFile = new File(smtBenchmarkFile.getParent()
				+ File.separatorChar + lemmaName + ".res");
		if (!smtResultFile.exists()) {
			smtResultFile.createNewFile();
		}
		final FileWriter fileWriter = new FileWriter(smtResultFile);
		fileWriter.write(solverResult);
		fileWriter.close();
	}

	/**
	 * Check if the result provided by the solver contains "unsat" string.
	 * "A formula is valid in a theory exactly when its negation is not satisfiable in this theory"
	 * So is set and returned "valid" attribut.
	 */
	private void parseSolverResult() {
		if (solverResult.contains("syntax error")
				|| solverResult.contains("parse error")
				|| solverResult.contains("Lexical_error")) {
			throw new IllegalArgumentException(solverName + " could not parse "
					+ lemmaName + ".smt. See " + lemmaName
					+ ".res for more details.");
		} else if (solverResult.contains("unsat")) {
			valid = true;
		} else if (solverResult.contains("sat")) {
			valid = false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ solverName + ". See " + lemmaName
					+ ".res for more details.");
		}
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

	protected final String smtFilePath() {
		return translationFolder + File.separatorChar + lemmaName
				+ SMT_LIB_FILE_EXTENSION;
	}

	protected String execProcess(final List<String> args) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		final Process p = pb.start();
		activeProcesses.add(p);
		final ProcessMonitor pm = new ProcessMonitor(null, p, this);
		final String resultString = new String(pm.output());
		return resultString;
	}

	/**
	 * This method
	 * 
	 * @throws IOException
	 */
	abstract protected void makeSMTBenchmarkFileV1_2() throws IOException;

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
	public static String mkTranslationFolder(final boolean cleanSmtFolder) {
		final String translationFolder;
		File folderFile = new File(TRANSLATION_PATH);
		/**
		 * Tries to create the translation folder
		 */
		if (!folderFile.mkdir()) {
			/**
			 * If couldn't, testes if the existing file is a directory
			 */
			if (folderFile.isDirectory()) {
				/**
				 * If it is, uses it as destination folder for translation files
				 */
				translationFolder = folderFile.getPath();
			} else {
				/**
				 * If it is not, creates a new fresh folder
				 */
				for (int i = 0;; i++) {
					folderFile = new File(TRANSLATION_PATH + i);
					if (!folderFile.mkdir()) {
						if (folderFile.isDirectory()) {
							translationFolder = folderFile.getPath();
							break;
						} else {
							continue;
						}
					} else {
						translationFolder = folderFile.getPath();
						break;
					}
				}
			}
		} else {
			translationFolder = folderFile.getPath();
		}

		if (cleanSmtFolder) {
			cleanSMTFolder(folderFile);
		}
		return translationFolder;
	}

	/**
	 * Method to call an SMT solver and get results back from it.
	 * 
	 * @param commandLine
	 *            Command-line which executes the solver on the produced
	 *            benchmark
	 * @throws IOException
	 */
	public void callProver(final List<String> commandLine) throws IOException,
			IllegalArgumentException {
		proofMonitor.setTask("Running SMT solver : "
				+ smtPreferences.getSolver());
		solverResult = execProcess(commandLine);
	}

	public void callSMTSolverAndComputeResult() throws IOException {
		callProver(solverCommandLine());
		makeSMTResultFile();
		parseSolverResult();
	}

	/**
	 * Run the external SMT solver on the sequent given at instance creation.
	 */
	@Override
	public void run() {
		try {
			/**
			 * Translates in SMT-LIB V1.2 language and tries to discharge with
			 * an SMT solver
			 */
			if (smtPreferences.getSolver().getsmtV1_2()) {
				makeSMTBenchmarkFileV1_2();
				callSMTSolverAndComputeResult();

			} else if (smtPreferences.getSolver().getsmtV2_0()) {
				/**
				 * TODO SMT-LIB v2.0
				 */
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Tells whether the sequent has been proved valid by the external SMT
	 * solver.
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
