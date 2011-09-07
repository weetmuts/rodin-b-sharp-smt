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

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

import java.math.BigInteger;

/**
 * This class represents a numeral in SMT-LIB grammar.
 */
public final class SMTNumeral extends SMTTerm {

	/** The internal value. */
	private final BigInteger value;

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value
	 */
	SMTNumeral(final BigInteger value) {
		this.value = value;
	}

	@Override
	public SMTSortSymbol getSort() {
		return SMTTheory.Ints.getInstance().getIntegerSort();
	}

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		if (value.signum() < 0) {
			builder.append(OPAR);
			builder.append(SMTTheory.Ints.getInstance().getUMinus().getName());
			builder.append(SPACE);
			builder.append(value.abs());
			builder.append(CPAR);
		} else {
			builder.append(value.abs());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}
}