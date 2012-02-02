/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.internal.ast.symbols.SMTSymbol;

/**
 * This class is the benchmark resultant of the translation in the PP approach
 * 
 */
public class SMTBenchmarkPP extends SMTBenchmark {
	/**
	 * Constructs a new benchmark.
	 * 
	 * @param lemmaName
	 *            the lemma of the benchmark
	 * @param signature
	 *            the signature of the benchmark
	 * @param assumptions
	 *            the assumptions of the benchmark
	 * @param formula
	 *            the formula of the benchmark
	 */
	public SMTBenchmarkPP(final String lemmaName, final SMTSignature signature,
			final List<SMTFormula> assumptions, final SMTFormula formula,
			final HashMap<String, ITrackedPredicate> labelMap) {
		super(lemmaName + "_pp", signature, assumptions, formula, labelMap);
		comments.add("translated from Event-B with the PP approach of Rodin SMT Plugin");
	}

	/**
	 * Remove unused symbols from the benchmark
	 */
	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption, symbols);
		}
		getUsedSymbols(formula, symbols);

		signature.removeUnusedSymbols(symbols);
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code term}.
	 * 
	 * @param term
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTTerm term,
			final Set<SMTSymbol> symbols) {
		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fa = (SMTFunApplication) term;
			getUsedSymbols(fa, symbols);

		} else if (term instanceof SMTNumeral) {
			final SMTNumeral num = (SMTNumeral) term;
			getUsedSymbols(num, symbols);

		} else if (term instanceof SMTVar) {
			final SMTVar var = (SMTVar) term;
			getUsedSymbols(var, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code fa}.
	 * 
	 * @param fa
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTFunApplication fa,
			final Set<SMTSymbol> symbols) {
		symbols.add(fa.getSort());
		symbols.add(fa.getSymbol());
		final SMTTerm[] terms = fa.getArgs();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code atom}.
	 * 
	 * @param atom
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTAtom atom, final Set<SMTSymbol> symbols) {
		symbols.add(atom.getPredicate());

		final SMTTerm[] terms = atom.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from
	 * {@code formula}.
	 * 
	 * @param smtFormula
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTFormula smtFormula,
			final Set<SMTSymbol> symbols) {
		if (smtFormula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) smtFormula;
			getUsedSymbols(atom, symbols);

		} else if (smtFormula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) smtFormula;
			getUsedSymbols(con, symbols);

		} else if (smtFormula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) smtFormula;
			getUsedSymbols(qf, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ smtFormula.getClass().toString());
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code con}.
	 * 
	 * @param con
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTConnectiveFormula con,
			final Set<SMTSymbol> symbols) {
		final SMTFormula[] formulas = con.getFormulas();
		for (final SMTFormula smtFormula : formulas) {
			getUsedSymbols(smtFormula, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code qf}.
	 * 
	 * @param qf
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTQuantifiedFormula qf,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(qf.getFormula(), symbols);
	}
}
