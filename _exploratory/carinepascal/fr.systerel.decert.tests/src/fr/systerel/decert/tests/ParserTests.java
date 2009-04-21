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
package fr.systerel.decert.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaParser;
import fr.systerel.decert.LemmaPredicate;
import fr.systerel.decert.Theory;

public class ParserTests extends AbstractTests {

	private final static URL XML_FILE = ParserTests.class
			.getResource("xml/lemmas.xml");
	private final static URL DTD_FILE = ParserTests.class
			.getResource("xml/lemmas.dtd");

	@Test
	public void testLoadWithValidXml() throws SAXException {
		Document document = LemmaParser.load(XML_FILE, DTD_FILE);
		assertNotNull(document);
	}

	@Test
	public void testLoadWithInvalidXml() throws IOException {
		// Try to load an XML file without doctype
		Document document = null;
		URL url = LemmaParser.patch(XML_FILE,"<!DOCTYPE .*?>", "");
		try {
			document = LemmaParser.load(url, DTD_FILE);
			fail("An XML file without doctype should not be successfully loaded!");
		} catch (SAXException e) {
			// expected behavior
		}
		assertNull(document);

		// Try to load an XML file without title
		document = null;
		url = LemmaParser.patch(XML_FILE,"<title>.*?>", "");
		try {
			document = LemmaParser.load(url, DTD_FILE);
			fail("An XML file without doctype should not be successfully loaded!");
		} catch (SAXException e) {
			// expected behavior
		}
		assertNull(document);

		// Try to load an XML file without a mandatory element
		document = null;
		url = LemmaParser.patch(XML_FILE,"<origin>.*?>", "");
		try {
			document = LemmaParser.load(url, DTD_FILE);
			fail("An XML file without doctype should not be successfully loaded!");
		} catch (SAXException e) {
			// expected behavior
		}
		assertNull(document);

		// Try to load an XML file without a mandatory attribute
		document = null;
		url = LemmaParser.patch(XML_FILE,"name=\"n\" ", "");
		try {
			document = LemmaParser.load(url, DTD_FILE);
			fail("An XML file without doctype should not be successfully loaded!");
		} catch (SAXException e) {
			// expected behavior
		}
		assertNull(document);
	}

	@Test
	public void testParse() throws Exception {
		Document document = LemmaParser.load(XML_FILE, DTD_FILE);
		Lemma lemma = LemmaParser.parse(document).get(0);
		assertEquals("The lemma title", lemma.getTitle());
		assertEquals("ssf | rec3 | cbuf_write/inv1/INV", lemma.getOrigin());
		assertEquals("Some additional comments", lemma.getComment());

		assertElements(lemma.getTheories(), Theory.NONLINEAR_ARITH);

		Type type = ff.makeIntegerType();
		ITypeEnvironment expTypenv = ff.makeTypeEnvironment();
		expTypenv.addName("n", type);
		expTypenv.addName("x", type);
		expTypenv.addName("y", type);
		assertEquals(expTypenv, lemma.getTypeEnvironment());

		LemmaPredicate hyp1 = new LemmaPredicate(ff, "n ∈ ℕ", false);
		LemmaPredicate hyp2 = new LemmaPredicate(ff, "x ∈ ℕ", true);
		LemmaPredicate hyp3 = new LemmaPredicate(ff, "y ∈ ℕ", true);
		LemmaPredicate hyp4 = new LemmaPredicate(ff, "¬ n=0", true);
		LemmaPredicate hyp5 = new LemmaPredicate(ff, "x = y mod n", true);
		assertElements(lemma.getHypotheses(), hyp1, hyp2, hyp3, hyp4, hyp5);

		LemmaPredicate goal = new LemmaPredicate(ff, "(x+1) mod n = (y+1) mod n", true);
		assertEquals(goal, lemma.getGoal());
	}
}