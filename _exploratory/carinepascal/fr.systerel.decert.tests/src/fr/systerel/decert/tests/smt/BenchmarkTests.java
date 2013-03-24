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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaPredicate;
import fr.systerel.decert.Variable;
import fr.systerel.decert.smt.Annotation;
import fr.systerel.decert.smt.Benchmark;
import fr.systerel.decert.smt.BenchmarkFormula;
import fr.systerel.decert.smt.BenchmarkFunction;
import fr.systerel.decert.smt.Status;

public class BenchmarkTests extends AbstractSMTTests {

	private final Benchmark benchmark;

	public BenchmarkTests() throws Exception {
		Variable a1 = new Variable(ff, "a1", "ℤ");
		Variable r1 = new Variable(ff, "r1", "ℤ");

		LemmaPredicate hyp1 = new LemmaPredicate(ff, "a1 ≤ r1", true);
		LemmaPredicate hyp2 = new LemmaPredicate(ff, "r1 ≤ a1+1", true);
		LemmaPredicate hyp3 = new LemmaPredicate(ff, "¬ r1 = a1", true);
		LemmaPredicate goal = new LemmaPredicate(ff, "r1 = a1+1", true);

		Lemma lemma = new Lemma("The lemma title", "ch8_circ_arbiter | arb_m0 | cir1/inv6/INV",
				"The lemma description", goal);
		lemma.addHypothesis(hyp1);
		lemma.addHypothesis(hyp2);
		lemma.addHypothesis(hyp3);

		lemma.addToTypeEnvironment(a1);
		lemma.addToTypeEnvironment(r1);

		benchmark = new Benchmark(lemma);
	}

	@Test
	public void testName() {
		assertEquals("The lemma title", benchmark.getName());
	}

	@Test
	public void testLogic() {
		assertEquals("QF_LIA", benchmark.getLogic());
	}

	@Test
	public void testAssumptions() {
		BenchmarkFormula f1 = new BenchmarkFormula("(<= a1 r1)");
		BenchmarkFormula f2 = new BenchmarkFormula("(<= r1 (+ a1 1))");
		BenchmarkFormula f3 = new BenchmarkFormula("(not (= r1 a1))");
		assertElements(benchmark.getAssumptions(), f1, f2, f3);
	}

	@Test
	public void testFormula() {
		BenchmarkFormula f = new BenchmarkFormula("(= r1 (+ a1 1))");
		assertEquals(benchmark.getFormula(), f);
	}

	@Test
	public void testStatus() {
		assertEquals(Status.SAT, benchmark.getStatus());
	}

	@Test
	public void testSorts() {
		assertElements(benchmark.getSorts());
	}

	@Test
	public void testFunctions() {
		BenchmarkFunction f1 = new BenchmarkFunction("a1", ff.makeIntegerType());
		BenchmarkFunction f2 = new BenchmarkFunction("r1", ff.makeIntegerType());
		assertElements(benchmark.getFunctions(), f1, f2);
	}

	@Test
	public void testPredicates() {
		assertElements(benchmark.getPredicates());
	}

	@Test
	public void testNotes() {
		assertEquals("The lemma description", benchmark.getNotes());
	}
	
	@Test
	public void testAnnotations() {
		Annotation a = new Annotation("origin", "ch8_circ_arbiter | arb_m0 | cir1/inv6/INV");
		assertElements(benchmark.getAnnotations(), a);
	}
}
