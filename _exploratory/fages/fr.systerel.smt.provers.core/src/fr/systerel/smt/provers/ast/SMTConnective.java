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

import java.util.ArrayList;
import java.util.List;

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

	public static final List<String> getConnectiveSymbols() {
		final SMTConnective[] smtConnectives = SMTConnective.values();
		final List<String> connectives = new ArrayList<String>(
				smtConnectives.length);
		for (final SMTConnective connective : smtConnectives) {
			connectives.add(connective.toString());
		}
		return connectives;
	}
}
