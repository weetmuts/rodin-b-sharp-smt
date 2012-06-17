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

import static org.eventb.smt.core.internal.preferences.AbstractLoader.error;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eventb.smt.core.preferences.IRegistry;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractRegistry<T> implements IRegistry<T> {
	public abstract Map<String, T> getRegistry();

	@Override
	public Set<String> getIDs() {
		if (getRegistry() == null) {
			loadRegistry();
		}
		return getRegistry().keySet();
	}

	@Override
	public T get(String id) {
		if (getRegistry() == null) {
			loadRegistry();
		}
		return getRegistry().get(id);
	}

	/**
	 * Initializes the registry using extensions to the extension point.
	 */
	protected abstract void loadRegistry();

	protected static void checkPoint(final IExtensionPoint point,
			final String id) {
		if (point == null)
			throw error("Invalid extension point id: " + id
					+ " (no such extension point was found).");
	}

	protected static void checkRegistry(final IExtensionRegistry registry) {
		if (registry == null)
			throw error("Invalid extension registry: null pointer.");
	}
}
