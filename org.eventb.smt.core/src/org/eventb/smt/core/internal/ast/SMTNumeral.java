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

import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import java.math.BigInteger;

import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.theories.TheoryV1_2;
import org.eventb.smt.core.internal.ast.theories.TheoryV2_0;

/**
 * This class represents a numeral in SMT-LIB grammar.
 */
public final class SMTNumeral extends SMTTerm {

	/** The internal value. */
	private final BigInteger value;

	private final SMTLIBVersion smtlibVersion;

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value
	 */
	SMTNumeral(final BigInteger value, final SMTLIBVersion smtlibVersion) {
		this.value = value;
		this.smtlibVersion = smtlibVersion;
	}

	@Override
	public SMTSortSymbol getSort() {
		if (smtlibVersion == V1_2) {
			return TheoryV1_2.Ints.getInstance().getIntegerSort();
		} else {
			return TheoryV2_0.Ints.getInstance().getIntegerSort();
		}
	}

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		if (value.signum() < 0) {
			builder.append(OPAR);
			if (smtlibVersion == V1_2) {
				builder.append(TheoryV1_2.Ints.getInstance().getUMinus()
						.getName());
			} else {
				builder.append(TheoryV2_0.Ints.getInstance().getUMinus()
						.getName());
			}
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