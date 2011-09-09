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
import org.eventb.smt.translation.SMTLIBVersion;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * PP.
 * 
 */
public class SMTSignaturePP extends SMTSignature {
	public SMTSignaturePP(final SMTLogic.SMTLogicPP logic,
			final SMTLIBVersion smtlibVersion) {
		super(logic, smtlibVersion);
	}

	@Override
	public SMTLogic.SMTLogicPP getLogic() {
		return (SMTLogic.SMTLogicPP) logic;
	}
}
