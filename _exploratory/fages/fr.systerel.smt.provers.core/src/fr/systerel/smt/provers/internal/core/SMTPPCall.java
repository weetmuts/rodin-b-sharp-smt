/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YGU) - Creation, Implementation and comments
 *     Vitor Alcantara de Almeida - Commented code 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import static br.ufrn.smt.solver.translation.Translator.DEBUG;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.SMTThroughPP;
import fr.systerel.smt.provers.ast.SMTBenchmark;

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
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm, preferences, lemmaName);

		final String translationPathPreferenceValue = preferences
				.getTranslationPath();
		if (translationPathPreferenceValue != null
				&& !translationPathPreferenceValue.isEmpty()) {
			translationPath = translationPathPreferenceValue
					+ File.separatorChar + "pp";
		} else {
			translationPath = DEFAULT_PP_TRANSLATION_PATH;
		}

		ppTranslationFolder = new File(translationPath);
		if (!ppTranslationFolder.mkdirs()) {
			// TODO handle the error
		} else {
			if (DEBUG) {
				System.out.println("Created temporary PP translation folder '"
						+ ppTranslationFolder + "'");
			} else {
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
		final SMTBenchmark benchmark = SMTThroughPP
				.translateToSmtLibBenchmark(lemmaName, hypotheses, goal,
						smtPreferences.getSolver().getId());

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
		benchmark.print(smtFileWriter);
		smtFileWriter.close();
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}
}
