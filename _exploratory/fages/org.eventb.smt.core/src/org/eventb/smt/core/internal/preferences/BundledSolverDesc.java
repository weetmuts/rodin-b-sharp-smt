/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import static org.eclipse.core.runtime.Platform.getBundle;
import static org.eventb.smt.core.internal.preferences.SMTSolver.EDITABLE;
import static org.eventb.smt.core.internal.preferences.Utils.checkBundle;
import static org.eventb.smt.core.internal.preferences.Utils.checkId;
import static org.eventb.smt.core.preferences.ExtensionLoadingException.makeNullPathException;
import static org.eventb.smt.core.provers.SolverKind.parseKind;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.smt.core.preferences.ExtensionLoadingException;
import org.osgi.framework.Bundle;

/**
 * Bridge class used by the bundled solver registry to store data about bundled
 * solvers.
 * 
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverDesc extends AbstractDescriptor {
	/**
	 * Bundled solver instance lazily loaded using
	 * <code>configurationElement</code>
	 */
	private SMTSolver instance;

	public BundledSolverDesc(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	@Override
	public void load() throws ExtensionLoadingException,
			InvalidRegistryObjectException {
		/**
		 * The ID of the extension. Example: <code>bundled_verit</code>.
		 */
		final String localId = configurationElement.getAttribute("id");
		checkId(localId);
		/**
		 * The bundle name of the extension. Example
		 * <code>org.eventb.smt.verit</code>.
		 */
		final String nameSpace = configurationElement.getNamespaceIdentifier();
		id = nameSpace + "." + localId;
		final IPath path = makeSolverPath(nameSpace,
				configurationElement.getAttribute("localpath"));
		checkPath(path);
		instance = new SMTSolver(id, configurationElement.getAttribute("name"),
				parseKind(configurationElement.getAttribute("kind")), path,
				!EDITABLE);
	}

	/**
	 * 
	 * @param bundleName
	 *            FIXME isn't it available another way ?
	 * @throws ExtensionLoadingException
	 */
	private static IPath makeSolverPath(final String bundleName,
			final String localPathStr) throws ExtensionLoadingException {
		final Bundle bundle = getBundle(bundleName);
		checkBundle(bundleName, bundle);
		checkPath(localPathStr);
		return BundledFileExtractor.extractFile(bundle, new Path(localPathStr),
				true);
	}

	private static void checkPath(final Object path)
			throws ExtensionLoadingException {
		if (path == null)
			throw makeNullPathException();
	}

	@Override
	public SMTSolver getInstance() {
		return instance;
	}
}
