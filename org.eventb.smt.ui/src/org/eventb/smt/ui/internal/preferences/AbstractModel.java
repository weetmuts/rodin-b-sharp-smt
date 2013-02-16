/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;

/**
 * Abstract model backing a preference table in a preference page. This class
 * serves as a bridge between the UI table viewers and the core SMT plug-in
 * preferences.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractModel<U, T extends AbstractElement<U>> {

	public final List<T> elements = new ArrayList<T>();
	private TableViewer viewer;

	protected final void addElements(U[] coreElements) {
		for (final U coreElement : coreElements) {
			final T element = convert(coreElement);
			elements.add(element);
		}
	}

	protected abstract T convert(U coreElement);

	public final void setViewer(TableViewer tableViewer) {
		viewer = tableViewer;
	}

	/**
	 * Load elements from the core plug-in.
	 */
	public final void load() {
		doLoad();
		viewer.refresh(true); // Enablement might have changed
	}

	protected abstract void doLoad();

	/**
	 * Remove all user-defined elements.
	 */
	public final void loadDefault() {
		final Iterator<T> iter = elements.iterator();
		while (iter.hasNext()) {
			final T desc = iter.next();
			if (desc.editable) {
				iter.remove();
			}
		}
		doLoadDefaults();
		viewer.refresh(false);
	}

	protected abstract void doLoadDefaults();

	/**
	 * Returns a freshly created new element, but do not store it in this model
	 * yet. One needs to call <code>update</code> to enter it into this model.
	 *
	 * @return a freshly created new element
	 */
	public abstract T newElement();

	/**
	 * Returns a freshly created copy of the given element, but do not store it
	 * in this model yet. One needs to call <code>update</code> to enter it into
	 * this model.
	 *
	 * @return a freshly created copy of the given element
	 */
	@SuppressWarnings("unchecked")
	public final T clone(T element) {
		return (T) element.clone();
	}

	/**
	 * Removes the given element from this model.
	 *
	 * @param element
	 *            element to remove from this model
	 */
	public final void remove(T element) {
		elements.remove(element);
		viewer.refresh(false);
	}

	/**
	 * Updates the given element in this model. Adds the element to this model
	 * if it was not yet present.
	 *
	 * @param element
	 *            element to update in this model
	 */
	public final void update(T element) {
		if (elements.contains(element)) {
			viewer.update(element, (String[]) viewer.getColumnProperties());
		} else {
			elements.add(element);
			viewer.refresh(false);
		}
	}

	/**
	 * Returns all element names present in this model, except the name of the
	 * given element.
	 *
	 * @param elementToIgnore
	 *            some element, maybe <code>null</code>
	 */
	public final Set<String> usedNames(T elementToIgnore) {
		final Set<String> result = new HashSet<String>();
		for (final T element : elements) {
			if (element != elementToIgnore) {
				result.add(element.name);
			}
		}
		return result;
	}

	/**
	 * Store all solvers into the core plug-in.
	 */
	public final void store() {
		final U[] coreElements = newArray(elements.size());
		int count = 0;
		for (final T element : elements) {
			coreElements[count++] = element.toCore();
		}
		doStore(coreElements);
	}

	protected abstract U[] newArray(int length);

	protected abstract void doStore(U[] coreElements);

}