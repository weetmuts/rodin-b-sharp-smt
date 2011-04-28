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
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

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
	public void toString(final StringBuilder builder) {
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
}