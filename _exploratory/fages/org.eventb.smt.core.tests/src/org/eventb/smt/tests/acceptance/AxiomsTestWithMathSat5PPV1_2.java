/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.provers.internal.core.SMTSolver.MATHSAT5;

public class AxiomsTestWithMathSat5PPV1_2 extends AxiomsTestWithPPV1_2 {

	public AxiomsTestWithMathSat5PPV1_2() {
		super(MATHSAT5);
	}
}