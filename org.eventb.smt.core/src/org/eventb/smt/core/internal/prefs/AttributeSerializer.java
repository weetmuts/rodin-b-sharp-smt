/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import org.osgi.service.prefs.Preferences;

/**
 * Common implementation for serializing a descriptor attribute to and from a
 * preference node.
 *
 * @author Laurent Voisin
 */
public abstract class AttributeSerializer<T> {

	private final String key;

	public AttributeSerializer(String key) {
		this.key = key;
	}

	/**
	 * Stores the given attribute value into the given preference node.
	 *
	 * @param node
	 *            some preference node
	 * @param value
	 *            some attribute value
	 */
	public void store(Preferences node, T value) {
		node.put(key, serialize(value));
	}

	protected abstract String serialize(T value);

	/**
	 * Loads the attribute value stored in the given preference node and returns
	 * it.
	 *
	 * @param node
	 *            some preference node
	 * @return the value stored in the given node
	 * @throws IllegalArgumentException
	 *             if the preference is absent or ill-formed
	 */
	public T load(Preferences node) throws IllegalArgumentException {
		final String image = node.get(key, null);
		if (image == null) {
			throw new IllegalArgumentException("Missing preference "
					+ node.absolutePath() + "/" + key);
		}
		return deserialize(image);
	}

	protected abstract T deserialize(String image)
			throws IllegalArgumentException;

}
