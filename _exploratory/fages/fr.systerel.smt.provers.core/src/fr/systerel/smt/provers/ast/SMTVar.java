/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * @author guyot
 *
 */
public class SMTVar extends SMTTerm {
	final SMTVarSymbol symbol;

	public SMTVar(final SMTVarSymbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(SMTFactory.QVAR);
		builder.append(this.symbol.getName());
	}
}
