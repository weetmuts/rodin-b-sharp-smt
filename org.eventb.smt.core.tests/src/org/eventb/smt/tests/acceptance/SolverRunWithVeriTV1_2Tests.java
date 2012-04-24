/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *  UFRN - additional tests
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.provers.SolverKind;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests, whatever is the solver, but with
 * SMT-LIB 1.2 benchmarks translated with veriT.
 * 
 * @author Yoann Guyot
 */
public abstract class SolverRunWithVeriTV1_2Tests extends SolverRunTests {
	public SolverRunWithVeriTV1_2Tests(final SolverKind solverKind) {
		super(solverKind, USING_VERIT, V1_2);
	}

	@Test
	public void unsatCall() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void truePred() {
		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void boolSet2() {
		final List<String> hyps = Arrays.asList( //
				"b↦c ∈ BOOL×BOOL", //
				"b↦c = TRUE↦FALSE");

		doTest("bool_set2", hyps, "b = TRUE", arith_te, VALID);
	}

	@Test
	// FIXME fix veriT translation approach for this case
	@Ignore("Translation fails")
	public void setsEquality() {
		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("sets_equality", hyps, "p = q", te, VALID);
	}
}