/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import org.eventb.smt.ast.theories.SMTLogic;

public class SMTSignatureV2_0PP extends SMTSignatureV2_0 {
	/**
	 * Construts a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignatureV2_0PP(final SMTLogic logic) {
		super(logic);
	}

	@Override
	public SMTLogic.SMTLogicPP getLogic() {
		return (SMTLogic.SMTLogicPP) logic;
	}
}
