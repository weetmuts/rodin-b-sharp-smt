/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.theories;

import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;

/**
 * This interface is meant to be implemented by Logic or Theory instances
 * which define boolean SMTFunctionSymbol
 */
public interface IBoolFuns {
	public abstract SMTFunctionSymbol getTrue();

	public abstract SMTFunctionSymbol getFalse();

	public abstract SMTFunctionSymbol getNot();

	public abstract SMTFunctionSymbol getImplies();

	public abstract SMTFunctionSymbol getAnd();

	public abstract SMTFunctionSymbol getOr();

	public abstract SMTFunctionSymbol getXor();

	public abstract SMTFunctionSymbol getEqual(final SMTSortSymbol[] sort);

	public abstract SMTFunctionSymbol getDistinct(final SMTSortSymbol sort);

	public abstract SMTFunctionSymbol getITE(final SMTSortSymbol sort);
}