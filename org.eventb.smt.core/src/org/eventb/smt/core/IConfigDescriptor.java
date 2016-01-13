/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core;


/**
 * Common protocol for describing how to run an SMT solver connected to the
 * Rodin platform. Instance of this interface are immutable and the information
 * has been provided by either a plug-in or the end-user.
 *
 * @author Laurent Voisin
 *
 * @noextend This interface is not intended to be subclassed by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConfigDescriptor extends IDescriptor {

	/**
	 * Returns the name of the solver used by this configuration. This name must
	 * correspond to a registered solver (see {@link ISolverDescriptor}.
	 *
	 * @return the name of the solver used by this configuration
	 */
	String getSolverName();

	/**
	 * Returns the arguments that will be passed to the solver.
	 *
	 * @return the solver arguments
	 */
	String getArgs();

	/**
	 * Returns the approach to use for translating to SMT-LIB.
	 *
	 * @return the translation approach of this configuration
	 * @deprecated PP translation approach is assumed everywhere
	 */
	@Deprecated
	TranslationApproach getTranslationApproach();

	/**
	 * Returns the version of the SMT-LIB to use with the solver.
	 *
	 * @return the version of the SMT-LIB to use with the solver
	 * @deprecated SMT-LIB 2 is assumed everywhere
	 */
	@Deprecated
	SMTLIBVersion getSmtlibVersion();

	/**
	 * Tells whether this configuration shall be run within the default SMT
	 * auto-tactic.
	 * 
	 * @return <code>true</code> if this configuration is part of the default
	 *         SMT auto-tactic
	 */
	boolean isEnabled();

}