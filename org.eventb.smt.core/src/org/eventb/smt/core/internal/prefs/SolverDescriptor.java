/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.SolverKind;
import org.osgi.service.prefs.Preferences;

public class SolverDescriptor extends Descriptor implements ISolverDescriptor {

	private static final SolverKindSerializer KIND = new SolverKindSerializer(
			"kind"); //$NON-NLS-1$
	private static final PathSerializer PATH = new PathSerializer("path"); //$NON-NLS-1$

	private final SolverKind kind;
	private final IPath path;

	public SolverDescriptor(String name, boolean bundled, SolverKind kind,
			IPath path) {
		super(name, bundled);
		this.kind = kind;
		this.path = path;
	}

	public SolverDescriptor(Preferences node) {
		super(node);
		this.kind = KIND.load(node);
		this.path = PATH.load(node);
	}

	@Override
	public void serialize(Preferences node) {
		super.serialize(node);
		KIND.store(node, kind);
		PATH.store(node, path);
	}

	@Override
	public SolverKind getKind() {
		return kind;
	}

	@Override
	public IPath getPath() {
		return path;
	}

}
