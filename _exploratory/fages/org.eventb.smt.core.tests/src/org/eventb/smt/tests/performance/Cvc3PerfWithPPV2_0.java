/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.performance;

import static org.eventb.smt.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;


public class Cvc3PerfWithPPV2_0 extends SolverPerfWithPP {

	public Cvc3PerfWithPPV2_0() {
		super(CVC3, V2_0);
	}

}
