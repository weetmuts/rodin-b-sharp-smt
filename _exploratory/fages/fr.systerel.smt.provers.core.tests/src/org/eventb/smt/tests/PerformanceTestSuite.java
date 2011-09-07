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

import org.eventb.smt.tests.performance.AltErgoPerfWithPP;
import org.eventb.smt.tests.performance.AltErgoPerfWithVeriT;
import org.eventb.smt.tests.performance.Cvc3PerfWithPP;
import org.eventb.smt.tests.performance.Cvc3PerfWithVeriT;
import org.eventb.smt.tests.performance.VeriTPerfWithPP;
import org.eventb.smt.tests.performance.VeriTPerfWithVeriT;
import org.eventb.smt.tests.performance.Z3PerfWithPP;
import org.eventb.smt.tests.performance.Z3PerfWithVeriT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPP.class, Cvc3PerfWithPP.class,
		VeriTPerfWithPP.class, Z3PerfWithPP.class, AltErgoPerfWithVeriT.class,
		Cvc3PerfWithVeriT.class, VeriTPerfWithVeriT.class,
		Z3PerfWithVeriT.class })
public class PerformanceTestSuite {
	// Just for tests
}
