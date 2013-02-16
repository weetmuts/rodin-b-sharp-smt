/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.tests.tactics;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.smt.core.SMTCore.getTacticDescriptor;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.junit.Test;

/**
 * Acceptance tests about dynamic registration of tactic descriptors for running
 * SMT configurations.
 * 
 * @author Laurent Voisin
 */
public class SMTDynTacticProviderTests {

	/**
	 * Ensures that every known configuration has a tactic descriptor
	 * dynamically registered.
	 */
	@Test
	public void allConfigsAreDynamicallyRegistered() {
		assertAreDynamicallyRegistered(SMTCore.getConfigurations());
	}

	private void assertAreDynamicallyRegistered(IConfigDescriptor[] configs) {
		for (IConfigDescriptor config : configs) {
			final ITacticDescriptor desc = getTacticDescriptor(config.getName());
			assertIsDynamicallyRegistered(desc);
		}
	}

	private void assertIsDynamicallyRegistered(ITacticDescriptor desc) {
		final List<String> dynamicTacticIds = getDynamicTacticIds();
		final String tacticID = desc.getTacticID();
		assertTrue(dynamicTacticIds.contains(tacticID));
	}

	/**
	 * Returns the list of the ids of the tactics that have been dynamically
	 * registered with the auto-tactic registry.
	 */
	private List<String> getDynamicTacticIds() {
		final IAutoTacticRegistry registry = getAutoTacticRegistry();
		final ITacticDescriptor[] descs = registry.getDynTactics();
		final List<String> result = new ArrayList<String>(descs.length);
		for (ITacticDescriptor desc : descs) {
			result.add(desc.getTacticID());
		}
		return result;
	}

}
