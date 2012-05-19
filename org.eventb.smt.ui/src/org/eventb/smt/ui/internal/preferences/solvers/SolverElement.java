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

import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.SMTSolverFactory;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.ui.internal.preferences.AbstractElement;

/**
 * Model element describing a solver element in the SMT solver table.
 *
 * @see SolverModel
 * @author Laurent Voisin
 */
public class SolverElement extends AbstractElement<ISMTSolver> {

	SolverKind kind;
	IPath path;

	public SolverElement() {
		super(true, "", ""); // FIXME wrong
		this.kind = UNKNOWN;
		this.path = new Path("");
	}

	public SolverElement(ISMTSolver origin, boolean editable) {
		super(editable, origin.getID(), origin.getName());
		this.kind = origin.getKind();
		this.path = origin.getPath();
	}

	@Override
	public ISMTSolver toCore() {
		return SMTSolverFactory.newSolver(id, name, kind, path);
	}

}
