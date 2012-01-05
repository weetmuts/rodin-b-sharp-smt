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

import org.eventb.smt.tests.acceptance.AxiomsTestWithAltErgoPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc3PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc4PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithMathSat5PPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithOpenSMTPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithVeriTPPV2_0;
import org.eventb.smt.tests.acceptance.AxiomsTestWithZ3PPV2_0;
import org.eventb.smt.tests.acceptance.RunProverTestWithPPV2_0;
import org.eventb.smt.tests.unit.LogicTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV2_0;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPPV2_0.class,
		TranslationTestsWithVeriTV2_0.class, LogicTestsWithPPV2_0.class,
		RunProverTestWithPPV2_0.class, AxiomsTestWithAltErgoPPV2_0.class,
		AxiomsTestWithCvc3PPV2_0.class, AxiomsTestWithCvc4PPV2_0.class,
		AxiomsTestWithMathSat5PPV2_0.class, AxiomsTestWithOpenSMTPPV2_0.class,
		AxiomsTestWithVeriTPPV2_0.class, AxiomsTestWithZ3PPV2_0.class })
public class QuickRegressionTestSuiteV2_0 {
	// Just for tests
}
