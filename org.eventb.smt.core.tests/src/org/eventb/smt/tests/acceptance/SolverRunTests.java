/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - portability of paths
 *******************************************************************************/
package org.eventb.smt.tests.acceptance;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.tests.ConfigProvider;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests, whatever is the solver, the tested
 * SMT-LIB version or the translation approach. Specific tests shall be put in
 * specific sub-classes.
 * 
 * @author Yoann Guyot
 * 
 */
public abstract class SolverRunTests extends CommonSolverRunTests {

	protected static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	protected static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public SolverRunTests(ConfigProvider provider) {
		super(provider, null, !GET_UNSAT_CORE);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(configLemmaName(lemmaName), inputHyps, inputGoal, te,
				!TRIVIAL, expectedSolverResult);
	}

	/**
	 * Returns a name made of the name of this configuration and the given lemma
	 * name and which is a valid SMT identifier.
	 */
	private String configLemmaName(String lemmaName) {
		String result = configuration.getName() + "_" + lemmaName;
		result = result.replace(' ', '_');
		result = result.replaceAll("[()]", "");
		return result;
	}

	@Test
	public void sat() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void division() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList( //
				" 4 ÷  2 =  2", //
				"−4 ÷  2 = −2", //
				"−4 ÷ −2 =  2", //
				" 4 ÷ −2 = −2", //
				" 3 ÷  2 =  1", //
				"−3 ÷  2 = −1", //
				"−3 ÷ −2 =  1");

		doTest("division", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void exponentiation() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void modulo() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList( //
				" 4 mod  2 =  0", //
				"−4 mod  2 =  0", //
				"−4 mod −2 =  0", //
				" 4 mod −2 =  0");

		doTest("modulo", hyps, "3 mod 2 = 1", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void integerSet() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}
}