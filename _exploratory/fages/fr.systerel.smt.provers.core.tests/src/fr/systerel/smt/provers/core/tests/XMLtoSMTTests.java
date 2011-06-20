/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Implementation
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests;

import static br.ufrn.smt.solver.translation.SMTTranslationApproach.USING_PP;
import static br.ufrn.smt.solver.translation.SMTTranslationApproach.USING_VERIT;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.ufrn.smt.solver.translation.SMTSolver;
import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.core.tests.utils.LemmaParser;

/**
 * This class is used to make tests with XML files. It's a parameterized tests
 * class.
 * 
 * @author vitor
 * 
 */
@RunWith(Parameterized.class)
public class XMLtoSMTTests extends CommonSolverRunTests {

	/**
	 * If true, is printed details of the test for each test iteration.
	 */
	private final boolean PRINT_INFO = true;
	private static int round = 0;

	/**
	 * The chosen solver for the tests
	 */
	private final SMTSolver SOLVER = SMTSolver.Z3;

	@Parameters
	public static List<LemmaData[]> getDocumentDatas() {
		final List<LemmaData[]> totalDocData = new ArrayList<LemmaData[]>();
		final File DTDFile = new File(DTDFolder, "DTDLemma.dtd");
		final File dir = new File(XMLFolder);
		if (dir.isDirectory()) {
			final File[] files = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(final File file, final String name) {
					return name.endsWith(".xml");
				}
			});
			for (final File file : files) {
				URL XMLFile = null;
				try {
					XMLFile = LemmaParser.setDoctype(file.toURI().toURL(),
							DTDFile.toURI().toURL());
				} catch (final MalformedURLException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				Document document = null;
				try {
					document = LemmaParser.load(XMLFile, DTDFile.toURI()
							.toURL());
				} catch (final MalformedURLException e) {
					e.printStackTrace();
				} catch (final SAXException e) {
					e.printStackTrace();
				}
				String output = file.getName();
				output = output.substring(0, output.indexOf("."));
				final File outputFolder = new File(new File(SMTFolder), output);
				outputFolder.mkdir();
				final List<LemmaData[]> docDatas = parse(document);

				totalDocData.addAll(docDatas);
			}
		}
		return totalDocData;
	}

	/**
	 * Constructs a new test.
	 * 
	 * @param data
	 *            the parameter of one test.
	 */
	public XMLtoSMTTests(final LemmaData data) {
		this.data = data;
		System.out.println("Loop: " + round++ / 2);
	}

	private final LemmaData data;

	/**
	 * The path of the input folder containing the XML files for the Event-B
	 * lemmas to be translated in SMT-LIB format, and their associated DTD file.
	 */
	private final static String XMLFolder = "/u/vitor/rodin_xml_tmp_files/xml";
	private final static String DTDFolder = "src/fr/systerel/smt/provers/core/tests/utils";

	/**
	 * The path of the output folder where to store the generated SMT files.
	 */
	private final static String SMTFolder = "/u/vitor/rodin_xml_tmp_files/smt";

	/**
	 * Asserts that the given formula is typed.
	 */
	public static void assertTypeChecked(final Formula<?> formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

	/**
	 * Parses the elements of a {@link Document} that contains the lemma info
	 * and stores it in a {@link LemmaData}
	 * 
	 * @param document
	 *            the document that contains the lemma info
	 * @return the parsed content
	 */
	public static List<LemmaData[]> parse(final Document document) {
		final NodeList nodelist = document.getElementsByTagName("lemma");
		final ArrayList<LemmaData[]> docDatas = new ArrayList<LemmaData[]>();

		for (int i = 0; i < nodelist.getLength(); i++) {
			final Element node = (Element) nodelist.item(i);
			Element element;
			// Title
			NodeList elements = node.getElementsByTagName("title");
			String title = null;
			if (elements.getLength() > 0) {
				title = patch(((Element) elements.item(0)).getTextContent());
			}

			// Origin
			elements = node.getElementsByTagName("origin");
			String origin = null;
			if (elements.getLength() > 0) {
				origin = patch(((Element) elements.item(0)).getTextContent());
			}

			elements = node.getElementsByTagName("comment");
			String comment = null;
			if (elements.getLength() > 0) {
				comment = ((Element) elements.item(0)).getTextContent();
			}

			// Goal
			elements = node.getElementsByTagName("goal");
			String goal = null;
			if (elements.getLength() > 0) {
				element = (Element) elements.item(0);
				goal = element.getTextContent();
			}

			final ArrayList<String> theories = new ArrayList<String>();
			elements = node.getElementsByTagName("theories");
			if (elements.getLength() > 0) {
				elements = ((Element) elements.item(0))
						.getElementsByTagName("theory");
				for (int j = 0; j < elements.getLength(); j++) {
					theories.add(((Element) elements.item(j))
							.getAttribute("name"));
				}
			}

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

			final ITypeEnvironment te = mTypeEnvironment(teVar);

			final List<String> predicates = new ArrayList<String>();

			// Hypotheses
			elements = node.getElementsByTagName("hypothesis");
			for (int j = 0; j < elements.getLength(); j++) {
				element = (Element) elements.item(j);
				predicates.add(element.getTextContent());
			}
			final LemmaData[] data = { new LemmaData(title, predicates, goal,
					te, origin, comment, theories) };

			docDatas.add(data);
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
	 * Translates the each lemma of each xml file.
	 */
	@Test(timeout = 3000)
	public void testTranslateWithVerit() {
		setPreferencesForSolverTest(SOLVER);

		String name = data.getLemmaName();
		if (name.isEmpty()) {
			name = data.getOrigin();
		}
		if (PRINT_INFO) {
			System.out.println("Testing lemma: " + name + ".\n");
		}
		name = name + "vt";
		doTest(USING_VERIT, name, data.getHypotheses(), data.getGoal(),
				data.getTe(), VALID);
	}

	/**
	 * Translates the each lemma of each xml file.
	 */
	@Test(timeout = 3000)
	public void testTranslateWithPP() {
		setPreferencesForSolverTest(SOLVER);

		String name = data.getLemmaName();
		if (name.isEmpty()) {
			name = data.getOrigin();
		}
		if (PRINT_INFO) {
			System.out.println("Testing lemma: " + name + ".\n");
		}
		name = name + "pp";
		doTest(USING_PP, name, data.getHypotheses(), data.getGoal(),
				data.getTe(), VALID);
	}
}