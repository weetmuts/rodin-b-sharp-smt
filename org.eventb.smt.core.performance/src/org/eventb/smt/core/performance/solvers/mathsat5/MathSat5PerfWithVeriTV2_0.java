/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance.solvers.mathsat5;

import static org.eventb.smt.core.provers.SolverKind.MATHSAT5;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.performance.solvers.SolverPerfWithVeriT;
import org.junit.Ignore;
import org.junit.Test;

public class MathSat5PerfWithVeriTV2_0 extends SolverPerfWithVeriT {
	public MathSat5PerfWithVeriTV2_0() {
		super(MATHSAT5, !BUNDLED, V2_0);
	}

	@Test
	@Ignore("MathSat5 is not well integrated because it can read on its input only")
	public void testUnsatMathSat5Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testSatMathSat5Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_sat", hyps, "x > z", arith_te, !VALID);
	}
}
