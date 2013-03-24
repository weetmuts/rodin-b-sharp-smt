/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.tests.tactics;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.smt.core.SMTCore.getTacticDescriptor;
import static org.eventb.smt.core.internal.prefs.ConfigPreferences.getBundledConfigs;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.CONFIG_NAME_LABEL;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.RESTRICTED_LABEL;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.TIMEOUT_DELAY_LABEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.smt.core.IConfigDescriptor;
import org.junit.Test;

/**
 * Acceptance tests about creation of parameterized tactics for running SMT
 * configurations.
 * 
 * @author Laurent Voisin
 */
public class SMTTacticDescriptorTests extends TacticTests {

	private static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	/**
	 * Ensures that creating a parameterized tactic does not register it with
	 * the auto-tactic registry.
	 */
	@Test
	public void tacticIsNotRegistered() {
		final ITacticDescriptor desc = getTacticDescriptor("foo");
		assertFalse(REGISTRY.isRegistered(desc.getTacticID()));
	}

	/**
	 * Ensures that the tactic parameters are the given configuration and
	 * default values for the other parameters.
	 */
	@Test
	public void correctParameters() {
		final String configName = "foo";
		final ITacticDescriptor desc = getTacticDescriptor(configName);
		assertTrue(desc instanceof IParamTacticDescriptor);
		final IParamTacticDescriptor pDesc = (IParamTacticDescriptor) desc;
		final IParameterValuation valuation = pDesc.getValuation();
		assertEquals(configName, valuation.getString(CONFIG_NAME_LABEL));
		assertEquals(1000, valuation.getLong(TIMEOUT_DELAY_LABEL));
		assertEquals(true, valuation.getBoolean(RESTRICTED_LABEL));
	}

	/**
	 * Ensure that a tactic descriptor returned by the core plug-in can be used
	 * to discharge a sequent.
	 */
	@Test
	public void descriptorIsUsable() {
		assertDischarges(getTacticDescriptorForBundledConfig());
	}

	/*
	 * Returns a tactic descriptor for running an arbitrary bundled SMT
	 * configuration.
	 */
	private ITacticDescriptor getTacticDescriptorForBundledConfig() {
		final IConfigDescriptor[] configs = getBundledConfigs();
		assertTrue(configs.length != 0);
		final String configName = configs[0].getName();
		return getTacticDescriptor(configName);
	}

}
