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
 * This interface represents a solver configuration for the SMT-Solver Plug-in.
 * It is identified by an internal ID and defined by different parameters:
 * <ul>
 * <li>A name chosen by the user.</li>
 * <li>The ID of the solver it configures.</li>
 * <li>The arguments to be given to the solver.</li>
 * <li>The version of SMT-LIB target by the translation of the event-B sequents.
 * </li>
 * <li>Whether it can be edited by the user through the UI of the plug-in, or
 * not. Bundled configurations are not editable through it, whereas user-added
 * configurations are.</li>
 * </ul>
 * 
 * @author Systerel (yguyot)
 */
public interface ISolverConfig {
	/**
	 * Returns the ID of this configuration.
	 * 
	 * @return the ID of this configuration
	 */
	public String getID();

	/**
	 * Returns the name of this configuration. It is human-readable.
	 * 
	 * @return the name of this configuration
	 */
	public String getName();

	/**
	 * Returns the ID of the solver used in this configuration.
	 * 
	 * @return the ID of the configured solver
	 */
	public String getSolverId();

	/**
	 * Returns the arguments given to the solver when using this configuration.
	 * 
	 * @return the arguments of this configuration solver
	 */
	public String getArgs();

	/**
	 * Returns the version of SMT-LIB target by the SMT translation.
	 * 
	 * @return the SMT-LIB version of this configuration
	 */
	public SMTLIBVersion getSmtlibVersion();

	/**
	 * Returns the time (in milliseconds) after which Rodin will terminate the
	 * solver if it has not returned.
	 * 
	 * @return the time-out of this configuration
	 */
	public int getTimeOut();

	/**
	 * Tells whether this configuration is editable by the user (through the UI
	 * of the SMT-Solvers Plug-in) or not.
	 * 
	 * @return <code>true</code> if this configuration is editable, otherwise
	 *         <code>false</code>
	 */
	public boolean isEditable();

	/**
	 * Tells whether this configuration is enabled for execution when the user
	 * calls the SMT tactic, or not.
	 * 
	 * @return <code>true</code> if this configuration is enabled, otherwise
	 *         <code>false</code>
	 */
	public boolean isEnabled();

	public void setEnabled(final boolean value);

	/**
	 * Appends the encoded string representation of this configuration into the
	 * given buffer.
	 * 
	 * @param builder
	 *            the buffer in which this solver string representation must be
	 *            appended.
	 */
	public void toString(final StringBuilder builder);
}
