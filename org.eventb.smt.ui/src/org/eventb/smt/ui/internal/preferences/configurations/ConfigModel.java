/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eventb.smt.core.SMTCore.getUserConfigs;
import static org.eventb.smt.core.SMTCore.setUserConfigs;

import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.ui.internal.preferences.AbstractModel;

/**
 * Model backing the SMT configurations table in the corresponding preference
 * page.
 *
 * @author Laurent Voisin
 */
public class ConfigModel extends AbstractModel<ISolverConfig, ConfigElement> {

	public ConfigModel() {
		super(SMTCore.getBundledConfigs());
	}

	@Override
	protected ConfigElement convert(ISolverConfig origin, boolean editable) {
		return new ConfigElement(origin, editable);
	}

	@Override
	protected void doLoad() {
		addElements(getUserConfigs(), true);
		// FIXME missing enabledness initialisation
	}

	@Override
	protected void doLoadDefaults() {
		for (final ConfigElement element : bundledElements()) {
			element.enabled = true;
		}
	}

	@Override
	public ConfigElement newElement() {
		return new ConfigElement();
	}

	@Override
	public void doStore(ISolverConfig[] coreElements) {
		setUserConfigs(coreElements);
		// FIXME missing persistence of enabledness
	}

	@Override
	protected ISolverConfig[] newArray(final int length) {
		return new ISolverConfig[length];
	}

}
