/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - SMT-LIB 2.0 implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import static java.util.regex.Pattern.compile;
import static org.eventb.smt.core.SolverKind.VERIT;
import static org.eventb.smt.core.internal.provers.Messages.SMTVeriTCall_SMTLIBV2_0_deactivated;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG_DETAILS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.SMTPrintOptions;
import org.eventb.smt.core.internal.prefs.SimplePreferences;
import org.eventb.smt.core.internal.translation.SMTThroughPP;

/**
 * This class represents a call to an SMT solver using the veriT approach. More
 * precisely, this class is called when a client wants to discharge an Event-B
 * sequent by using the veriT approach to translate it to an SMT-LIB benchmark
 * and some selected SMT solver to discharge it.
 */
public class SMTVeriTCall extends SMTProverCall {

	private static final String SIMPLIFY_ARGUMENT_STRING = "--print-simp-and-exit";
	private static final String PRINT_FLAT = "--print-flat";
	private static final String DISABLE_BANNER = "--disable-banner";
	private static final String DISABLE_ACKERMANN = "--disable-ackermann";
	private static final String DISABLE_PRINT_SUCCESS = "--disable-print-success";
	private static final String INPUTSMT2 = "--input=smtlib2";
	private static final String OUTPUTSMT2 = "--output=smtlib2";

	private IPath veritPath;

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

	/**
	 * FOR TESTS ONLY
	 */
	public SMTVeriTCall(final ISimpleSequent sequent,
			final IProofMonitor pm, final StringBuilder debugBuilder,
			final SMTConfiguration config, final String poName) {
		super(sequent, pm, debugBuilder, config, poName, new SMTThroughPP());
		setVeriTPath();
	}

	private void setVeriTPath() {
		this.veritPath = SimplePreferences.getVeriTPath();
	}

	/**
	 * FOR DEBUG ONLY: prints the temporary benchmark (containing macros) on the
	 * standard output.
	 */
	private void showVeriTBenchmarkFile() {
		showFile(debugBuilder, veriTBenchmarkFile);
	}

	/**
	 * This method calls veriT in order to make it process the macros of the
	 * temporary SMT benchmark, and updates the field
	 * <code>macrosTranslated</code> according to its success.
	 * 
	 * @throws IOException
	 */
	private void callVeriT(SMTBenchmark smtBenchmark) throws IOException {
		callVeriT2_0();
	}

	private void callVeriT2_0() throws IOException {
		// TODO Auto-generated method stub
		final List<String> cmd = new ArrayList<String>();

		if (veritPath == null || veritPath.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.SmtProversCall_veriT_path_not_defined);
		}

		cmd.add(veritPath.toOSString());
		cmd.add(DISABLE_BANNER);
		cmd.add(DISABLE_PRINT_SUCCESS);
		cmd.add(INPUTSMT2);
		cmd.add(OUTPUTSMT2);
		cmd.add(SIMPLIFY_ARGUMENT_STRING);
		cmd.add(PRINT_FLAT);
		cmd.add(DISABLE_ACKERMANN);
		cmd.add(veriTBenchmarkFile.getPath());
		if (DEBUG_DETAILS) {
			debugBuilder.append("About to launch veriT command:\n   ");
			for (String arg : cmd) {
				debugBuilder.append(' ').append(arg);
			}
			debugBuilder.append("\n");
		}

		try {
			final ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			activeProcesses.add(process);
			final ProcessMonitor monitor = new ProcessMonitor(null, process,
					this);

			if (DEBUG_DETAILS)
				showProcessOutcome(debugBuilder, monitor);

			veriTResult = new String(monitor.output());
			macrosTranslated = checkVeriTResult();

			if (DEBUG_DETAILS) {
				debugBuilder.append("veriT ");
				debugBuilder.append(macrosTranslated ? "succeeded\n"
						: "failed:\n");
				debugBuilder.append(veriTResult).append("\n");
			}

		} finally {
			if (DEBUG_DETAILS)
				debugBuilder.append("veriT command finished.\n");
		}

	}

	/**
	 * This method checks if the response of veriT, after it processed the
	 * macros contained in the benchmark produced by the plug-in, contains
	 * "(benchmark", which we assume to mean that no error happened.
	 * 
	 * TODO: Create same method for SMT 2.0
	 * 
	 * @throws IOException
	 *             if any IO problem occurs when accessing the SMT files and
	 *             folders
	 */
	private boolean checkVeriTResult() throws IOException {
		if (veriTResult.contains("(set-logic")) {
			return true;
		}
		return false;
	}

	/**
	 * Makes temporary files in the given path
	 */
	@Override
	public void makeTempFiles() throws IOException {
		super.makeTempFiles();
		String fileExtension = NON_STANDARD_SMT_LIB2_FILE_EXTENSION;

		veriTBenchmarkFile = tempFiles.create("smt_mac", fileExtension);
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				debugBuilder.append("Created temporary veriT benchmark file '");
				debugBuilder.append(veriTBenchmarkFile).append("'\n");
			}
		}
	}

	@Override
	public void makeSMTBenchmarkFile() throws IOException {
		makeSMTBenchmarkFileV2_0();
	}

	private void makeSMTBenchmarkFileV2_0() throws IOException {
		if (DEBUG) {

			/**
			 * Updates the name of the benchmark (the name originally given
			 * could have been changed by the translator if it was a reserved
			 * symbol)
			 */
			lemmaName = benchmark.getName();

			/**
			 * Makes temporary files
			 */
			makeTempFiles();

			/**
			 * Prints the benchmark with macros in a file
			 */
			final PrintWriter veriTBenchmarkWriter = openSMTFileWriter(veriTBenchmarkFile);
			final SMTPrintOptions options = new SMTPrintOptions();
			options.printAnnotations = true;
			benchmark.print(veriTBenchmarkWriter, options);
			veriTBenchmarkWriter.close();

			/**
			 * Calls veriT to process the macros of the benchmark
			 */
			if (DEBUG_DETAILS) {
				debugBuilder.append("Launching ").append(VERIT)
						.append(" with input:\n\n");
				showVeriTBenchmarkFile();
			}

			callVeriT(benchmark);

			/**
			 * Prints the SMT-LIB benchmark in a file
			 */
			if (macrosTranslated) {
				final FileWriter smtBenchmarkWriter = new FileWriter(
						smtBenchmarkFile);
				try {
					smtBenchmarkWriter.write(veriTResult);
				} finally {
					smtBenchmarkWriter.close();
				}
			} else {
				throw new IllegalArgumentException(veriTResult);
			}
		} else {
			throw new IllegalArgumentException(
					SMTVeriTCall_SMTLIBV2_0_deactivated);
		}
	}

	@Override
	// FIXME: How to extract unsat core in veriT approach
	protected void extractUnsatCore() {
		final Set<Predicate> foundNeededHypotheses = new HashSet<Predicate>();
		goalNeeded = false;
		final Map<String, ITrackedPredicate> labelMap = benchmark.getLabelMap();
		for (final Entry<String, ITrackedPredicate> entry : labelMap.entrySet()) {
			final String label = entry.getKey();
			final ITrackedPredicate trPredicate = entry.getValue();
			if (compile(label).matcher(solverResult).find()) {
				if (trPredicate.isHypothesis()) {
					foundNeededHypotheses.add(trPredicate.getOriginal());
				} else {
					goalNeeded = true;
				}
			}
		}

		if (!foundNeededHypotheses.isEmpty()) {
			neededHypotheses = foundNeededHypotheses;
		}
	}
}
