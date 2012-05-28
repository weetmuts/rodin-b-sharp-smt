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

import static org.eventb.smt.core.SMTCore.getUserConfigs2;
import static org.eventb.smt.core.SMTCore.setUserConfigs2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.ui.internal.preferences.AbstractModel;
import org.eventb.smt.ui.internal.preferences.solvers.SolverElement;
import org.eventb.smt.ui.internal.preferences.solvers.SolverModel;

/**
 * Model backing the SMT configurations table in the corresponding preference
 * page.
 *
 * @author Laurent Voisin
 */
public class ConfigModel extends
		AbstractModel<IConfigDescriptor, ConfigElement> {

	private final SolverModel solverModel;

	public ConfigModel(SolverModel solverModel) {
		super(SMTCore.getBundledConfigs2());
		this.solverModel = solverModel;
	}

	@Override
	protected ConfigElement convert(IConfigDescriptor origin, boolean editable) {
		return new ConfigElement(origin, editable);
	}

	@Override
	protected void doLoad() {
		addElements(getUserConfigs2(), true);
		EnablementStore.load(elements);
	}

	@Override
	protected void doLoadDefaults() {
		EnablementStore.setToDefault();
	}

	@Override
	public ConfigElement newElement() {
		return new ConfigElement();
	}

	@Override
	public void doStore(IConfigDescriptor[] coreElements) {
		setUserConfigs2(coreElements);
		EnablementStore.store(elements);
	}

	@Override
	protected IConfigDescriptor[] newArray(final int length) {
		return new IConfigDescriptor[length];
	}

	public List<SolverElement> getSolverElements() {
		return solverModel.elements;
	}

	public Set<ConfigElement> using(SolverElement solver) {
		final Set<ConfigElement> result = new HashSet<ConfigElement>();
		for (final ConfigElement config : elements) {
			if (solver.name.equals(config.solverName)) {
				result.add(config);
			}
		}
		return result;
	}

}
