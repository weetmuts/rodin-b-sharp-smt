/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.acceptance;

import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.smt.core.SMTCore.getSMTConfiguration;
import static org.eventb.smt.core.internal.provers.ExternalSMT.REASONER_ID;
import static org.eventb.smt.tests.CommonSolverRunTests.BUNDLED_CVC3_PP_SMT2_ID;
import static org.eventb.smt.tests.CommonSolverRunTests.BUNDLED_VERIT_PP_SMT2_ID;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.eventb.smt.core.provers.ISMTConfiguration;

public class SMTPPReasonerTests extends AbstractReasonerTests {

	private static final boolean DEFAULT_RESTRICTED_VALUE = true;
	private static final long DEFAULT_TIMEOUT_DELAY = 1000;

	final ISMTConfiguration veriTConfig = getSMTConfiguration(BUNDLED_VERIT_PP_SMT2_ID);
	final ISMTConfiguration cvc3Config = getSMTConfiguration(BUNDLED_CVC3_PP_SMT2_ID);

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(
						genSeq(" x < y ;; y < z |- x < z "), new SMTInput(
								DEFAULT_RESTRICTED_VALUE,
								DEFAULT_TIMEOUT_DELAY, veriTConfig)), //
				new SuccessfullReasonerApplication(
						genSeq(" x < y ;; z = x + y ;; y < z |- x < z "),
						new SMTInput(DEFAULT_RESTRICTED_VALUE,
								DEFAULT_TIMEOUT_DELAY, veriTConfig)), //
				new SuccessfullReasonerApplication(
						genSeq(" x < y ;; y < z |- x < z "), new SMTInput(
								DEFAULT_RESTRICTED_VALUE,
								DEFAULT_TIMEOUT_DELAY, cvc3Config)) };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				new UnsuccessfullReasonerApplication(genSeq(" x = 1 |- x = 2"),
						new SMTInput(DEFAULT_RESTRICTED_VALUE,
								DEFAULT_TIMEOUT_DELAY, veriTConfig), "Failed"),
				new UnsuccessfullReasonerApplication(genSeq(" x = 1 |- x = 2"),
						new SMTInput(DEFAULT_RESTRICTED_VALUE,
								DEFAULT_TIMEOUT_DELAY, cvc3Config), "Failed") };
	}
}
