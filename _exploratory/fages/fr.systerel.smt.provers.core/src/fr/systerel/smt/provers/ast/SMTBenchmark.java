/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import static fr.systerel.smt.provers.ast.SMTSymbol.BENCHMARK;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.macros.SMTMacroTerm;

/**
 * This class builds an SMT-LIB SMTBenchmark
 * 
 * @author guyot
 * 
 */
public class SMTBenchmark {
	private final String name;
	private final SMTSignature signature;
	private final List<SMTFormula> assumptions;
	private final SMTFormula goal;

	private final Set<SMTFunctionSymbol> funSet = new HashSet<SMTFunctionSymbol>();
	private final Set<SMTPredicateSymbol> predSet = new HashSet<SMTPredicateSymbol>();
	private final Set<SMTSortSymbol> sortSet = new HashSet<SMTSortSymbol>();
	private final Set<SMTMacroSymbol> macroSet = new HashSet<SMTMacroSymbol>();

	private void getUsedSymbols() {
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption);
		}
		getUsedSymbols(goal);
	}

	private void getUsedSymbols(final SMTVeritFiniteFormula vff) {
		funSet.add(vff.getfArgument());
		funSet.add(vff.getkArgument());
		predSet.add(vff.getpArgument());
		macroSet.add(vff.getFinitePred());

		final SMTTerm[] terms = vff.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(final SMTFunApplication fa) {
		sortSet.add(fa.getSort());
		funSet.add(fa.getSymbol());
		final SMTTerm[] terms = fa.getArgs();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(final SMTITETerm ite) {
		sortSet.add(ite.getSort());
		getUsedSymbols(ite.getFormula());
		getUsedSymbols(ite.getfTerm());
		getUsedSymbols(ite.gettTerm());
	}

	private void getUsedSymbols(final SMTMacroTerm mt) {
		for (final SMTTerm term : mt.getArgs()) {
			getUsedSymbols(term);
		}
		macroSet.add(mt.getMacroSymbol());
		sortSet.add(mt.getSort());
	}

	private void getUsedSymbols(final SMTNumeral num) {
		// Do nothing
	}

	private void getUsedSymbols(final SMTVar var) {
		sortSet.add(var.getSort());
	}

	private void getUsedSymbols(final SMTVeriTTerm var) {
		sortSet.add(var.getSort());
		predSet.add(var.getSymbol());
	}

	private void getUsedSymbols(final SMTTerm term) {

		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fa = (SMTFunApplication) term;
			getUsedSymbols(fa);

		} else if (term instanceof SMTITETerm) {
			final SMTITETerm ite = (SMTITETerm) term;
			getUsedSymbols(ite);

		} else if (term instanceof SMTMacroTerm) {
			final SMTMacroTerm mt = (SMTMacroTerm) term;
			getUsedSymbols(mt);

		} else if (term instanceof SMTNumeral) {
			final SMTNumeral num = (SMTNumeral) term;
			getUsedSymbols(num);

		} else if (term instanceof SMTVar) {
			final SMTVar var = (SMTVar) term;
			getUsedSymbols(var);

		} else if (term instanceof SMTVeriTTerm) {
			final SMTVeriTTerm vt = (SMTVeriTTerm) term;
			getUsedSymbols(vt);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
	}

	private void getUsedSymbols(final SMTVeritCardFormula vcf) {
		macroSet.add(vcf.getCardSymbol());
		funSet.add(vcf.getfArgument());
		funSet.add(vcf.getkArgument());

		final SMTTerm[] terms = vcf.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(final SMTVeriTAtom va) {
		macroSet.add(va.getPredicate());

		final SMTTerm[] terms = va.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(final SMTQuantifiedFormula qf) {
		getUsedSymbols(qf.getFormula());
	}

	private void getUsedSymbols(final SMTFormula formula) {
		if (formula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) formula;
			getUsedSymbols(atom);

		} else if (formula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) formula;
			getUsedSymbols(con);

		} else if (formula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) formula;
			getUsedSymbols(qf);

		} else if (formula instanceof SMTVeriTAtom) {
			final SMTVeriTAtom va = (SMTVeriTAtom) formula;
			getUsedSymbols(va);

		} else if (formula instanceof SMTVeritCardFormula) {
			final SMTVeritCardFormula vcf = (SMTVeritCardFormula) formula;
			getUsedSymbols(vcf);

		} else if (formula instanceof SMTVeritFiniteFormula) {
			final SMTVeritFiniteFormula vff = (SMTVeritFiniteFormula) formula;
			getUsedSymbols(vff);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ formula.getClass().toString());
		}
	}

	private void getUsedSymbols(final SMTConnectiveFormula con) {
		final SMTFormula[] formulas = con.getFormulas();
		for (final SMTFormula formula : formulas) {
			getUsedSymbols(formula);
		}
	}

	private void getUsedSymbols(final SMTAtom atom) {
		predSet.add(atom.getPredicate());

		final SMTTerm[] terms = atom.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	/**
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	public static void smtCmdOpening(final StringBuilder sb,
			final String element, final String name) {
		sb.append(OPAR);
		sb.append(element);
		sb.append(SPACE);
		sb.append(name);
		sb.append("\n");
	}

	/**
	 * Appends the string representation of the assumptions section to the
	 * string builder
	 * 
	 * @param sb
	 *            the builder that will receive the representation
	 */
	private void assumptionsSection(final StringBuilder sb) {
		for (final SMTFormula assumption : assumptions) {
			sb.append(" :assumption ");
			assumption.toString(sb, false);
			sb.append("\n");
		}
	}

	/**
	 * Appends the string representation of the formula section to the strinh
	 * builder
	 * 
	 * @param sb
	 *            the builder that will receive the representation
	 */
	private void formulaSection(final StringBuilder sb) {
		sb.append(" :formula (not ");
		goal.toString(sb, false);
		sb.append(")\n");
	}

	/**
	 * Constructs a new SMT Benchmark. It is composed by the name of the
	 * benchmark, the signature, the assumptions and the goal.
	 * 
	 * @param lemmaName
	 *            the name of the benchmark
	 * @param signature
	 *            the signature
	 * @param assumptions
	 *            the list of assumptions
	 * @param goal
	 *            the goal formula
	 */
	public SMTBenchmark(final String lemmaName, final SMTSignature signature,
			final List<SMTFormula> assumptions, final SMTFormula goal) {
		name = lemmaName;
		this.signature = signature;
		this.assumptions = assumptions;
		this.goal = goal;
		getUsedSymbols();
		signature.removeUnusedSymbols(funSet, predSet, sortSet);
	}

	/**
	 * returns the name of the benchmark
	 * 
	 * @return the name of the benchmark
	 */
	public String getName() {
		return name;
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
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
}
