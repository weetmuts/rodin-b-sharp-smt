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
 * This interface represents an SMT-solver integrated to the Rodin platform. It
 * is identified by an internal ID and defined by different parameters:
 * <ul>
 * <li>A name chosen by the user.</li>
 * <li>Its kind (alt-ergo, cvc3, veriT...) which is used to handle non-standard
 * options for example. A default 'UNKNOWN' value is available which ensure a
 * universal behaviour of the plug-in.</li>
 * <li>The path of its binary file.</li>
 * <li>Whether it can be edited by the user through the UI of the plug-in, or
 * not. Bundled solvers are not editable through it, whereas user-added solvers
 * are.</li>
 * </ul>
 * 
 * @author Systerel (yguyot)
 */
public interface ISMTSolver {
	/**
	 * Returns the ID of this solver.
	 * 
	 * @return the ID of this solver
	 */
	public abstract String getID();

	/**
	 * Returns the name of this solver. It is human-readable.
	 * 
	 * @return the name of this solver
	 */
	public abstract String getName();

	/**
	 * Returns the kind of this solver.
	 * 
	 * @return the kind of this solver
	 * @see SolverKind
	 */
	public abstract SolverKind getKind();

	/**
	 * Returns the path of this solver. It is local to the workspace. For
	 * bundled solvers, it is local to its providing plug-in.
	 * 
	 * @return the local path of this solver
	 */
	public abstract IPath getPath();

	/**
	 * Tells whether this solver is editable by the user (through the UI of the
	 * SMT-Solvers Plug-in) or not.
	 * 
	 * @return <code>true</code> if this solver is editable, otherwise
	 *         <code>false</code>.
	 */
	public abstract boolean isEditable();

	/**
	 * Appends the encoded string representation of this solver into the given
	 * buffer.
	 * 
	 * @param builder
	 *            the buffer in which this solver string representation must be
	 *            appended.
	 */
	public abstract void toString(final StringBuilder builder);
}
