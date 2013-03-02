/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;

import org.eventb.smt.core.prefs.IConfigDescriptor;
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
public class ConfigElement extends AbstractElement<IConfigDescriptor> {

	String solverName;
	String args;
	TranslationApproach approach;
	SMTLIBVersion version;
	boolean enabled;

	public ConfigElement() {
		super(true, "");
		this.solverName = "";
		this.args = "";
		this.approach = USING_PP;
		this.version = V2_0;
		this.enabled = true;
	}

	public ConfigElement(IConfigDescriptor origin) {
		super(!origin.isBundled(), origin.getName());
		this.solverName = origin.getSolverName();
		this.args = origin.getArgs();
		this.approach = origin.getTranslationApproach();
		this.version = origin.getSmtlibVersion();
		this.enabled = origin.isEnabled();
	}

	@Override
	public IConfigDescriptor toCore() {
		return newConfigDescriptor(name, solverName, args, approach, version,
				enabled);
	}

}
