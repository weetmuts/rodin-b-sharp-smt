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

import static org.eventb.smt.core.internal.preferences.Utils.encode;
import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.preferences.AbstractSMTSolver;
import org.eventb.smt.core.provers.SolverKind;

/**
 * This is a class to integrate an SMT-solver into the Rodin platform. This is
 * mainly done by indicating its path and kind.
 * 
 * @author Systerel (yguyot)
 */
public class SMTSolver extends AbstractSMTSolver {
	public static final boolean EDITABLE = true;
	public static final String SEPARATOR = "|"; //$NON-NLS-1$

	private static final IPath DEFAULT_SOLVER_PATH = new Path(""); //$NON-NLS-1$

	private final String id;
	private final String name;
	private final SolverKind kind;
	private final IPath path;
	private final boolean editable;

	public SMTSolver() {
		id = "";
		name = "";
		kind = UNKNOWN;
		path = new Path("");
		editable = true;
	}

	public SMTSolver(final String id, final String name, final SolverKind kind,
			final IPath path, final boolean editable) {
		this.id = id;
		this.name = name;
		this.kind = kind;
		this.path = path;
		this.editable = editable;
	}

	public SMTSolver(final String id, final String name, final SolverKind kind,
			final IPath path) {
		this(id, name, kind, path, EDITABLE);
	}

	public SMTSolver(final String id, final String name, final SolverKind kind,
			final boolean editable) {
		this(id, name, kind, DEFAULT_SOLVER_PATH, editable);
	}

	/**
	 * @return the id
	 */
	@Override
	public String getID() {
		return id;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the kind
	 */
	@Override
	public SolverKind getKind() {
		return kind;
	}

	/**
	 * @return the path
	 */
	@Override
	public IPath getPath() {
		return path;
	}

	/**
	 * @return true if the solver installation is editable
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(encode(id)).append(SEPARATOR);
		builder.append(encode(name)).append(SEPARATOR);
		builder.append(encode(kind.toString())).append(SEPARATOR);
		builder.append(encode(path.toOSString())).append(SEPARATOR);
		builder.append(editable);
	}
}
