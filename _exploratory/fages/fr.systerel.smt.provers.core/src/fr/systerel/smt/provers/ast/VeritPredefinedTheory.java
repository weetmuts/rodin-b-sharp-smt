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

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class implements the Theories used by VeriT.
 **/
public class VeritPredefinedTheory extends SMTTheory implements
		ISMTArithmeticFunsExtended, ISMTArithmeticPreds, ISMTIntegerSort,
		ISMTBooleanSort {

	private static final String NAME = "verit_theory";

	private final static SMTSortSymbol BOOL = new SMTSortSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, PREDEFINED);

	private final static SMTSortSymbol INT = new SMTSortSymbol(SMTSymbol.INT,
			PREDEFINED);

	private final static SMTSortSymbol[] INT_TAB = { INT };
	private final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

	public static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol();
	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };

	private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
			SMTSymbol.EQUAL, POLYMORPHIC_PAIRS, PREDEFINED);

	private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
			SMTSymbol.LT, INT_INT_TAB, PREDEFINED);
	private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
			SMTSymbol.LE, INT_INT_TAB, PREDEFINED);
	private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
			SMTSymbol.GT, INT_INT_TAB, PREDEFINED);
	private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
			SMTSymbol.GE, INT_INT_TAB, PREDEFINED);

	private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
			SMTSymbol.UMINUS, INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
	private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
			SMTSymbol.MINUS, INT_INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
	private static final SMTFunctionSymbol DIV = new SMTFunctionSymbol(
			SMTSymbol.DIV, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED);
	private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
			SMTSymbol.PLUS, INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
	private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
			SMTSymbol.MUL, INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
	private static final SMTFunctionSymbol EXPN = new SMTFunctionSymbol(
			SMTSymbol.EXPN, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED);
	private static final SMTFunctionSymbol MOD = new SMTFunctionSymbol(
			SMTSymbol.MOD, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED);

	private static final SMTFunctionSymbol BOOL_FUNCTION = new SMTFunctionSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, new SMTSortSymbol[] {}, BOOL,
			false, true);

	public static SMTFunctionSymbol getBoolFunction() {
		return BOOL_FUNCTION;
	}

	private static final SMTSortSymbol[] SORTS = { BOOL, INT };

	private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE, GT,
			GE };

	private static final SMTFunctionSymbol[] FUNCTIONS = { UMINUS, MINUS, PLUS,
			MUL, DIV, MOD, EXPN };

	/**
	 * Constructs the veriT predefined theory
	 */
	protected VeritPredefinedTheory() {
		super(NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	/**
	 * Instance of the VeriT Predefined Theory
	 */
	private static final VeritPredefinedTheory INSTANCE = new VeritPredefinedTheory();

	/**
	 * returns the instance of veriT predefined theory
	 * 
	 * @return the instance of veriT predefined theory
	 */
	public static VeritPredefinedTheory getInstance() {
		return INSTANCE;
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL;
	}

	@Override
	public SMTSortSymbol getIntegerSort() {
		return INT;
	}

	@Override
	public SMTFunctionSymbol getUMinus() {
		return UMINUS;
	}

	@Override
	public SMTFunctionSymbol getPlus() {
		return PLUS;
	}

	@Override
	public SMTFunctionSymbol getMul() {
		return MUL;
	}

	@Override
	public SMTFunctionSymbol getMinus() {
		return MINUS;
	}

	@Override
	public SMTSymbol getDiv() {
		return DIV;
	}

	@Override
	public SMTPredicateSymbol getLessThan() {
		return LT;
	}

	@Override
	public SMTPredicateSymbol getLessEqual() {
		return LE;
	}

	@Override
	public SMTPredicateSymbol getGreaterThan() {
		return GT;
	}

	@Override
	public SMTPredicateSymbol getGreaterEqual() {
		return GE;
	}

	/**
	 * 
	 * @return the pair sorts {Int,Int}
	 */
	public static SMTSortSymbol[] getIntIntTab() {
		return INT_INT_TAB;
	}

	/**
	 * returns the exponential symbol
	 */
	@Override
	public SMTSymbol getExpn() {
		return EXPN;
	}

	@Override
	public SMTSymbol getMod() {
		return MOD;
	}

}