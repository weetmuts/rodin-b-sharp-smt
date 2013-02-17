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

import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.junit.Assert.assertTrue;

import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Acceptance tests for the SMT auto-tactic.
 * 
 * @author Laurent Voisin
 */
public class SMTAutoTacticTests extends TacticTests {

	private static final IConfigDescriptor[] originalConfigs = SMTCore
			.getConfigurations();

	/*
	 * Puts back the original list of configurations after these tests.
	 */
	@AfterClass
	public static void resetAllConfigurations() {
		SMTCore.setConfigurations(originalConfigs);
	}

	/**
	 * Ensures that the SMT auto-tactic can discharge a simple sequent.
	 */
	@Test
	public void smtAutoCanDischarge() {
		enableAllConfigurations(true);
		assertDischarges(SMTCore.smtAutoTactic);
	}

	/**
	 * Ensures that the SMT auto-tactic can fail if no solver is enabled.
	 */
	@Test
	public void smtAutoCanFail() {
		enableAllConfigurations(false);
		assertFails(SMTCore.smtAutoTactic);
	}

	/*
	 * Change the enablement of all known SMT configurations to the given value.
	 */
	public static void enableAllConfigurations(boolean enabled) {
		final int length = originalConfigs.length;
		assertTrue("No known SMT configuration !", length != 0);
		final IConfigDescriptor[] newConfigs = new IConfigDescriptor[length];
		for (int i = 0; i < length; i++) {
			newConfigs[i] = makeEnabled(originalConfigs[i], enabled);
		}
		SMTCore.setConfigurations(newConfigs);
	}

	/*
	 * Returns an enabled copy of the given descriptor.
	 */
	private static IConfigDescriptor makeEnabled(IConfigDescriptor desc,
			boolean enabled) {
		return newConfigDescriptor(desc.getName(), desc.getSolverName(),
				desc.getArgs(), desc.getTranslationApproach(),
				desc.getSmtlibVersion(), enabled);
	}

}
