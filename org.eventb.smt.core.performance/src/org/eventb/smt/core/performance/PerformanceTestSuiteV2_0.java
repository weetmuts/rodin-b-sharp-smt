/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance;

import org.eventb.smt.core.performance.solvers.AltErgoPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.Cvc3PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.Cvc4PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.MathSat5PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.OpenSMTPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.VeriTPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.Z3PerfWithPPV2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPPV2_0.class, Cvc3PerfWithPPV2_0.class,
		Cvc4PerfWithPPV2_0.class, MathSat5PerfWithPPV2_0.class,
		OpenSMTPerfWithPPV2_0.class, VeriTPerfWithPPV2_0.class,
		Z3PerfWithPPV2_0.class })
public class PerformanceTestSuiteV2_0 {
	// Just for tests
}