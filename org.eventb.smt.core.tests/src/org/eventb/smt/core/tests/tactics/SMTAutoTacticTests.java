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

import org.eventb.smt.core.SMTCore;
import org.junit.Test;

/**
 * Acceptance tests for the SMT auto-tactic.
 * 
 * @author Laurent Voisin
 */
public class SMTAutoTacticTests extends TacticTests {

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

}
