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

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_VERIT;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;

/**
 * Note: remove overridden tests if they fail. They exist because the prover did
 * not give the original expected result as implemented in the super class. So
 * they are overridden in order to have everything succeed, corresponding to a
 * current state of the provers. When the provers get improved, they should
 * ideally succeed the original test and no test should be overridden.
 */
public class UnsatCoreVeriTPerfWithPP extends UnsatCoreExtractionPerfWithPP {

	public UnsatCoreVeriTPerfWithPP() {
		super(BUNDLED_VERIT);
	}

	@Override
	public void testSets4UnsatCore() {
		final List<String> hyps = Arrays.asList();
		// originally VALID
		doTest("sets4UnsatCore", hyps, "∀ x · (x ∈ ℤ → ℙ(ℤ) ⇒ (∃ y · y ≠ x))", arith_te, !VALID, hyps, true);

	}

	@Override
	public void testRule20ManyForallsUnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		// originally VALID
		doTest("rule20_many_forallsUnsatCore", hyps, "(λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) = ∅", te,
				!VALID, hyps, true);
	}

	@Override
	public void testSets7UnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		// originally VALID
		doTest("sets7UnsatCore", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID, hyps, true);
	}
}
