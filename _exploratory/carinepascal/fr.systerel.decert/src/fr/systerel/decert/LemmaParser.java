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
package fr.systerel.decert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class allows to parse a XML file containing lemmas. A Document Object
 * Model (DOM) structure is built.
 */
public class LemmaParser {

	/** The built lemmas. */
	private static List<Lemma> lemmas;

	/**
	 * Gets the built lemmas.
	 * 
	 * @return a list of lemmas.
	 */
	protected final static List<Lemma> getLemmas() {
		return lemmas;
	}

	/** The resources. */
	private static Resources resources;

	/**
	 * Sets the resources.
	 * 
	 * @param r
	 *            the resources
	 */
	protected final static void setResources(final Resources r) {
		resources = r;
	}

	/**
	 * Loads the specified XML file.
	 * 
	 * @param XMLFile
	 *            the URL of the XML file
	 * @param DTDFile
	 *            the URL of the associated DTD file
	 * @return a DOM structure
	 * @throws SAXException
	 *             if a problem occurs when loading the file
	 */
	public final static Document load(final URL XMLFile, final URL DTDFile)
			throws SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				public void fatalError(SAXParseException spe)
						throws SAXException {
					throw new SAXException(spe);
				}

				public void error(SAXParseException spe) throws SAXException {
					throw new SAXException(spe);
				}

