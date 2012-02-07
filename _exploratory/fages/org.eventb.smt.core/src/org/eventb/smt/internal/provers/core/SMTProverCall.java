/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - code refactoring
 *******************************************************************************/

package org.eventb.smt.internal.provers.core;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static org.eventb.smt.internal.provers.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.internal.provers.core.SMTSolver.OPENSMT;
import static org.eventb.smt.internal.provers.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.provers.core.SMTSolver.Z3;
import static org.eventb.smt.internal.provers.core.SMTSolver.Z3_PARAM_AUTO_CONFIG;
import static org.eventb.smt.internal.provers.core.SMTSolver.Z3_PARAM_MBQI;
import static org.eventb.smt.internal.provers.core.SMTSolver.setZ3ParameterToFalse;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.internal.translation.Translator.DEBUG;
import static org.eventb.smt.internal.translation.Translator.DEBUG_DETAILS;

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
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.smt.internal.ast.SMTBenchmark;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.translation.SMTLIBVersion;
import org.eventb.smt.internal.translation.TranslationResult;
import org.eventb.smt.internal.translation.Translator;

/**
 * 
 * Each instance of this class represents a call to an external SMT solver.
 * 
 */
public abstract class SMTProverCall extends XProverCall2 {
	protected static final String RES_FILE_EXTENSION = ".res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	protected static final String NON_STANDARD_SMT_LIB2_FILE_EXTENSION = ".smt2";

	/**
	 * FOR DEBUG ONLY
	 */
	protected final StringBuilder debugBuilder;

	/**
	 * The benchmark produced by the translator if the sequent was not
	 * simplified to a trivial predicate
	 */
	protected SMTBenchmark benchmark;
	// FIXME cannot this field be removed ? (used to check veriT pre-processing)
	protected boolean translationPerformed = false;

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	protected boolean exceptionRaised = false;

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

	protected final Translator translator;

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
	File smtTranslationDir = null;
	File smtBenchmarkFile;
	File smtResultFile;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param sequent
	 *            the sequent to discharge
	 * @param pm
	 *            proof monitor used for cancellation
	 * @param poName
	 *            name of the lemma to prove
	 */
	protected SMTProverCall(final ISimpleSequent sequent,
			final IProofMonitor pm, final SMTSolverConfiguration solverConfig,
			final String poName, final String translationPath,
			final Translator translator) {
		this(sequent, pm, new StringBuilder(), solverConfig, poName,
				translationPath, translator);
	}

