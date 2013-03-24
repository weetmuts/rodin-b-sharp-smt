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

/**
 * This is the SMTSignatureV1_2 to be used by the SMT translation process
 * through PP.
 * 
 */
public class SMTSignatureV1_2PP extends SMTSignatureV1_2 {
	public SMTSignatureV1_2PP(final Logic.SMTLogicPP logic) {
		super(logic);
	}

	@Override
	public Logic.SMTLogicPP getLogic() {
		return (Logic.SMTLogicPP) logic;
	}
}
