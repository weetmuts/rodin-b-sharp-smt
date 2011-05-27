/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.macros.SMTMacroTerm;

/**
 * This class stores methods used to make extended SMT-LIB elements. This class
 * is used only in the VeriT translation approach
 */
final public class SMTFactoryVeriT extends SMTFactory {
	/**
	 * An instance of the Factory
	 */
	private final static SMTFactoryVeriT DEFAULT_INSTANCE = new SMTFactoryVeriT();

	/**
	 * Returns the instance of the factory
	 * 
	 * @return the instance of the factory
	 */
	public static SMTFactoryVeriT getInstance() {
		return DEFAULT_INSTANCE;
	}

	public static SMTTerm makeMacroTerm(final SMTMacroSymbol macro,
			final SMTTerm... args) {
		return new SMTMacroTerm(macro, args);
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


	/**
	 * This method returns the translation of an Event-B String
	 * 
	 * @param atomicExpression
	 *            The event-B string
	 * @param signature
	 *            the signature used to get a new constant name or to add macros
	 *            to the signature (it's necessary to define a macro for some
	 *            Event-B strings)
	 */
	public static String getSMTAtomicExpressionFormat(
			final String atomicExpression, final SMTSignatureVerit signature) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return SMTSymbol.INT;
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			signature.addMacro(SMTMacroFactory.NAT_MACRO);
			return SMTMacroSymbol.NAT;
		} else if (atomicExpression.equals("\u2115" + 1)) {
			signature.addMacro(SMTMacroFactory.NAT1_MACRO);
			return SMTMacroSymbol.NAT1;
		} else if (atomicExpression.equals("BOOL")) {
			return SMTMacroSymbol.BOOL_SORT_VERIT;
		} else {
			return signature.freshCstName(atomicExpression);
		}
	}

	/**
	 * makes and returns a SMT formula which the operator is distinct.
	 * 
	 * @param args
	 *            The arguments of the formula
	 * @return a new SMT formula
	 */
	public static SMTFormula makeDistinct(final SMTTerm[] args) {
		return new SMTAtom(DISTINCT, args);
	}

	/**
	 * Creates and return a pair sort symbol. The string representation of a
	 * pair sort symbol is:
	 * <p>
	 * (Pair A B)
	 * <p>
	 * where A and B are two SMT sort symbols
	 * 
	 * @param sourceSymbol
	 *            The first sort symbol
	 * @param targetSymbol
	 *            The second sort symbol
	 * @return A pair sort symbol
	 */
	public static SMTSortSymbol makePairSortSymbol(
			final SMTSortSymbol sourceSymbol, final SMTSortSymbol targetSymbol) {

		final StringBuffer sb = new StringBuffer();
		sb.append("(Pair ");
		sb.append(sourceSymbol.toString());
		sb.append(" ");
		sb.append(targetSymbol.toString());
		sb.append(")");
		return new SMTSortSymbol(sb.toString(), false);
	}

	/**
	 * This method creates and returns a macro atom.
	 * 
	 * @param macroSymbol
	 *            the symbol of the atom
	 * @param args
	 *            the args of the atom
	 * @return a macro atom
	 */
	public static SMTFormula makeMacroAtom(final SMTMacroSymbol macroSymbol,
			final SMTTerm... args) {
		return new SMTVeriTAtom(macroSymbol, args);
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
	public SMTTerm makeVeriTConstantTerm(final SMTSymbol smtSymbol,
			final SMTSignature signature) {
		if (smtSymbol instanceof SMTPredicateSymbol) {
			return new SMTVeriTTerm((SMTPredicateSymbol) smtSymbol);

		} else if (smtSymbol instanceof SMTFunctionSymbol) {
			return makeFunApplication((SMTFunctionSymbol) smtSymbol, signature);
		} else {
			throw new IllegalArgumentException(
					"In the translation for veriT extended SMT-LIB, the Symbol should be a function or a verit pred symbol");
		}
	}

	/**
	 * This method converts verit SMT-TERM into formulas. These terms must be of
	 * sort Bool, predefined in VeriT.
	 * 
	 * @param terms
	 *            the terms
	 * @return the formulas from the terms
	 */
	public SMTFormula[] convertVeritTermsIntoFormulas(final SMTTerm... terms) {
		final SMTFormula[] formulas = new SMTFormula[terms.length];
		int i = 0;
		for (final SMTTerm term : terms) {
			if (!term
					.getSort()
					.toString()
					.equals(VeritPredefinedTheory.getInstance()
							.getBooleanSort().toString())) {
				throw new IllegalArgumentException(
						"VeriT translation does not accept equal operator under terms with different operators");
			} else {
				if (term instanceof SMTFunApplication) {
					final SMTFunApplication function = (SMTFunApplication) term;
					final SMTSortSymbol[] sortSymbols = new SMTSortSymbol[function.args.length];
					for (int j = 0; j < function.args.length; j++) {
						sortSymbols[j] = function.args[j].getSort();
					}
					final SMTPredicateSymbol predicateSymbol = new SMTPredicateSymbol(
							function.symbol.name, !PREDEFINED, sortSymbols);
					final SMTAtom atom = new SMTAtom(predicateSymbol);
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
			final SMTFunctionSymbol operatorSymbol, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(operatorSymbol);
		return new SMTFunApplication(operatorSymbol, args);
	}

	/**
	 * This method creates and returns a new SMT sort symbol
	 * 
	 * @param name
	 *            the name of the sort
	 * @param signature
	 *            the signature used to create a fresh name for the symbol
	 * @return a new sort symbol
	 */
	public static SMTSortSymbol makeVeriTSortSymbol(final String name,
			final SMTSignatureVerit signature) {
		final String symbolName = getSMTAtomicExpressionFormat(name, signature);
		return new SMTSortSymbol(symbolName, false);
	}

	/**
	 * This method creates and returns a new SMT predicate symbol
	 * 
	 * @param name
	 *            the name of the predicate
	 * @param sort
	 *            the sort of the first argument of the predicate
	 * @return a new predicate symbol
	 */
	public SMTPredicateSymbol makeVeriTPredSymbol(final String name,
			final SMTSortSymbol sort) {
		return new SMTPredicateSymbol(name, !SMTSymbol.PREDEFINED, sort);
	}

}
