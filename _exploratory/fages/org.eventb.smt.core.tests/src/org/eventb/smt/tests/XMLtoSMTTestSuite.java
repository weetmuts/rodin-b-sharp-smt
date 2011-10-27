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

import org.eventb.smt.tests.performance.XMLtoSMTTestsL1AltErgoV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1AltErgoV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1CVC3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1CVC3V2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1VeritV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1VeritV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1Z3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL1Z3V2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2AltErgoV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2AltErgoV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2CVC3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2CVC3V2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2VeritV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2VeritV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2Z3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL2Z3V2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3AltErgoV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3AltErgoV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3CVC3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3CVC3V2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3VeritV1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3VeritV2_0;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3Z3V1_2;
import org.eventb.smt.tests.performance.XMLtoSMTTestsL3Z3V2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { // SMT 1.2 level 1
		XMLtoSMTTestsL1AltErgoV1_2.class, //
		XMLtoSMTTestsL1CVC3V1_2.class, //
		XMLtoSMTTestsL1VeritV1_2.class, //
		XMLtoSMTTestsL1Z3V1_2.class,

		// SMT 1.2 level 2
		XMLtoSMTTestsL2AltErgoV1_2.class, //
		XMLtoSMTTestsL2CVC3V1_2.class, //
		XMLtoSMTTestsL2VeritV1_2.class, //
		XMLtoSMTTestsL2Z3V1_2.class,

		// SMT 1.2 level 3
		XMLtoSMTTestsL3AltErgoV1_2.class, //
		XMLtoSMTTestsL3CVC3V1_2.class, //
		XMLtoSMTTestsL3VeritV1_2.class, //
		XMLtoSMTTestsL3Z3V1_2.class,

		// SMT 2.0 level 1
		XMLtoSMTTestsL1AltErgoV2_0.class, //
		XMLtoSMTTestsL1CVC3V2_0.class, //
		XMLtoSMTTestsL1VeritV2_0.class, //
		XMLtoSMTTestsL1Z3V2_0.class,

		// SMT 2.0 level 2
		XMLtoSMTTestsL2AltErgoV2_0.class, //
		XMLtoSMTTestsL2CVC3V2_0.class, //
		XMLtoSMTTestsL2VeritV2_0.class, //
		XMLtoSMTTestsL2Z3V2_0.class,

		// SMT 2.0 level 3
		XMLtoSMTTestsL3AltErgoV2_0.class, //
		XMLtoSMTTestsL3CVC3V2_0.class, //
		XMLtoSMTTestsL3VeritV2_0.class, //
		XMLtoSMTTestsL3Z3V2_0.class })
public class XMLtoSMTTestSuite {
	// Just for tests
}
