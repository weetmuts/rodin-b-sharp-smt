/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests;

import org.eventb.smt.tests.acceptance.AxiomsTestWithAltErgoPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithAltErgoPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc3PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc3PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc4PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc4PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithMathSat5PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithMathSat5PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithOpenSMTPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithOpenSMTPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithVeriTPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithVeriTPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithZ3PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithZ3PPV2_0;
import org.eventb.smt.tests.acceptance.RunProverTestWithPPV1_2;
import org.eventb.smt.tests.acceptance.RunProverTestWithPPV2_0;
import org.eventb.smt.tests.acceptance.RunProverTestWithVeriTV1_2;
import org.eventb.smt.tests.unit.LogicTestsWithPPV1_2;
import org.eventb.smt.tests.unit.LogicTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Systerel (yguyot)
 * 
 */
@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPPV1_2.class,
		TranslationTestsWithVeriTV1_2.class, LogicTestsWithPPV1_2.class,
		RunProverTestWithPPV1_2.class, RunProverTestWithVeriTV1_2.class,
		AxiomsTestWithAltErgoPPV1_2.class, AxiomsTestWithCvc3PPV1_2.class,
		AxiomsTestWithCvc4PPV1_2.class, AxiomsTestWithMathSat5PPV1_2.class,
		AxiomsTestWithOpenSMTPPV1_2.class, AxiomsTestWithVeriTPPV1_2.class,
		AxiomsTestWithZ3PPV1_2.class, TranslationTestsWithPPV2_0.class,
		TranslationTestsWithVeriTV2_0.class, LogicTestsWithPPV2_0.class,
		RunProverTestWithPPV2_0.class, AxiomsTestWithAltErgoPPV2_0.class,
		AxiomsTestWithCvc3PPV2_0.class, AxiomsTestWithCvc4PPV2_0.class,
		AxiomsTestWithMathSat5PPV2_0.class, AxiomsTestWithOpenSMTPPV2_0.class,
		AxiomsTestWithVeriTPPV2_0.class, AxiomsTestWithZ3PPV2_0.class })
public class QuickRegressionTestSuite {
	// Just for tests
}
