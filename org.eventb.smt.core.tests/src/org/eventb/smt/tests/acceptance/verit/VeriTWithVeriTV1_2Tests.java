/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *  UFRN - additional tests
 *******************************************************************************/

package org.eventb.smt.tests.acceptance.verit;

import static org.eventb.smt.core.SolverKind.VERIT;

import org.eventb.smt.tests.acceptance.SolverRunWithVeriTV1_2Tests;

/**
 * This class contains acceptance tests dedicated to solver runs, which are,
 * tests to check that a solver is ran correctly on the entire chain call.
 * 
 * One shall put in this class such tests for the solver veriT on SMT-LIB 1.2
 * benchmarks translated with veriT.
 * 
 * @author Yoann Guyot
 * 
 */
public class VeriTWithVeriTV1_2Tests extends SolverRunWithVeriTV1_2Tests {
	public VeriTWithVeriTV1_2Tests() {
		super(VERIT);
	}
}