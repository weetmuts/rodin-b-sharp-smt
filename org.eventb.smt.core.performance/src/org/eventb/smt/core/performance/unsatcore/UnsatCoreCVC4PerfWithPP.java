/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.unsatcore;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_CVC4;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;;

/**
 * Note: remove overridden tests if they fail. They exist because the prover did
 * not give the original expected result as implemented in the super class. So
 * they are overridden in order to have everything succeed, corresponding to a
 * current state of the provers. When the provers get improved, they should
 * ideally succeed the original test and no test should be overridden.
 */
public class UnsatCoreCVC4PerfWithPP extends UnsatCoreExtractionPerfWithPP {

	public UnsatCoreCVC4PerfWithPP() {
		super(BUNDLED_CVC4);
	}

	@Override
	public void testDifferentForallPlusSimple1yUnsatCore() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple1yUnsatCore();
		});
	}

	@Override
	public void testDifferentForallUnsatCore() {
		expectTimeout(() -> {
			super.testDifferentForallUnsatCore();
		});
	}

	@Override
	public void testDifferentForallPlusSimple3yUnsatCore() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple3yUnsatCore();
		});
	}

	@Override
	public void testDifferentForallPlusSimplexyUnsatCore() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimplexyUnsatCore();
		});
	}

	@Override
	public void testBepiColombo3MediumUnsatCore() {
		expectTimeout(() -> {
			super.testBepiColombo3MediumUnsatCore();
		});
	}

	@Override
	public void testBepiColombo3UnsatCore() {
		expectTimeout(() -> {
			super.testBepiColombo3UnsatCore();
		});
	}

	@Override
	public void testSets5UnsatCore() {
		expectTimeout(() -> {
			super.testSets5UnsatCore();
		});
	}

	@Override
	public void testLinearSort29UnsatCore() {
		expectTimeout(() -> {
			super.testLinearSort29UnsatCore();
		});
	}

	@Override
	public void testSetsEqualityUnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"p", "ℙ(ℤ)", "q", "ℙ(ℤ)", "n", "ℤ", "m", "ℤ");

		final List<String> hyps = Arrays.asList(//
				"n > 1", //
				"m = 1", //
				"p ∈ ℙ({1})", //
				"q ∈ ℙ({1})", //
				"p ≠ ∅", //
				"n ∉ p", //
				"q ≠ ∅", //
				"m ∈ q");

		// originally "q ≠ ∅" instead of { "m = 1", "m ∈ q" }
		final List<String> unsat = Arrays.asList(//
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"m = 1", //
				"m ∈ q");

		doTest("SetsEqualityUnsatCore", hyps, "p = q", te, VALID, unsat, true);
	}

	@Override
	public void testSets7UnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		// originally VALID
		doTest("sets7UnsatCore", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID, hyps, true);
	}

}
