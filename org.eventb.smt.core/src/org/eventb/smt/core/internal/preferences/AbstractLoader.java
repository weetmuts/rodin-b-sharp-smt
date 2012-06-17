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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Common implementation for loading configuration details from an extension
 * point.
 * 
 * @author Yoann Guyot
 */
public abstract class AbstractLoader<T> {

	public static class LoadingException extends RuntimeException {

		private static final long serialVersionUID = -2787953160141168010L;

		protected LoadingException(String message) {
			super(message);
		}

	}

	public static LoadingException error(String message) {
		return new LoadingException(message);
	}

	protected final IConfigurationElement ce;

	public AbstractLoader(final IConfigurationElement configurationElement) {
		this.ce = configurationElement;
	}

	public String getId() {
		final String nameSpace = ce.getNamespaceIdentifier();
		final String localId = getRequiredAttribute("id");
		if (localId.indexOf('.') != -1) {
			throw error("Invalid id: " + localId + " (must not contain a dot).");
		}
		if (!isJavaIdentifier(localId)) {
			throw error("Invalid id: " + localId
					+ " (must be a valid Java identifier).");
		}
		return nameSpace + "." + localId;
	}

	public String getName() {
		final String name = getRequiredAttribute("name");
		if (name.isEmpty()) {
			throw error("Invalid empty name");
		}
		return name;
	}

	protected String getRequiredAttribute(String name) {
		final String value = ce.getAttribute(name);
		if (value == null) {
			throw error("Missing attribute " + name + " in extension from "
					+ ce.getContributor());
		}
		return value;
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

}
