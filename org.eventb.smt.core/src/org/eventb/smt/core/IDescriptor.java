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
package org.eventb.smt.core;

/**
 * Common protocol of solver and configuration descriptors. Every descriptor has
 * a name which is provided by a plug-in through an extension point or directly
 * by the end user.
 * 
 * @author Laurent Voisin
 * 
 * @noextend This interface is not intended to be subclassed by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDescriptor {

	/**
	 * Returns the name of this descriptor.
	 * 
	 * @return the name of this descriptor
	 */
	String getName();

	/**
	 * Returns whether this descriptor corresponds to a bundled object. Bundled
	 * objects are mostly immutable and the contents of most of their attributes
	 * are defined by extensions and cannot be changed by clients.
	 * 
	 * @return <code>true</code> iff the described object is bundled
	 */
	boolean isBundled();

}