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

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.junit.Test;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaPredicate;
import fr.systerel.decert.Theory;
import fr.systerel.decert.Variable;

public class LemmaTests extends AbstractTests {

	private final Lemma lemma;
	private final Type type;
	private final Variable n;
	private final LemmaPredicate goal;

	public LemmaTests() throws Exception {
		type = ff.makeIntegerType();
		n = new Variable(ff, "n", "ℤ");
		goal = new LemmaPredicate(ff, "(x+1) mod n = (y+1) mod n", true);
		lemma = new Lemma("The lemma title","The lemma origin","The lemma description",goal);
	}

	@Test
	public void testTitle() {
		assertEquals("The lemma title", lemma.getTitle());
	}

	@Test
	public void testOrigin() {
		assertEquals("The lemma origin", lemma.getOrigin());
	}

	@Test
	public void testComment() {
		assertEquals("The lemma description", lemma.getComment());
	}

	@Test
	public void testTheories() {
		assertElements(lemma.getTheories());

		lemma.addTheory(Theory.BASIC_RELATION);
		assertElements(lemma.getTheories(), Theory.BASIC_RELATION);
	}

	@Test
	public void testTypeEnvironment() {
		assertTrue(lemma.getTypeEnvironment().isEmpty());

		lemma.addToTypeEnvironment(n);
		assertEquals(type, lemma.getTypeEnvironment().getType("n"));
	}

	@Test
	public void testHypotheses() throws Exception {
		assertElements(lemma.getHypotheses());

		LemmaPredicate hypothesis = new LemmaPredicate(ff, "n ∈ ℕ", true);
		lemma.addHypothesis(hypothesis);
		assertElements(lemma.getHypotheses(), hypothesis);
	}

	@Test
	public void testGoal() {
		assertEquals(goal, lemma.getGoal());

	}

	@Test
	public void testTypeCheck() throws Exception {
		lemma.addToTypeEnvironment(n);
		Variable x = new Variable(ff, "x", "ℤ");
		lemma.addToTypeEnvironment(x);
		Variable y = new Variable(ff, "y", "ℤ");
		lemma.addToTypeEnvironment(y);

		ITypeEnvironment initialEnvironment = lemma.getTypeEnvironment();

		LemmaPredicate hypothesis = new LemmaPredicate(ff, "n ∈ ℕ", true);
		lemma.addHypothesis(hypothesis);
		hypothesis = new LemmaPredicate(ff, "x ∈ ℕ", true);
		lemma.addHypothesis(hypothesis);
		hypothesis = new LemmaPredicate(ff, "y ∈ ℕ", true);
		lemma.addHypothesis(hypothesis);
		hypothesis = new LemmaPredicate(ff, "¬ n=0", true);
		lemma.addHypothesis(hypothesis);
		hypothesis = new LemmaPredicate(ff, "x = y mod n", true);
		lemma.addHypothesis(hypothesis);

		assertTrue(lemma.typeCheck().isEmpty());

		ITypeEnvironment finalEnvironment = lemma.getTypeEnvironment();
		assertEquals(initialEnvironment, finalEnvironment);
	}

}
