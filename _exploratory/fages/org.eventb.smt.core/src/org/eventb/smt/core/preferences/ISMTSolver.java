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

import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.provers.SolverKind;

/**
 * @author Systerel (yguyot)
 * 
 */
public interface ISMTSolver {
	public abstract String getID();

	public abstract String getName();

	public abstract SolverKind getKind();

	public abstract IPath getPath();

	public abstract boolean isEditable();

	public abstract void toString(final StringBuilder builder);
}
