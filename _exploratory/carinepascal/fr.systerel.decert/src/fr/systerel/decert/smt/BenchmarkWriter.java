/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.smt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaParser;
import fr.systerel.decert.Theory;

/**
 * This class allows to write SMT files containing benchmarks.
 */
public final class BenchmarkWriter extends LemmaParser {

	/**
	 * Patches a file name.
	 * 
	 * @param filename
	 *            the file name to be patched
	 * @return the patched file name
	 */
	public final static String patch(String filename) {
		filename = filename.replaceAll("\\s", "");
		filename = filename.replaceAll("\\|", ".");
		filename = filename.replaceAll("/", "_");
		return filename;
	}

	/**
	 * Writes a benchmark.
	 * 
	 * @param benchmark
	 *            the benchmark to be written
	 * @param SMTFile
	 *            the file where to write to
	 * @throws IOException
	 *             if a problem occurs when patching the XML file
	 */
	public final static void write(final Benchmark benchmark, final File SMTFile)
			throws IOException {

		final PrintWriter out = new PrintWriter(SMTFile);
		out.println("(benchmark " + patch(benchmark.getName()) + ".smt");
		out.println("          :status " + benchmark.getStatus().getName());
		out.println("          :logic " + benchmark.getLogic());
		for (Annotation annotation : benchmark.getAnnotations()) {
			out.println("          :" + annotation.getAttribute() + " {"
					+ annotation.getValue() + "}");
		}
		if (!benchmark.getFunctions().isEmpty()) {
			out.print("          :extrafuns (");
			for (BenchmarkFunction function : benchmark.getFunctions()) {
				out.print(" (" + function.getName());
				for (Sort sort : function.getSignature())
					out.print(" " + sort.getName());
				out.print(")");
			}
			out.println(" )");
		}
		for (BenchmarkFormula formula : benchmark.getAssumptions()) {
			out.println("          :assumption " + formula);
		}
		out.println("          :formula " + benchmark.getFormula());
		String notes = benchmark.getNotes();
		if (notes != null && notes.length() != 0) {
			out.println("          :notes " + benchmark.getNotes());
		}
		out.println(")");
		out.close();
	}

	/**
	 * Writes a benchmark.
	 * 
	 * @param lemma
	 *            the lemma to be converted
	 * @param SMTFile
	 *            the file where to write to
	 * @return <tt>true</tt> iff the file was successfully written
	 * @throws IOException
	 *             if a problem occurs when patching the XML file
	 */
	public final static boolean write(final Lemma lemma, final File SMTFile)
			throws IOException {
		// The currently supported theories
		List<Theory> supportedTheories = new ArrayList<Theory>();
		supportedTheories.add(Theory.LINEAR_ARITH);
		supportedTheories.add(Theory.LINEAR_ORDER_INT);
		supportedTheories.add(Theory.INTEGER);
		lemma.getTheories().removeAll(supportedTheories);
		if (lemma.getTheories().isEmpty()) {
			Benchmark benchmark = new Benchmark(lemma);
			write(benchmark, SMTFile);
			return true;
		}
		return false;
	}

	/**
	 * The entry point method.
	 * 
	 * @param args
	 *            the command line options
	 */
	public final static void main(String[] args) {
		Resources resources = new Resources();
		LemmaParser.setResources(resources);
		LemmaParser.main(args);

		for (Lemma lemma : getLemmas()) {
			resources.log("Converting the " + lemma.getTitle() + " lemma"
					+ "...", 1);
			File SMTFile = new File(resources.getSMTFolder(), patch(lemma
					.getTitle())
					+ ".smt");
			resources.log("Writting the SMT file: "
					+ SMTFile.toString() + "...", 1);
			try {
				write(lemma, SMTFile);
			} catch (IOException e) {
				System.err
						.println("A problem occurred when trying to write the SMT file!");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
