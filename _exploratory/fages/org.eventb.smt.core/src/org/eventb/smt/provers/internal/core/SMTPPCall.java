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
import static org.eventb.smt.ast.SMTBenchmark.PRINT_GET_UNSAT_CORE_COMMANDS;
import static org.eventb.smt.ast.SMTBenchmark.PRINT_Z3_SPECIFIC_COMMANDS;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.provers.internal.core.SMTSolver.Z3;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.Translator.DEBUG;
import static org.eventb.smt.translation.Translator.DEBUG_DETAILS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.preferences.SMTSolverConfiguration;
import org.eventb.smt.translation.SMTThroughPP;

/**
 * This class represents a call to an SMT solver using the PP approach. More
 * precisely, this class is called when a client wants to discharge an Event-B
 * sequent by using the PP approach to translate it to an SMT-LIB benchmark and
 * some selected SMT solver to discharge it.
 */
public class SMTPPCall extends SMTProverCall {
	private static final String DEFAULT_PP_TRANSLATION_PATH = DEFAULT_TRANSLATION_PATH
			+ File.separatorChar + "pp";
	private File ppTranslationFolder = null;

	protected SMTPPCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTSolverConfiguration solverConfig, final String poName,
			final String translationPath) {
		this(hypotheses, goal, pm, new StringBuilder(), solverConfig, poName,
				translationPath);
	}

	protected SMTPPCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final StringBuilder debugBuilder,
			final SMTSolverConfiguration solverConfig, final String poName,
			final String translationPath) {
		super(hypotheses, goal, pm, debugBuilder, solverConfig, poName,
				translationPath);
		if (this.translationPath != null && !this.translationPath.isEmpty()) {
			this.translationPath = this.translationPath + File.separatorChar
					+ "pp";
		} else {
			this.translationPath = DEFAULT_PP_TRANSLATION_PATH;
		}

		ppTranslationFolder = new File(translationPath);
		if (!ppTranslationFolder.mkdirs()) {
			// TODO handle the error
		} else {
			if (DEBUG) {
				if (DEBUG_DETAILS) {
					debugBuilder
							.append("Created temporary PP translation folder '");
					debugBuilder.append(ppTranslationFolder).append("'\n");
				}
			} else {
				/**
				 * The deletion will be done when exiting Rodin.
				 */
				ppTranslationFolder.deleteOnExit();
			}
		}
	}

	/**
	 * Executes the translation of the Event-B sequent using the PP approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public synchronized void makeSMTBenchmarkFileV1_2() throws IOException {
		/**
		 * Produces an SMT benchmark.
		 */
		proofMonitor.setTask("Translating Event-B proof obligation");
		/**
		 * Creation of an SMT-LIB benchmark using the PP approach of Event-B to
		 * SMT-LIB translation
		 */
		benchmark = SMTThroughPP.translateToSmtLibBenchmark(lemmaName,
				hypotheses, goal, V1_2);

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
		 * Prints the SMT-LIB benchmark in a file
		 */
		final PrintWriter smtFileWriter = openSMTFileWriter(smtBenchmarkFile);
		benchmark.print(smtFileWriter, !PRINT_ANNOTATIONS,
				!PRINT_GET_UNSAT_CORE_COMMANDS, !PRINT_Z3_SPECIFIC_COMMANDS);
		smtFileWriter.close();
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}

	/**
	 * Executes the translation of the Event-B sequent using the PP approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public synchronized void makeSMTBenchmarkFileV2_0() throws IOException {
		/**
		 * Produces an SMT benchmark.
		 */
		proofMonitor.setTask("Translating Event-B proof obligation");
		/**
		 * Creation of an SMT-LIB benchmark using the PP approach of Event-B to
		 * SMT-LIB translation
		 */
		benchmark = SMTThroughPP.translateToSmtLibBenchmark(lemmaName,
				hypotheses, goal, V2_0);

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
		 * Prints the SMT-LIB benchmark in a file
		 */
		final PrintWriter smtFileWriter = openSMTFileWriter(smtBenchmarkFile);
		final SMTSolver solver = solverConfig.getSolver();
		if (solver.equals(Z3)) { // FIXME Add Z3 version checking
			benchmark.print(smtFileWriter, PRINT_ANNOTATIONS,
					PRINT_GET_UNSAT_CORE_COMMANDS, PRINT_Z3_SPECIFIC_COMMANDS);
		} else if (solver.equals(ALT_ERGO) || solver.equals(VERIT)) {
			benchmark
					.print(smtFileWriter, PRINT_ANNOTATIONS,
							!PRINT_GET_UNSAT_CORE_COMMANDS,
							!PRINT_Z3_SPECIFIC_COMMANDS);
		} else {
			benchmark
					.print(smtFileWriter, !PRINT_ANNOTATIONS,
							!PRINT_GET_UNSAT_CORE_COMMANDS,
							!PRINT_Z3_SPECIFIC_COMMANDS);
		}
		smtFileWriter.close();
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}

	@Override
	protected void extractUnsatCore() { // FIXME use regex see checkResult
		final Set<Predicate> foundNeededHypotheses = new HashSet<Predicate>();
		goalNeeded = false;
		final Map<String, ITrackedPredicate> labelMap = benchmark.getLabelMap();
		for (final String label : labelMap.keySet()) {
			if (solverResult.contains(label)) {
				final ITrackedPredicate trPredicate = labelMap.get(label);
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

	@Override
	protected void extractUnsatCoreFromVeriTProof() { // FIXME use
														// extractUnsatCore
														// instead
		final Set<Predicate> foundNeededHypotheses = new HashSet<Predicate>();
		goalNeeded = false;
		String separator = "";
		final Map<String, ITrackedPredicate> labelMap = benchmark.getLabelMap();
		if (DEBUG) {
			debugBuilder.append("unsat-core: (");
		}
		for (final String label : labelMap.keySet()) {
			if (solverResult.contains(label)) {
				if (DEBUG) {
					debugBuilder.append(separator + label);
					separator = ", ";
				}
				final ITrackedPredicate trPredicate = labelMap.get(label);
				if (trPredicate.isHypothesis()) {
					foundNeededHypotheses.add(trPredicate.getOriginal());
				} else {
					goalNeeded = true;
				}
			}
		}
		if (DEBUG) {
			debugBuilder.append(").\n");
		}
		if (!foundNeededHypotheses.isEmpty()) {
			neededHypotheses = foundNeededHypotheses;
		}
	}
}
