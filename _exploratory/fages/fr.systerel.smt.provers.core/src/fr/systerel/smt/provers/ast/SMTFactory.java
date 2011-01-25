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

import java.math.BigInteger;

import org.eventb.core.ast.Type;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public final class SMTFactory {

	private final static SMTFactory DEFAULT_INSTANCE = new SMTFactory();

	public final static SMTTerm[] EMPTY_TERM = {};

	public final static SMTSortSymbol INT = new SMTSortSymbol("Int");
	public final static SMTSortSymbol[] EMPTY_SORT = {};
	public final static SMTSortSymbol[] INT_TAB = { INT };
	public final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

	public final static String OPAR = "(";
	public final static String CPAR = ")";
	public final static String SPACE = " ";
	public final static String QVAR = "?";

	public final static String ITE_TERM = "ite";

	/**
	 * Arithmetic symbols
	 */
	public final static SMTFunctionSymbol PLUS = new SMTFunctionSymbol("+",
			INT_INT_TAB, INT);
	public final static SMTFunctionSymbol MINUS = new SMTFunctionSymbol("-",
			INT_INT_TAB, INT);
	public final static SMTFunctionSymbol MUL = new SMTFunctionSymbol("*",
			INT_INT_TAB, INT);
	public final static SMTFunctionSymbol UMINUS = new SMTFunctionSymbol("~",
			INT_TAB, INT);
	public final static SMTPredicateSymbol EQUAL = new SMTPredicateSymbol("=",
			INT_INT_TAB);
	public final static SMTPredicateSymbol LT = new SMTPredicateSymbol("<",
			INT_INT_TAB);
	public final static SMTPredicateSymbol LE = new SMTPredicateSymbol("<=",
			INT_INT_TAB);
	public final static SMTPredicateSymbol GT = new SMTPredicateSymbol(">",
			INT_INT_TAB);
	public final static SMTPredicateSymbol GE = new SMTPredicateSymbol(">=",
			INT_INT_TAB);

	/**
	 * Connective symbols
	 */
	public final static SMTConnective NOT = SMTConnective.NOT;
	public final static SMTConnective IMPLIES = SMTConnective.IMPLIES;
	public final static SMTConnective ITE_FORMULA = SMTConnective.ITE;
	public final static SMTConnective AND = SMTConnective.AND;
	public final static SMTConnective OR = SMTConnective.OR;
	public final static SMTConnective XOR = SMTConnective.XOR;
	public final static SMTConnective IFF = SMTConnective.IFF;

	/**
	 * Propositionnal atoms
	 */
	public final static SMTPredicateSymbol PTRUE = new SMTPredicateSymbol(
			"true", EMPTY_SORT);
	public final static SMTPredicateSymbol PFALSE = new SMTPredicateSymbol(
			"false", EMPTY_SORT);

	/**
	 * Quantifier symbols
	 */
	public final static SMTQuantifierSymbol EXISTS = SMTQuantifierSymbol.EXISTS;
	public final static SMTQuantifierSymbol FORALL = SMTQuantifierSymbol.FORALL;

	/**
	 * Returns the default instance of the factory.
	 * 
	 * @return the single instance of this class
	 */
	public static SMTFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * Creates a new atomic formula from a relation expression. {EQUAL, LT, LE,
	 * GT, GE}
	 */
	public SMTFormula makeEqual(SMTTerm[] args) {
		return new SMTAtom(EQUAL, args);
	}

	public SMTFormula makeNotEqual(SMTTerm[] args) {
		final SMTFormula[] tabEqual = { makeEqual(args) };
		return makeNot(tabEqual);
	}

	public SMTFormula makeLesserThan(SMTTerm[] args) {
		return new SMTAtom(LT, args);
	}

	public SMTFormula makeLesserEqual(SMTTerm[] args) {
		return new SMTAtom(LE, args);
	}

	public SMTFormula makeGreaterThan(SMTTerm[] args) {
		return new SMTAtom(GT, args);
	}

	public SMTFormula makeGreaterEqual(SMTTerm[] args) {
		return new SMTAtom(GE, args);
	}

	/**
	 * Creates a new arithmetic term. {PLUS, MINUS, MUL, UMINUS}
	 */
	public SMTTerm makePlus(SMTTerm[] args) {
		return new SMTFunApplication(PLUS, args);
	}

	public SMTTerm makeMinus(SMTTerm[] args) {
		return new SMTFunApplication(MINUS, args);
	}

	public SMTTerm makeMul(SMTTerm[] args) {
		return new SMTFunApplication(MUL, args);
	}

	public SMTTerm makeUMinus(SMTTerm[] arg) {
		return new SMTFunApplication(UMINUS, arg);
	}

	/**
	 * Creates a new connective formula. {NOT, IMPLIES, IF_THEN_ELSE, AND, OR,
	 * XOR, IFF}
	 */
	public SMTFormula makeNot(SMTFormula[] formula) {
		return new SMTConnectiveFormula(NOT, formula);
	}

	public SMTFormula makeImplies(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(IMPLIES, formulas);
	}

	public SMTFormula makeIfThenElse(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(ITE_FORMULA, formulas);
	}

	public SMTFormula makeAnd(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(AND, formulas);
	}

	public SMTFormula makeOr(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(OR, formulas);
	}

	public SMTFormula makeXor(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(XOR, formulas);
	}

	public SMTFormula makeIff(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(IFF, formulas);
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
	 */
	// TODO When BOOL theory implemented
	/*
	 * public SMTBoolean makeBoolean() { return new SMTBoolean(); }
	 */

	/**
	 * Creates a new ITE_FORMULA term.
	 * 
	 * @param formula
	 *            a SMT formula
	 * @param tTerm
	 *            an SMT term
	 * @param fTerm
	 *            an SMT term
	 * @return the newly created ITE_FORMULA term
	 */
	public SMTITETerm makeITETerm(SMTFormula formula, SMTTerm tTerm,
			SMTTerm fTerm) {
		return new SMTITETerm(formula, tTerm, fTerm);
	}

	/**
	 * Creates a new propositional atom. {FALSE, TRUE}
	 */
	public SMTFormula makePTrue() {
		return this.makePropAtom(PTRUE);
	}

	public SMTFormula makePFalse() {
		return this.makePropAtom(PFALSE);
	}

	public SMTTerm makeVar(final String identifier, final Type type) {
		// FIXME this is not correct
		return new SMTVar(new SMTVarSymbol(identifier, new SMTSortSymbol(
				type.toString())));
	}

	/**
	 * Creates a new quantified pred.
	 */
	public SMTFormula makeForAll(final SMTTerm[] terms, final SMTFormula formula) {
		return new SMTQuantifiedFormula(FORALL, null, formula);
	}

	public SMTFormula makeExists(final SMTTerm[] terms, final SMTFormula formula) {
		return new SMTQuantifiedFormula(EXISTS, null, formula);
	}

	public SMTTerm makeFunApplication(final SMTFunctionSymbol functionSymbol,
			final SMTTerm[] args) {
		return new SMTFunApplication(functionSymbol, args);
	}

	public SMTFormula makeAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTTerm[] args) {
		return new SMTAtom(predicateSymbol, args);
	}

	public SMTTerm makeConstant(final SMTFunctionSymbol functionSymbol) {
		return makeFunApplication(functionSymbol, EMPTY_TERM);
	}

	public SMTFormula makePropAtom(final SMTPredicateSymbol predicateSymbol) {
		return makeAtom(predicateSymbol, EMPTY_TERM);
	}
}
