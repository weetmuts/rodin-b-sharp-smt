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
public enum SMTConnective {
	NOT("not"), //
	IMPLIES("implies"), //
	ITE("if_then_else"), //
	AND("and"), //
	OR("or"), //
	XOR("xor"), //
	IFF("iff");

	private String symbol;

	SMTConnective(final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return this.symbol;
	}
}
