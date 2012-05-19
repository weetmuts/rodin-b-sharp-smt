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

/**
 * Common implementation for model elements used in table viewers.
 *
 * @author Laurent Voisin
 *
 * @param <T>
 *            type of the corresponding object in the core plug-in
 */
public abstract class AbstractElement<T> implements Cloneable {

	public final boolean editable;
	public String id;
	public String name;

	public AbstractElement(boolean editable, String id, String name) {
		this.editable = editable;
		this.id = id;
		this.name = name;
	}

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// Cannot happen as we implement Cloneable
			assert (false);
			return null;
		}
	}

	public abstract T toCore();

}