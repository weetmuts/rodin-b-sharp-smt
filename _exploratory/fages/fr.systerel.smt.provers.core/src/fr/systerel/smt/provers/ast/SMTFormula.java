/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * Common class for SMT-LIB formulas.
 */
public abstract class SMTFormula extends SMTNode<SMTFormula> {

	/**
	 * prints in the {@code builder} the string representation of the SMT
	 * formula. If the formula is being used inside a macro, it needs to print a
	 * dot after the declaration of the bound variables in a SMT quantified
	 * formula. The boolean {@code printPoint} is the argument to define if it's
	 * necessary to print the dot or not.
	 * 
	 * @param builder
	 *            it will store the string representation of the
	 *            {@link SMTFormula}
	 * @param printPoint
	 *            true if it's necessary to print a dot in the situation
	 *            described in {@link SMTFormula}, false otherwise.
	 * */
	public abstract void toString(StringBuilder builder, boolean printPoint);
}
