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

import org.eventb.smt.tests.acceptance.RunProverTestWithPP;
import org.eventb.smt.tests.acceptance.RunProverTestWithVeriT;
import org.eventb.smt.tests.unit.LogicTestsWithPP;
import org.eventb.smt.tests.unit.TranslationTestsWithPP;
import org.eventb.smt.tests.unit.TranslationTestsWithVeriT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPP.class,
		TranslationTestsWithVeriT.class, LogicTestsWithPP.class,
		RunProverTestWithPP.class, RunProverTestWithVeriT.class })
public class QuickRegressionTestSuite {
	// Just for tests
}
