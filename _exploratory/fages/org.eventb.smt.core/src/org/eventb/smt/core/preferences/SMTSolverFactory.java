/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.internal.preferences.SMTSolver;
import org.eventb.smt.core.provers.SolverKind;

/**
 * This factory provides method to construct SMT-solvers instances.
 * 
 * @author Systerel (yguyot)
 */
public class SMTSolverFactory {
	public static final int ID_COL = SMTSolver.ID_COL;
	public static final int NAME_COL = SMTSolver.NAME_COL;
	public static final int KIND_COL = SMTSolver.KIND_COL;
	public static final int PATH_COL = SMTSolver.PATH_COL;
	public static final int EDITABLE_COL = SMTSolver.EDITABLE_COL;

	public static final ISMTSolver newSolver(String id) {
		return new SMTSolver(id);
	}

	public static final ISMTSolver newSolver(final String id,
			final String name, final SolverKind kind, final IPath path) {
		return new SMTSolver(id, name, kind, path);
	}
}
