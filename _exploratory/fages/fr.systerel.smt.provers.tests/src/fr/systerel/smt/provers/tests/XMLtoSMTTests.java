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
package fr.systerel.smt.provers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.decert.Lemma;
import fr.systerel.decert.smt.Benchmark;
import fr.systerel.decert.smt.BenchmarkWriter;
import fr.systerel.smt.provers.internal.core.SmtProverCall;

/**
 * The class used to translate a set of Event-B lemmas to SMT-LIB benchmarks.
 * The lemmas are passed as XML input files to the <tt>BenchmarkWriter.main</tt>
 * entry point method.
 * <p>
 * It is possible to check the format of the built SMT files by turning on the
 * <tt>CHECK_FORMAT</tt> option. In the same manner, the SMT benchmark is
 * checked for satisfiability iff the <tt>CHECK_SAT</tt> option is set.
 */
public class XMLtoSMTTests extends AbstractTests {

	/**
	 * The path of the input folder containing the XML files for the Event-B
	 * lemmas to be translated in SMT-LIB format, and their associated DTD file.
	 */
	private final String XMLFolder = "/u/vitor/rodin_xml_tmp_files/xml"; // "C:\\Utilisateurs\\pascal\\workspace\\c444\\1\\Sorted Lemmas";

	/**
	 * The path of the output folder where to store the generated SMT files.
	 */
	private final String SMTFolder = "/u/vitor/rodin_xml_tmp_files/smt"; // "C:\\Utilisateurs\\pascal\\workspace\\c444\\1\\Benchmarks";

	private static final NullProofMonitor MONITOR = new NullProofMonitor();

	private static final boolean VALID = true;

	/**
	 * A ProofMonitor is necessary for SmtProverCall instances creation.
	 * Instances from this ProofMonitor do nothing.
	 */
	private static class NullProofMonitor implements IProofMonitor {
		public NullProofMonitor() {
			// Nothing do to
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void setCanceled(boolean value) {
			// nothing to do
		}

		@Override
		public void setTask(String name) {
			// nothing to do
		}
	}

	/**
	 * TODO: Comment this
	 * 
	 * @param lemma
	 * @param SMTFile
	 * @throws IOException
	 */
	public final static void write(final Lemma lemma, final File SMTFile)
			throws IOException {
		Benchmark benchmark = new Benchmark(lemma);

		BenchmarkWriter.write(benchmark, SMTFile);
	}

