/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests;

import org.eventb.smt.tests.acceptance.cvc3.AxiomsTestWithCvc3PPV2_0;
import org.eventb.smt.tests.acceptance.cvc3.CVC3WithPPV2_0Tests;
import org.eventb.smt.tests.acceptance.cvc3.CVC3WithVeriTV2_0Tests;
import org.eventb.smt.tests.acceptance.verit.AxiomsTestWithVeriTPPV2_0;
import org.eventb.smt.tests.acceptance.verit.UnsatCoreVeriTWithPP;
import org.eventb.smt.tests.acceptance.verit.VeriTWithPPV2_0Tests;
import org.eventb.smt.tests.acceptance.verit.VeriTWithVeriTV2_0Tests;
import org.eventb.smt.tests.unit.LogicTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPPV2_0.class, //
		TranslationTestsWithVeriTV2_0.class, //

		LogicTestsWithPPV2_0.class, //

		VeriTWithPPV2_0Tests.class, //
		VeriTWithVeriTV2_0Tests.class, //
		CVC3WithPPV2_0Tests.class, //
		CVC3WithVeriTV2_0Tests.class, //

		AxiomsTestWithCvc3PPV2_0.class, //
		AxiomsTestWithVeriTPPV2_0.class, //

		UnsatCoreVeriTWithPP.class })
public class QuickRegressionTestSuiteV2_0 {
	// Just for tests
}
