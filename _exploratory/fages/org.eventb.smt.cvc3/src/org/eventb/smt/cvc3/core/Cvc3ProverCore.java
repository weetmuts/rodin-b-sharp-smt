/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.cvc3.core;

import static org.eventb.smt.internal.provers.core.SMTSolver.CVC3;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;

import org.eclipse.core.runtime.Plugin;
import org.eventb.smt.cvc3.internal.core.ProverShell;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;

/**
 * @author Systerel (yguyot)
 * 
 */
public class Cvc3ProverCore extends Plugin {
	public static final String PLUGIN_ID = "org.eventb.smt.cvc3";

	private static final String CVC3_CONFIG_ID = "integrated_cvc3";
	private static final String CVC3_PATH = ProverShell.getCvc3Path();
	private static final String CVC3_ARGS = "-lang smt2 -timeout 3";

	private static final SMTSolverConfiguration CVC3_CONFIG = new SMTSolverConfiguration(
			CVC3_CONFIG_ID, CVC3, CVC3_PATH, CVC3_ARGS, V2_0);

	public Cvc3ProverCore() {
	}

	public static SMTSolverConfiguration getCvc3Config() {
		return CVC3_CONFIG;
	}
}