	protected SMTProverCall(final ISimpleSequent sequent,
			final IProofMonitor pm, final StringBuilder debugBuilder,
			final SMTSolverConfiguration solverConfig, final String poName,
			final String translationPath, final Translator translator) {
		super(sequent, pm);
		this.debugBuilder = debugBuilder;
		this.solverConfig = solverConfig;
		this.lemmaName = poName;
		this.translationPath = translationPath;
		this.translator = translator;
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private static void showProcessOutput(final StringBuilder debugBuilder,
			ProcessMonitor monitor, boolean error) {
		final String kind = error ? "error" : "output";
		debugBuilder.append("-- Begin dump of process ").append(kind)
				.append(" --\n");
		final byte[] bytes = error ? monitor.error() : monitor.output();
		if (bytes.length != 0) {
			final String output = new String(bytes);
			if (output.endsWith("\n")) {
				debugBuilder.append(error);
			} else {
				debugBuilder.append(error).append("\n");
			}
		}
		debugBuilder.append("-- End dump of process ").append(kind)
				.append(" --\n");
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTBenchmarkFile() {
		showFile(debugBuilder, smtBenchmarkFile);
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTResultFile() {
		showFile(debugBuilder, smtResultFile);
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
		 * This is a patch to deactivate the z3 MBQI module which is buggy.
		 */
		if (solverConfig.getSmtlibVersion().equals(SMTLIBVersion.V1_2)
				&& solverConfig.getSolver().equals(SMTSolver.Z3)) {
			commandLine.add(setZ3ParameterToFalse(Z3_PARAM_AUTO_CONFIG));
			commandLine.add(setZ3ParameterToFalse(Z3_PARAM_MBQI));
		}

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

		/**
		 * Benchmark file produced by translating the Event-B sequent
		 */
		if (solverConfig.getSolver().equals(SMTSolver.MATHSAT5)) {
			commandLine.add("< " + smtBenchmarkFile.getAbsolutePath());
		} else {
			commandLine.add(smtBenchmarkFile.getAbsolutePath());
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
			debugBuilder.append("About to launch solver command:\n   ");
			for (String arg : commandLine) {
				debugBuilder.append(" ");
				debugBuilder.append(arg);
			}
			debugBuilder.append("\n");
		}

		try {
			final ProcessBuilder builder = new ProcessBuilder(commandLine);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			activeProcesses.add(process);
			final ProcessMonitor monitor = new ProcessMonitor(null, process,
					this);

			if (DEBUG_DETAILS)
				showProcessOutcome(debugBuilder, monitor);

			solverResult = new String(monitor.output());
			if (DEBUG) {
				printSMTResultFile();
			}
			if (DEBUG_DETAILS) {
				debugBuilder.append("Result file contains:\n");
				showSMTResultFile();
			}

			valid = checkResult();
			if (DEBUG_DETAILS) {
				debugBuilder.append("Prover ").append(
						valid ? "succeeded" : "failed");
				debugBuilder.append("\n");
			}

		} finally {
			if (DEBUG_DETAILS)
				debugBuilder.append("Solver command finished.\n");
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
	protected static void showFile(final StringBuilder builder, File file) {
		if (file == null) {
			builder.append("***File has been cleaned up***\n");
			return;
		}
		try {
			final BufferedReader rdr = new BufferedReader(new FileReader(file));
			String line;
			while ((line = rdr.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch (IOException e) {
			builder.append("***Exception when reading file: ");
			builder.append(e.getMessage()).append("***\n");
		}
	}

	/**
	 * FOR DEBUG ONLY
	 */
	protected static void showProcessOutcome(final StringBuilder builder,
			ProcessMonitor monitor) {
		showProcessOutput(builder, monitor, false);
		showProcessOutput(builder, monitor, true);
		builder.append("Exit code is: ").append(monitor.exitCode())
				.append("\n");
	}

	/**
	 * Makes temporary files in the given path
	 */
	protected synchronized void makeTempFileNames() throws IOException {
		final String benchmarkTargetPath = translationPath + File.separatorChar
				+ lemmaName;

		if (smtTranslationDir == null) {
			smtTranslationDir = new File(benchmarkTargetPath);
			if (!smtTranslationDir.mkdirs()) {
				if (smtTranslationDir.exists()) {
					if (DEBUG) {
						if (DEBUG_DETAILS) {
							debugBuilder
									.append("The directory already exists.");
						}
					}
				} else {
					throw new IOException(
							"An error occured while trying to make the temporary SMT translation directory.");
				}
			} else {
				if (DEBUG) {
					if (DEBUG_DETAILS) {
						debugBuilder
								.append("Made temporary SMT translation directory '");
						debugBuilder.append(smtTranslationDir).append("'\n");
					}
				} else {
					/**
					 * The deletion will be done when exiting Rodin.
					 */
					smtTranslationDir.deleteOnExit();
				}
			}
		}

		final SMTSolver solver = solverConfig.getSolver();
		if (solverConfig.getSmtlibVersion().equals(V2_0)
				&& (solver.equals(ALT_ERGO) || solver.equals(OPENSMT))) {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					NON_STANDARD_SMT_LIB2_FILE_EXTENSION, smtTranslationDir);
		} else {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					SMT_LIB_FILE_EXTENSION, smtTranslationDir);
		}
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				debugBuilder.append("Created temporary SMT benchmark file '");
				debugBuilder.append(smtBenchmarkFile).append("'\n");
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
					smtTranslationDir);
			if (DEBUG_DETAILS) {
				debugBuilder.append("Created temporary SMT result file '");
				debugBuilder.append(smtResultFile).append("'\n");
			}

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

	abstract protected void extractUnsatCore();

	/**
	 * Runs the external SMT solver on the sequent given at instance creation.
	 */
	@Override
	public void run() {
		try {
			proofMonitor.setTask("Translating Event-B proof obligation");

			/**
			 * Translation of the event-b sequent
			 */
			final TranslationResult result = translator.translate(lemmaName,
					sequent);

			/**
			 * If it was simplified to a trivial predicate, the sequent is set
			 * valid and the predicate used as an unsat-core.
			 */
			if (result.isTrivial()) {
				final ITrackedPredicate pred = result.getTrivialPredicate();
				if (pred.isHypothesis()) {
					valid = true;
					neededHypotheses = singleton(pred.getPredicate());
					goalNeeded = false;
				} else {
					valid = true;
					neededHypotheses = emptySet();
					goalNeeded = true;
				}
			} else {
				/**
				 * !result.isTrivial(), an SMT-LIB benchmark was produced
				 */
				benchmark = result.getSMTBenchmark();

				translationPerformed = false;
				try {
					if (solverConfig.getSmtlibVersion().equals(V1_2)) {
						makeSMTBenchmarkFileV1_2();
					} else {
						/**
						 * smtlibVersion.equals(V2_0)
						 */
						makeSMTBenchmarkFileV2_0();
					}
					translationPerformed = true;
				} catch (IllegalArgumentException e) {
					if (DEBUG) {
						debugBuilder.append("Due to translation failure, ");
						debugBuilder.append("the solver won't be launched.\n");
						e.printStackTrace();
					}
				}

				if (translationPerformed) {
					final String solverName = solverConfig.getSolver()
							.toString();
					if (DEBUG_DETAILS) {
						debugBuilder.append("Launching ").append(solverName);
						debugBuilder.append(" with input:\n\n");
						showSMTBenchmarkFile();
					}

					setMonitorMessage("Running SMT solver : " + solverName
							+ ".");

					try {
						callProver(solverCommandLine());
					} catch (IllegalArgumentException e) {
						if (DEBUG) {
							exceptionRaised = true;
							debugBuilder
									.append("Exception raised during prover call : ");
							debugBuilder.append(e.getMessage()).append("\n");
						}
					}
				}

				if (isValid()) {
					if ((solverConfig.getSolver().equals(VERIT) //
							&& solverConfig.getArgs().contains("--proof=")) //
							|| (solverConfig.getSmtlibVersion().equals(V2_0) //
							&& solverConfig.getSolver().equals(Z3))) {
						// FIXME it is not possible to check z3 version, so make
						// errors be catched if not a version capable of manage
						// unsat-cores.
						extractUnsatCore();
					}
				}
			}

		} catch (final IOException e) {
			if (DEBUG) {
				debugBuilder.append(e.getMessage()).append("\n");
			}
			throw new IllegalArgumentException(e);
		} finally {
			if (DEBUG) {
				debugBuilder.append("End of prover call.\n");
				System.out.print(debugBuilder);
			}
		}
	}

	/**
	 * PUBLIC FOR ACCEPTANCE TESTS ONLY
	 */
	public boolean benchmarkIsNull() {
		return benchmark == null;
	}

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	public boolean isTranslationPerformed() {
		return translationPerformed;
	}

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	public boolean isExceptionRaised() {
		return exceptionRaised;
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
		/**
		 * Currently, when no benchmark was produced, it means that PP found a
		 * trivial predicate in the sequent.
		 */
		if (benchmarkIsNull()) {
			message.append("PP (trivial)");
		} else {
			message.append("SMT-").append(solverConfig.getId());
		}
		return message.toString();
	}

	/**
	 * Cleans up this prover call: destroys processes.
	 */
	@Override
	public synchronized void cleanup() {
		for (final Process p : activeProcesses) {
			p.destroy();
		}
	}
}
