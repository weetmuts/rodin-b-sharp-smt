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

import org.eventb.smt.tests.acceptance.AxiomsTestWithPPV2_0;

public class AxiomsTestWithMathSat5PPV2_0 extends AxiomsTestWithPPV2_0 {

	public AxiomsTestWithMathSat5PPV2_0() {
		super(MATHSAT5);
	}
}