	/**
	 * First, calls the translation of the given sequent (hypothesis and goal
	 * 'Predicate' instances) into SMT-LIB syntax, and then calls the SMT
	 * prover. The test is successful if the solver returns the expected result.
	 * 
	 * @param parsedHypothesis
	 *            list of the sequent hypothesis (Predicate instances)
	 * @param parsedGoal
	 *            sequent goal (Predicate instance)
	 * @param expectedSolverResult
	 *            the result expected to be produced by the solver call
	 */
	private static void doTest(final String lemmaName,
			final List<Predicate> parsedHypothesis, final Predicate parsedGoal,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (Predicate predicate : parsedHypothesis) {
			assertTypeChecked(predicate);
		}

		// Create an instance of SmtProversCall
		final SmtProverCall smtProverCall = new SmtProverCall(parsedHypothesis,
				parsedGoal, MONITOR, lemmaName) {
			@Override
			public String displayMessage() {
				return "SMT";
			}
		};

		try {
			final List<String> smtArgs = new ArrayList<String>(
					smtProverCall.smtTranslationThroughVeriT());
			smtProverCall.callProver(smtArgs);
			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (TranslationException t) {
			fail(t.getMessage());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		} catch (IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
	}

	/**
	 * Parses the given sequent in the given type environment and launch the
	 * test with the such produced 'Predicate' instances
	 * 
	 * @param inputHyps
	 *            list of the sequent hypothesis written in Event-B syntax
	 * @param inputGoal
	 *            the sequent goal written in Event-B syntax
	 * @param te
	 *            the given type environment
	 * @param expectedSolverResult
	 *            the result expected to be produced by the solver call
	 */
	private static void doTest(final String lemmaName,
			final List<String> inputHyps, final String inputGoal,
			final ITypeEnvironment te, final boolean expectedSolverResult) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(lemmaName, hypotheses, goal, expectedSolverResult);
	}

	/**
	 * Asserts that the given formula is typed.
	 */
	public static void assertTypeChecked(final Formula<?> formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

	public List<XMLDocumentData> parse(final Document document) {
		NodeList nodelist = document.getElementsByTagName("lemma");
		List<XMLDocumentData> docDatas = new ArrayList<XMLDocumentData>();

		for (int i = 0; i < nodelist.getLength(); i++) {
			Element node = (Element) nodelist.item(i);
			Element element;
			// Title
			NodeList elements = node.getElementsByTagName("title");
			String title = null;
			if (elements.getLength() > 0)
				title = ((Element) elements.item(0)).getTextContent();

			// Origin

			elements = node.getElementsByTagName("origin");
			String origin = null;
			if (elements.getLength() > 0)
				origin = ((Element) elements.item(0)).getTextContent();

			// Comment
			// TODO Add comments later
			// elements = node.getElementsByTagName("comment");
			// String comment = null;
			// if (elements.getLength() > 0)
			// comment = ((Element) elements.item(0)).getTextContent();

			// Goal
			elements = node.getElementsByTagName("goal");
			String goal = null;
			if (elements.getLength() > 0) {
				element = (Element) elements.item(0);
				goal = element.getTextContent();
			}

			// Theories
			// TODO Add theories later
			// elements = node.getElementsByTagName("theories");
			// if (elements.getLength() > 0) {
			// elements = ((Element) elements.item(0))
			// .getElementsByTagName("theory");
			// for (int j = 0; j < elements.getLength(); j++)
			// lemma.addTheory(Theory.fromName(((Element) elements.item(j))
			// .getAttribute("name")));
			// }
			// Type environment

			String[] teVar = { "" };

			elements = node.getElementsByTagName("typenv");
			if (elements.getLength() > 0) {
				elements = ((Element) elements.item(0))
						.getElementsByTagName("variable");
				teVar = new String[elements.getLength() * 2];

				for (int j = 0; j < elements.getLength(); j++) {
					element = (Element) elements.item(j);

					teVar[j * 2] = element.getAttribute("name");
					teVar[j * 2 + 1] = element.getAttribute("type");
				}
			}

			ITypeEnvironment te = super.mTypeEnvironment(teVar);

			List<String> predicates = new ArrayList<String>();

			// Hypotheses
			elements = node.getElementsByTagName("hypothesis");
			for (int j = 0; j < elements.getLength(); j++) {
				element = (Element) elements.item(j);
				predicates.add(element.getTextContent());
			}
			docDatas.add(new XMLDocumentData(title, predicates, goal, te,
					origin));
		}
		return docDatas;
	}

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
	 * TODO: Comment this
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTranslate() throws Exception {

		RunProverTestWithPP.setPreferencesForZ3Test();

		File DTDFile = new File(XMLFolder, "lemmas.dtd");
		File dir = new File(XMLFolder);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return (name.endsWith(".xml"));
				}
			});

			for (int i = 0; i < files.length; i++) {
				URL XMLFile = BenchmarkWriter.setDoctype(files[i].toURI()
						.toURL(), DTDFile.toURI().toURL());
				Document document = BenchmarkWriter.load(XMLFile, DTDFile
						.toURI().toURL());
				String output = files[i].getName();
				output = output.substring(0, output.indexOf("."));
				File outputFolder = new File(new File(SMTFolder), output);
				outputFolder.mkdir();
				List<XMLDocumentData> docDatas = this.parse(document);
				for (XMLDocumentData docData : docDatas) {
					String name = patch(docData.getLemmaName());
					if (docData.getLemmaName().isEmpty()) {
						name = patch(docData.getOrigin());
					}
					doTest(name, docData.getHypotheses(), docData.getGoal(),
							docData.getTe(), VALID);
				}
			}
		}
	}
}