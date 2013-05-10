/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static org.eclipse.core.runtime.Platform.getBundle;
import static org.eventb.core.seqprover.xprover.BundledFileExtractor.extractFile;
import static org.eventb.smt.core.SolverKind.UNKNOWN;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.SolverKind;
import org.osgi.framework.Bundle;

/**
 * Implements loading configuration details from a "solver" element.
 *
 * @author Yoann Guyot
 */
public class BundledSolverLoader extends AbstractLoader {

	public BundledSolverLoader(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	public SolverKind getKind() {
		return getEnumAttribute("kind", UNKNOWN);
	}

	public IPath getPath() {
		final String localPathStr = ce.getAttribute("localpath");
		if (localPathStr == null) {
			throw error("Missing path in extension named " + getName());
		}
		final String bundleName = ce.getContributor().getName();
		final Bundle bundle = getBundle(bundleName);
		if (bundle == null) {
			throw error("Unknown bundle " + bundleName);
		}
		final IPath path = extractFile(bundle, new Path(localPathStr), true);
		if (path == null) {
			throw error("Invalid local path " + localPathStr);
		}
		return path;
	}

}
