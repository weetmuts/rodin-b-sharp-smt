/*******************************************************************************
 * Copyright (c) 2010, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.core.internal.provers.ExternalSMT.REASONER_ID;

import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.junit.Test;

/**
 * Acceptance tests for the SMT reasoner. We use the bundled configurations to
 * perform the tests.
 * 
 * @author Yoann Guyot
 */
public class SMTPPReasonerTests extends AbstractReasonerTests {

	public static final String BUNDLED_VERIT_PP_SMT2_ID = "veriT";
	public static final String BUNDLED_CVC3_PP_SMT2_ID = "CVC3";
	public static final String BUNDLED_CVC4_PP_SMT2_ID = "CVC4";
	public static final String BUNDLED_Z3_PP_SMT2_ID = "Z3";

	private static final boolean DEFAULT_RESTRICTED_VALUE = true;
	private static final long DEFAULT_TIMEOUT_DELAY = 1000;

	private static final SMTInput VERIT_INPUT = new SMTInput(
			BUNDLED_VERIT_PP_SMT2_ID, DEFAULT_RESTRICTED_VALUE,
			DEFAULT_TIMEOUT_DELAY);

	private static final SMTInput CVC3_INPUT = new SMTInput(
			BUNDLED_CVC3_PP_SMT2_ID, DEFAULT_RESTRICTED_VALUE,
			DEFAULT_TIMEOUT_DELAY);

	private static final SMTInput CVC4_INPUT = new SMTInput(
			BUNDLED_CVC4_PP_SMT2_ID, DEFAULT_RESTRICTED_VALUE,
			DEFAULT_TIMEOUT_DELAY);

	private static final SMTInput Z3_INPUT = new SMTInput(
			BUNDLED_Z3_PP_SMT2_ID, DEFAULT_RESTRICTED_VALUE,
			DEFAULT_TIMEOUT_DELAY);

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Test
	public void testSuccess() throws Exception {
		assertReasonerSuccess(" x < y ;; y < z |- x < z ", VERIT_INPUT);
		assertReasonerSuccess(" x < y ;; z = x + y ;; y < z |- x < z ",	VERIT_INPUT);
		assertReasonerSuccess(" x < y ;; y < z |- x < z ", CVC3_INPUT);
		assertReasonerSuccess(" x < y ;; y < z |- x < z ", CVC4_INPUT);
		assertReasonerSuccess(" x < y ;; y < z |- x < z ", Z3_INPUT);
	}

	@Test
	public void testFailure() throws Exception {
		assertReasonerFailure(" x = 1 |- x = 2", VERIT_INPUT, "Failed");
		assertReasonerFailure(" x = 1 |- x = 2", CVC3_INPUT, "Failed");
		assertReasonerFailure(" x = 1 |- x = 2", CVC4_INPUT, "Failed");
		assertReasonerFailure(" x = 1 |- x = 2", Z3_INPUT, "Failed");
	}
}
