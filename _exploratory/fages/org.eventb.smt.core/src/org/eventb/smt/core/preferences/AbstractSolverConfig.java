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

import static java.lang.Boolean.parseBoolean;
import static java.util.regex.Pattern.quote;
import static org.eventb.smt.core.internal.preferences.SolverConfiguration.SEPARATOR;
import static org.eventb.smt.core.internal.preferences.Utils.decode;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import org.eventb.smt.core.internal.preferences.SolverConfiguration;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractSolverConfig {
	public abstract String getID();

	public abstract String getName();

	public abstract String getSolverId();

	public abstract String getArgs();

	public abstract SMTLIBVersion getSmtlibVersion();

	public abstract boolean isEditable();

	public abstract void toString(final StringBuilder builder);

	public static final AbstractSolverConfig newConfig() {
		return new SolverConfiguration();
	}

	public static final AbstractSolverConfig newConfig(final String id,
			final String name, final String solverId, final String args,
			final SMTLIBVersion smtlibVersion) {
		return new SolverConfiguration(id, name, solverId, args, smtlibVersion);
	}

	/**
	 * Parses a preference string to build a solver configuration
	 * 
	 * @param configStr
	 *            the string to parse
	 * @return the solver configuration represented by the string
	 */
	public final static AbstractSolverConfig parseConfig(final String configStr) {
		final String[] columns = configStr.split(quote(SEPARATOR));
		return new SolverConfiguration(decode(columns[0]), decode(columns[1]),
				decode(columns[2]), decode(columns[3]),
				parseVersion(columns[4]), parseBoolean(columns[5]));
	}
}
