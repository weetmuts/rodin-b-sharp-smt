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

import static org.eventb.smt.core.SolverKind.UNKNOWN;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.SolverKind;
import org.eventb.smt.ui.internal.preferences.AbstractElement;

/**
 * Model element describing a solver element in the SMT solver table.
 *
 * @see SolverModel
 * @author Laurent Voisin
 */
public class SolverElement extends AbstractElement<ISolverDescriptor> {

	SolverKind kind;
	IPath path;

	public SolverElement() {
		super(true, "");
		this.kind = UNKNOWN;
		this.path = new Path("");
	}

	public SolverElement(ISolverDescriptor origin) {
		super(!origin.isBundled(), origin.getName());
		this.kind = origin.getKind();
		this.path = origin.getPath();
	}

	@Override
	public ISolverDescriptor toCore() {
		return SMTCore.newSolverDescriptor(name, kind, path);
	}

}
