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
		for (SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption);
		}
		getUsedSymbols(goal);
	}

	private void getUsedSymbols(SMTVeritFiniteFormula vff) {
		funSet.add(vff.getfArgument());
		funSet.add(vff.getkArgument());
		predSet.add(vff.getpArgument());
		macroSet.add(vff.getFinitePred());

		SMTTerm[] terms = vff.getTerms();
		for (SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(SMTFunApplication fa) {
		sortSet.add(fa.getSort());
		funSet.add(fa.getSymbol());
	}

	private void getUsedSymbols(SMTITETerm ite) {
		sortSet.add(ite.getSort());
		getUsedSymbols(ite.getFormula());
		getUsedSymbols(ite.getfTerm());
		getUsedSymbols(ite.gettTerm());
	}

	private void getUsedSymbols(SMTMacroTerm mt) {
		for (SMTTerm term : mt.getArgs()) {
			getUsedSymbols(term);
		}
		macroSet.add(mt.getMacroSymbol());
		sortSet.add(mt.getSort());
	}

	private void getUsedSymbols(SMTNumeral num) {
		// Do nothing
	}

	private void getUsedSymbols(SMTVar var) {
		sortSet.add(var.getSort());
	}

	private void getUsedSymbols(SMTVeriTTerm var) {
		sortSet.add(var.getSort());
		predSet.add(var.getSymbol());
	}

	private void getUsedSymbols(SMTTerm term) {

		if (term instanceof SMTFunApplication) {
			SMTFunApplication fa = (SMTFunApplication) term;
			getUsedSymbols(fa);

		} else if (term instanceof SMTITETerm) {
			SMTITETerm ite = (SMTITETerm) term;
			getUsedSymbols(ite);

		} else if (term instanceof SMTMacroTerm) {
			SMTMacroTerm mt = (SMTMacroTerm) term;
			getUsedSymbols(mt);

		} else if (term instanceof SMTNumeral) {
			SMTNumeral num = (SMTNumeral) term;
			getUsedSymbols(num);

		} else if (term instanceof SMTVar) {
			SMTVar var = (SMTVar) term;
			getUsedSymbols(var);

		} else if (term instanceof SMTVeriTTerm) {
			SMTVeriTTerm vt = (SMTVeriTTerm) term;
			getUsedSymbols(vt);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
	}

	private void getUsedSymbols(SMTVeritCardFormula vcf) {
		macroSet.add(vcf.getCardSymbol());
		funSet.add(vcf.getfArgument());
		funSet.add(vcf.getkArgument());

		SMTTerm[] terms = vcf.getTerms();
		for (SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(SMTVeriTAtom va) {
		macroSet.add(va.getPredicate());

		SMTTerm[] terms = va.getTerms();
		for (SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	private void getUsedSymbols(SMTQuantifiedFormula qf) {
		getUsedSymbols(qf.getFormula());
	}

	private void getUsedSymbols(SMTFormula formula) {
		if (formula instanceof SMTAtom) {
			SMTAtom atom = (SMTAtom) formula;
			getUsedSymbols(atom);

		} else if (formula instanceof SMTConnectiveFormula) {
			SMTConnectiveFormula con = (SMTConnectiveFormula) formula;
			getUsedSymbols(con);

		} else if (formula instanceof SMTQuantifiedFormula) {
			SMTQuantifiedFormula qf = (SMTQuantifiedFormula) formula;
			getUsedSymbols(qf);

		} else if (formula instanceof SMTVeriTAtom) {
			SMTVeriTAtom va = (SMTVeriTAtom) formula;
			getUsedSymbols(va);

		} else if (formula instanceof SMTVeritCardFormula) {
			SMTVeritCardFormula vcf = (SMTVeritCardFormula) formula;
			getUsedSymbols(vcf);

		} else if (formula instanceof SMTVeritFiniteFormula) {
			SMTVeritFiniteFormula vff = (SMTVeritFiniteFormula) formula;
			getUsedSymbols(vff);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ formula.getClass().toString());
		}
	}

	private void getUsedSymbols(SMTConnectiveFormula con) {
		SMTFormula[] formulas = con.getFormulas();
		for (SMTFormula formula : formulas) {
			getUsedSymbols(formula);
		}
	}

	private void getUsedSymbols(SMTAtom atom) {
		predSet.add(atom.getPredicate());

		SMTTerm[] terms = atom.getTerms();
		for (SMTTerm term : terms) {
			getUsedSymbols(term);
		}
	}

	/**
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	public static void smtCmdOpening(final StringBuilder sb, String element,
			String name) {
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
