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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.smt.BenchmarkWriter;

import static org.junit.Assert.assertEquals;

public class WriterTests extends AbstractSMTTests {

	private final static URL XML_FILE = BenchmarkTests.class
			.getResource("../xml/linear_arith.xml");
	private final static URL DTD_FILE = BenchmarkTests.class
			.getResource("../xml/lemmas.dtd");

	@Test
	public void testWrite() throws Exception {
		Document document = BenchmarkWriter.load(XML_FILE, DTD_FILE);
		List<Lemma> lemmas = BenchmarkWriter.parse(document);
		for (Lemma lemma : lemmas) {
			File tmp = File.createTempFile(lemma.getTitle(),".smt");
			tmp.deleteOnExit();
			if (!BenchmarkWriter.write(lemma, tmp))
				continue;

			// Read the temporary file
			FileInputStream fis1 = new FileInputStream(tmp);
			StringWriter writer1 = new StringWriter();
			InputStreamReader streamReader1 = new InputStreamReader(fis1);
			BufferedReader buffer1 = new BufferedReader(streamReader1);
			String line = "";
			while ((line = buffer1.readLine()) != null)
				writer1.write(line + "\n");

			// Read the expected file
			URL expected = BenchmarkTests.class.getResource("linear_arith/"
					+ BenchmarkWriter.patch(lemma.getTitle()) + ".smt");
			StringWriter writer2 = new StringWriter();
			InputStreamReader streamReader2 = new InputStreamReader(expected
					.openStream());
			BufferedReader buffer2 = new BufferedReader(streamReader2);
			line = "";
			while ((line = buffer2.readLine()) != null)
				writer2.write(line + "\n");

			assertEquals(writer1.toString(), writer2.toString());
		}
	}
}
