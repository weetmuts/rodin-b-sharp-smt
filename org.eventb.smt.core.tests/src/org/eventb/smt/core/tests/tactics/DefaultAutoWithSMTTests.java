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

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.smt.core.SMTCore;
import org.junit.Test;

/**
 * Acceptance tests for the default auto with SMT tactic descriptor.
 * 
 * @author Laurent Voisin
 */
public class DefaultAutoWithSMTTests extends TacticTests {

	private static final ITacticDescriptor DEFAULT_AUTO_WITH_SMT = SMTCore
			.getDefaultAutoWithSMT();

	/**
	 * Ensures that the SMT solvers are run within a lasso.
	 */
	@Test
	public void canDischargeAfterLasso() {
		enableAllConfigurations(true);
		assertDischarges(DEFAULT_AUTO_WITH_SMT, "1 < x ;H; ;S; |- 0 < x");
	}

}
