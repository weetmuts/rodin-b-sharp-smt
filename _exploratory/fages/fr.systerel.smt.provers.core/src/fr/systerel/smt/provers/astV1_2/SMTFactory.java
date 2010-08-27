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
package fr.systerel.smt.provers.astV1_2;

import java.math.BigInteger;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
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
	 * Creates a new arithmetic formula.
	 * <p>
	 * {EQUAL, LT, LE, GT, GE}
	 * 
	 * @param tag
	 *            the tag of the arithmetic formula
	 * @param children
	 *            the children of the arithmetic formula
	 * @return the newly created formula
	 */
	public SMTArithmeticFormula makeArithmeticFormula(int tag,
			SMTTerm[] children) {
		return new SMTArithmeticFormula(tag, children);
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
	 * Creates a new connective formula.
	 * <p>
	 * {NOT, IMPLIES, IF_THEN_ELSE, AND, OR, XOR, IFF}
	 * 
	 * @param tag
	 *            the tag of the connective formula
	 * @param children
	 *            the children of the connective formula
	 * @return the newly created formula
	 */
	public SMTConnectiveFormula makeConnectiveFormula(int tag,
			SMTFormula[] children) {
		return new SMTConnectiveFormula(tag, children);
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

	/**
	 * Creates a new ITE term.
	 * 
	 * @param formula
	 *            a SMT formula
	 * @param tTerm
	 *            an SMT term
	 * @param fTerm
	 *            an SMT term
	 * @return the newly created ITE term
	 */
	public SMTITETerm makeITETerm(SMTFormula formula, SMTTerm tTerm,
			SMTTerm fTerm) {
		return new SMTITETerm(SMTNode.ITE, formula, tTerm, fTerm);
	}

	/**
	 * Creates a new propositional atom.
	 * <p>
	 * {FALSE, TRUE}
	 * 
	 * @param tag
	 *            the tag of the propositional atom
	 * @return the newly created propositional atom
	 */
	public SMTPropAtom makePropAtom(int tag) {
		return new SMTPropAtom(tag);
	}
	
	/**
	 * Creates a macro.
	 * 
	 * @param tag
	 *            the tag of the macro
	 * @return the newly created macro
	 */
	public SMTMacro makeMacro(int tag, String macroId, SMTTerm[] children, boolean not) {
		return new SMTMacro(tag, macroId,children,not);
	}
	
	
	/**
	 * Creates a command (SMT lib v2.0).
	 * 
	 * @param tag
	 *            the tag of the propositional atom
	 * @return the newly created propositional atom
	 */
	public SMTMacro makeCommand(int tag, String macroId, SMTTerm[] children, boolean not) {
		return new SMTMacro(tag, macroId,children,not);
	}
}
