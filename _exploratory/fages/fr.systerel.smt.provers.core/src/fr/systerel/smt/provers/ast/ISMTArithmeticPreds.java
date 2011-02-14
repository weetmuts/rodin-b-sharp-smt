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
 * which define arithmetic SMTPredicateSymbol which string representations are
 * the SMT-LIB symbols "<", "<=", ">" and ">=".
 */
public interface ISMTArithmeticPreds {

	public SMTPredicateSymbol getLessThan();

	public SMTPredicateSymbol getLessEqual();

	public SMTPredicateSymbol getGreaterThan();

	public SMTPredicateSymbol getGreaterEqual();
}
