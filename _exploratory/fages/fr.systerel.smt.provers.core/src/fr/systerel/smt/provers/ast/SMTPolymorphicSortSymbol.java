/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * This class represent polymorphic sorts, which are used with macros, because
 * the arguments of macros are higher order and polymorphic.
 * 
 * @author vitor
 */
public class SMTPolymorphicSortSymbol extends SMTSortSymbol {

	/**
	 * Initializes the sort class. By default, they have no string associated
	 * and is not predefined.
	 */
	SMTPolymorphicSortSymbol() {
		super("", !PREDEFINED);
	}

	/**
	 * Initializes the sort class.
	 * 
	 * @param symbolName
	 *            the name of the sort
	 */
	public SMTPolymorphicSortSymbol(final String symbolName) {
		super(symbolName, !PREDEFINED);
	}

	@Override
	public boolean isCompatibleWith(final SMTSortSymbol other) {
		return true;
	}

}
