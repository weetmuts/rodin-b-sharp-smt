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

import static org.eventb.smt.core.internal.preferences.SolverConfiguration.ENABLED_COL;

import org.eventb.smt.core.internal.preferences.SolverConfiguration;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This factory provides method to construct solver configurations instances.
 * 
 * @author Systerel (yguyot)
 */
public class SolverConfigFactory {
	public static final boolean ENABLED = SolverConfiguration.ENABLED;

	public static final ISolverConfig newConfig(final String id) {
		return new SolverConfiguration(id);
	}

	public static final ISolverConfig newConfig(final String id,
			final String name, final String solverId, final String args,
			final SMTLIBVersion smtlibVersion) {
		return new SolverConfiguration(id, name, solverId, args, smtlibVersion);
	}

	public static final int getEnabledColumnNumber() {
		return ENABLED_COL;
	}
}
