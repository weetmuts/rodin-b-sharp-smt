/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Vitor Alcantara de Almeida - implementation
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void fatalError(final SAXParseException spe)
						throws SAXException {
					throw new SAXException(spe);
				}

				@Override
				public void error(final SAXParseException spe)
						throws SAXException {
					throw new SAXException(spe);
				}

				@Override
				public void warning(final SAXParseException spe)
						throws SAXException {
					throw new SAXException(spe);
				}
			});
			builder.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(final String publicId,
						final String systemId) throws SAXException {
					try {
						final InputSource source = new InputSource(DTDFile
								.openStream());
						return source;
					} catch (final IOException ioe) {
						throw new SAXException(ioe);
					}
				}
			});
		} catch (final ParserConfigurationException pce) {
			throw new SAXException(pce);
		}

		try {
			return builder.parse(XMLFile.toString());
		} catch (final SAXException sxe) {
			throw new SAXException(sxe);
		} catch (final IOException ioe) {
			throw new SAXException(ioe);
		}
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
	public static URL patch(final URL XMLFile, final String regex,
			final String replacement) throws IOException {
		// Reads the XML file
		final InputStreamReader streamReader = new InputStreamReader(
				XMLFile.openStream(), "UTF-8");
		final BufferedReader buffer = new BufferedReader(streamReader);
		final StringWriter sw = new StringWriter();
		String line = "";
		while ((line = buffer.readLine()) != null) {
			sw.write(line + "\n");
		}

		// Replaces the regular expression
		final String s = sw.toString().replaceFirst(regex, replacement);

		// Writes a temporary XML file
		final File tmp = File.createTempFile("lemmas_patched", ".xml");
		tmp.deleteOnExit();

		final FileOutputStream outputStream = new FileOutputStream(tmp);
		final OutputStreamWriter streamWriter = new OutputStreamWriter(
				outputStream, "UTF8");
		final BufferedWriter output = new BufferedWriter(streamWriter);
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
	public static URL setDoctype(final URL XMLFile, final URL DTDFile)
			throws IOException {
		return patch(XMLFile, "(<?.*?>)", "$1\n<!DOCTYPE lemmas SYSTEM \""
				+ DTDFile + "\">");
	}
}
