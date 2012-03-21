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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractDescriptor {
	protected final IConfigurationElement configurationElement;
	protected String id;

	public AbstractDescriptor(final IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	public String getId() {
		return id;
	}

	public abstract void load();

	public abstract Object getInstance();
}
