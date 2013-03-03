/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - additional methods
 *******************************************************************************/

package org.eventb.smt.core.internal.ast;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroFactory.POLYMORPHICS;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.PREDEFINED;

import java.math.BigInteger;

import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public abstract class SMTFactory {

	public final static String OPAR = "(";
	public final static String CPAR = ")";
	public final static String SPACE = " ";
	public final static String QVAR = "?";
	public final static String POINT = ".";

	private static SMTTerm[] EMPTY_TERMS = {};

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
			"true", new SMTSortSymbol[] {}, PREDEFINED, V1_2);
	public final static SMTPredicateSymbol PFALSE = new SMTPredicateSymbol(
			"false", new SMTSortSymbol[] {}, PREDEFINED, V1_2);

	public final static SMTPredicateSymbol DISTINCT = new SMTPredicateSymbol(
			SMTSymbol.DISTINCT, POLYMORPHICS, true, PREDEFINED, V1_2);

	/**
	 * Quantifier symbols
	 */
	public final static SMTQuantifierSymbol EXISTS = SMTQuantifierSymbol.EXISTS;
	public final static SMTQuantifierSymbol FORALL = SMTQuantifierSymbol.FORALL;

	/**
	 * Creates a new atomic formula from a relation expression. {EQUAL, LT, LE,
	 * GT, GE}
	 */
	public static SMTFormula makeEqual(final SMTTerm[] args,
			final SMTLIBVersion smtlibVersion) {
		final SMTSortSymbol sort0 = args[0].getSort();
		final SMTSortSymbol sort[] = { sort0, sort0 };
		return new SMTAtom(
				new SMTPredicateSymbol.SMTEqual(sort, smtlibVersion), args);
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
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(gt);
		return new SMTAtom(gt, args);
	}

	public SMTFormula makeGreaterEqual(final SMTPredicateSymbol ge,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(ge);
		return new SMTAtom(ge, args);
	}

	/**
	 * Creates a new arithmetic term. {PLUS, MINUS, MUL, UMINUS,DIV,MOD,EXPN}
	 */
	public SMTTerm makePlus(final SMTFunctionSymbol plus, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(plus);
		return new SMTFunApplication(plus, args);
	}

	public SMTTerm makeExpn(final SMTFunctionSymbol expn, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(expn);
		return new SMTFunApplication(expn, args);
	}

	public SMTTerm makeMinus(final SMTFunctionSymbol minus,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyFunctionSignature(minus);
		return new SMTFunApplication(minus, args);
	}

	public SMTTerm makeMul(final SMTFunctionSymbol mul, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(mul);
		return new SMTFunApplication(mul, args);
	}

	public SMTTerm makeUMinus(final SMTFunctionSymbol uminus,
			final SMTTerm[] arg, final SMTSignature signature) {
		signature.verifyFunctionSignature(uminus);
		return new SMTFunApplication(uminus, arg);
	}

	public SMTTerm makeDiv(final SMTFunctionSymbol div, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(div);
		return new SMTFunApplication(div, args);
	}

	public SMTTerm makeMod(final SMTFunctionSymbol mod, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(mod);
		return new SMTFunApplication(mod, args);
	}

	/**
	 * Creates a new connective formula. {NOT, IMPLIES, IF_THEN_ELSE, AND, OR,
	 * XOR, IFF}
	 */
	public static SMTFormula makeNot(final SMTFormula[] formula,
			final SMTLIBVersion smtlibVersion) {
		return new SMTConnectiveFormula(NOT, smtlibVersion, formula);
	}

	public static SMTFormula makeImplies(final SMTFormula[] formulas,
			final SMTLIBVersion smtlibVersion) {
		return new SMTConnectiveFormula(IMPLIES, smtlibVersion, formulas);
	}

	public static SMTFormula makeAnd(final SMTFormula[] formulas,
			final SMTLIBVersion smtlibVersion) {
		return new SMTConnectiveFormula(AND, smtlibVersion, formulas);
	}

	public static SMTFormula makeOr(final SMTFormula[] formulas,
			final SMTLIBVersion smtlibVersion) {
		return new SMTConnectiveFormula(OR, smtlibVersion, formulas);
	}

	public static SMTFormula makeIff(final SMTFormula[] formulas,
			final SMTLIBVersion smtlibVersion) {
		return new SMTConnectiveFormula(IFF, smtlibVersion, formulas);
	}

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value for this numeral
	 * @return the newly created numeral
	 */
	public static SMTNumeral makeNumeral(final BigInteger value,
			final SMTLIBVersion smtlibVersion) {
		return new SMTNumeral(value, smtlibVersion);
	}

	/**
	 * Creates a new propositional atom. {, }
	 */
	public static SMTFormula makePTrue() {
		return new SMTAtom(PTRUE, new SMTTerm[] {});
	}

	public static SMTFormula makePFalse() {
		return new SMTAtom(PFALSE, new SMTTerm[] {});
	}

	public static SMTTerm makeInteger(final SMTFunctionSymbol integerCste,
			final SMTSignature signature) {
		return makeConstant(integerCste, signature);
	}

	public static SMTTerm makeBool(final SMTFunctionSymbol booleanCste,
			final SMTSignature signature) {
		return makeConstant(booleanCste, signature);
	}

	public static SMTTerm makeVar(final String name, final SMTSortSymbol sort,
			final SMTLIBVersion smtlibVersion) {
		return new SMTVar(new SMTVarSymbol(name, sort, !SMTSymbol.PREDEFINED,
				smtlibVersion), smtlibVersion);
	}

	public static SMTFormula makeForAll(final SMTTerm[] terms,
			final SMTFormula formula, final SMTLIBVersion smtlibVersion) {
		return makeSMTQuantifiedFormula(FORALL, terms, formula, smtlibVersion);
	}

	public static SMTFormula makeExists(final SMTTerm[] terms,
			final SMTFormula formula, final SMTLIBVersion smtlibVersion) {
		return makeSMTQuantifiedFormula(EXISTS, terms, formula, smtlibVersion);
	}

	/**
	 * This method creates and returns a SMT quantified formula.
	 * 
	 * @param qSymbol
	 *            the quantifier symbol
	 * @param formula
	 *            the subformula of the quantifier formula
	 * @param terms
	 *            the terms that contains the bound identifier symbols
	 * @return the SMT quantified formula
	 */
	public static SMTFormula makeSMTQuantifiedFormula(
			final SMTQuantifierSymbol qSymbol, final SMTTerm[] terms,
			final SMTFormula formula, final SMTLIBVersion smtlibVersion) {
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
		return makeSMTQuantifiedFormula(qSymbol, qVars, formula, smtlibVersion);
	}

	/**
	 * Creates and returns a SMT quantified formula
	 * 
	 * @param qSymbol
	 *            The quantifier symbol
	 * @param qVars
	 *            the terms that contains the bound identifier symbols
	 * @param formula
	 *            the subformula of the quantifier formula
	 * @return a new SMT quantified formula
	 */
	public static SMTFormula makeSMTQuantifiedFormula(
			final SMTQuantifierSymbol qSymbol, final SMTVarSymbol[] qVars,
			final SMTFormula formula, final SMTLIBVersion smtlibVersion) {
		return new SMTQuantifiedFormula(qSymbol, qVars, formula, smtlibVersion);
	}

	/**
	 * Creates and returns a new function application
	 * 
	 * @param functionSymbol
	 *            the function symbol
	 * @param args
	 *            the arguments of the function
	 * @param signature
	 *            the signature used to check the rank of the function
	 * @return a new term which is the function application
	 */
	public static SMTTerm makeFunApplication(
			final SMTFunctionSymbol functionSymbol, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(functionSymbol);
		return new SMTFunApplication(functionSymbol, args);
	}

	/**
	 * Creates and returns a new atom
	 * 
	 * @param predicateSymbol
	 *            the predicate of the atom
	 * @param args
	 *            the arguments of the predicate
	 * @param signature
	 *            the signature used to check the rank of the predicate
	 * @return a new atom formula
	 */
	public static SMTFormula makeAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTTerm[] args, final SMTSignature signature) {
		signature.verifyPredicateSignature(predicateSymbol);
		return new SMTAtom(predicateSymbol, args);
	}

	public static SMTFormula makeAtom2(
			final SMTPredicateSymbol predicateSymbol, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyPredicateSignature(predicateSymbol);
		return new SMTAtom(predicateSymbol, args) {
			@Override
			public void toString(final StringBuilder builder, final int offset,
					final boolean printPoint) {
				terms[0].toString(builder, offset);
			}
		};
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
	public static SMTTerm makeConstant(final SMTFunctionSymbol functionSymbol,
			final SMTSignature signature) {
		return makeFunApplication(functionSymbol, EMPTY_TERMS, signature);
	}
}
