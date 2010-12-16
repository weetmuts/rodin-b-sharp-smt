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
 * @author guyot
 *
 */
public class SMTVarSymbol implements SMTSymbol {
	final private String symbol;
	final private SMTSort sort;

	public SMTVarSymbol(final String symbol, final SMTSort sort) {
		this.symbol = symbol;
		this.sort = sort;
	}

	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(QVAR);
		buffer.append(this.symbol);
		buffer.append(SPACE);
		buffer.append(this.sort);
		buffer.append(CPAR);
		return buffer.toString();
	}
}
