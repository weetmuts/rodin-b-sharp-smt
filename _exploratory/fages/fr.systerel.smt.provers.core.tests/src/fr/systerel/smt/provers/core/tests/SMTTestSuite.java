package fr.systerel.smt.provers.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TranslationTestsWithPP.class,
		TranslationTestsWithVeriT.class, RunProverTestWithPP.class,
		RunProverTestWithVeriT.class })
public class SMTTestSuite {
	// Just for tests
}
