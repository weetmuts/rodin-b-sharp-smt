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

import org.eventb.smt.core.performance.solvers.SolverPerfWithVeriT;
import org.eventb.smt.tests.ConfigProvider;

public class BundledVeriTPerfWithVeriTV2_0 extends SolverPerfWithVeriT {

	public BundledVeriTPerfWithVeriTV2_0() {
		super(ConfigProvider.BUNDLED_VERIT);
	}

}
