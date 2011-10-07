/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - code refactoring
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static org.eventb.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.Translator.DEBUG;
import static org.eventb.smt.translation.Translator.DEBUG_DETAILS;
import static org.eventb.smt.translation.Translator.DEV;

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
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.preferences.SMTSolverConfiguration;

/**
 * 
 * Each instance of this class represents a call to an external SMT solver.
 * 
 */
public abstract class SMTProverCall extends XProverCall {
	protected static final String RES_FILE_EXTENSION = ".res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	protected static final String SMT_LIB2_FILE_EXTENSION_FOR_ALTERGO = ".smt2";

	/**
	 * Solver output at the end of the call
	 */
	protected String solverResult;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	private volatile boolean valid;

	volatile Set<Predicate> neededHypotheses = null;

	volatile boolean goalNeeded = true;

	volatile SMTBenchmark benchmark;

	final List<Process> activeProcesses = new ArrayList<Process>();

	SMTSolverConfiguration solverConfig;

	String translationPath = null;

	/**
	 * Name of the lemma to prove
	 */
	String lemmaName;

	/**
	 * Access to these files must be synchronized. smtBenchmarkFile contains the
	 * sequent to discharge translated to SMT-LIB language, smtResultFile
	 * contains the result of the solver
	 */
	File smtTranslationFolder = null;
	File smtBenchmarkFile;
	File smtResultFile;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param hypotheses
	 *            hypotheses of the sequent to discharge
	 * @param goal
	 *            goal of the sequent to discharge
	 * @param pm
	 *            proof monitor used for cancellation
	 * @param poName
	 *            name of the lemma to prove
	 */
	protected SMTProverCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTSolverConfiguration solverConfig, final String poName,
			final String translationPath) {
		super(hypotheses, goal, pm);
		this.solverConfig = solverConfig;
		this.lemmaName = poName;
		this.translationPath = translationPath;
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
		commandLine.add(solverConfig.getPath());
		/**
		 * Benchmark file produced by translating the Event-B sequent
		 */
		commandLine.add(smtBenchmarkFile.getAbsolutePath());
		/**
		 * Selected solver parameters
		 */
		final String args = solverConfig.getArgs();
		if (!args.isEmpty()) {
			final String[] argumentsString = args.split(" ");
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

		if (DEBUG_DETAILS) {
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

			if (DEBUG_DETAILS)
				showProcessOutcome(monitor);

			solverResult = new String(monitor.output());
			if (DEBUG) {
				printSMTResultFile();
			}
			if (DEBUG_DETAILS) {
				System.out.println("Result file contains:");
				showSMTResultFile();
			}

			valid = checkResult();
			if (DEBUG_DETAILS) {
				System.out
						.println("Prover " + (valid ? "succeeded" : "failed"));
			}

		} finally {
			if (DEBUG_DETAILS)
				System.out.println("Solver command finished.");
		}
	}

	/**
	 * Checks if the result provided by the solver contains the "unsat" string.
	 * "A formula is valid in a theory exactly when its negation is not satisfiable in this theory"
	 * So is set and returned "valid" attribut.
	 */
	private boolean checkResult() {
		if (compile("^unsat$", MULTILINE).matcher(solverResult).find()) {
			return true;
		} else if (compile("^sat$", MULTILINE).matcher(solverResult).find()
				|| compile("^unknown$", MULTILINE).matcher(solverResult).find()) {
			return false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ solverConfig.getSolver().toString() + ". See "
					+ lemmaName + ".res for more details.");
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
					if (DEBUG_DETAILS) {
						System.out
								.println("Created temporary SMT translation folder '"
										+ smtTranslationFolder + "'");
					}
				} else {
					/**
					 * The deletion will be done when exiting Rodin.
					 */
					smtTranslationFolder.deleteOnExit();
				}
			}
		}

		final SMTSolver solver = solverConfig.getSolver();
		if (solverConfig.getSmtlibVersion().equals(V2_0)
				&& solver.equals(ALT_ERGO)) {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					SMT_LIB2_FILE_EXTENSION_FOR_ALTERGO, smtTranslationFolder);
		} else {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					SMT_LIB_FILE_EXTENSION, smtTranslationFolder);
		}
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				System.out.println("Created temporary SMT benchmark file '"
						+ smtBenchmarkFile + "'");
			}
		} else {
			/**
			 * The deletion will be done when exiting Rodin.
			 */
			smtBenchmarkFile.deleteOnExit();
		}

		if (DEBUG) {
			smtResultFile = File.createTempFile(
					lemmaName + "_" + solver.toString(), RES_FILE_EXTENSION,
					smtTranslationFolder);
			if (DEBUG_DETAILS) {
				System.out.println("Created temporary SMT result file '"
						+ smtResultFile + "'");
			}

			// Fill the result file with some random characters that can not be
			// considered as a success.
			final PrintStream stream = new PrintStream(smtResultFile);
			stream.println("FAILED");
			stream.close();
		}
	}

	/**
	 * Translates the sequent in SMT-LIB V1.2 language and sets the benchmark
	 * file with the result.
	 * 
	 * @throws IOException
	 */
	abstract protected void makeSMTBenchmarkFileV1_2() throws IOException;

	/**
	 * Translates the sequent in SMT-LIB V2.0 language and sets the benchmark
	 * file with the result.
	 * 
	 * @throws IOException
	 */
	abstract protected void makeSMTBenchmarkFileV2_0() throws IOException;

	abstract void extractUnsatCoreFromVeriTProof();

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
			boolean smtBenchmarkFileMade = false;
			if (solverConfig.getSmtlibVersion().equals(V1_2)) {
				try {
					makeSMTBenchmarkFileV1_2();
					smtBenchmarkFileMade = true;
				} catch (IllegalArgumentException e) {
					if (DEBUG) {
						System.out
								.println("Due to translation failure, the solver won't be launched.");
					}
				}

			} else if (DEV) {
				/**
				 * smtlibVersion.equals(V2_0)
				 */
				try {
					makeSMTBenchmarkFileV2_0();
					smtBenchmarkFileMade = true;
				} catch (IllegalArgumentException e) {
					if (DEBUG) {
						System.out
								.println("Due to translation failure, the solver won't be launched.");
					}
				}
			}

			if (smtBenchmarkFileMade) {
				final String solverName = solverConfig.getSolver().toString();
				if (DEBUG_DETAILS) {
					System.out.println("Launching " + solverName
							+ " with input:\n");
					showSMTBenchmarkFile();
				}

				setMonitorMessage("Running SMT solver : " + solverName + ".");

				try {
					callProver(solverCommandLine());
				} catch (IllegalArgumentException e) {
					if (DEBUG) {
						System.out
								.println("Exception raised during prover call : "
										+ e.getMessage());
					}
				}
			}

			if (DEV & isValid()) {
				if (solverConfig.getSolver().equals(VERIT)
						&& solverConfig.getArgs().contains("--proof=")) {
					extractUnsatCoreFromVeriTProof();
				}
			}

		} catch (final IOException e) {
			if (DEBUG) {
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
			}
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

	@Override
	public Set<Predicate> neededHypotheses() {
		return neededHypotheses;
	}

	@Override
	public boolean isGoalNeeded() {
		return goalNeeded;
	}

	/**
	 * Human-readable message to be displayed for this proof.
	 */
	@Override
	public String displayMessage() {
		final StringBuilder message = new StringBuilder();
		message.append("SMT-").append(solverConfig.getSolver().toString());
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
	}
}
