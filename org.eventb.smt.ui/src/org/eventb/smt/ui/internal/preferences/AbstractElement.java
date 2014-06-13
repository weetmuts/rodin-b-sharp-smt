/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
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
public abstract class AbstractElement<T> {

	public final boolean editable;
	public String name;

	public AbstractElement(boolean editable, String name) {
		this.editable = editable;
		this.name = name;
	}

	/**
	 * Duplicates this element into an editable copy.
	 * 
	 * @return an editable copy of this element
	 */
	public abstract AbstractElement<T> duplicate();
	
	public abstract T toCore();

}