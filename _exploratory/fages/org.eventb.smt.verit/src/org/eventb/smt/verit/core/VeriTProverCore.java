/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.verit.core;

import static org.eventb.smt.internal.provers.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;

import org.eclipse.core.runtime.Plugin;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.verit.internal.core.ProverShell;

/**
 * @author Systerel (yguyot)
 * 
 */
public class VeriTProverCore extends Plugin {
	public static final String PLUGIN_ID = "org.eventb.smt.verit";

	private static final String VERIT_CONFIG_ID = "integrated_verit";
	private static final String VERIT_PATH = ProverShell.getVeriTPath();
	private static final String VERIT_ARGS = "-i smtlib2 --disable-print-success --disable-banner --proof=- --proof-version=1 --proof-prune --disable-e --max-time=3";

	private static final SMTSolverConfiguration VERIT_CONFIG = new SMTSolverConfiguration(
			VERIT_CONFIG_ID, VERIT, VERIT_PATH, VERIT_ARGS, V2_0);

	public VeriTProverCore() {
	}

	public static SMTSolverConfiguration getVeriTConfig() {
		return VERIT_CONFIG;
	}

	public static String getVeriTPath() {
		return VERIT_PATH;
	}
}
