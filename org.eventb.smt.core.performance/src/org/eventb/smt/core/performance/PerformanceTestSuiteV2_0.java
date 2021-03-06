/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance;

import org.eventb.smt.core.performance.solvers.altergo.AltErgoPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.cvc3.BundledCvc3PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.cvc4.BundledCvc4PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.mathsat5.MathSat5PerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.opensmt.OpenSMTPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.verit.BundledVeriTPerfWithPPV2_0;
import org.eventb.smt.core.performance.solvers.z3.BundledZ3PerfWithPPV2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPPV2_0.class, BundledCvc3PerfWithPPV2_0.class,
		BundledCvc4PerfWithPPV2_0.class, MathSat5PerfWithPPV2_0.class,
		OpenSMTPerfWithPPV2_0.class, BundledVeriTPerfWithPPV2_0.class,
		BundledZ3PerfWithPPV2_0.class })
public class PerformanceTestSuiteV2_0 {
	// Just for tests
}
