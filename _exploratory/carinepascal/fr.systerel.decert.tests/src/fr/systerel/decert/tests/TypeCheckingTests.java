/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaParser;
import fr.systerel.decert.Result;

/**
 * The class used to type-check a set of Event-B lemmas.
 * The lemmas are passed as XML input files to the <tt>LemmaParser.main</tt>
 * entry point method.
 */
public class TypeCheckingTests extends AbstractTests {

	/**
	 * The path of the folder containing the XML files for the Event-B lemmas,
	 * and the associated DTD file.
	 */
	private final String XMLFolder = "C:\\Utilisateurs\\pascal\\workspace\\c444\\1\\Lemmas";

	@Test
	public void testTypeCheck() throws Exception {
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
				String message = "File " + files[i].getAbsolutePath() + "\n";
				URL XMLFile = LemmaParser.setDoctype(files[i].toURI().toURL(), DTDFile
						.toURI().toURL());
				Document document = LemmaParser.load(XMLFile, DTDFile.toURI().toURL());
				List<Lemma> lemmas = LemmaParser.parse(document);
				for (Lemma lemma : lemmas) {
					List<Result> results = lemma.typeCheck();
					if (!results.isEmpty())
						message = message + "Lemma " + lemma.getTitle() + "\n";
					for (Result result : results)
						message = message + result.toString() + "\n";
					assertTrue(message, results.isEmpty());
				}
			}
		}
	}
}
