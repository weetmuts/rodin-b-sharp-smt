/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.verit;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;

import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.eventb.smt.tests.ConfigProvider;

public class BundledVeriTPerfWithPPV1_2 extends SolverPerfWithPP {

	public BundledVeriTPerfWithPPV1_2() {
		super(ConfigProvider.BUNDLED_VERIT, V1_2);
	}

}
