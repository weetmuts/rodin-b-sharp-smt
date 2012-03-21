/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance.solvers;

import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;


public class VeriTPerfWithVeriTV1_2 extends SolverPerfWithVeriT {
	public VeriTPerfWithVeriTV1_2() {
		super(VERIT, V1_2);
	}
}
