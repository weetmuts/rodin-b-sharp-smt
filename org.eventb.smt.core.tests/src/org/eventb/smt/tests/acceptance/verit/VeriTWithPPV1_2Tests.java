/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - portability of paths
 *******************************************************************************/
package org.eventb.smt.tests.acceptance.verit;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_VERIT;

import org.eventb.smt.tests.acceptance.SolverRunWithPPV1_2Tests;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests for the solver veriT on SMT-LIB 1.2
 * benchmarks translated with PP.
 * 
 * @author Yoann Guyot
 * 
 */
public class VeriTWithPPV1_2Tests extends SolverRunWithPPV1_2Tests {

	public VeriTWithPPV1_2Tests() {
		super(BUNDLED_VERIT);
	}

}