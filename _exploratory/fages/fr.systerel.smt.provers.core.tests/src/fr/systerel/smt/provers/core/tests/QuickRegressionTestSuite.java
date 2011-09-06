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

import fr.systerel.smt.provers.core.tests.acceptance.RunProverTestWithPP;
import fr.systerel.smt.provers.core.tests.acceptance.RunProverTestWithVeriT;
import fr.systerel.smt.provers.core.tests.unit.LogicTestsWithPP;
import fr.systerel.smt.provers.core.tests.unit.TranslationTestsWithPP;
import fr.systerel.smt.provers.core.tests.unit.TranslationTestsWithVeriT;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPP.class,
		TranslationTestsWithVeriT.class, LogicTestsWithPP.class,
		RunProverTestWithPP.class, RunProverTestWithVeriT.class })
public class QuickRegressionTestSuite {
	// Just for tests
}
