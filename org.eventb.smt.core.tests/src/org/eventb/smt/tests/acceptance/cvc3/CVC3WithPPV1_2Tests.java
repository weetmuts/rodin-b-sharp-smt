/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - portability of paths
 *******************************************************************************/
package org.eventb.smt.tests.acceptance.cvc3;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_CVC3;

import org.eventb.smt.tests.acceptance.SolverRunWithPPV1_2Tests;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests for the solver CVC3 on SMT-LIB 1.2
 * benchmarks translated with PP.
 * 
 * @author Yoann Guyot
 * 
 */
public class CVC3WithPPV1_2Tests extends SolverRunWithPPV1_2Tests {

	public CVC3WithPPV1_2Tests() {
		super(BUNDLED_CVC3);
	}

}