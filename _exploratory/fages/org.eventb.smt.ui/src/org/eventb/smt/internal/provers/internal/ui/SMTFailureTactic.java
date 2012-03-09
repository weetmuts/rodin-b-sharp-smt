/*******************************************************************************
 * Copyright (c) 2012 Systerel and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.internal.provers.internal.ui;

import static org.eventb.smt.internal.provers.core.Messages.SMTProversCore_NoSMTSolverSelected;
import static org.eventb.smt.internal.provers.core.Messages.SMTProversCore_NoSMTSolverSet;
import static org.eventb.smt.internal.provers.core.Messages.SMTProversCore_ProofTreeOriginError;
import static org.eventb.smt.internal.provers.core.Messages.SMTProversCore_SMTSolverConfigError;
import static org.eventb.smt.internal.provers.core.Messages.SMTProversCore_VeriTPathNotSet;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * Common tactics that always fail with some error message.
 * 
 * @author Yoann Guyot
 */
public enum SMTFailureTactic implements ITactic {

	SMT_SOLVER_CONFIG_ERROR(SMTProversCore_SMTSolverConfigError), //
	NO_SMT_SOLVER_SELECTED(SMTProversCore_NoSMTSolverSelected), //
	NO_SMT_SOLVER_SET(SMTProversCore_NoSMTSolverSet), //
	VERIT_PATH_NOT_SET(SMTProversCore_VeriTPathNotSet), //
	PROOF_TREE_ORIGIN_ERROR(SMTProversCore_ProofTreeOriginError), //
	;

	private final String message;

	private SMTFailureTactic(final String message) {
		this.message = message;
	}

	@Override
	public Object apply(final IProofTreeNode ptNode, final IProofMonitor pm) {
		return message;
	}

}