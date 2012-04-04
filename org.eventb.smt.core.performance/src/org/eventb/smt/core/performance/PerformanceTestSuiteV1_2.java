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

import org.eventb.smt.core.performance.solvers.AltErgoPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.AltErgoPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.Cvc3PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.Cvc3PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.Cvc4PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.Cvc4PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.MathSat5PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.MathSat5PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.OpenSMTPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.OpenSMTPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.VeriTPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.VeriTPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.Z3PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.Z3PerfWithVeriTV1_2;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPPV1_2.class, Cvc3PerfWithPPV1_2.class,
		Cvc4PerfWithPPV1_2.class, MathSat5PerfWithPPV1_2.class,
		OpenSMTPerfWithPPV1_2.class, VeriTPerfWithPPV1_2.class,
		Z3PerfWithPPV1_2.class, AltErgoPerfWithVeriTV1_2.class,
		Cvc3PerfWithVeriTV1_2.class, Cvc4PerfWithVeriTV1_2.class,
		MathSat5PerfWithVeriTV1_2.class, OpenSMTPerfWithVeriTV1_2.class,
		VeriTPerfWithVeriTV1_2.class, Z3PerfWithVeriTV1_2.class })
public class PerformanceTestSuiteV1_2 {
	// Just for tests
}
