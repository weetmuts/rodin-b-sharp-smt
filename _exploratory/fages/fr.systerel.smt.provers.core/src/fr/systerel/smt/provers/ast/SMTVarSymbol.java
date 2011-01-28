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
import static fr.systerel.smt.provers.ast.SMTFactory.QVAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * Represents quantified var symbols (constants are represented with
 * SMTFunctionSymbol)
 * 
 */
public class SMTVarSymbol extends SMTSymbol {
	final private SMTSortSymbol sort;

	public SMTVarSymbol(final String symbolName, final SMTSortSymbol sort,
			final boolean predefined) {
		super(symbolName, predefined);
		this.sort = sort;
	}

	public SMTSortSymbol getSort() {
		return sort;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(QVAR);
		buffer.append(name);
		buffer.append(SPACE);
		buffer.append(sort);
		buffer.append(CPAR);
		return buffer.toString();
	}
}
