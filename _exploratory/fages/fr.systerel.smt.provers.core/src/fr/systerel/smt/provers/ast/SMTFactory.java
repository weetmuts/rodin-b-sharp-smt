/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import java.math.BigInteger;

/**
 * This class is the factory class for all the AST nodes.
 */
public final class SMTFactory {

	private final static SMTFactory DEFAULT_INSTANCE = new SMTFactory();

	/**
	 * Returns the default instance of the factory.
	 * 
	 * @return the single instance of this class
	 */
	public static SMTFactory getDefault() {
		return DEFAULT_INSTANCE;
	}
	
	/**
	 * Creates a new arithmetic term.
	 * <p>
	 * {PLUS, MINUS, MUL, DIV, MODULO, UNARY_MINUS}
	 * 
	 * @param tag
	 *            the tag of the arithmetic term
	 * @param children
	 *            the children of the arithmetic term
	 * @return the newly created term
	 */
	public SMTArithmeticTerm makeArithmeticTerm(int tag,
			SMTTerm[] children) {
		return new SMTArithmeticTerm(tag, children);
	}

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value for this numeral
	 * @return the newly created numeral
	 */
	public SMTNumeral makeNumeral(BigInteger value) {
		return new SMTNumeral(value);
	}

	/**
	 * Creates a new boolean.
	 * 
	 * @param tag
	 *            the tag of the boolean
	 * @return the newly created boolean
	 */
	public SMTBoolean makeBoolean(int tag) {
		return new SMTBoolean(tag);
	}

	/**
	 * Creates a new identifier.
	 * 
	 * @param identifier
	 *            the identifier
	 * @return the newly created identifier
	 */
	public SMTIdentifier makeIdentifier(String identifier) {
		return new SMTIdentifier(identifier);
	}
}
