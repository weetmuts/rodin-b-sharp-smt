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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Common implementation for loading configuration details from an extension
 * point.
 *
 * @author Yoann Guyot
 */
public abstract class AbstractLoader {

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
}
