/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.systerel.smt.provers.core.tests.performance.AltErgoPerfWithPP;
import fr.systerel.smt.provers.core.tests.performance.AltErgoPerfWithVeriT;
import fr.systerel.smt.provers.core.tests.performance.Cvc3PerfWithPP;
import fr.systerel.smt.provers.core.tests.performance.Cvc3PerfWithVeriT;
import fr.systerel.smt.provers.core.tests.performance.VeriTPerfWithPP;
import fr.systerel.smt.provers.core.tests.performance.VeriTPerfWithVeriT;
import fr.systerel.smt.provers.core.tests.performance.Z3PerfWithPP;
import fr.systerel.smt.provers.core.tests.performance.Z3PerfWithVeriT;

@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPP.class, Cvc3PerfWithPP.class,
		VeriTPerfWithPP.class, Z3PerfWithPP.class, AltErgoPerfWithVeriT.class,
		Cvc3PerfWithVeriT.class, VeriTPerfWithVeriT.class,
		Z3PerfWithVeriT.class })
public class PerformanceTestSuite {
	// Just for tests
}
