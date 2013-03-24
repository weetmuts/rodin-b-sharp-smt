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

import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;

/**
 * This interface is meant to be implemented by Logic or Theory instances
 * which define arithmetic SMTFunctionSymbol which string representations are
 * the SMT-LIB symbols "<", "<=", ">" and ">=".
 */
public interface IArithBoolFuns {

	/**
	 * returns the predicate < (less than)
	 * 
	 * @return the predicate < (less than)
	 */
	public SMTFunctionSymbol getLessThan();

	/**
	 * returns the predicate <= (less or equal than)
	 * 
	 * @return the predicate <= (less or equal than)
	 */
	public SMTFunctionSymbol getLessEqual();

	/**
	 * returns the predicate > (greater than)
	 * 
	 * @return the predicate > (greater than)
	 */
	public SMTFunctionSymbol getGreaterThan();

	/**
	 * returns the predicate >= (greater equal than)
	 * 
	 * @return the predicate >= (greater equal than)
	 */
	public SMTFunctionSymbol getGreaterEqual();
}
