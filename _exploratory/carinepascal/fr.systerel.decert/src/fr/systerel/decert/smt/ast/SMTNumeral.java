/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.smt.ast;

import java.math.BigInteger;

/**
 * This class represents a numeral in SMT-LIB grammar.
 */
public final class SMTNumeral extends SMTBaseTerm {

	/** The internal value. */
	private final BigInteger value;

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value
	 */
	SMTNumeral(BigInteger value) {
		super(value.toString(), SMTNode.NUMERAL);
		this.value = value;
	}

	@Override
	protected void toString(StringBuilder builder) {
		final String prefix, suffix;
		if (value.signum() < 0) {
			prefix = "(~ ";
			suffix = ")";
		} else {
			prefix = "";
			suffix = "";
		}
		builder.append(prefix);
		builder.append(value.abs());
		builder.append(suffix);
	}
}
