/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.xml;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.SolverKind.VERIT;
import static org.eventb.smt.core.SolverKind.Z3;
import static org.eventb.smt.core.TranslationApproach.USING_VERIT;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.SolverKind;
import org.eventb.smt.core.performance.CommonPerformanceTests;
import org.eventb.smt.core.performance.xml.utils.LemmaData;
import org.eventb.smt.core.performance.xml.utils.LemmaParser;
import org.eventb.smt.utils.Theory;
import org.eventb.smt.utils.Theory.TheoryLevel;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used to make tests with XML files. It's a parameterized tests
 * class.
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public abstract class XMLtoSMTTests extends CommonPerformanceTests {
	private final StringBuilder debugBuilder;
	private static int round = 0;

	/**
	 * The path of the input folder containing the XML files for the Event-B
	 * lemmas to be translated in SMT-LIB format, and their associated DTD file.
	 */
	public final static String XMLFolder = System.getProperty("user.home")
			+ File.separatorChar + "c444" + File.separatorChar + "7"
			+ File.separatorChar + "exploratory" + File.separatorChar
			+ "xml_lemmas";
	public final static String XMLFolder2 = System.getProperty("user.home")
			+ File.separatorChar + "c444" + File.separatorChar + "7"
			+ File.separatorChar + "exploratory" + File.separatorChar
			+ "xml_lemmas2";
	/**
	 * The path of the output folder where to store the generated SMT files.
	 */
	public final static String SMTFolder = DEFAULT_TEST_TRANSLATION_PATH
			.toOSString();
	public final static String DTDFolder = "src/org/eventb/smt/core/performance/xml/utils";

	private final LemmaData data;

	/**
	 * Constructs a new test.
	 * 
	 * @param data
	 *            the parameter of one test.
	 */
	public XMLtoSMTTests(final LemmaData data, final SolverKind solver,
			final SMTLIBVersion smtlibVersion, final boolean getUnsatCore) {
		super(solver, !BUNDLED, Theory.fromNames(data.getTheories()), USING_VERIT,
				smtlibVersion, getUnsatCore);
		this.data = data;
		debugBuilder = new StringBuilder();
		debugBuilder.append("\n\n----------------------------\n\nLoop: ");
		debugBuilder.append(round++).append("\n");
	}

	/**
	 * Constructs a new test.
	 * 
	 * @param data
	 *            the parameter of one test.
	 */
	public XMLtoSMTTests(final LemmaData data, final SolverKind solver,
			final SMTLIBVersion smtlibVersion) {
		this(data, solver, smtlibVersion, !GET_UNSAT_CORE);
	}

	public static List<LemmaData[]> getDocumentDatas(
			final List<TheoryLevel> levels) {
		final List<LemmaData[]> totalDocData = new ArrayList<LemmaData[]>();
		final File DTDFile = new File(DTDFolder, "DTDLemma.dtd");
		final File dir = new File(XMLFolder2);
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
				final List<LemmaData[]> docDatas = parse(document, levels);
				totalDocData.addAll(docDatas);
			}
		}
		return totalDocData;
	}

	public static void exportUnsatCore(final String title,
			final Set<Predicate> neededHypotheses, final boolean goalNeeded,
			final ITypeEnvironment te) {

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
					Source source = new DOMSource(document);
					Result result = new StreamResult(file);
					TransformerFactory factory = TransformerFactory
							.newInstance();
					Transformer transformer = factory.newTransformer();

					final NodeList nodelist = document
							.getElementsByTagName("lemma");

					for (int i = 0; i < nodelist.getLength(); i++) {
						final Element node = (Element) nodelist.item(i);
						Element element;

						NodeList elements = node.getElementsByTagName("title");
						if (elements.getLength() > 0) {
							final String currentTitle = patch(((Element) elements
									.item(0)).getTextContent());
							if (currentTitle.equals(title)) {
								System.out.println("\n" + file.getName());
								/**
								 * Sets hypotheses
								 */
								elements = node
										.getElementsByTagName("hypothesis");
								for (int j = 0; j < elements.getLength(); j++) {
									element = (Element) elements.item(j);
									final String predicate = element
											.getTextContent();
									if (neededHypotheses.contains(parse(
											predicate, te))) {
										if (!element.getAttribute("needed")
												.equals("true")) {
											element.setAttribute("needed",
													"true");
											System.out.println(element
													.getTextContent()
													+ " now set to 'needed'");
										}
									} else {
										if (element.getAttribute("needed")
												.equals("true")) {
											element.removeAttribute("needed");
											System.out
													.println(element
															.getTextContent()
															+ " now set to 'not needed'");
										}
									}
								}

								/**
								 * Sets goal
								 */
								elements = node.getElementsByTagName("goal");
								if (elements.getLength() > 0) {
									element = (Element) elements.item(0);
									if (element.getAttribute("needed").equals(
											"false")) {
										if (goalNeeded) {
											element.removeAttribute("needed");
											System.out.println(element
													.getTextContent()
													+ " now set to 'needed'");
										}
									} else {
										if (!goalNeeded) {
											element.setAttribute("needed",
													"false");
											System.out
													.println(element
															.getTextContent()
															+ " now set to 'not needed'");
										}
									}
								}
							}
						}
					}

					transformer.transform(source, result);

				} catch (final MalformedURLException e) {
					e.printStackTrace();
				} catch (final SAXException e) {
					e.printStackTrace();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

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
	public static List<LemmaData[]> parse(final Document document,
			final List<TheoryLevel> levels) {
		final NodeList nodelist = document.getElementsByTagName("lemma");
		final ArrayList<LemmaData[]> docDatas = new ArrayList<LemmaData[]>();

		for (int i = 0; i < nodelist.getLength(); i++) {
			final Element node = (Element) nodelist.item(i);
			Element element;

			/**
			 * Theories
			 */
			final Set<String> theories = new HashSet<String>();
			NodeList elements = node.getElementsByTagName("theories");
			if (elements.getLength() > 0) {
				elements = ((Element) elements.item(0))
						.getElementsByTagName("theory");
				for (int j = 0; j < elements.getLength(); j++) {
					theories.add(((Element) elements.item(j))
							.getAttribute("name"));
				}
			}

			if (levels
					.contains(Theory.getComboLevel(Theory.fromNames(theories)))) {

				// Title
				elements = node.getElementsByTagName("title");
				String title = null;
				if (elements.getLength() > 0) {
					title = patch(((Element) elements.item(0)).getTextContent());
				}

				// Origin
				elements = node.getElementsByTagName("origin");
				String origin = null;
				if (elements.getLength() > 0) {
					origin = patch(((Element) elements.item(0))
							.getTextContent());
				}

				elements = node.getElementsByTagName("comment");
				String comment = null;
				if (elements.getLength() > 0) {
					comment = ((Element) elements.item(0)).getTextContent();
				}

				// Goal
				elements = node.getElementsByTagName("goal");
				String goal = null;
				boolean goalNeeded = true;
				if (elements.getLength() > 0) {
					element = (Element) elements.item(0);
					goal = element.getTextContent();
					if (element.getAttribute("needed").equals("false")) {
						goalNeeded = false;
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
				final List<String> unsat = new ArrayList<String>();

				// Hypotheses
				elements = node.getElementsByTagName("hypothesis");
				for (int j = 0; j < elements.getLength(); j++) {
					element = (Element) elements.item(j);
					final String predicate = element.getTextContent();
					predicates.add(predicate);
					if (element.getAttribute("needed").equals("true")) {
						unsat.add(predicate);
					}
				}
				final LemmaData[] data = { new LemmaData(title, predicates,
						goal, te, origin, comment, theories, unsat, goalNeeded) };

				docDatas.add(data);
			}
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
		if (configuration.getSmtlibVersion() == V2_0) {
			Assert.assertTrue(
					"SMT-LIB 2.0 is not handled by the veriT approach yet",
					false);
		} else {
			String name = data.getLemmaName();
			if (name.isEmpty()) {
				name = data.getOrigin();
			}

			debugBuilder.append("Testing lemma (veriT approach): ")
					.append(name);
			debugBuilder.append(data.getTheories().toString()).append(".\n\n");

			name = name + "vt";
			doTest(name, data.getHypotheses(), data.getGoal(), data.getTe(),
					!TRIVIAL, VALID, debugBuilder);
		}
	}

	/**
	 * Translates the each lemma of each xml file.
	 */
	@Test(timeout = 3000)
	public void testTranslateWithPP() {
		String name = data.getLemmaName();
		if (name.isEmpty()) {
			name = data.getOrigin();
		}

		debugBuilder.append("Testing lemma (PP approach): ").append(name);
		debugBuilder.append(data.getTheories().toString()).append(".\n\n");

		doTest(name, data.getHypotheses(), data.getGoal(), data.getTe(),
				!TRIVIAL, VALID, debugBuilder);
	}

	/**
	 * Translates the each lemma of each xml file.
	 */
	// @Test(timeout = 3000)
	public void testTranslateWithPPandGetUnsatCore() {
		if (configuration.getSmtlibVersion() == V1_2) {
			Assert.assertTrue(
					"Unsat core extraction in SMT-LIB v1.2 is not handled yet",
					false);
		} else {
			String name = data.getLemmaName();
			if (name.isEmpty()) {
				name = data.getOrigin();
			}

			debugBuilder.append("Testing lemma (PP approach): ").append(name);
			debugBuilder.append(data.getTheories().toString()).append(".\n\n");

			final SolverKind kind = configuration.getKind();
			if (kind == VERIT || kind == Z3) {
				doTest(name, data.getHypotheses(), data.getGoal(),
						data.getTe(), VALID, data.getNeededHypotheses(),
						data.isGoalNeeded(), debugBuilder);
			} else {
				Assert.assertTrue("Unsat core extraction is not handled with "
						+ configuration.getSolverName() + " yet", false);
			}
		}
	}
}