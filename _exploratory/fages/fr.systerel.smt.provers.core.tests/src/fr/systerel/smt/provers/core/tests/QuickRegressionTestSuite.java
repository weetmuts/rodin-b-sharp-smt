package fr.systerel.smt.provers.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.systerel.smt.provers.core.tests.acceptance.RunProverTestWithPP;
import fr.systerel.smt.provers.core.tests.acceptance.RunProverTestWithVeriT;
import fr.systerel.smt.provers.core.tests.unit.TranslationTestsWithPP;
import fr.systerel.smt.provers.core.tests.unit.TranslationTestsWithVeriT;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPP.class,
		TranslationTestsWithVeriT.class, RunProverTestWithPP.class,
		RunProverTestWithVeriT.class })
public class QuickRegressionTestSuite {
	// Just for tests
}
