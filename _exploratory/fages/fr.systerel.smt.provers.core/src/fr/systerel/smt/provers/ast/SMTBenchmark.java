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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			symbols.addAll(getUsedSymbols(assumption));
		}
		symbols.addAll(getUsedSymbols(goal));
		signature.removeUnusedSymbols(symbols);
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTVeritFiniteFormula vff) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(vff.getfArgument());
		symbol.add(vff.getkArgument());
		symbol.add(vff.getpArgument());
		symbol.add(vff.getFinitePred());

		final SMTTerm[] terms = vff.getTerms();
		for (final SMTTerm term : terms) {
			symbol.addAll(getUsedSymbols(term));
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTFunApplication fa) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(fa.getSort());
		symbol.add(fa.getSymbol());
		final SMTTerm[] terms = fa.getArgs();
		for (final SMTTerm term : terms) {
			symbol.addAll(getUsedSymbols(term));
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTITETerm ite) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(ite.getSort());
		symbol.addAll(getUsedSymbols(ite.getFormula()));
		symbol.addAll(getUsedSymbols(ite.getfTerm()));
		symbol.addAll(getUsedSymbols(ite.gettTerm()));
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTMacroTerm mt) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		for (final SMTTerm term : mt.getArgs()) {
			symbol.addAll(getUsedSymbols(term));
		}
		symbol.add(mt.getMacroSymbol());
		symbol.add(mt.getSort());
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTNumeral num) {
		return new HashSet<SMTSymbol>(Arrays.asList(num.getSort()));
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTVar var) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(var.getSort());
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTVeriTTerm var) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(var.getSort());
		symbol.add(var.getSymbol());
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTTerm term) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fa = (SMTFunApplication) term;
			symbol.addAll(getUsedSymbols(fa));

		} else if (term instanceof SMTITETerm) {
			final SMTITETerm ite = (SMTITETerm) term;
			symbol.addAll(getUsedSymbols(ite));

		} else if (term instanceof SMTMacroTerm) {
			final SMTMacroTerm mt = (SMTMacroTerm) term;
			symbol.addAll(getUsedSymbols(mt));

		} else if (term instanceof SMTNumeral) {
			final SMTNumeral num = (SMTNumeral) term;
			symbol.addAll(getUsedSymbols(num));

		} else if (term instanceof SMTVar) {
			final SMTVar var = (SMTVar) term;
			symbol.addAll(getUsedSymbols(var));

		} else if (term instanceof SMTVeriTTerm) {
			final SMTVeriTTerm vt = (SMTVeriTTerm) term;
			symbol.addAll(getUsedSymbols(vt));

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTVeritCardFormula vcf) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(vcf.getCardSymbol());
		symbol.add(vcf.getfArgument());
		symbol.add(vcf.getkArgument());

		final SMTTerm[] terms = vcf.getTerms();
		for (final SMTTerm term : terms) {
			symbol.addAll(getUsedSymbols(term));
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTVeriTAtom va) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(va.getPredicate());

		final SMTTerm[] terms = va.getTerms();
		for (final SMTTerm term : terms) {
			symbol.addAll(getUsedSymbols(term));
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTQuantifiedFormula qf) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.addAll(getUsedSymbols(qf.getFormula()));
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTFormula formula) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		if (formula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) formula;
			symbol.addAll(getUsedSymbols(atom));

		} else if (formula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) formula;
			symbol.addAll(getUsedSymbols(con));

		} else if (formula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) formula;
			symbol.addAll(getUsedSymbols(qf));

		} else if (formula instanceof SMTVeriTAtom) {
			final SMTVeriTAtom va = (SMTVeriTAtom) formula;
			symbol.addAll(getUsedSymbols(va));

		} else if (formula instanceof SMTVeritCardFormula) {
			final SMTVeritCardFormula vcf = (SMTVeritCardFormula) formula;
			symbol.addAll(getUsedSymbols(vcf));

		} else if (formula instanceof SMTVeritFiniteFormula) {
			final SMTVeritFiniteFormula vff = (SMTVeritFiniteFormula) formula;
			symbol.addAll(getUsedSymbols(vff));

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ formula.getClass().toString());
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTConnectiveFormula con) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		final SMTFormula[] formulas = con.getFormulas();
		for (final SMTFormula formula : formulas) {
			symbol.addAll(getUsedSymbols(formula));
		}
		return symbol;
	}

	private Set<SMTSymbol> getUsedSymbols(final SMTAtom atom) {
		final Set<SMTSymbol> symbol = new HashSet<SMTSymbol>();
		symbol.add(atom.getPredicate());

		final SMTTerm[] terms = atom.getTerms();
		for (final SMTTerm term : terms) {
			symbol.addAll(getUsedSymbols(term));
		}
		return symbol;
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
