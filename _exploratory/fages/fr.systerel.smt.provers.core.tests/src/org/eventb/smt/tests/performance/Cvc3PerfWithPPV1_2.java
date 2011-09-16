/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.performance;

import static org.eventb.smt.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;

import org.eventb.smt.tests.SolverPerfWithPP;

public class Cvc3PerfWithPPV1_2 extends SolverPerfWithPP {

	public Cvc3PerfWithPPV1_2() {
		super(CVC3, V1_2);
	}

}
