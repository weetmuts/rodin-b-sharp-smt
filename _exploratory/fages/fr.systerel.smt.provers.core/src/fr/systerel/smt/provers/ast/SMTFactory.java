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

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.POLYMORPHICS;

import java.math.BigInteger;

import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public abstract class SMTFactory {

	public final static String OPAR = "(";
	public final static String CPAR = ")";
	public final static String SPACE = " ";
	public final static String QVAR = "?";
	public final static String POINT = ".";

	public final static String ITE_TERM = "ite";

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
			"true", PREDEFINED);
	public final static SMTPredicateSymbol PFALSE = new SMTPredicateSymbol(
			"false", PREDEFINED);
	public final static SMTPredicateSymbol DISTINCT = new SMTPredicateSymbol(
			SMTSymbol.DISTINCT, POLYMORPHICS, PREDEFINED, true);

	/**
	 * Quantifier symbols
	 */
	public final static SMTQuantifierSymbol EXISTS = SMTQuantifierSymbol.EXISTS;
	public final static SMTQuantifierSymbol FORALL = SMTQuantifierSymbol.FORALL;

	public static SMTSortSymbol makeSortSymbol(final String symbolName,
			final boolean predefined) {
		return new SMTSortSymbol(symbolName, predefined);
	}

	/**
	 * Creates a new atomic formula from a relation expression. {EQUAL, LT, LE,
	 * GT, GE}
	 */
	public static SMTFormula makeEqual(final SMTTerm... args) {
		final SMTSortSymbol sort0 = args[0].getSort();
		final SMTSortSymbol sort[] = { sort0, sort0 };
		return new SMTAtom(new SMTPredicateSymbol.SMTEqual(sort), args);
	}

	/**
	 * The SMT-LIB language doesn't define a <code>NOTEQUAL</code> symbol. Thus
	 * we use <code>EQUAL</code> and <code>NOT</code> symbols to build it.
	 */
	public static SMTFormula makeNotEqual(final SMTTerm... args) {
		final SMTSortSymbol sort0 = args[0].getSort();
		final SMTSortSymbol sort[] = { sort0, sort0 };
		final SMTFormula[] argsT = { new SMTAtom(
				new SMTPredicateSymbol.SMTEqual(sort), args) };
		return makeNot(argsT);
	}

	public SMTFormula makeNotIff(final SMTFormula... args) {
		final SMTFormula[] formulas = { makeIff(args) };
		return makeNot(formulas);
	}

	public SMTFormula makeLessThan(final SMTPredicateSymbol lt,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(lt);
		return new SMTAtom(lt, args);
	}

	public SMTFormula makeLessEqual(final SMTPredicateSymbol le,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(le);
		return new SMTAtom(le, args);
	}

	public SMTFormula makeGreaterThan(final SMTPredicateSymbol gt,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyPredicateSignature(gt);
		return new SMTAtom(gt, args);
	}

	public SMTFormula makeGreaterEqual(final SMTPredicateSymbol ge,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(ge);
		return new SMTAtom(ge, args);
	}

	/**
	 * Creates a new arithmetic term. {PLUS, MINUS, MUL, UMINUS}
	 */
	public SMTTerm makePlus(final SMTFunctionSymbol plus,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(plus);
		return new SMTFunApplication(plus, args);
	}

	public SMTTerm makeExpn(final SMTFunctionSymbol expn,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(expn);
		return new SMTFunApplication(expn, args);
	}

	public SMTTerm makeMinus(final SMTFunctionSymbol minus,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(minus);
		return new SMTFunApplication(minus, args);
	}

	public SMTTerm makeMul(final SMTFunctionSymbol mul,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(mul);
		return new SMTFunApplication(mul, args);
	}

	public static SMTTerm makeMacroTerm(final SMTMacroSymbol macro,
			final SMTTerm... args) {
		return new SMTMacroTerm(macro, args);
	}

	public SMTTerm makeUMinus(final SMTFunctionSymbol uminus,
			final SMTSignature signature, final SMTTerm... arg) {
		signature.verifyFunctionSignature(uminus);
		return new SMTFunApplication(uminus, arg);
	}

	public SMTTerm makeDiv(final SMTFunctionSymbol div,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(div);
		return new SMTFunApplication(div, args);
	}

	/**
	 * Creates a new connective formula. {NOT, IMPLIES, IF_THEN_ELSE, AND, OR,
	 * XOR, IFF}
	 */
	public static SMTFormula makeNot(final SMTFormula... formula) {
		return new SMTConnectiveFormula(NOT, formula);
	}

	public static SMTFormula makeImplies(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(IMPLIES, formulas);
	}

	public SMTFormula makeIfThenElse(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(ITE_FORMULA, formulas);
	}

	public static SMTFormula makeAnd(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(AND, formulas);
	}

	public static SMTFormula makeOr(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(OR, formulas);
	}

	public SMTFormula makeXor(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(XOR, formulas);
	}

	public static SMTFormula makeIff(final SMTFormula... formulas) {
		return new SMTConnectiveFormula(IFF, formulas);
	}

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value for this numeral
	 * @return the newly created numeral
	 */
	public static SMTNumeral makeNumeral(final BigInteger value) {
		return new SMTNumeral(value);
	}

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
	public SMTITETerm makeITETerm(final SMTFormula formula,
			final SMTTerm tTerm, final SMTTerm fTerm) {
		return new SMTITETerm(formula, tTerm, fTerm);
	}

	/**
	 * Creates a new propositional atom. {, }
	 */
	public SMTFormula makePTrue(final SMTSignature signature) {
		return makePropAtom(PTRUE, signature);
	}

	public SMTFormula makePFalse(final SMTSignature signature) {
		return makePropAtom(PFALSE, signature);
	}

	/**
	 * Creates a new boolean constant. {FALSE, TRUE}
	 * 
	 * @param trueLogicConstant
	 */
	public SMTTerm makeTrue(final SMTFunctionSymbol trueLogicConstant,
			final SMTSignature signature) {
		return makeConstant(trueLogicConstant, signature);
	}

	public SMTTerm makeFalse(final SMTFunctionSymbol falseLogicConstant,
			final SMTSignature signature) {
		return makeConstant(falseLogicConstant, signature);
	}

	public SMTTerm makeInteger(final SMTFunctionSymbol integerCste,
			final SMTSignature signature) {
		return makeConstant(integerCste, signature);
	}

	public SMTTerm makeBool(final SMTFunctionSymbol booleanCste,
			final SMTSignature signature) {
		return makeConstant(booleanCste, signature);
	}

	public SMTTerm makeVar(final String name, final SMTSortSymbol sort) {
		return new SMTVar(new SMTVarSymbol(name, sort, !SMTSymbol.PREDEFINED));
	}

	public static SMTFormula makeForAll(final SMTFormula formula,
			final SMTTerm... terms) {
		return makeSMTQuantifiedFormula(FORALL, formula, terms);
	}

	public SMTFormula makeExists(final SMTFormula formula,
			final SMTTerm... terms) {
		return makeSMTQuantifiedFormula(EXISTS, formula, terms);
	}

	public static SMTFormula makeSMTQuantifiedFormula(
			final SMTQuantifierSymbol qSymbol, final SMTFormula formula,
			final SMTTerm... terms) {
		final SMTVarSymbol[] qVars = new SMTVarSymbol[terms.length];
		for (int i = 0; i < terms.length; i++) {
			final SMTTerm term = terms[i];
			if (term instanceof SMTVar) {
				final SMTVar var = (SMTVar) term;
				qVars[i] = var.getSymbol();
			} else {
				throw new IllegalArgumentException(
						"The term should be an SMTVar");
			}
		}
		return makeSMTQuantifiedFormula(qSymbol, formula, qVars);
	}

	public static SMTFormula makeSMTQuantifiedFormula(
			final SMTQuantifierSymbol qSymbol, final SMTFormula formula,
			final SMTVarSymbol... qVars) {
		return new SMTQuantifiedFormula(qSymbol, qVars, formula);
	}

	public SMTTerm makeFunApplication(final SMTFunctionSymbol functionSymbol,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyFunctionSignature(functionSymbol);
		return new SMTFunApplication(functionSymbol, args);
	}

	public static SMTFormula makeAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTSignature signature, final SMTTerm... args) {
		signature.verifyPredicateSignature(predicateSymbol);
		return new SMTAtom(predicateSymbol, args);
	}

	/**
	 * this method makes a new constant symbol
	 * 
	 * @param functionSymbol
	 *            the function symbol
	 * @param signature
	 *            the signature used to check the rank
	 * @return a new term with the function symbol
	 */
	public SMTTerm makeConstant(final SMTFunctionSymbol functionSymbol,
			final SMTSignature signature) {
		return makeFunApplication(functionSymbol, signature);
	}

	/**
	 * this method makes a propositional atom.
	 * 
	 * @param predicateSymbol
	 *            the predicate symbol of the atom.
	 * @param signature
	 *            the signature, used to check the rank of the predicate symbol.
	 * @return a new SMT formula with the predicate symbol.
	 */
	public SMTFormula makePropAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTSignature signature) {
		return makeAtom(predicateSymbol, signature);
	}

	/**
	 * Creates a extended SMT-LIB macro term with no arguments.
	 * 
	 * @param macroSymbol
	 *            the symbol of the term
	 * @return a new smt term with the macro symbol.
	 */
	public static SMTTerm makeMacroTerm(final SMTMacroSymbol macroSymbol,
			final SMTSortSymbol returnSort) {
		return makeMacroTerm(macroSymbol);
	}

}
