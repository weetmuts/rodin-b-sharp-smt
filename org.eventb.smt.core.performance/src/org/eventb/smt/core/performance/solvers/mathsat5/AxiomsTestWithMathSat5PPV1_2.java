/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.mathsat5;

import static org.eventb.smt.tests.ConfigProvider.LAST_MATHSAT5;

import org.eventb.smt.tests.acceptance.AxiomsTestWithPPV1_2;
import org.junit.Ignore;

@Ignore("Cannot run acceptance tests with non-bundled solver")
public class AxiomsTestWithMathSat5PPV1_2 extends AxiomsTestWithPPV1_2 {

	public AxiomsTestWithMathSat5PPV1_2() {
		super(LAST_MATHSAT5);
	}

}