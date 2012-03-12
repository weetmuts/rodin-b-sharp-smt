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

import org.eventb.smt.core.provers.SMTSolver;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * @author Systerel (yguyot)
 * 
 */
public interface ISolverConfiguration {
	public String getID();

	public String getName();

	public SMTSolver getSolver();

	public String getPath();

	public String getArgs();

	public SMTLIBVersion getSmtlibVersion();

	public boolean isEditable();

	public void toString(final StringBuilder builder);
}
