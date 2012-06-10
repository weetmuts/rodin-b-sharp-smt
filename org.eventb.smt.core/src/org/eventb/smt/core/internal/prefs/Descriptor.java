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

import org.osgi.service.prefs.Preferences;

/**
 * Common implementation of a solver or configuration descriptor. Contains just
 * a name.
 *
 * @author Laurent Voisin
 */
public abstract class Descriptor {

	private static final StringSerializer NAME = new StringSerializer("name"); //$NON-NLS-1$

	private final String name;

	public Descriptor(String name) {
		this.name = name;
	}

	public Descriptor(Preferences node) {
		this.name = NAME.load(node);
	}

	public void serialize(Preferences node) {
		NAME.store(node, name);
	}

	public String getName() {
		return name;
	}

}
