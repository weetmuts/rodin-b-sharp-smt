/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.TranslationApproach.USING_PP;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.TranslationApproach;

/**
 * Implements loading configuration details from an extension to point "config".
 * 
 * @author Yoann Guyot
 */
public class BundledConfigLoader extends AbstractLoader {

	public BundledConfigLoader(final IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	public String getSolverName() {
		return getRequiredAttribute("solverName");
	}

	public String getArgs() {
		return getOptionalAttribute("args");
	}

	public TranslationApproach getTranslationApproach() {
		return getEnumAttribute("translator", USING_PP);
	}

	public SMTLIBVersion getVersion() {
		return getEnumAttribute("smt-lib", V2_0);
	}

}
