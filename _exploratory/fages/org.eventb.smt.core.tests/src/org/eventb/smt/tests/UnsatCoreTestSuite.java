/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import org.eventb.smt.tests.performance.unsatcore.UnsatCoreVeriTPerfWithPP;
import org.eventb.smt.tests.performance.unsatcore.UnsatCoreZ3PerfWithPP;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { UnsatCoreVeriTPerfWithPP.class,
		UnsatCoreZ3PerfWithPP.class })
public class UnsatCoreTestSuite {
	// Just for tests
}
