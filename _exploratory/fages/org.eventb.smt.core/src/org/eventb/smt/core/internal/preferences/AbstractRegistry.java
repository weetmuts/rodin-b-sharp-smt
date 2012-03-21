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

import static org.eventb.smt.core.preferences.ExtensionLoadingException.makeNoExtensionException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.preferences.ExtensionLoadingException;
import org.eventb.smt.core.preferences.IRegistry;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractRegistry<T extends AbstractDescriptor> implements
		IRegistry<Object> {
	public abstract Map<String, T> getRegistry();

	public Object getContentInstance(String id) {
		return getRegistry().get(id).getInstance();
	}

	public synchronized boolean isRegistered(String id)
			throws ExtensionLoadingException, InvalidRegistryObjectException {
		if (getRegistry() == null) {
			loadRegistry();
		}
		return getRegistry().containsKey(id);
	}

	/**
	 * Initializes the registry using extensions to the extension point.
	 * 
	 * @throws ExtensionLoadingException
	 */
	protected abstract void loadRegistry() throws ExtensionLoadingException,
			InvalidRegistryObjectException;

	/**
	 * @param point
	 * @throws ExtensionLoadingException
	 */
	protected static void checkPoint(final IExtensionPoint point,
			final String id) throws ExtensionLoadingException {
		if (point == null)
			throw makeNoExtensionException(id);
	}

	@Override
	public Map<String, Object> getMap() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		final Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (getRegistry() == null) {
			loadRegistry();
		}
		for (final Map.Entry<String, T> entry : getRegistry().entrySet()) {
			final Object instance = entry.getValue().getInstance();
			if (instance != null) {
				map.put(entry.getKey(), instance);
			}
		}
		return map;
	}
}
