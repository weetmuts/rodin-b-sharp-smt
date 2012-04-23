/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance.solvers.cvc4;

import static org.eventb.smt.core.provers.SolverKind.CVC4;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.performance.solvers.SolverPerfWithVeriT;
import org.junit.Ignore;
import org.junit.Test;


public class Cvc4PerfWithVeriTV2_0 extends SolverPerfWithVeriT {
	public Cvc4PerfWithVeriTV2_0() {
		super(CVC4, V1_2);
	}

	@Test
	@Ignore("CVC4 needs a known logic to be set")
	public void testUnsatCvc4Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore("See the problem")
	// TODO: See the problem
	public void testSatCvc4Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_sat", hyps, "x > z", arith_te, !VALID);
	}
}
