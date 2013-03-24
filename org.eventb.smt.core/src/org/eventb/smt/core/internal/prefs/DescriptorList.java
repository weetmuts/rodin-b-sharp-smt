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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eventb.smt.core.IDescriptor;

/**
 * Common implementation for storing a list of descriptor while enforcing some
 * global invariant, thanks to the {@link #isValid(IDescriptor)} method.
 *
 * @param <T>
 *            the actual descriptor type
 *
 * @author Laurent Voisin
 */
public abstract class DescriptorList<T extends IDescriptor> implements
		Iterable<T> {

	private final List<T> descs;

	public DescriptorList() {
		this.descs = new ArrayList<T>();
	}

	public void addAll(T[] newDescs) {
		for (final T newDesc : newDescs) {
			add(newDesc);
		}
	}

	/*
	 * Adds all descriptors of the given list which name is not already present
	 * in this list.
	 */
	public void addIfNotPresent(DescriptorList<T> newDescs) {
		for (final T newDesc : newDescs) {
			final String name = newDesc.getName();
			if (get(name) == null) {
				add(newDesc);
			}
		}
	}

	public void addAll(Collection<T> newDescs) {
		for (final T newDesc : newDescs) {
			add(newDesc);
		}
	}

	public void add(T newDesc) {
		if (!isValid(newDesc)) {
			return;
		}
		descs.add(newDesc);
	}

	/**
	 * Tells whether the given descriptor can be added to this list. The test
	 * should call <code>smtError</code> to trace the error in the plug-in log.
	 *
	 * @param desc
	 *            some descriptor
	 * @return <code>true</code> iff the given descriptor is valid
	 */
	public abstract boolean isValid(T desc);

	public void clear() {
		descs.clear();
	}

	public T get(String name) {
		for (final T desc : descs) {
			if (name.equals(desc.getName())) {
				return desc;
			}
		}
		return null;
	}

	@Override
	public Iterator<T> iterator() {
		return descs.iterator();
	}

	public final T[] toArray() {
		return descs.toArray(newArray(descs.size()));
	}

	protected abstract T[] newArray(int length);

}