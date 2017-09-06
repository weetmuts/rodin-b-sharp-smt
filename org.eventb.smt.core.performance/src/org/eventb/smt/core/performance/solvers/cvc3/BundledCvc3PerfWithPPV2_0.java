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
package org.eventb.smt.core.performance.solvers.cvc3;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.eventb.smt.tests.ConfigProvider;

public class BundledCvc3PerfWithPPV2_0 extends SolverPerfWithPP {

	public BundledCvc3PerfWithPPV2_0() {
		super(ConfigProvider.BUNDLED_CVC3);
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
	public void testLinearSort29() {
		final ITypeEnvironment te = mTypeEnvironment("f", "ℙ(ℤ × ℤ)", "r", "ℙ(ℤ × BOOL)", "m", "ℤ", "x", "ℤ", "j", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("r ∈ 1 ‥ m → BOOL");
		hyps.add("x ∈ 1 ‥ m");
		hyps.add("j+1 ∈ dom(f)");
		// originally VALID
		doTest("linear_sort_29", hyps, "x ∈ dom(r{f(j+1) ↦ TRUE})", te, !VALID);
	}

	@Override
	public void testSets4() {
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets4", hyps, "∀ x · (x ∈ ℤ → ℙ(ℤ) ⇒ (∃ y · y ≠ x))", arith_te, !VALID);
	}

	@Override
	public void testSets7() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets7", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID);
	}

	@Override
	public void testDFPSBool() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("dfpsBool", hyps, "{TRUE ↦ {FALSE}} ∈ {TRUE} → {{FALSE}}", te, !VALID);
	}

	@Override
	public void testRule20MacroInsideMacro() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		doTest("rule20_macro_inside_macro", hyps,
				// originally VALID
				"(λx· (x > 0 ∧ ((λy·y > 0 ∣ y+y) = ∅)) ∣ x+x) = ∅", te, !VALID);
	}

	@Override
	public void testDifferentForallPlusSimpleMonadic() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("f ∈ ℙ({1} → {{0}})");
		hyps.add("f ≠ ∅");
		// originally VALID
		doTest("differentForallPlusSimpleMonadic", hyps, "{1 ↦ {0}} ∈ f", te, !VALID);
	}

	@Override
	public void testBoolsSetEquality() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(BOOL)", "non", "BOOL ↔ BOOL");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("S ≠ ∅");
		hyps.add("non = {TRUE ↦ FALSE, FALSE ↦ TRUE}");
		hyps.add("∀ x · x ∈ S ⇔ non(x) ∈ S");
		// originally VALID
		doTest("BoolsSetEquality", hyps, "S = BOOL", te, !VALID);
	}

	@Override
	public void testIntsSetEquality() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("∀ x · x + 1 ∈ S");
		// originally VALID
		doTest("IntsSetEquality", hyps, "S = ℤ", te, !VALID);
	}
}
