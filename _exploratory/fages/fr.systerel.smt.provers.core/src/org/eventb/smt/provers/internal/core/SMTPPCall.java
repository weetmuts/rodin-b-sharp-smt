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
import static org.eventb.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.Translator.DEBUG_DETAILS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.translation.SMTLIBVersion;
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
			final SMTLIBVersion smtlibVersion, final SMTSolver solver,
			final String solverName, final String solverPath,
			final String solverParameters, final String poName,
			final String translationPath) {
		super(hypotheses, goal, pm, smtlibVersion, solver, solverName,
				solverPath, solverParameters, poName, translationPath);
		if (this.translationPath != null && !this.translationPath.isEmpty()) {
			this.translationPath = translationPath + File.separatorChar + "pp";
		} else {
			this.translationPath = DEFAULT_PP_TRANSLATION_PATH;
		}

		ppTranslationFolder = new File(translationPath);
		if (!ppTranslationFolder.mkdirs()) {
			// TODO handle the error
		} else {
			if (DEBUG_DETAILS) {
				System.out.println("Created temporary PP translation folder '"
						+ ppTranslationFolder + "'");
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
				hypotheses, goal, solver.toString(), V1_2);

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
		benchmark.print(smtFileWriter, !PRINT_ANNOTATIONS);
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
				hypotheses, goal, solver.toString(), V2_0);

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
		if (solver.equals(ALT_ERGO) || solver.equals(VERIT)
				|| solver.toString().equals("veriT-proof-producing")) {
			benchmark.print(smtFileWriter, PRINT_ANNOTATIONS);
		} else {
			benchmark.print(smtFileWriter, !PRINT_ANNOTATIONS);
		}
		smtFileWriter.close();
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}

	@Override
	void extractUnsatCoreFromVeriTProof() {
		neededHypotheses = new HashSet<Predicate>();
		goalNeeded = false;
		final Map<String, ITrackedPredicate> labelMap = benchmark.getLabelMap();
		for (final String label : labelMap.keySet()) {
			if (solverResult.contains(label)) {
				final ITrackedPredicate trPredicate = labelMap.get(label);
				if (trPredicate.isHypothesis()) {
					neededHypotheses.add(trPredicate.getOriginal());
				} else {
					goalNeeded = true;
				}
			}
		}
	}
}
