/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences.configurations;

import static org.eventb.smt.core.internal.preferences.configurations.SolverConfiguration.EDITABLE;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;
import static org.eventb.smt.core.translation.TranslationApproach.parseApproach;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.smt.core.internal.preferences.AbstractLoader;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

/**
 * Implements loading configuration details from an extension to point "config".
 *
 * @author Yoann Guyot
 */
public class BundledConfigLoader extends AbstractLoader<SolverConfiguration> {

	public BundledConfigLoader(final IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	public String getSolverName() {
		return getRequiredAttribute("solverName");
	}

	public String getSolverId() {
		return getRequiredAttribute("solverid");
	}

	public String getArgs() {
		return getRequiredAttribute("args");
	}

	public TranslationApproach getTranslationApproach() {
		final String value = ce.getAttribute("translator");
		return parseApproach(value);
	}

	public SMTLIBVersion getVersion() {
		final String value = ce.getAttribute("smt-lib");
		return parseVersion(value);
	}

	@Override
	public SolverConfiguration load() {
		final String id = getId();
		final String name = getName();
		final String solverId = getSolverId();
		final String args = getArgs();
		final TranslationApproach approach = getTranslationApproach();
		final SMTLIBVersion version = getVersion();
		return new SolverConfiguration(id, name, solverId, args, approach,
				version, !EDITABLE);
	}

}
