/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

import org.eventb.smt.core.internal.preferences.SolverConfiguration;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This factory provides method to construct solver configurations instances.
 * 
 * @author Systerel (yguyot)
 */
public class SolverConfigFactory {
	public static final boolean ENABLED = SolverConfiguration.ENABLED;
	public static final int ID_COL = SolverConfiguration.ID_COL;
	public static final int ENABLED_COL = SolverConfiguration.ENABLED_COL;
	public static final int NAME_COL = SolverConfiguration.NAME_COL;
	public static final int SOLVER_COL = SolverConfiguration.SOLVER_ID_COL;
	public static final int ARGS_COL = SolverConfiguration.ARGS_COL;
	public static final int SMTLIB_COL = SolverConfiguration.SMTLIB_VERSION_COL;
	public static final int TIME_OUT_COL = SolverConfiguration.TIME_OUT_COL;
	public static final int EDITABLE_COL = SolverConfiguration.EDITABLE_COL;

	public static final ISolverConfig newConfig(final String id) {
		return new SolverConfiguration(id);
	}

	public static final ISolverConfig newConfig(final String id,
			final String name, final String solverId, final String args,
			final SMTLIBVersion smtlibVersion) {
		return new SolverConfiguration(id, name, solverId, args, smtlibVersion);
	}

	public static final ISolverConfig newConfig(final String id,
			final String name, final String solverId, final String args,
			final SMTLIBVersion smtlibVersion, final int timeOut) {
		return new SolverConfiguration(id, name, solverId, args, smtlibVersion,
				timeOut);
	}
}
