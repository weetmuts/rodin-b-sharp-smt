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

import java.util.Arrays;
import java.util.List;

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
}
