/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents SMT Connectives.
 * 
 * @author Yoann Guyot
 * 
 */
public enum SMTConnective {
	NOT("not"), //
	IMPLIES("=>"), //
	ITE("ite"), //
	AND("and"), //
	OR("or"), //
	XOR("xor"), //
	IFF("=");

	/**
	 * The symbol of the connective
	 */
	private String symbolV2_0;

	/**
	 * Constructs a new SMT connective with a symbol.
	 */
	SMTConnective(final String symbolV2_0) {
		this.symbolV2_0 = symbolV2_0;
	}

	@Override
	public String toString() {
		return symbolV2_0;
	}

	/**
	 * Retrieves all the SMT connective symbols.
	 * 
	 * @return the list with all the connective symbols.
	 */
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
