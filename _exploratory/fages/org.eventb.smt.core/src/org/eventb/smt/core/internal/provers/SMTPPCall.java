/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.provers;

import static java.util.regex.Pattern.compile;
import static org.eventb.smt.core.internal.ast.SMTBenchmark.PRINT_ANNOTATIONS;
import static org.eventb.smt.core.internal.ast.SMTBenchmark.PRINT_GET_UNSAT_CORE_COMMANDS;
import static org.eventb.smt.core.internal.ast.SMTBenchmark.PRINT_Z3_SPECIFIC_COMMANDS;
import static org.eventb.smt.core.internal.provers.Messages.SmtProversCall_SMT_file_does_not_exist;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG_DETAILS;
import static org.eventb.smt.core.preferences.AbstractPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.core.provers.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.provers.SolverKind.Z3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.provers.SolverKind;

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

	protected SMTPPCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final ISolverConfig solverConfig, final String poName,
			final String translationPath) {
		this(sequent, pm, new StringBuilder(), solverConfig, poName,
				translationPath);
	}

	protected SMTPPCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final StringBuilder debugBuilder,
			final ISolverConfig solverConfig, final String poName,
			final String translationPath) {
		super(sequent, pm, debugBuilder, solverConfig, poName, translationPath,
				new SMTThroughPP(solverConfig.getSmtlibVersion()));
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
			System.out.println(SmtProversCall_SMT_file_does_not_exist);
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
		final SolverKind solverKind = solver.getKind();
		if (solverKind.equals(Z3)) { // FIXME Add Z3 version checking
			benchmark.print(smtFileWriter, PRINT_ANNOTATIONS,
					PRINT_GET_UNSAT_CORE_COMMANDS, PRINT_Z3_SPECIFIC_COMMANDS);
		} else if (solverKind.equals(ALT_ERGO) || solverKind.equals(VERIT)) {
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
			System.out.println(SmtProversCall_SMT_file_does_not_exist);
		}
	}

	/**
	 * <p>
	 * Parses the solver result to find mentioned assertion labels. If the goal
	 * label is mentioned, <code>goalNeeded</code> becomes <code>true</code>,
	 * else <code>false</code>. If some hypothesis label is mentioned, the
	 * corresponding hypotheses are saved in <code>neededHypotheses</code>.
	 * </p>
	 * <p>
	 * When this method is called, the solverResult is assumed to contain an
	 * unsat-core written in the SMT-LIB 2.0 format, that is :</br>
	 * <code>(get-unsat-core response) gucr ::= f*</code>
	 * </p>
	 * <p>
	 * As the plug-in is supposed to label all the assertions produced from
	 * translating the original Event-B sequent, we can ignore the following
	 * point mentioned in the SMT-LIB standard : "The semantics of this
	 * command’s output is that the reported assertions together with all the
	 * unlabeled ones in the set of all assertions are jointly unsatisfiable".
	 * </p>
	 * TODO : same as in SMTVeriTCall, shall be moved up
	 */
	@Override
	protected void extractUnsatCore() {
		final Set<Predicate> foundNeededHypotheses = new HashSet<Predicate>();
		goalNeeded = false;
		final Map<String, ITrackedPredicate> labelMap = benchmark.getLabelMap();
		for (final String label : labelMap.keySet()) {
			if (compile(label).matcher(solverResult).find()) {
				final ITrackedPredicate trPredicate = labelMap.get(label);
				if (trPredicate.isHypothesis()) {
					foundNeededHypotheses.add(trPredicate.getOriginal());
				} else {
					goalNeeded = true;
				}
			}
		}

		neededHypotheses = foundNeededHypotheses;
	}
}
