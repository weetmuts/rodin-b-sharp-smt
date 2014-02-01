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
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

import fr.systerel.decert.LemmaPredicate;

public class LemmaPredicateTests extends AbstractTests {

	private final LemmaPredicate hypothesis;
	private final Predicate predicate;
	private final String predicateContent;

	public LemmaPredicateTests() throws Exception {
		predicateContent = "x = y mod n";
		predicate = ff.makeRelationalPredicate(Formula.EQUAL, ff
				.makeFreeIdentifier("x", null), ff.makeBinaryExpression(
						Formula.MOD, ff.makeFreeIdentifier("y", null), ff
						.makeFreeIdentifier("n", null), null), null);
		hypothesis = new LemmaPredicate(ff, predicateContent, true);
	}

	@Test
	public void testRequired() {
		assertTrue(hypothesis.isRequired());
	}

	@Test
	public void testContent() throws Exception {
		assertEquals(predicate, hypothesis.getContent());
	}

	@Test
	public void testToString() throws Exception {
		assertEquals(predicate.toString(), hypothesis.toString());
	}
	
}
