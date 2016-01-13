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
package org.eventb.smt.core.internal.prefs;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Implements loading configuration details from a "configuration" element.
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

}
