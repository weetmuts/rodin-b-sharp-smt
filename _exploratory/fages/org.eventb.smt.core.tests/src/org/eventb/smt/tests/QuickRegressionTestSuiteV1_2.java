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

import org.eventb.smt.tests.acceptance.AxiomsTestWithAltErgoPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc3PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithCvc4PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithMathSat5PPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithOpenSMTPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithVeriTPPV1_2;
import org.eventb.smt.tests.acceptance.AxiomsTestWithZ3PPV1_2;
import org.eventb.smt.tests.acceptance.RunProverTestWithPPV1_2;
import org.eventb.smt.tests.acceptance.RunProverTestWithVeriTV1_2;
import org.eventb.smt.tests.unit.LogicTestsWithPPV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithPPV1_2;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriTV1_2;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPPV1_2.class,
		TranslationTestsWithVeriTV1_2.class, LogicTestsWithPPV1_2.class,
		RunProverTestWithPPV1_2.class, RunProverTestWithVeriTV1_2.class,
		AxiomsTestWithAltErgoPPV1_2.class, AxiomsTestWithCvc3PPV1_2.class,
		AxiomsTestWithCvc4PPV1_2.class, AxiomsTestWithMathSat5PPV1_2.class,
		AxiomsTestWithOpenSMTPPV1_2.class, AxiomsTestWithVeriTPPV1_2.class,
		AxiomsTestWithZ3PPV1_2.class })
public class QuickRegressionTestSuiteV1_2 {
	// Just for tests
}
