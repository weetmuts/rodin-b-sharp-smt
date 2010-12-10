/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.PrintWriter;
import java.util.List;

import org.eventb.core.ast.Predicate;

/**
 * This class builds an SMT-LIB Benchmark
 * 
 * @author guyot
 * 
 */
public class Benchmark {
	private String name;
	private Signature signature;
	private Sequent sequent;

	/**
	 * Adds the closing format of a benchmark command to the given string
	 * builder.
	 */
	private static void benchmarkCmdClosing(final StringBuilder sb) {
		sb.append(")");
	}

	/**
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	private static void benchmarkCmdOpening(final StringBuilder sb,
			final String name) {
		sb.append("(benchmark ");
		sb.append(name);
		sb.append("\n");
	}

	public Benchmark(final String lemmaName, final Signature signature, final Sequent sequent) {
		this.name = lemmaName;
		this.signature = signature;
		this.sequent = sequent;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		benchmarkCmdOpening(sb, this.name);
		this.signature.toString(sb);
		sb.append("\n");
		this.sequent.toString(sb);
		benchmarkCmdClosing(sb);
		pw.println(sb.toString());
	}
}
