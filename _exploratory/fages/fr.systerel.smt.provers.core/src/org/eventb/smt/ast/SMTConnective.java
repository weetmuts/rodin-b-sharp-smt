/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.translation.SMTLIBVersion.V1_2;

import java.util.ArrayList;
import java.util.List;

import org.eventb.smt.translation.SMTLIBVersion;

/**
 * This class represents SMT Connectives.
 * 
 * @author guyot
 * 
 */
public enum SMTConnective {
	NOT("not", "not"), //
	IMPLIES("implies", "=>"), //
	ITE("if_then_else", "ite"), //
	AND("and", "and"), //
	OR("or", "or"), //
	XOR("xor", "xor"), //
	IFF("iff", "=");

	/**
	 * The symbol of the connective
	 */
	private String symbolV1_2;
	private String symbolV2_0;

	/**
	 * Constructs a new SMT connective with a symbol.
	 */
	SMTConnective(final String symbolV1_2, final String symbolV2_0) {
		this.symbolV1_2 = symbolV1_2;
		this.symbolV2_0 = symbolV2_0;
	}

	public String toString(final SMTLIBVersion smtlibVersion) {
		if (smtlibVersion.equals(V1_2)) {
			return symbolV1_2;
		} else {
			/**
			 * smtlibVersion.equals(V2_0)
			 */
			return symbolV2_0;
		}
	}

	/**
	 * Retrieves all the SMT connective symbols.
	 * 
	 * @return the list with all the connective symbols.
	 */
	public static final List<String> getConnectiveSymbols(
			final SMTLIBVersion smtlibVersion) {
		final SMTConnective[] smtConnectives = SMTConnective.values();
		final List<String> connectives = new ArrayList<String>(
				smtConnectives.length);
		for (final SMTConnective connective : smtConnectives) {
			connectives.add(connective.toString(smtlibVersion));
		}
		return connectives;
	}
}
