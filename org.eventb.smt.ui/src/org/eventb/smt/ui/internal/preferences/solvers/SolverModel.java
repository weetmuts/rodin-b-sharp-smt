/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eventb.smt.core.SMTCore.getBundledSolvers;
import static org.eventb.smt.core.SMTCore.getUserSolvers;
import static org.eventb.smt.core.SMTCore.setUserSolvers;

import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.ui.internal.preferences.AbstractModel;
import org.eventb.smt.ui.internal.preferences.configurations.ConfigModel;

/**
 * Model backing the SMT solver table in the corresponding preference page.
 *
 * @author Laurent Voisin
 */
public class SolverModel extends
		AbstractModel<ISolverDescriptor, SolverElement> {

	// Is final in principle
	private ConfigModel configModel;

	public SolverModel() {
		super(getBundledSolvers());
	}

	@Override
	protected void doLoad() {
		addElements(getUserSolvers(), true);
	}

	@Override
	protected void doLoadDefaults() {
		// Nothing to do
	}

	@Override
	protected SolverElement convert(ISolverDescriptor origin, boolean editable) {
		return new SolverElement(origin, editable);
	}

	@Override
	public SolverElement newElement() {
		return new SolverElement();
	}

	@Override
	public void doStore(ISolverDescriptor[] solvers) {
		setUserSolvers(solvers);
	}

	@Override
	protected ISolverDescriptor[] newArray(int length) {
		return new ISolverDescriptor[length];
	}

	public void setConfigModel(ConfigModel configModel) {
		this.configModel = configModel;
	}

	public ConfigModel getConfigModel() {
		return configModel;
	}

}
