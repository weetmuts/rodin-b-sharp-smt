/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

import org.eventb.smt.core.internal.ast.theories.Logic;

public class SMTSignatureV2_0PP extends SMTSignatureV2_0 {
	/**
	 * Constructs a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignatureV2_0PP(final Logic logic) {
		super(logic);
	}

	@Override
	public Logic.SMTLogicPP getLogic() {
		return (Logic.SMTLogicPP) logic;
	}
}
