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
package fr.systerel.decert.tests.smt;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.smt.BenchmarkWriter;

/**
 * The class used to translate a set of Event-B lemmas to SMT-LIB benchmarks.
 * The lemmas are passed as XML input files to the <tt>BenchmarkWriter.main</tt>
 * entry point method.
 * <p>
 * It is possible to check the format of the built SMT files by turning on the
 * <tt>CHECK_FORMAT</tt> option. In the same manner, the SMT benchmark is
 * checked for satisfiability iff the <tt>CHECK_SAT</tt> option is set.
 */
public class TranslationTests extends AbstractSMTTests {

	/**
	 * The path of the input folder containing the XML files for the Event-B
	 * lemmas to be translated in SMT-LIB format, and their associated DTD file.
	 */
	private final String XMLFolder = "C:\\Utilisateurs\\pascal\\workspace\\c444\\1\\Sorted Lemmas";

	/**
	 * The path of the output folder where to store the generated SMT files.
	 */
	private final String SMTFolder = "C:\\Utilisateurs\\pascal\\workspace\\c444\\1\\Benchmarks";

	/**
	 * The path of the SMT-LIB parser tool.
	 * <p>
	 * This path is useless if the <tt>CHECK_FORMAT</tt> option is turned off.
	 */
	private final String SMTParserFolder = "C:\\softs\\smtlib-parser";

	/**
	 * The path of the Z3 SMT solver tool.
	 * <p>
	 * This path is useless if the <tt>CHECK_SAT</tt> option is turned off.
	 */
	private final String SMTSolverFolder = "C:\\softs\\Microsoft Research\\Z3-1.3.6\\bin";

	/**
	 * The option used to determine whether the format of the generated SMT
	 * files is to be checked, or not.
	 * <p>
	 * This option shall be set to <tt>true</tt> iff the check shall be
	 * performed.
	 */
	private final static boolean CHECK_FORMAT = true;

	/**
	 * The option used to determine whether the benchmarks contained in the
	 * generated SMT files are to be checked for satisfiability, or not.
	 * <p>
	 * This option shall be set to <tt>true</tt> iff the check shall be
	 * performed.
	 */
	private final static boolean CHECK_SAT = true;

	@Test
	public void testTranslate() throws Exception {
		File DTDFile = new File(XMLFolder, "lemmas.dtd");
		File dir = new File(XMLFolder);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File file, String name) {
					return (name.endsWith(".xml"));
				}
			});

			for (int i = 0; i < files.length; i++) {
				URL XMLFile = BenchmarkWriter.setDoctype(files[i].toURL(),
						DTDFile.toURL());
				Document document = BenchmarkWriter.load(XMLFile, DTDFile
						.toURL());
				String output = files[i].getName();
				output = output.substring(0, output.indexOf("."));
				File outputFolder = new File(new File(SMTFolder), output);
				outputFolder.mkdir();
				List<Lemma> lemmas = BenchmarkWriter.parse(document);
				for (Lemma lemma : lemmas) {
					File SMTFile = new File(outputFolder, BenchmarkWriter
							.patch(lemma.getTitle())
							+ ".smt");
					if (!BenchmarkWriter.write(lemma, SMTFile))
						continue;

					// Check the format of the built file
					if (CHECK_FORMAT) {
						Process p = Runtime.getRuntime().exec(
								SMTParserFolder + "/smtbench.exe "
										+ SMTFile.getAbsolutePath());
						InputStream in = p.getInputStream();
						StringBuilder sb = new StringBuilder();
						char c = (char) in.read();
						while (c != (char) -1) {
							sb.append(c);
							c = (char) in.read();
						}
						assertTrue(SMTFile.getAbsolutePath() + "\n"
								+ sb.toString(),
								sb.toString().indexOf("error") == -1);
					}

					// Check that the benchmark contained in the built file is
					// satisfiable
					if (CHECK_SAT) {
						Process p = Runtime.getRuntime().exec(
								SMTSolverFolder + "/z3.exe "
										+ SMTFile.getAbsolutePath());
						InputStream in = p.getInputStream();
						StringBuilder sb = new StringBuilder();
						char c = (char) in.read();
						while (c != (char) -1) {
							sb.append(c);
							c = (char) in.read();
						}
						assertTrue(SMTFile.getAbsolutePath() + "\n"
								+ sb.toString(),
								sb.toString().indexOf("sat") >= 0);
					}
				}
			}
		}
	}
}
