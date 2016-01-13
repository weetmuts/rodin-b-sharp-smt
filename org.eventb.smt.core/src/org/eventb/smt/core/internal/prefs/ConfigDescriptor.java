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
package org.eventb.smt.core.internal.prefs;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.TranslationApproach.USING_PP;

import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.TranslationApproach;
import org.osgi.service.prefs.Preferences;

@SuppressWarnings("deprecation")
public class ConfigDescriptor extends Descriptor implements IConfigDescriptor {

	private static final StringSerializer SOLVER_NAME = new StringSerializer(
			"solver"); //$NON-NLS-1$
	private static final StringSerializer ARGS = new StringSerializer("args"); //$NON-NLS-1$
	private static final BooleanSerializer ENABLED = new BooleanSerializer(
			"enabled"); //$NON-NLS-1$

	private final String solverName;
	private final String args;
	
	// This field is the only one that can mutate
	private boolean enabled;

	public ConfigDescriptor(String name, boolean bundled, String solverName, String args, boolean enabled) {
		super(name, bundled);
		this.solverName = solverName;
		this.args = args;
		this.enabled = enabled;
	}

	public ConfigDescriptor(Preferences node) {
		super(node);
		this.solverName = SOLVER_NAME.load(node);
		this.args = ARGS.load(node);
		this.enabled = ENABLED.load(node);
	}

	@Override
	public void serialize(Preferences node) {
		super.serialize(node);
		SOLVER_NAME.store(node, solverName);
		ARGS.store(node, args);
		ENABLED.store(node, enabled);
	}

	@Override
	public String getSolverName() {
		return solverName;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public TranslationApproach getTranslationApproach() {
		return USING_PP;
	}

	@Override
	public SMTLIBVersion getSmtlibVersion() {
		return V2_0;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	protected void toString(StringBuilder sb) {
		toStringSep(sb);
		toStringQuoted(sb, solverName);
		toStringSep(sb);
		toStringQuoted(sb, args);
		toStringSep(sb);
		sb.append(enabled ? "enabled" : "disabled");
	}

}
