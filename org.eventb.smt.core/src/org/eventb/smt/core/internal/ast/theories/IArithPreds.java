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

import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;

/**
 * This interface is meant to be implemented by Logic or Theory instances
 * which define arithmetic SMTPredicateSymbol which string representations are
 * the SMT-LIB symbols "<", "<=", ">" and ">=".
 */
public interface IArithPreds {

	/**
	 * returns the predicate < (less than)
	 * 
	 * @return the predicate < (less than)
	 */
	public SMTPredicateSymbol getLessThan();

	/**
	 * returns the predicate <= (less or equal than)
	 * 
	 * @return the predicate <= (less or equal than)
	 */
	public SMTPredicateSymbol getLessEqual();

	/**
	 * returns the predicate > (greater than)
	 * 
	 * @return the predicate > (greater than)
	 */
	public SMTPredicateSymbol getGreaterThan();

	/**
	 * returns the predicate >= (greater equal than)
	 * 
	 * @return the predicate >= (greater equal than)
	 */
	public SMTPredicateSymbol getGreaterEqual();
}
