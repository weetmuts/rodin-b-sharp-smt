/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.internal.preferences;

import static java.io.File.separatorChar;
import static org.eclipse.core.runtime.Platform.getBundle;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeDotInIDException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNoSuchBundleException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNullBinaryNameException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNullIDException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNullPathException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeWhitespaceOrColonInIDException;
import static org.eventb.smt.core.provers.SMTSolver.parseSolver;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.EDITABLE;

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
				configurationElement.getAttribute("binary"));
		checkPath(path);
		instance = new SMTSolverConfiguration(id,
				configurationElement.getAttribute("name"),
				parseSolver(configurationElement.getAttribute("kind")), path,
				configurationElement.getAttribute("args"),
				parseVersion(configurationElement.getAttribute("smt-lib")),
				!EDITABLE);
	}

	private static String extractFile(final String bundleName,
			String localPathString) throws BundledSolverLoadingException {
		final Bundle bundle = getBundle(bundleName);
		checkBundle(bundleName, bundle);
		final IPath localPath = new Path(localPathString);
		final IPath path = BundledFileExtractor.extractFile(bundle, localPath,
				true);
		return path != null ? path.toOSString() : null;
	}

	/**
	 * 
	 * @param bundleName
	 *            FIXME isn't it available another way ?
	 * @throws BundledSolverLoadingException
	 */
	private static String makeSolverPath(final String bundleName,
			final String binaryName) throws BundledSolverLoadingException {
		checkBinaryName(binaryName);
		final StringBuilder localPathBuilder = new StringBuilder();
		localPathBuilder.append("$os$").append(separatorChar);
		localPathBuilder.append(binaryName);
		return extractFile(bundleName, localPathBuilder.toString());
	}

	/**
	 * Checks if a string contains a whitespace character or a colon
	 * 
	 * @param str
	 *            String to check for.
	 * @return <code>true</code> iff the string contains a whitespace character
	 *         or a colon.
	 */
	private static boolean containsWhitespaceOrColon(String str) {
		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			if (c == ':' || Character.isWhitespace(c))
				return true;
		}
		return false;
	}

	private static void checkId(String id) throws BundledSolverLoadingException {
		if (id == null) {
			throw makeNullIDException();
		}
		if (id.indexOf('.') != -1) {
			throw makeDotInIDException(id);
		}
		if (containsWhitespaceOrColon(id)) {
			throw makeWhitespaceOrColonInIDException(id);
		}
	}

	/**
	 * @param binaryName
	 * @throws BundledSolverLoadingException
	 */
	private static void checkBinaryName(final String binaryName)
			throws BundledSolverLoadingException {
		if (binaryName == null)
			throw makeNullBinaryNameException();
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
