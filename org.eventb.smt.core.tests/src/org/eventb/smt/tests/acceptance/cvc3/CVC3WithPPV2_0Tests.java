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
package org.eventb.smt.tests.acceptance.cvc3;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_CVC3;

import org.eventb.smt.tests.acceptance.SolverRunWithPPV2_0Tests;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests for the solver CVC3 on SMT-LIB 2.0
 * benchmarks translated with PP.
 * 
 * @author Yoann Guyot
 * 
 */
public class CVC3WithPPV2_0Tests extends SolverRunWithPPV2_0Tests {

	public CVC3WithPPV2_0Tests() {
		super(BUNDLED_CVC3);
	}

}