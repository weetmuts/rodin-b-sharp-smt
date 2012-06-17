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

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;
import static org.eventb.smt.core.internal.preferences.Messages.BundledSolverRegistry_RegistrationError;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eventb.smt.core.internal.prefs.DescriptorList;
import org.eventb.smt.core.prefs.IDescriptor;

/**
 * Common protocol for a list of descriptors contributed by an extension point.
 *
 * @author Laurent Voisin
 */
public abstract class BundledDescriptorList<T extends IDescriptor> extends
		DescriptorList<T> {

	private final String pointId;

	public BundledDescriptorList(String pointId) {
		this.pointId = pointId;
		try {
			loadRegistry();
		} catch (Exception e) {
			logError(BundledSolverRegistry_RegistrationError, e);
		}
	}

	/**
	 * Populates the list from the extension point.
	 */
	protected void loadRegistry() {
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint point = xRegistry.getExtensionPoint(pointId);
		final IConfigurationElement[] elements = point
				.getConfigurationElements();
		for (IConfigurationElement element : elements) {
			try {
				loadElement(element);
			} catch (Exception e) {
				logError(BundledSolverRegistry_RegistrationError, e);
			}
		}
	}

	protected abstract void loadElement(IConfigurationElement element);

	@Override
	public final boolean isValid(T desc) {
		final String name = desc.getName();
		if (get(name) != null) {
			logError("Duplicate bundled name " + name + " ignored", null);
			return false;
		}
		return true;
	}

}
