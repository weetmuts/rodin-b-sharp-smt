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
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.EDITABLE;
import static org.eventb.smt.internal.provers.core.SMTSolver.parseSolver;
import static org.eventb.smt.internal.translation.SMTLIBVersion.parseVersion;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
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
	private String name;

	/**
	 * Bundled solver configuration instance lazily loaded using
	 * <code>configurationElement</code>
	 */
	private SMTSolverConfiguration instance;

	public static class BundledSolverLoadingException extends Exception {
		/**
		 * Generated serial version ID.
		 */
		private static final long serialVersionUID = -2787953160141168010L;

		public BundledSolverLoadingException(String message) {
			super(message);
		}
	}

	public BundledSolverDesc(final IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	public void load() throws BundledSolverLoadingException {
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
		/**
		 * The name of the extension.
		 */
		name = configurationElement.getAttribute("name");
		instance = new SMTSolverConfiguration(id, name,
				parseSolver(configurationElement.getAttribute("kind")),
				makeSolverPath(nameSpace,
						configurationElement.getAttribute("binary")),
				configurationElement.getAttribute("args"),
				parseVersion(configurationElement.getAttribute("smt-lib")),
				!EDITABLE);
	}

	private static String extractFile(final String bundleName,
			String localPathString) {
		final Bundle bundle = getBundle(bundleName);
		final IPath localPath = new Path(localPathString);
		final IPath path = BundledFileExtractor.extractFile(bundle, localPath,
				true);
		return path != null ? path.toOSString() : null;
	}

	/**
	 * 
	 * @param bundleName
	 *            FIXME isn't it available another way ?
	 */
	private static String makeSolverPath(final String bundleName,
			final String binaryName) {
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
		if (id.indexOf('.') != -1) {
			throw new BundledSolverLoadingException("Invalid id: " + id
					+ " (must not contain a dot)");
		}
		if (containsWhitespaceOrColon(id)) {
			throw new BundledSolverLoadingException("Invalid id: " + id
					+ " (must not contain a whitespace or a colon)");
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public SMTSolverConfiguration getInstance() {
		return instance;
	}
}
