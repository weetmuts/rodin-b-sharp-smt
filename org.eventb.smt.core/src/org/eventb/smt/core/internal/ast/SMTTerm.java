/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast;

import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;

/**
 * Common class for SMT-LIB terms.
 */
public abstract class SMTTerm extends SMTNode<SMTTerm> {
	protected SMTSortSymbol sort;

	/**
	 * Prints into the stringbuilder the string representation of the SMT term.
	 * 
	 * @param builder
	 *            the StringBuilder that will store the string representation of
	 *            the SMTTerm.
	 */
	public abstract void toString(final StringBuilder builder, final int offset);

	/**
	 * Returns the sort of the SMT term.
	 * 
	 * @return the sort of the SMT term.
	 */
	public SMTSortSymbol getSort() {
		return sort;
	}
}
