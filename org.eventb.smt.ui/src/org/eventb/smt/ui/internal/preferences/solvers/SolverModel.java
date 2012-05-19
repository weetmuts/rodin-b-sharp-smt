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

import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.ui.internal.preferences.AbstractModel;

/**
 * Model backing the SMT solver table in the corresponding preference page.
 *
 * @author Laurent Voisin
 */
public class SolverModel extends AbstractModel<ISMTSolver, SolverElement> {

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
	protected SolverElement convert(ISMTSolver origin, boolean editable) {
		return new SolverElement(origin, editable);
	}

	@Override
	public SolverElement newElement() {
		return new SolverElement();
	}

	@Override
	public void doStore(ISMTSolver[] solvers) {
		setUserSolvers(solvers);
	}

	@Override
	protected ISMTSolver[] newArray(int length) {
		return new ISMTSolver[length];
	}

}
