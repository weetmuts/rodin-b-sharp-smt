/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.core.internal.ast.SMTPrintOptions;
import org.eventb.smt.core.internal.translation.SMTThroughPP;

/**
 * This class represents a call to an SMT solver using the PP approach. More
 * precisely, this class is called when a client wants to discharge an Event-B
 * sequent by using the PP approach to translate it to an SMT-LIB benchmark and
 * some selected SMT solver to discharge it.
 */
public class SMTPPCall extends SMTProverCall {

	protected SMTPPCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final SMTConfiguration config) {
		this(sequent, pm, new StringBuilder(), config);
	}

	protected SMTPPCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final StringBuilder debugBuilder, final SMTConfiguration config) {
		super(sequent, pm, debugBuilder, config,
				new SMTThroughPP());
	}

	/**
	 * FOR TESTS ONLY
	 */
	public SMTPPCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final StringBuilder debugBuilder, final SMTConfiguration config, final String poName) {
		super(sequent, pm, debugBuilder, config, poName,
				new SMTThroughPP());
	}

	@Override
	protected void makeSMTBenchmarkFile() throws IOException {
		/**
		 * Updates the name of the benchmark (the name originally given could
		 * have been changed by the translator if it was a reserved symbol)
		 */
		lemmaName = benchmark.getName();

		/**
		 * Makes temporary files
		 */
		makeTempFiles();

		/**
		 * Prints the SMT-LIB benchmark in a file
		 */
		final PrintWriter smtFileWriter = openSMTFileWriter(smtBenchmarkFile);
		final SMTPrintOptions options = getPrintingOptions();
		benchmark.print(smtFileWriter, options);
		smtFileWriter.close();
	}

	private SMTPrintOptions getPrintingOptions() {
		final SMTPrintOptions options = new SMTPrintOptions();
		switch (config.getKind()) {
		case Z3:
			// FIXME Add Z3 version checking
			options.printAnnotations = true;
			options.printGetUnsatCoreCommands = true;
			break;
		case ALT_ERGO:
		case VERIT:
			options.printAnnotations = true;
			break;
		case CVC4:
			options.printAnnotations = true;
			options.printGetUnsatCoreCommands = true;
			break;
		default:
			// Nothing to set
			break;
		}
		return options;
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
	 */
	@Override
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

		neededHypotheses = foundNeededHypotheses;
	}
}
