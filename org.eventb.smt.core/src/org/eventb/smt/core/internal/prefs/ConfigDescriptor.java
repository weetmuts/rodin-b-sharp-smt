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

import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.osgi.service.prefs.Preferences;

public class ConfigDescriptor extends Descriptor implements IConfigDescriptor {

	private static final StringSerializer SOLVER_NAME = new StringSerializer(
			"solver"); //$NON-NLS-1$
	private static final StringSerializer ARGS = new StringSerializer("args"); //$NON-NLS-1$
	private static final TranslationApproachSerializer APPROACH = new TranslationApproachSerializer(
			"approach"); //$NON-NLS-1$
	private static final SMTLIBVersionSerializer VERSION = new SMTLIBVersionSerializer(
			"version"); //$NON-NLS-1$
	private static final BooleanSerializer ENABLED = new BooleanSerializer(
			"enabled"); //$NON-NLS-1$

	private final String solverName;
	private final String args;
	private final TranslationApproach approach;
	private final SMTLIBVersion version;
	
	// This field is the only one that can mutate
	private boolean enabled;

	public ConfigDescriptor(String name, boolean bundled, String solverName,
			String args, TranslationApproach approach, SMTLIBVersion version,
			boolean enabled) {
		super(name, bundled);
		this.solverName = solverName;
		this.args = args;
		this.approach = approach;
		this.version = version;
		this.enabled = enabled;
	}

	public ConfigDescriptor(Preferences node) {
		super(node);
		this.solverName = SOLVER_NAME.load(node);
		this.args = ARGS.load(node);
		this.approach = APPROACH.load(node);
		this.version = VERSION.load(node);
		this.enabled = ENABLED.load(node);
	}

	@Override
	public void serialize(Preferences node) {
		super.serialize(node);
		SOLVER_NAME.store(node, solverName);
		ARGS.store(node, args);
		APPROACH.store(node, approach);
		VERSION.store(node, version);
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
		return approach;
	}

	@Override
	public SMTLIBVersion getSmtlibVersion() {
		return version;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
