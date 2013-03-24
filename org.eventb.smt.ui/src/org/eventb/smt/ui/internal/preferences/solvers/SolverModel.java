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
package org.eventb.smt.ui.internal.preferences.solvers;

import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SMTCore;
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
		super();
	}

	@Override
	protected void doLoad() {
		addElements(SMTCore.getSolvers());
	}

	@Override
	protected void doLoadDefaults() {
		// Nothing to do
	}

	@Override
	protected SolverElement convert(ISolverDescriptor origin) {
		return new SolverElement(origin);
	}

	@Override
	public SolverElement newElement() {
		return new SolverElement();
	}

	@Override
	public void doStore(ISolverDescriptor[] solvers) {
		SMTCore.setSolvers(solvers);
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
