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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * @author guyot
 * 
 */
public class SMTFunctionSymbol implements SMTSymbol {
	/**
	 * The function
	 */
	final private String symbol;
	/**
	 * The rank (as defined in SMT-LIB Signature definition). It was chosen to
	 * distinguish between the result sort and the argument sorts by putting
	 * them in two distinct fields. Consequently, argSorts can be null whereas
	 * resultSort cannot.
	 */
	final private SMTSort[] argSorts;
	final private SMTSort resultSort;

	public SMTFunctionSymbol(final String symbol, final SMTSort resultSort,
			final SMTSort... argSorts) {
		this.symbol = symbol;
		this.argSorts = argSorts;
		// Must not be null
		this.resultSort = resultSort;
	}

	/**
	 * If argSorts is null, then this symbol is a base term: function constant.
	 */
	public boolean isConstant() {
		return this.argSorts == null;
	}

	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(this.symbol);
		buffer.append(SPACE);
		buffer.append(this.resultSort);
		for (SMTSort sort: this.argSorts) {
			buffer.append(SPACE);
			buffer.append(sort);
		}
		buffer.append(CPAR);
		return buffer.toString();
	}
}
