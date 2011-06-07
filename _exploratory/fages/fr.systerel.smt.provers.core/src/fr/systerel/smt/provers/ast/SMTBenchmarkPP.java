/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTSymbol.BENCHMARK;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vitor
 * 
 */
public class SMTBenchmarkPP extends SMTBenchmark {

	/**
	 * The signature of the benchmark
	 */
	private final SMTSignaturePP signature;

	/**
	 * Constructs a new benchmark.
	 * 
	 * @param lemmaName
	 *            the lemma of the benchmark
	 * @param signature
	 *            the signature of the benchmark
	 * @param assumptions
	 *            the assumptions of the benchmark
	 * @param goal
	 *            the goal of the benchmark
	 */
	public SMTBenchmarkPP(final String lemmaName,
			final SMTSignaturePP signature, final List<SMTFormula> assumptions,
			final SMTFormula goal) {
		super(lemmaName, assumptions, goal);
		this.signature = signature;
	}

	@Override
	public SMTSignature getSignature() {
		return signature;
	}

	/**
	 * Remove unused symbols from the benchmark
	 */
	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption, symbols);
		}
		getUsedSymbols(goal, symbols);

		signature.removeUnusedSymbols(symbols);
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
	@Override
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		smtCmdOpening(sb, BENCHMARK, name);
		signature.toString(sb);
		sb.append("\n");
		assumptionsSection(sb);
		formulaSection(sb);
		sb.append(CPAR);
		pw.println(sb.toString());
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
	 * @param formula
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTFormula formula,
			final Set<SMTSymbol> symbols) {
		if (formula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) formula;
			getUsedSymbols(atom, symbols);

		} else if (formula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) formula;
			getUsedSymbols(con, symbols);

		} else if (formula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) formula;
			getUsedSymbols(qf, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ formula.getClass().toString());
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
		for (final SMTFormula formula : formulas) {
			getUsedSymbols(formula, symbols);
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
