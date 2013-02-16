/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.tactics;

import static org.eventb.smt.core.SMTCore.getBundledConfigs;
import static org.eventb.smt.core.SMTCore.getUserConfigs;
import static org.eventb.smt.core.SMTCore.getTacticDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IDynTacticProvider;
import org.eventb.smt.core.prefs.IConfigDescriptor;

/**
 * Provides one tactic descriptor per registered SMT configuration.
 * 
 * @author Laurent Voisin
 */
public class SMTDynTacticProvider implements IDynTacticProvider {

	@Override
	public Collection<ITacticDescriptor> getDynTactics() {
		final List<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>();
		addConfigDescriptors(result, getBundledConfigs());
		addConfigDescriptors(result, getUserConfigs());
		return result;
	}

	private void addConfigDescriptors(List<ITacticDescriptor> result,
			IConfigDescriptor[] configs) {
		for (IConfigDescriptor config : configs) {
			result.add(getTacticDescriptor(config.getName()));
		}
	}

}
