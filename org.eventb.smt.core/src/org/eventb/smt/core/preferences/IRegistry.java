/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

import java.util.Set;

/**
 * This interface represents a registry of extensions for the SMT-solvers
 * Plug-in. It provides methods to access the available extensions. Currently,
 * extensions are bundled solvers and bundled configurations.
 * 
 * @author Systerel (yguyot)
 */
public interface IRegistry<T> {
	/**
	 * Returns the set of registered extensions IDs.
	 * 
	 * @return the set of registered extensions IDs
	 */
	public Set<String> getIDs();

	/**
	 * Returns the element associated with the given ID.
	 * 
	 * @param id
	 *            the ID of the element which must be returned
	 * @return the element associated with the given ID
	 */
	public T get(String id);
}
