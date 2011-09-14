/*******************************************************************************
 * Copyright (c) 2010, 2011 UFRN. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	UFRN - initial API and implementation
 * 	Systerel - full code refactoring 
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;
import static org.eventb.smt.ast.commands.SMTCheckSatCommand.getCheckSatCommand;
import static org.eventb.smt.ast.symbols.SMTSymbol.BENCHMARK;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.smt.ast.commands.SMTAssertCommand;
import org.eventb.smt.ast.symbols.SMTSymbol;

/**
 * This class builds an SMT-LIB SMTBenchmark
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
	 * @param builder
	 *            the builder which will receive the string
	 */
	protected void appendComments(final StringBuilder builder) {
		for (final String comment : comments) {
			builder.append("; ");
			builder.append(comment);
			builder.append("\n");
		}
	}

	/**
	 * Appends the string representation of the assumptions section to the
	 * string builder
	 * 
	 * @param builder
	 *            the builder that will receive the representation
	 */
	protected void assumptionsSection(final StringBuilder builder) {
		for (final SMTFormula assumption : assumptions) {
			assumption.printComment(builder);
			switch (signature.getSMTLIBVersion()) {
			case V1_2:
				builder.append(" :assumption ");
				assumption.toString(builder, 13, false);
				break;

			default:
				final SMTAssertCommand assertCommand = new SMTAssertCommand(
						assumption);
				assertCommand.toString(builder);
				break;
			}
			builder.append("\n");
		}
	}

	/**
	 * Appends the string representation of the formula section to the strinh
	 * builder
	 * 
	 * @param builder
	 *            the builder that will receive the representation
	 */
	protected void formulaSection(final StringBuilder builder) {
		switch (signature.getSMTLIBVersion()) {
		case V1_2:
			builder.append(" :formula (not ");
			formula.toString(builder, 15, false);
			builder.append(")\n");
			break;

		default:
			final SMTAssertCommand assertCommand = new SMTAssertCommand(
					SMTFactory.makeNot(new SMTFormula[] { formula }, V2_0));
			assertCommand.toString(builder);
			builder.append("\n");
			break;
		}
	}

	protected void benchmarkContent(final StringBuilder builder) {
		appendComments(builder);
		builder.append("\n");
		signature.toString(builder);
		builder.append("\n");
		assumptionsSection(builder);
		formulaSection(builder);
	}

	/**
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	public static void smtCmdOpening(final StringBuilder builder,
			final String element, final String name) {
		builder.append(OPAR);
		builder.append(element);
		builder.append(SPACE);
		builder.append(name);
		builder.append("\n");
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
		final StringBuilder builder = new StringBuilder();
		switch (signature.getSMTLIBVersion()) {
		case V1_2:
			smtCmdOpening(builder, BENCHMARK, name);
			benchmarkContent(builder);
			builder.append(CPAR);
			break;

		default:
			benchmarkContent(builder);
			getCheckSatCommand().toString(builder);
			break;
		}
		pw.println(builder.toString());
	}
}