				public void warning(SAXParseException spe) throws SAXException {
					throw new SAXException(spe);
				}
			});
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException {
					try {
						InputSource source = new InputSource(DTDFile
								.openStream());
						return source;
					} catch (IOException ioe) {
						throw new SAXException(ioe);
					}
				}
			});
		} catch (ParserConfigurationException pce) {
			throw new SAXException(pce);
		}

		try {
			return builder.parse(XMLFile.toString());
		} catch (SAXException sxe) {
			throw new SAXException(sxe);
		} catch (IOException ioe) {
			throw new SAXException(ioe);
		}
	}

	/**
	 * Parses the specified document.
	 * 
	 * @param document
	 *            the DOM structure to be parsed
	 * @return the lemmas contained in the document
	 * @throws ParseException
	 *             if a problem occurs when parsing the document
	 */
	public final static List<Lemma> parse(Document document)
			throws ParseException {
		List<Lemma> l = new ArrayList<Lemma>();
		NodeList nodelist = document.getElementsByTagName("lemma");
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
			elements = node.getElementsByTagName("comment");
			String comment = null;
			if (elements.getLength() > 0)
				comment = ((Element) elements.item(0)).getTextContent();

			// Goal
			elements = node.getElementsByTagName("goal");
			LemmaPredicate goal = null;
			if (elements.getLength() > 0) {
				element = (Element) elements.item(0);
				goal = new LemmaPredicate(Lemma.ff, element.getTextContent(),
						Boolean.parseBoolean(element.getAttribute("needed")));
			}

			Lemma lemma = new Lemma(title, origin, comment, goal);

			// Theories
			elements = node.getElementsByTagName("theories");
			if (elements.getLength() > 0) {
				elements = ((Element) elements.item(0))
						.getElementsByTagName("theory");
				for (int j = 0; j < elements.getLength(); j++)
					lemma.addTheory(Theory
							.fromName(((Element) elements.item(j))
									.getAttribute("name")));
			}
			// Type environment
			elements = node.getElementsByTagName("typenv");
			if (elements.getLength() > 0) {
				elements = ((Element) elements.item(0))
						.getElementsByTagName("variable");
				for (int j = 0; j < elements.getLength(); j++) {
					element = (Element) elements.item(j);
					lemma
							.addToTypeEnvironment(new Variable(Lemma.ff,
									element.getAttribute("name"), element
											.getAttribute("type")));
				}
			}
			// Hypotheses
			elements = node.getElementsByTagName("hypothesis");
			for (int j = 0; j < elements.getLength(); j++) {
				element = (Element) elements.item(j);
				lemma.addHypothesis(new LemmaPredicate(Lemma.ff, element
						.getTextContent(), Boolean.parseBoolean(element
						.getAttribute("needed"))));
			}

			l.add(lemma);
		}
		return l;
	}

	/**
	 * Patches the specified XML file.
	 * 
	 * @param XMLFile
	 *            the XML file to be patched
	 * @param regex
	 *            the regular expression to search for
	 * @param replacement
	 *            the replacement string.
	 * @return the URL of the patched XML file (the input file is not modified)
	 * @throws IOException
	 *             if a problem occurs when patching the XML file
	 */
	public static URL patch(URL XMLFile, String regex, String replacement)
			throws IOException {
		// Reads the XML file
		InputStreamReader streamReader = new InputStreamReader(XMLFile
				.openStream(), "UTF-8");
		BufferedReader buffer = new BufferedReader(streamReader);
		StringWriter sw = new StringWriter();
		String line = "";
		while ((line = buffer.readLine()) != null)
			sw.write(line + "\n");

		// Replaces the regular expression
		String s = sw.toString().replaceFirst(regex, replacement);

		// Writes a temporary XML file
		File tmp = File.createTempFile("lemmas_patched", ".xml");
		tmp.deleteOnExit();

		FileOutputStream outputStream = new FileOutputStream(tmp);
		OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream,
				"UTF8");
		BufferedWriter output = new BufferedWriter(streamWriter);
		output.write(s);
		output.flush();
		output.close();

		return tmp.toURL();
	}

	/**
	 * Sets the doctype in the specified XML file.
	 * 
	 * @param XMLFile
	 *            the XML file to be patched
	 * @param DTDFile
	 *            the DTD file to be referenced
	 * @return the URL of the patched XML file (the input file is not modified)
	 * @throws IOException
	 *             if a problem occurs when patching the XML file
	 */
	public static URL setDoctype(URL XMLFile, URL DTDFile) throws IOException {
		return patch(XMLFile, "(<?.*?>)", "$1\n<!DOCTYPE lemmas SYSTEM \""
				+ DTDFile + "\">");
	}

	/**
	 * The entry point method.
	 * 
	 * @param args
	 *            the command line options
	 */
	public static void main(String[] args) {
		lemmas = null;
	    if (resources == null)
	    	setResources(new Resources());

		// Parses the command line options and loads resources
		resources.log("Parsing the command line...", 1);
		try {
			resources.parseOptions(args);
			resources.log("=> Command line successfully parsed.", 1);
		} catch (ResourceException e) {
			System.err
					.println("A problem occurred when trying to parse the command line options!");
			e.printStackTrace();
			System.exit(1);
		}

		// Patches the XML file
		URL XMLFile = null;
		try {
			XMLFile = resources.getXMLFile().toURL();
		} catch (MalformedURLException e) {
			System.err
					.println("A problem occurred when trying to load the XML file!");
			e.printStackTrace();
			System.exit(1);
		}
		URL DTDFile = null;
		try {
			DTDFile = resources.getDTDFile().toURL();
		} catch (MalformedURLException e) {
			System.err
					.println("A problem occurred when trying to load the DTD file!");
			e.printStackTrace();
			System.exit(1);
		}
		resources.log("Patching the following XML file: " + XMLFile.toString()
				+ "...", 1);
		try {
			XMLFile = setDoctype(XMLFile, DTDFile);
			resources.log("=> The following temporary XML file was created: "
					+ XMLFile.toString(), 2);
		} catch (IOException e) {
			System.err
					.println("A problem occurred when trying to patch the XML file!");
			e.printStackTrace();
			System.exit(1);
		}

		// Loads the XML file and validates its structure, according to the
		// associated DTD file
		resources.log("Loading and parsing the XML file...", 1);
		Document document = null;
		try {
			document = load(XMLFile, DTDFile);
			resources.log("=> XML file successfully parsed.", 1);
		} catch (SAXException e) {
			System.err
					.println("A problem occurred when trying to parse the XML file!");
			e.printStackTrace();
			System.exit(1);
		}

		// Parses the DOM structure
		resources.log("Going through the DOM structure ...", 1);
		try {
			lemmas = parse(document);
			resources.log("=> DOM structure successfully parsed.", 1);
		} catch (ParseException e) {
			System.err
					.println("A problem occurred when trying to go through the DOM structure!");
			e.printStackTrace();
			System.exit(1);
		}

		// Performs the type checking
		resources.log("Type-checking...", 1);
		for (Lemma lemma : lemmas) {
			resources.log("Lemma " + lemma.getTitle(), 1);
			List<Result> results = lemma.typeCheck();
			for (Result result : results)
				resources.log(result.toString(), 1);
			if (results.isEmpty())
				resources.log("=> OK", 1);
			else
				resources.log("=> KO", 1);
		}
	}
}
