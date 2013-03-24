/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.theories;

import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;

/**
 * This interface is meant to be implemented by Logic or Theory instances
 * which define arithmetic SMTFunctionSymbol which string representations are
 * the SMT-LIB symbols "÷", "expn".
 */
public interface IArithFunsExt extends IArithFuns {

	/**
	 * returns the division function symbol
	 * 
	 * @return the division function symbol
	 */
	public SMTSymbol getDiv();

	/**
	 * returns the exponentiation function symbol
	 * 
	 * @return the exponentiation function symbol
	 */
	public SMTSymbol getExpn();

	/**
	 * returns the mod function symbol
	 * 
	 * @return the mod function symbol
	 */
	public SMTSymbol getMod();
}
