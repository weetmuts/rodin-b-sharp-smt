/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import static org.eventb.smt.ast.SMTBenchmark.PRINT_ANNOTATIONS;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.translation.Translator.DEBUG;
import static org.eventb.smt.translation.Translator.DEBUG_DETAILS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.smt.preferences.SMTSolverConfiguration;
import org.eventb.smt.translation.SMTThroughVeriT;

/**
 * This class represents a call to an SMT solver using the veriT approach. More
 * precisely, this class is called when a client wants to discharge an Event-B
 * sequent by using the veriT approach to translate it to an SMT-LIB benchmark
 * and some selected SMT solver to discharge it.
 */
public class SMTVeriTCall extends SMTProverCall {
	private static final String DEFAULT_VERIT_TRANSLATION_PATH = DEFAULT_TRANSLATION_PATH
			+ File.separatorChar + SMTSolver.VERIT;
	private static final String TEMP_FILE = "_prep";
	private static final String SIMPLIFY_ARGUMENT_STRING = "--print-simp-and-exit";
	private static final String PRINT_FLAT = "--print-flat";
	private static final String DISABLE_BANNER = "--disable-banner";
	private static final String DISABLE_ACKERMANN = "--disable-ackermann";

	private String veritPath;

	private File veriTTranslationFolder = null;

	/**
	 * FOR DEBUG ONLY: this is the temporary SMT benchmark produced by the
	 * plug-in in the veriT approach. This benchmark contains some veriT macros,
	 * and is used as veriT input. VeriT translates the macros and produces a
	 * standard SMT-LIB benchmark.
	 */
	protected File veriTBenchmarkFile;

	/**
	 * This field contains the final standard SMT-LIB benchmark produced by the
	 * plug-in, after its macros were processed by veriT.
	 */
	private String veriTResult;

	/**
	 * This field is set to true when veriT successfully translated the macros
	 * of the temporary SMT-LIB benchmark. Actually, it is set to true if
	 * veriT's response contains the string "(benchmark" which means that no
	 * error happened during the macros processing.
	 */
	private boolean macrosTranslated = false;

