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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class builds an SMT-LIB SMTBenchmark
 * 
 * @author guyot
 * 
 */
public class SMTBenchmark {

	protected final String name;
	protected final SMTSignature signature;
	protected final List<SMTFormula> assumptions;
	protected final SMTFormula formula;
	protected final List<String> comments = new ArrayList<String>();

	/**
	 * Constructs a new SMT Benchmark. It is composed by the name of the
	 * benchmark, the signature, the assumptions and the formula.
	 * 
	 * @param lemmaName
	 *            the name of the benchmark
	 * @param assumptions
	 *            the list of assumptions
	 * @param formula
	 *            the formula formula
	 */
	public SMTBenchmark(final String lemmaName, final SMTSignature signature,
			final List<SMTFormula> assumptions, final SMTFormula formula) {
		this.signature = signature;
		name = this.signature.freshBenchmarkName(lemmaName);
		this.assumptions = assumptions;
		this.formula = formula;
	}

	protected void getUsedSymbols(final SMTNumeral num,
			final Set<SMTSymbol> symbols) {
		symbols.add(num.getSort());
	}

	protected void getUsedSymbols(final SMTVar var, final Set<SMTSymbol> symbols) {
		symbols.add(var.getSort());
	}

	/**
	 * Appends the string for notes section to the string builder
	 * 
	 * @param sb
	 *            the builder which will receive the string
	 */
	protected void appendComments(final StringBuilder sb) {
		for (final String comment : comments) {
			sb.append(";");
			sb.append(comment);
			sb.append("\n");
		}
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

	/**
	 * Appends the string representation of the formula section to the strinh
	 * builder
	 * 
	 * @param sb
	 *            the builder that will receive the representation
	 */
	protected void formulaSection(final StringBuilder sb) {
		sb.append(" :formula (not ");
		formula.toString(sb, false);
		sb.append(")\n");
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

	public SMTSignature getSignature() {
		return signature;
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
	 * get the assumptions of the benchmark
	 * 
	 * @return the assumptions of the benchmark
	 */
	public List<SMTFormula> getAssumptions() {
		return assumptions;
	}

	/**
	 * get the formula of the benchmark
	 * 
	 * @return the formula of the benchmark
	 */
	public SMTFormula getFormula() {
		return formula;
	}

	/**
	 * This method is created to print the string representation of the
	 * benchmark in the PrintWriter
	 * 
	 * @param pw
	 *            the printwriter that will receive the string representation of
	 *            the benchmark
	 */
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		smtCmdOpening(sb, BENCHMARK, name);
		appendComments(sb);
		sb.append("\n");
		signature.toString(sb);
		sb.append("\n");
		assumptionsSection(sb);
		formulaSection(sb);
		sb.append(CPAR);
		pw.println(sb.toString());
	}
}
