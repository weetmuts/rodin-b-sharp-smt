/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.provers;

import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.ISMTConfiguration;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

/**
 * Implementation of an SMT solver configuration. Instances are immutable and
 * stand-alone (they do not depend on any registry).
 */
public class SMTConfiguration implements ISMTConfiguration {

	private final String name;

	private final String solverName;

	private final SolverKind kind;

	private final IPath solverPath;

	private final String args;

	private final TranslationApproach translationApproach;

	private final SMTLIBVersion smtlibVersion;

	/**
	 * Constructs a new configuration from a configuration and a solver
	 * descriptor.
	 *
	 * @param config
	 *            a configuration descriptor
	 * @param solver
	 *            the corresponding solver descriptor
	 */
	public SMTConfiguration(final IConfigDescriptor config,
			final ISolverDescriptor solver) {
		assert config.getSolverName().equals(solver.getName());
		this.name = config.getName();
		this.solverName = config.getSolverName();
		this.kind = solver.getKind();
		this.solverPath = solver.getPath();
		this.args = config.getArgs();
		this.translationApproach = config.getTranslationApproach();
		this.smtlibVersion = config.getSmtlibVersion();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSolverName() {
		return solverName;
	}

	@Override
	public SolverKind getKind() {
		return kind;
	}

	@Override
	public IPath getSolverPath() {
		return solverPath;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public TranslationApproach getTranslationApproach() {
		return translationApproach;
	}

	@Override
	public SMTLIBVersion getSmtlibVersion() {
		return smtlibVersion;
	}

}
