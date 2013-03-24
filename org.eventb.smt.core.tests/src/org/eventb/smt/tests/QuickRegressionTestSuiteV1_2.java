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

import org.eventb.smt.tests.acceptance.cvc3.AxiomsTestWithCvc3PPV1_2;
import org.eventb.smt.tests.acceptance.cvc3.CVC3WithPPV1_2Tests;
import org.eventb.smt.tests.acceptance.cvc3.CVC3WithVeriTV1_2Tests;
import org.eventb.smt.tests.acceptance.verit.AxiomsTestWithVeriTPPV1_2;
import org.eventb.smt.tests.acceptance.verit.VeriTWithPPV1_2Tests;
import org.eventb.smt.tests.acceptance.verit.VeriTWithVeriTV1_2Tests;
import org.eventb.smt.tests.unit.LogicTestsWithPPV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV1_2;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPPV1_2.class, //
		TranslationTestsWithVeriTV1_2.class, //

		LogicTestsWithPPV1_2.class, //

		VeriTWithPPV1_2Tests.class, //
		VeriTWithVeriTV1_2Tests.class, //
		CVC3WithPPV1_2Tests.class, //
		CVC3WithVeriTV1_2Tests.class, //

		AxiomsTestWithCvc3PPV1_2.class, //
		AxiomsTestWithVeriTPPV1_2.class })
public class QuickRegressionTestSuiteV1_2 {
	// Just for tests
}
