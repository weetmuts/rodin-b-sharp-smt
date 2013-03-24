/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.verit;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;

import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.eventb.smt.tests.ConfigProvider;

public class BundledVeriTPerfWithPPV2_0 extends SolverPerfWithPP {

	public BundledVeriTPerfWithPPV2_0() {
		super(ConfigProvider.BUNDLED_VERIT, V2_0);
	}

}
