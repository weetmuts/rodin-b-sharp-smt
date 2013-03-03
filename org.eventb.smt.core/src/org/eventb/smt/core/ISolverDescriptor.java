/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core;

import org.eclipse.core.runtime.IPath;

/**
 * Common protocol for describing an SMT solver connected to the Rodin platform.
 * Instance of this interface are immutable and the information has been
 * provided by either a plug-in or the end-user.
 *
 * @author Laurent Voisin
 *
 * @noextend This interface is not intended to be subclassed by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISolverDescriptor extends IDescriptor {

	/**
	 * Returns the kind of this solver. This field tells which kind of solver is
	 * described, e.g., veriT, Z3, etc.
	 *
	 * @return the kind of this solver
	 */
	SolverKind getKind();

	/**
	 * Returns the path to the binary file of this solver. For proper operation,
	 * the path must denote an executable file in the file system. However, this
	 * interface does not guarantee that it is actually the case.
	 *
	 * @return the name to the binary file of this solver
	 */
	IPath getPath();

}