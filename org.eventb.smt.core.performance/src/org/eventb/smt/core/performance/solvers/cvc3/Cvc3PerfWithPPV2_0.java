/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.cvc3;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.tests.ConfigProvider.LAST_CVC3;

import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;


public class Cvc3PerfWithPPV2_0 extends SolverPerfWithPP {

	public Cvc3PerfWithPPV2_0() {
		super(LAST_CVC3, V2_0);
	}

}
