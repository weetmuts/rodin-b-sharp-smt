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

import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public final class SMTFactory {
	private final static SMTFactory DEFAULT_INSTANCE = new SMTFactory();

	public final static SMTTerm[] EMPTY_TERM = {};
	public final static SMTSortSymbol[] EMPTY_SORT = {};
	public final static SMTPredicateSymbol[] EMPTY_PREDICATE = {};

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
			"true", EMPTY_SORT, PREDEFINED);
	public final static SMTPredicateSymbol PFALSE = new SMTPredicateSymbol(
			"false", EMPTY_SORT, PREDEFINED);
	public final static SMTPredicateSymbol DISTINCT = new SMTPredicateSymbol(
			SMTSymbol.DISTINCT, POLYMORPHICS, PREDEFINED, true);

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
	 * This method is used by the Extended SMT-LIB.
	 * 
	 * @param atomicExpression
	 * @param signature
	 */
	public static String getSMTAtomicExpressionFormat(String atomicExpression,
			SMTSignatureVerit signature) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return "Int";
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			signature.addMacro(SMTMacroFactory.NAT_MACRO);
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			signature.addMacro(SMTMacroFactory.NAT1_MACRO);
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else
			return signature.freshCstName(atomicExpression);
	}

	public static SMTSortSymbol makeVeriTSortSymbol(
			final String sortSymbolName, final SMTSignatureVerit signature) {
		String symbolName = getSMTAtomicExpressionFormat(sortSymbolName,
				signature);
		return new SMTSortSymbol(symbolName, false);
	}

	public static SMTSortSymbol makeSortSymbol(String symbolName,
			boolean predefined) {
		return new SMTSortSymbol(symbolName, predefined);
	}

	public SMTPredicateSymbol makeVeriTPredSymbol(final String predName,
			final SMTSortSymbol symbol) {
		SMTSortSymbol[] symbols = { symbol };
		return new SMTPredicateSymbol(predName, symbols, !SMTSymbol.PREDEFINED);
	}

	public static SMTSortSymbol makePairSortSymbol(SMTSortSymbol sourceSymbol,
			SMTSortSymbol targetSymbol) {

		StringBuffer sb = new StringBuffer();
		sb.append("(Pair ");
		sb.append(sourceSymbol.toString());
		sb.append(" ");
		sb.append(targetSymbol.toString());
		sb.append(")");
		return new SMTSortSymbol(sb.toString(), false);
	}

	/**
	 * Creates a new atomic formula from a relation expression. {EQUAL, LT, LE,
	 * GT, GE}
	 */
	public static SMTFormula makeEqual(final SMTTerm[] args) {
		final SMTSortSymbol sort0 = args[0].getSort();
		final SMTSortSymbol sort[] = { sort0, sort0 };
		return new SMTAtom(new SMTPredicateSymbol.SMTEqual(sort), args);
	}

	public static SMTFormula makeDistinct(final SMTTerm[] args) {
		return new SMTAtom(DISTINCT, args);
	}

	/**
	 * The SMT-LIB language doesn't define a <code>NOTEQUAL</code> symbol. Thus
	 * we use <code>EQUAL</code> and <code>NOT</code> symbols to build it.
	 */
	public SMTFormula makeNotEqual(final SMTTerm[] args) {
		final SMTSortSymbol sort0 = args[0].getSort();
		final SMTSortSymbol sort[] = { sort0, sort0 };
		SMTFormula[] argsT = { new SMTAtom(
				new SMTPredicateSymbol.SMTEqual(sort), args) };
		return makeNot(argsT);
	}

	public SMTFormula makeNotIff(final SMTFormula[] args) {
		final SMTFormula[] formulas = { makeIff(args) };
		return makeNot(formulas);
	}

	public SMTFormula makeLessThan(final SMTPredicateSymbol lt,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyPredicateSignature(lt);
		return new SMTAtom(lt, args);
	}

	public SMTFormula makeLessEqual(final SMTPredicateSymbol le,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyPredicateSignature(le);
		return new SMTAtom(le, args);
	}

	public SMTFormula makeGreaterThan(final SMTPredicateSymbol gt,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyPredicateSignature(gt);
		return new SMTAtom(gt, args);
	}

	public SMTFormula makeGreaterEqual(final SMTPredicateSymbol ge,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyPredicateSignature(ge);
		return new SMTAtom(ge, args);
	}

	/**
	 * Creates a new arithmetic term. {PLUS, MINUS, MUL, UMINUS}
	 */
	public SMTTerm makePlus(final SMTFunctionSymbol plus, final SMTTerm[] args,
			SMTSignature signature) {
		signature.verifyFunctionSignature(plus);
		return new SMTFunApplication(plus, args);
	}

	public SMTTerm makeExpn(final SMTFunctionSymbol expn, final SMTTerm[] args,
			SMTSignature signature) {
		signature.verifyFunctionSignature(expn);
		return new SMTFunApplication(expn, args);
	}

	public SMTTerm makeMinus(final SMTFunctionSymbol minus,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyFunctionSignature(minus);
		return new SMTFunApplication(minus, args);
	}

	public SMTTerm makeMul(final SMTFunctionSymbol mul, final SMTTerm[] args,
			SMTSignature signature) {
		signature.verifyFunctionSignature(mul);
		return new SMTFunApplication(mul, args);
	}

	public static SMTTerm makeMacroTerm(SMTMacroSymbol macro, final SMTTerm[] args) {
		return new SMTMacroTerm(macro, args);
	}

	public SMTTerm makeUMinus(final SMTFunctionSymbol uminus,
			final SMTTerm[] arg, SMTSignature signature) {
		signature.verifyFunctionSignature(uminus);
		return new SMTFunApplication(uminus, arg);
	}

	public SMTTerm makeDiv(final SMTFunctionSymbol div, final SMTTerm[] args,
			SMTSignature signature) {
		signature.verifyFunctionSignature(div);
		return new SMTFunApplication(div, args);
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
	// TODO When BOOL_SORT theory implemented
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
	 * Creates a new propositional atom. {, }
	 */
	public SMTFormula makePTrue(SMTSignature signature) {
		return makePropAtom(PTRUE, signature);
	}

	public SMTFormula makePFalse(SMTSignature signature) {
		return makePropAtom(PFALSE, signature);
	}

	/**
	 * Creates a new boolean constant. {FALSE, TRUE}
	 * 
	 * @param trueLogicConstant
	 */
	public SMTTerm makeTrue(SMTFunctionSymbol trueLogicConstant,
			SMTSignature signature) {
		return makeConstant(trueLogicConstant, signature);
	}

	public SMTTerm makeFalse(SMTFunctionSymbol falseLogicConstant,
			SMTSignature signature) {
		return makeConstant(falseLogicConstant, signature);
	}

	public SMTTerm makeInteger(final SMTFunctionSymbol integerCste,
			SMTSignature signature) {
		return makeConstant(integerCste, signature);
	}

	public SMTTerm makeBool(final SMTFunctionSymbol booleanCste,
			SMTSignature signature) {
		return makeConstant(booleanCste, signature);
	}

	public SMTTerm makeVar(final String name, final SMTSortSymbol sort) {
		return new SMTVar(new SMTVarSymbol(name, sort, !SMTSymbol.PREDEFINED));
	}

	public SMTFormula makeForAll(final SMTTerm[] terms, final SMTFormula formula) {
		return makeSMTQuantifiedFormula(FORALL, terms, formula, false);
	}

	public SMTFormula makeExists(final SMTTerm[] terms, final SMTFormula formula) {
		return makeSMTQuantifiedFormula(EXISTS, terms, formula, false);
	}

	public SMTFormula makeSMTQuantifiedFormula(SMTQuantifierSymbol qSymbol,
			final SMTTerm[] terms, final SMTFormula formula,
			final boolean printPoint) {
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
		return makeSMTQuantifiedFormula(qSymbol, qVars, formula);
	}

	public static SMTFormula makeSMTQuantifiedFormula(
			SMTQuantifierSymbol qSymbol, final SMTVarSymbol[] qVars,
			final SMTFormula formula) {
		return new SMTQuantifiedFormula(qSymbol, qVars, formula);
	}

	public SMTTerm makeFunApplication(final SMTFunctionSymbol functionSymbol,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyFunctionSignature(functionSymbol);
		return new SMTFunApplication(functionSymbol, args);
	}

	public static SMTFormula makeAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTTerm[] args, SMTSignature signature) {
		signature.verifyPredicateSignature(predicateSymbol);
		return new SMTAtom(predicateSymbol, args);
	}

	public static SMTFormula makeMacroAtom(SMTMacroSymbol macroSymbol,
			SMTTerm[] args) {
		return new SMTAtom(macroSymbol, args);
	}

	/**
	 * this method makes a new predicate extended SMT-LIB macro atom.
	 * 
	 * @param predicateSymbol
	 *            the predicate symbol
	 * @param args
	 *            the arguments
	 * @param signature
	 *            used to check the rank of the predicate
	 * @return
	 */
	public SMTFormula makeVeriTMacroAtom(
			final SMTPredicateSymbol predicateSymbol, final SMTTerm[] args,
			SMTSignature signature) {
		// TODO Insert a method here to verify signature of the macros
		if (!(predicateSymbol instanceof SMTMacroSymbol)) {
			throw new IllegalArgumentException("faiou");
		}

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
			SMTSignature signature) {
		return makeFunApplication(functionSymbol, EMPTY_TERM, signature);
	}

	/**
	 * this method makes a VeriT term. The difference between normal terms and
	 * VeriT terms is that VeriT terms can accept predicate symbols instead of
	 * function symbols. This happens when predicates are used as arguments of
	 * macros, which in this case, the predicates is used with no arguments and
	 * macros are in the terms level.
	 * 
	 * @param smtSymbol
	 *            the symbol of the term
	 * @param signature
	 *            used to check the rank of the term
	 * @return a new SMT term with the symbol
	 */
	public SMTTerm makeVeriTConstantTerm(SMTSymbol smtSymbol,
			SMTSignature signature) {
		if (smtSymbol instanceof SMTPredicateSymbol) {
			return new SMTVeriTTerm((SMTPredicateSymbol) smtSymbol);

		} else if (smtSymbol instanceof SMTFunctionSymbol) {
			return makeFunApplication((SMTFunctionSymbol) smtSymbol,
					EMPTY_TERM, signature);
		} else {
			throw new IllegalArgumentException(
					"In the translation for veriT extended SMT-LIB, the Symbol should be a function or a verit pred symbol");
		}
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
			SMTSignature signature) {
		return makeAtom(predicateSymbol, EMPTY_TERM, signature);
	}

	/**
	 * This method converts verit SMT-TERM into formulas. These terms must be of
	 * sort Bool, predefined in VeriT.
	 * 
	 * @param terms
	 *            the terms
	 * @return the formulas from the terms
	 */
	public SMTFormula[] convertVeritTermsIntoFormulas(SMTTerm[] terms) {
		SMTFormula[] formulas = new SMTFormula[terms.length];
		int i = 0;
		for (SMTTerm term : terms) {
			if (!term.getSort().toString().equals(SMTSymbol.BOOL_SORT)) {
				throw new IllegalArgumentException(
						"VeriT translation does not accept equal operator under terms with different operators");
			} else {
				if (term instanceof SMTFunApplication) {
					SMTFunApplication function = (SMTFunApplication) term;
					SMTSortSymbol[] sortSymbols = new SMTSortSymbol[function.args.length];
					for (int j = 0; j < function.args.length; j++) {
						sortSymbols[j] = function.args[j].getSort();
					}
					SMTPredicateSymbol predicateSymbol = new SMTPredicateSymbol(
							function.symbol.name, sortSymbols, !PREDEFINED);
					SMTAtom atom = new SMTAtom(predicateSymbol, EMPTY_TERM);
					formulas[i] = atom;
				} else {
					throw new IllegalArgumentException(
							"Conversion from terms to formula in VeriT shall happen only if all arguments of the terms are functions and their return types are boolean");
				}
			}
			++i;
		}
		return formulas;
	}

	/**
	 * This method creates a function application.
	 * 
	 * @param operatorSymbol
	 *            the symbol of the function application
	 * @param args
	 *            the arguments of the application
	 * @param signature
	 *            the signature for checking the rank
	 * @return a new SMT term with the symbol and the arguments
	 */
	public SMTTerm makeVeriTTermOperatorApplication(
			SMTFunctionSymbol operatorSymbol, SMTTerm[] args,
			SMTSignature signature) {
		signature.verifyFunctionSignature(operatorSymbol);
		return new SMTFunApplication(operatorSymbol, args);
	}

	/**
	 * Creates a extended SMT-LIB macro term with no arguments.
	 * 
	 * @param macroSymbol
	 *            the symbol of the term
	 * @return a new smt term with the macro symbol.
	 */
	public static SMTTerm makeMacroTerm(SMTMacroSymbol macroSymbol) {
		return makeMacroTerm(macroSymbol, EMPTY_TERM);
	}

}
