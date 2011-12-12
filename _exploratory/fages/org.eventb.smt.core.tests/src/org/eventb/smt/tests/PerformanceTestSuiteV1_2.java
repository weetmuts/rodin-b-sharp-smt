/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import org.eventb.smt.tests.performance.AltErgoPerfWithPPV1_2;
import org.eventb.smt.tests.performance.AltErgoPerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.Cvc3PerfWithPPV1_2;
import org.eventb.smt.tests.performance.Cvc3PerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.Cvc4PerfWithPPV1_2;
import org.eventb.smt.tests.performance.Cvc4PerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.MathSat5PerfWithPPV1_2;
import org.eventb.smt.tests.performance.MathSat5PerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.OpenSMTPerfWithPPV1_2;
import org.eventb.smt.tests.performance.OpenSMTPerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.VeriTPerfWithPPV1_2;
import org.eventb.smt.tests.performance.VeriTPerfWithVeriTV1_2;
import org.eventb.smt.tests.performance.Z3PerfWithPPV1_2;
import org.eventb.smt.tests.performance.Z3PerfWithVeriTV1_2;
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
