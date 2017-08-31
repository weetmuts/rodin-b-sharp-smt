/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.z3;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_Z3;

import org.eventb.smt.tests.acceptance.AxiomsTestWithPPV2_0;

public class AxiomsTestWithZ3PPV2_0 extends AxiomsTestWithPPV2_0 {

	public AxiomsTestWithZ3PPV2_0() {
		super(BUNDLED_Z3);
	}

}
