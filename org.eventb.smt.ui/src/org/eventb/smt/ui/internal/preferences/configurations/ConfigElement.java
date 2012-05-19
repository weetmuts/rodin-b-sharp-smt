/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.SolverConfigFactory;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.eventb.smt.ui.internal.preferences.AbstractElement;

/**
 * Model element describing a configuration element in the SMT configurations
 * table.
 *
 * @see ConfigModel
 * @author Laurent Voisin
 */
public class ConfigElement extends AbstractElement<ISolverConfig> {

	String solverId;
	String args;
	TranslationApproach approach;
	SMTLIBVersion version;
	boolean enabled;

	public ConfigElement() {
		super(true, "", "");
		this.solverId = "";
		this.args = "";
		this.approach = TranslationApproach.USING_PP;
		this.version = SMTLIBVersion.V2_0;
		this.enabled = true;
	}

	public ConfigElement(ISolverConfig origin, boolean editable) {
		super(editable, origin.getID(), origin.getName());
		this.solverId = origin.getSolverId();
		this.args = origin.getArgs();
		this.approach = origin.getTranslationApproach();
		this.version = origin.getSmtlibVersion();
	}

	@Override
	public ISolverConfig toCore() {
		return SolverConfigFactory.newConfig(id, name, solverId, args,
				approach, version);
	}

}
