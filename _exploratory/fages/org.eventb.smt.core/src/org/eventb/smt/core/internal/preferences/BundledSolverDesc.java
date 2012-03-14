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

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static org.eclipse.core.runtime.Platform.getBundle;
import static org.eventb.smt.core.internal.preferences.SMTSolverConfiguration.EDITABLE;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeDotInIDException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeInvalidJavaIDException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNoSuchBundleException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNullIDException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNullPathException;
import static org.eventb.smt.core.provers.SMTSolver.parseSolver;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.smt.core.preferences.BundledSolverLoadingException;
import org.osgi.framework.Bundle;

/**
 * Bridge class used by the bundled solver registry to store data about bundled
 * solvers.
 * 
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverDesc {
	private final IConfigurationElement configurationElement;
	private String id;

	/**
	 * Bundled solver configuration instance lazily loaded using
	 * <code>configurationElement</code>
	 */
	private SMTSolverConfiguration instance;

	public BundledSolverDesc(final IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	public void load() throws BundledSolverLoadingException,
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
		final String path = makeSolverPath(nameSpace,
				configurationElement.getAttribute("localpath"));
		checkPath(path);
		instance = new SMTSolverConfiguration(id,
				configurationElement.getAttribute("name"),
				parseSolver(configurationElement.getAttribute("kind")), path,
				configurationElement.getAttribute("args"),
				parseVersion(configurationElement.getAttribute("smt-lib")),
				!EDITABLE);
	}

	/**
	 * 
	 * @param bundleName
	 *            FIXME isn't it available another way ?
	 * @throws BundledSolverLoadingException
	 */
	private static String makeSolverPath(final String bundleName,
			final String localPathStr) throws BundledSolverLoadingException {
		final Bundle bundle = getBundle(bundleName);
		checkBundle(bundleName, bundle);
		checkPath(localPathStr);
		final IPath path = BundledFileExtractor.extractFile(bundle, new Path(
				localPathStr), true);
		return path != null ? path.toOSString() : null;
	}

	/**
	 * Checks if a string is a legal Java identifier
	 * 
	 * @param s
	 *            the string to check
	 * @return true if the string is a legal Java identifier
	 */
	private static boolean isJavaIdentifier(String s) {
		if (s.length() == 0 || !isJavaIdentifierStart(s.charAt(0))) {
			return false;
		}
		for (int i = 1; i < s.length(); i++) {
			if (!isJavaIdentifierPart(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static void checkId(String id) throws BundledSolverLoadingException {
		if (id == null) {
			throw makeNullIDException();
		}
		if (id.indexOf('.') != -1) {
			throw makeDotInIDException(id);
		}
		if (!isJavaIdentifier(id)) {
			throw makeInvalidJavaIDException(id);
		}
	}

	/**
	 * @param bundleName
	 * @param bundle
	 * @throws BundledSolverLoadingException
	 */
	private static void checkBundle(final String bundleName, final Bundle bundle)
			throws BundledSolverLoadingException {
		if (bundle == null)
			throw makeNoSuchBundleException(bundleName);
	}

	private static void checkPath(final String path)
			throws BundledSolverLoadingException {
		if (path == null)
			throw makeNullPathException();
	}

	public String getId() {
		return id;
	}

	public SMTSolverConfiguration getInstance() {
		return instance;
	}
}
