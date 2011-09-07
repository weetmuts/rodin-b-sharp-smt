/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

/**
 * This interface is meant to be implemented by SMTLogic or SMTTheory instances
 * which define arithmetic SMTFunctionSymbol which string representations are
 * the SMT-LIB symbols "+", "*", "-".
 */

/**
 * The linear Presburger arithmetic logic given as example in The SMT-LIB
 * Standard V1.2 could not be defined with this interface because its signature
 * is more restrictive: {0, s, +, <}. But this is not a problem as far as such
 * logics will not be used.
 **/
public interface ISMTArithmeticFuns {
	/**
	 * returns the function symbol ~ (uminus)
	 * 
	 * @return the function symbol ~ (uminus)
	 */
	public abstract SMTFunctionSymbol getUMinus();

	/**
	 * returns the function symbol + (plus)
	 * 
	 * @return the function symbol + (plus)
	 */
	public abstract SMTFunctionSymbol getPlus();

	/**
	 * returns the function symbol * (multiplication)
	 * 
	 * @return the function symbol * (multiplication)
	 */
	public abstract SMTFunctionSymbol getMul();

	/**
	 * returns the function symbol - (minus)
	 * 
	 * @return the function symbol - (minus)
	 */
	public abstract SMTFunctionSymbol getMinus();
}