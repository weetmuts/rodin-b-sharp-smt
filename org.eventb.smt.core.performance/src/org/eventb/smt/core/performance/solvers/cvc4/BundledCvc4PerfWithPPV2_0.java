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
package org.eventb.smt.core.performance.solvers.cvc4;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_CVC4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.junit.Test;

public class BundledCvc4PerfWithPPV2_0 extends SolverPerfWithPP {

	public BundledCvc4PerfWithPPV2_0() {
		super(BUNDLED_CVC4);
	}

	@Test
	public void testUnsatCvc4Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test(timeout = 3000)
	public void testSatCvc4Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_sat", hyps, "x > z", arith_te, !VALID);
	}

	// NOTE: the following tests are overridden because the prover fails somehow;
	// ideally, they should not be overridden.

	@Override
	public void testCh7Conc29() {
		expectTimeout(() -> {
			super.testCh7Conc29();
		});
	}

	@Override
	public void testDynamicStableLSR_081014_15() {
		expectTimeout(() -> {
			super.testDynamicStableLSR_081014_15();
		});
	}

	@Override
	public void testDifferentForallPlusSimple1y() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple1y();
		});
	}

	@Override
	public void testDifferentForall() {
		expectTimeout(() -> {
			super.testDifferentForall();
		});
	}

	@Override
	public void testDifferentForallPlusSimple3y() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple3y();
		});
	}

	@Override
	public void testDifferentForallPlusSimplexy() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimplexy();
		});
	}

	@Override
	public void testDifferentForallPlusSimpleMonadic() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimpleMonadic();
		});
	}

	@Override
	public void testBepiColombo3Medium() {
		expectTimeout(() -> {
			super.testBepiColombo3Medium();
		});
	}

	@Override
	public void testBepiColombo3() {
		expectTimeout(() -> {
			super.testBepiColombo3();
		});
	}

	@Override
	public void testIntsSetEquality() {
		expectTimeout(() -> {
			super.testIntsSetEquality();
		});
	}

	@Override
	public void testSets5() {
		expectTimeout(() -> {
			super.testSets5();
		});
	}

	@Override
	public void testLinearSort29() {
		expectTimeout(() -> {
			super.testLinearSort29();
		});
	}
	
	@Override
	public void testSets7() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets7", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID);
	}
}
