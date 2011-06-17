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

import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * This class builds an SMT-LIB SMTBenchmark
 * 
 * @author guyot
 * 
 */
public abstract class SMTBenchmark {

	protected final String name;
	protected final List<SMTFormula> assumptions;
	protected final SMTFormula goal;

	protected void getUsedSymbols(final SMTNumeral num,
			final Set<SMTSymbol> symbols) {
		symbols.add(num.getSort());
	}

	protected void getUsedSymbols(final SMTVar var, final Set<SMTSymbol> symbols) {
		symbols.add(var.getSort());
	}

	public SMTFormula getGoal() {
		return goal;
	}

	public abstract SMTSignature getSignature();

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

	public List<SMTFormula> getAssumptions() {
		return assumptions;
	}

	/**
	 * Appends the string representation of the assumptions section to the
	 * string builder
	 * 
	 * @param sb
	 *            the builder that will receive the representation
	 */
	protected void assumptionsSection(final StringBuilder sb) {
		for (final SMTFormula assumption : assumptions) {
			sb.append(" :assumption ");
			assumption.toString(sb, false);
			sb.append("\n");
		}
	}

	public abstract void print(final PrintWriter pw);

	/**
	 * Appends the string representation of the formula section to the strinh
	 * builder
	 * 
	 * @param sb
	 *            the builder that will receive the representation
	 */
	protected void formulaSection(final StringBuilder sb) {
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
	 * @param assumptions
	 *            the list of assumptions
	 * @param goal
	 *            the goal formula
	 */
	public SMTBenchmark(final String lemmaName,
			final List<SMTFormula> assumptions, final SMTFormula goal) {
		name = lemmaName;
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
}
