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
	public SMTFunctionSymbol getUMinus();

	public SMTFunctionSymbol getPlus();

	public SMTFunctionSymbol getMul();

	public SMTFunctionSymbol getMinus();
}
