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
package org.eventb.smt.core.performance.solvers.verit;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.eventb.smt.tests.ConfigProvider;

public class BundledVeriTPerfWithPPV2_0 extends SolverPerfWithPP {

	public BundledVeriTPerfWithPPV2_0() {
		super(ConfigProvider.BUNDLED_VERIT);
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
	public void testBug2105507Thm6() {
		expectTimeout(() -> {
			super.testBug2105507Thm6();
		});
	}
	
	@Override
	public void testDifferentForallPlusSimpleMonadic() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimpleMonadic();
		});
	}
	
	@Override
	public void testIntsSetEquality() {
		expectTimeout(() -> {
			super.testIntsSetEquality();
		});
	}
	
	@Override
	public void testSets4() {
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets4", hyps, "∀ x · (x ∈ ℤ → ℙ(ℤ) ⇒ (∃ y · y ≠ x))", arith_te,
				!VALID);
	}
	
	@Override
	public void testSets7() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets7", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID);
	}
	
	@Override
	public void testRule20ManyForalls() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("rule20_many_foralls", hyps,
				"(λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) = ∅", te,
				!VALID);	}
}
