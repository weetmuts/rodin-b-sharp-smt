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

import static br.ufrn.smt.solver.translation.Translator.DEBUG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
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
	protected static final String RES_FILE_EXTENSION = ".res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	public static final String DEFAULT_TRANSLATION_PATH = System
			.getProperty("java.io.tmpdir")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";

	/**
	 * Solver output at the end of the call
	 */
	private String solverResult;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	private volatile boolean valid;

	protected final List<Process> activeProcesses = new ArrayList<Process>();

	/**
	 * The UI preferences of the SMT plugin
	 */
	protected final SMTPreferences smtPreferences;

	protected String translationPath = null;

	/**
	 * Name of the called external SMT solver
	 */
	protected final String solverName;

	/**
	 * Name of the lemma to prove
	 */
	protected String lemmaName;

	/**
	 * Access to these files must be synchronized. smtBenchmarkFile contains the
	 * sequent to discharge translated to SMT-LIB language, smtResultFile
	 * contains the result of the solver
	 */
	protected File smtTranslationFolder = null;
	protected File smtBenchmarkFile;
	protected File smtResultFile;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param hypotheses
	 *            hypotheses of the sequent to discharge
	 * @param goal
	 *            goal of the sequent to discharge
	 * @param pm
	 *            proof monitor used for cancellation
	 * @param preferences
	 *            preferences set for this calling
	 * @param lemmaName
	 *            name of the lemma to prove
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
	 * FOR DEBUG ONLY
	 */
	private static void showProcessOutput(ProcessMonitor monitor, boolean error) {
		final String kind = error ? "error" : "output";
		System.out.println("-- Begin dump of process " + kind + " --");
		final byte[] bytes = error ? monitor.error() : monitor.output();
		if (bytes.length != 0) {
			final String output = new String(bytes);
			if (output.endsWith("\n")) {
				System.out.print(error);
			} else {
				System.out.println(error);
			}
		}
		System.out.println("-- End dump of process " + kind + " --");
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTBenchmarkFile() {
		showFile(smtBenchmarkFile);
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTResultFile() {
		showFile(smtResultFile);
	}

	/**
	 * FOR DEBUG ONLY: print the SMT solver result into a file
	 * 
	 * @throws IOException
	 */
	private synchronized void printSMTResultFile() throws IOException {
		final FileWriter fileWriter = new FileWriter(smtResultFile);
		fileWriter.write(solverResult);
		fileWriter.close();
	}

	/**
	 * Sets up input arguments for solver.
	 */
	private synchronized List<String> solverCommandLine() {
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

	/**
	 * Calls an SMT solver and checks its result.
	 * 
	 * @param commandLine
	 *            Command-line which executes the solver on the produced
	 *            benchmark
	 * @throws IOException
	 */
	private void callProver(final List<String> commandLine) throws IOException,
			IllegalArgumentException {

		if (DEBUG) {
			System.out.println("About to launch solver command:");
			System.out.print("   ");
			for (String arg : commandLine) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();
		}

		try {
			final ProcessBuilder builder = new ProcessBuilder(commandLine);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			activeProcesses.add(process);
			final ProcessMonitor monitor = new ProcessMonitor(null, process,
					this);

			if (DEBUG)
				showProcessOutcome(monitor);

			solverResult = new String(monitor.output());
			valid = checkResult();

			if (DEBUG) {
				System.out
						.println("Prover " + (valid ? "succeeded" : "failed"));
				printSMTResultFile();
				System.out.println("Result file contains:");
				showSMTResultFile();
			}

		} finally {
			if (DEBUG)
				System.out.println("Solver command finished.");
		}
	}

	/**
	 * Checks if the result provided by the solver contains the "unsat" string.
	 * "A formula is valid in a theory exactly when its negation is not satisfiable in this theory"
	 * So is set and returned "valid" attribut.
	 */
	private boolean checkResult() {
		if (solverResult.contains("syntax error")
				|| solverResult.contains("parse error")
				|| solverResult.contains("Lexical_error")) {
			throw new IllegalArgumentException(solverName + " could not parse "
					+ lemmaName + ".smt. See " + lemmaName
					+ ".res for more details.");
		} else if (solverResult.contains("unsat")) {
			return true;
		} else if (solverResult.contains("sat")) {
			return false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ solverName + ". See " + lemmaName
					+ ".res for more details.");
		}
	}

	/**
	 * Creates a new PrintWriter given the file.
	 * 
	 * @param smtFile
	 *            the SMT file which will be the output of the translation
	 * @return the PrintWriter linked to the SMT file.
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

	/**
	 * FOR DEBUG ONLY
	 * 
	 * @param file
	 *            the file to show
	 */
	protected static void showFile(File file) {
		if (file == null) {
			System.out.println("***File has been cleaned up***");
			return;
		}
		try {
			final BufferedReader rdr = new BufferedReader(new FileReader(file));
			String line;
			while ((line = rdr.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("***Exception when reading file: "
					+ e.getMessage() + "***");
		}
	}

	/**
	 * FOR DEBUG ONLY
	 */
	protected static void showProcessOutcome(ProcessMonitor monitor) {
		showProcessOutput(monitor, false);
		showProcessOutput(monitor, true);
		System.out.println("Exit code is: " + monitor.exitCode());
	}

	/**
	 * Makes temporary files in the given path
	 */
	protected synchronized void makeTempFileNames() throws IOException {
		final String benchmarkTargetPath = translationPath + File.separatorChar
				+ lemmaName;

		if (smtTranslationFolder == null) {
			smtTranslationFolder = new File(benchmarkTargetPath);
			if (!smtTranslationFolder.mkdirs()) {
				// TODO handle the error
			} else {
				if (DEBUG) {
					System.out
							.println("Created temporary SMT translation folder '"
									+ smtTranslationFolder + "'");
				} else {
					smtTranslationFolder.deleteOnExit();
				}
			}
		}

		smtBenchmarkFile = File.createTempFile(lemmaName,
				SMT_LIB_FILE_EXTENSION, smtTranslationFolder);
		if (DEBUG) {
			System.out.println("Created temporary SMT benchmark file '"
					+ smtBenchmarkFile + "'");
		} else {
			smtBenchmarkFile.deleteOnExit();
		}

		smtResultFile = File.createTempFile(lemmaName + "_" + solverName,
				RES_FILE_EXTENSION, smtTranslationFolder);
		if (DEBUG) {
			System.out.println("Created temporary SMT result file '"
					+ smtResultFile + "'");
		} else {
			smtResultFile.deleteOnExit();
		}

		// Fill the result file with some random characters that can not be
		// considered as a success.
		final PrintStream stream = new PrintStream(smtResultFile);
		stream.println("FAILED");
		stream.close();
	}

	/**
	 * Translates the sequent in SMT-LIB V1.2 language and sets the benchmark
	 * file with the result.
	 * 
	 * @throws IOException
	 */
	abstract protected void makeSMTBenchmarkFileV1_2() throws IOException;

	/**
	 * Runs the external SMT solver on the sequent given at instance creation.
	 */
	@Override
	public void run() {
		try {
			/**
			 * Translates the sequent in SMT-LIB V1.2 language and tries to
			 * discharge it with an SMT solver
			 */
			if (smtPreferences.getSolver().getsmtV1_2()) {
				makeSMTBenchmarkFileV1_2();

				if (DEBUG) {
					System.out.println("Launching " + solverName
							+ " with input:\n");
					showSMTBenchmarkFile();
				}

				setMonitorMessage("Running SMT solver : "
						+ smtPreferences.getSolver());
				callProver(solverCommandLine());

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
	 * Human-readable message to be displayed for this proof.
	 */
	@Override
	public String displayMessage() {
		final StringBuilder message = new StringBuilder();
		message.append("SMT-").append(solverName);
		return message.toString();
	}

	/**
	 * Cleans up this prover call: destroys processes and deletes temporary
	 * files.
	 */
	@Override
	public synchronized void cleanup() {
		for (final Process p : activeProcesses) {
			p.destroy();
		}
		if (smtBenchmarkFile != null) {
			smtBenchmarkFile.delete();
			smtBenchmarkFile = null;
		}
		if (smtResultFile != null) {
			smtResultFile.delete();
			smtResultFile = null;
		}
	}
}
