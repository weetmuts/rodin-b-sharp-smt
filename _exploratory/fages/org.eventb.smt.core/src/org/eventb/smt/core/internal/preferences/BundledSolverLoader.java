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
public class BundledSolverLoader extends AbstractLoader<SMTSolver> {
	public BundledSolverLoader(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	/**
	 * Quickly loads the configuration element attributes, then checks the
	 * values and builds the <code>SMTSolver</code> instance to return.
	 */
	@Override
	public SMTSolver load() throws ExtensionLoadingException,
			InvalidRegistryObjectException {
		/**
		 * The ID of the extension. Example: <code>bundled_verit</code>.
		 */
		final String localId = configurationElement.getAttribute("id");
		/**
		 * The bundle name of the extension. Example
		 * <code>org.eventb.smt.verit</code>.
		 */
		final String nameSpace = configurationElement.getNamespaceIdentifier();
		final String localPathStr = configurationElement
				.getAttribute("localpath");
		final String name = configurationElement.getAttribute("name");
		final String kindStr = configurationElement.getAttribute("kind");

		id = nameSpace + "." + localId;

		checkId(localId);
		final IPath path = makeSolverPath(nameSpace, localPathStr);
		checkPath(path);

		return new SMTSolver(id, name, parseKind(kindStr), path, !EDITABLE);
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
}
