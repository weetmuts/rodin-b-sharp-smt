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

import org.eventb.smt.tests.acceptance.AxiomsTestWithPPV1_2;

public class AxiomsTestWithCvc4PPV1_2 extends AxiomsTestWithPPV1_2 {

	public AxiomsTestWithCvc4PPV1_2() {
		super(CVC4);
	}
}