	protected SMTVeriTCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTSolverConfiguration solverConfig, final String poName,
			final String translationPath, final String veritPath) {
		super(hypotheses, goal, pm, solverConfig, poName, translationPath);

		if (translationPath != null && !translationPath.isEmpty()) {
			this.translationPath = translationPath + File.separatorChar
					+ SMTSolver.VERIT.toString();
		} else {
			this.translationPath = DEFAULT_VERIT_TRANSLATION_PATH;
		}

		veriTTranslationFolder = new File(translationPath);
		if (!veriTTranslationFolder.mkdirs()) {
			// TODO handle the error
		} else {
			if (DEBUG) {
				if (DEBUG_DETAILS) {
					System.out
							.println("Created temporary veriT translation folder '"
									+ veriTTranslationFolder + "'");
				}
			} else {
				/**
				 * The deletion will be done when exiting Rodin.
				 */
				veriTTranslationFolder.deleteOnExit();
			}
		}

		this.veritPath = veritPath;
	}

	/**
	 * FOR DEBUG ONLY: prints the temporary benchmark (containing macros) on the
	 * standard output.
	 */
	private synchronized void showVeriTBenchmarkFile() {
		showFile(veriTBenchmarkFile);
	}

	/**
	 * This method calls veriT in order to make it process the macros of the
	 * temporary SMT benchmark, and updates the field
	 * <code>macrosTranslated</code> according to its success.
	 * 
	 * @throws IOException
	 */
	private void callVeriT() throws IOException {
		final List<String> cmd = new ArrayList<String>();

		if (veritPath == null || veritPath.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.SmtProversCall_veriT_path_not_defined);
		}

		cmd.add(veritPath);
		cmd.add(SIMPLIFY_ARGUMENT_STRING);
		cmd.add(PRINT_FLAT);
		cmd.add(DISABLE_BANNER);
		cmd.add(DISABLE_ACKERMANN);
		cmd.add(veriTBenchmarkFile.getPath());

		if (DEBUG_DETAILS) {
			System.out.println("About to launch veriT command:");
			System.out.print("   ");
			for (String arg : cmd) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();
		}

		try {
			final ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			activeProcesses.add(process);
			final ProcessMonitor monitor = new ProcessMonitor(null, process,
					this);

			if (DEBUG_DETAILS)
				showProcessOutcome(monitor);

			veriTResult = new String(monitor.output());
			macrosTranslated = checkVeriTResult();

			if (DEBUG_DETAILS)
				System.out.println("veriT "
						+ (macrosTranslated ? "succeeded" : "failed:\n"
								+ veriTResult));

		} finally {
			if (DEBUG_DETAILS)
				System.out.println("veriT command finished.");
		}
	}

	/**
	 * This method checks if the response of veriT, after it processed the
	 * macros contained in the benchmark produced by the plug-in, contains
	 * "(benchmark", which we assume to mean that no error happened.
	 * 
	 * @throws IOException
	 *             if any IO problem occurs when accessing the SMT files and
	 *             folders
	 */
	private boolean checkVeriTResult() throws IOException {
		if (veriTResult.contains("(benchmark")) {
			veriTResult = veriTResult.substring(veriTResult
					.indexOf("(benchmark"));
			return true;
		}
		return false;
	}

	/**
	 * Makes temporary files in the given path
	 */
	@Override
	public synchronized void makeTempFileNames() throws IOException {
		super.makeTempFileNames();

		veriTBenchmarkFile = File.createTempFile(lemmaName + TEMP_FILE,
				SMT_LIB_FILE_EXTENSION, smtTranslationFolder);
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				System.out.println("Created temporary veriT benchmark file '"
						+ veriTBenchmarkFile + "'");
			}
		} else {
			/**
			 * The deletion will be done when exiting Rodin.
			 */
			veriTBenchmarkFile.deleteOnExit();
		}
	}

	/**
	 * Executes the translation of the Event-B sequent using the VeriT approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public synchronized void makeSMTBenchmarkFileV1_2() throws IOException,
			IllegalArgumentException {
		/**
		 * Produces an SMT benchmark containing some veriT macros.
		 */
		proofMonitor.setTask("Translating Event-B proof obligation");
		benchmark = SMTThroughVeriT.translateToSmtLibBenchmark(lemmaName,
				hypotheses, goal);

		/**
		 * Updates the name of the benchmark (the name originally given could
		 * have been changed by the translator if it was a reserved symbol)
		 */
		lemmaName = benchmark.getName();

		/**
		 * Makes temporary files
		 */
		makeTempFileNames();

		/**
		 * Prints the benchmark with macros in a file
		 */
		final PrintWriter veriTBenchmarkWriter = openSMTFileWriter(veriTBenchmarkFile);
		benchmark.print(veriTBenchmarkWriter, !PRINT_ANNOTATIONS);
		veriTBenchmarkWriter.close();
		if (!veriTBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Calls veriT to process the macros of the benchmark
		 */
		if (DEBUG_DETAILS) {
			System.out.println("Launching " + SMTSolver.VERIT
					+ " with input:\n");
			showVeriTBenchmarkFile();
		}
		callVeriT();

		/**
		 * Prints the SMT-LIB benchmark in a file
		 */
		if (macrosTranslated) {
			final FileWriter smtBenchmarkWriter = new FileWriter(
					smtBenchmarkFile);
			smtBenchmarkWriter.write(veriTResult);
			smtBenchmarkWriter.close();
		} else {
			throw new IllegalArgumentException(veriTResult);
		}
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}

	@Override
	protected void makeSMTBenchmarkFileV2_0() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	void extractUnsatCoreFromVeriTProof() {
		// TODO Auto-generated method stub
	}
}