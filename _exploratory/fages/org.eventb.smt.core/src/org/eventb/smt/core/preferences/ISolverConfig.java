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

import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * @author Systerel (yguyot)
 * 
 */
public interface ISolverConfig {
	public abstract String getID();

	public abstract String getName();

	public abstract String getSolverId();

	public abstract String getArgs();

	public abstract SMTLIBVersion getSmtlibVersion();

	public abstract boolean isEditable();

	public abstract void toString(final StringBuilder builder);
}
