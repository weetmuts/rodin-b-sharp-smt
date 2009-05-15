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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

		FileOutputStream outputStream = new FileOutputStream(SMTFile);
		OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
		BufferedWriter output = new BufferedWriter(streamWriter);
		output.write("(benchmark " + patch(benchmark.getName()) + ".smt\n");
		output.write("          :status " + benchmark.getStatus().getName()
				+ "\n");
		output.write("          :logic " + benchmark.getLogic() + "\n");
		for (Annotation annotation : benchmark.getAnnotations())
			output.write("          :" + annotation.getAttribute() + " {"
					+ annotation.getValue() + "}\n");
		if (!benchmark.getFunctions().isEmpty()) {
			output.write("          :extrafuns (");
			for (BenchmarkFunction function : benchmark.getFunctions()) {
				output.write(" (" + function.getName());
				for (Sort sort : function.getSignature())
					output.write(" " + sort.getName());
				output.write(")");
			}
			output.write(" )\n");
		}
		for (BenchmarkFormula formula : benchmark.getAssumptions())
			output.write("          :assumption " + formula + "\n");
		output.write("          :formula " + benchmark.getFormula() + "\n");
		String notes = benchmark.getNotes();
		if ((notes != null) && !notes.equals(""))
			output.write("          :notes " + benchmark.getNotes() + "\n");
		output.write(")");
		output.flush();
		output.close();
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
