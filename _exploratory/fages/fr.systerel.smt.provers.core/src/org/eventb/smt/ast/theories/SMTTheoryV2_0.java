/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ast.theories;

import static org.eventb.smt.ast.SMTFactory.EMPTY_SORT;
import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.symbols.SMTSymbol.BOOLS;
import static org.eventb.smt.ast.symbols.SMTSymbol.DISTINCT;
import static org.eventb.smt.ast.symbols.SMTSymbol.EQUAL;
import static org.eventb.smt.ast.symbols.SMTSymbol.INTS;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTTheoryV2_0 extends SMTTheory {
	protected SMTTheoryV2_0(String name, SMTSortSymbol[] sorts,
			SMTPredicateSymbol[] predicates, SMTFunctionSymbol[] functions) {
		super(name, sorts, predicates, functions);
	}

	public static class Core extends SMTTheoryV2_0 implements ISMTBooleanSort,
			ISMTBooleanFuns {
		private static final String CORE_THEORY_NAME = "Core";
		private static final String POW_BOOL = "PB";

		/**
		 * Sorts of the core theory
		 */
		private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
				SMTSymbol.BOOL, !PREDEFINED, V2_0);
		private final static SMTSortSymbol POW_BOOL_SORT = new SMTSortSymbol(
				POW_BOOL, !PREDEFINED, V2_0);
		private static final SMTSortSymbol[] SORTS = { BOOL_SORT, POW_BOOL_SORT };

		private static final SMTSortSymbol[] BOOL_TAB = { BOOL_SORT };
		public final static SMTSortSymbol[] BOOL_BOOL_TAB = { BOOL_SORT,
				BOOL_SORT };

		private final static SMTPredicateSymbol[] PREDICATES = {};

		private static final SMTFunctionSymbol BOOLS_SET = new SMTFunctionSymbol(
				BOOLS, new SMTSortSymbol[] {}, POW_BOOL_SORT, !ASSOCIATIVE,
				!PREDEFINED, V2_0);

		private static final SMTFunctionSymbol TRUE = new SMTFunctionSymbol(
				"true", EMPTY_SORT, BOOL_SORT, !ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
				"false", EMPTY_SORT, BOOL_SORT, !ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol NOT = new SMTFunctionSymbol(
				"not", BOOL_TAB, BOOL_SORT, !ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol IMPLIES = new SMTFunctionSymbol(
				"=>", BOOL_BOOL_TAB, BOOL_SORT, ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol AND = new SMTFunctionSymbol(
				"and", BOOL_BOOL_TAB, BOOL_SORT, ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol OR = new SMTFunctionSymbol("or",
				BOOL_BOOL_TAB, BOOL_SORT, ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTFunctionSymbol XOR = new SMTFunctionSymbol(
				"xor", BOOL_BOOL_TAB, BOOL_SORT, ASSOCIATIVE, PREDEFINED, V2_0);

		private static final SMTFunctionSymbol[] FUNCTIONS = { BOOLS_SET, TRUE,
				FALSE, NOT, IMPLIES, AND, OR, XOR };

		private static final Core INSTANCE = new Core();

		protected Core() {
			super(CORE_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
		}

		public static Core getInstance() {
			return INSTANCE;
		}

		public static SMTFunctionSymbol getBoolsSet() {
			return BOOLS_SET;
		}

		public SMTSortSymbol getPowerSetBooleanSort() {
			return POW_BOOL_SORT;
		}

		@Override
		public SMTFunctionSymbol getTrue() {
			return TRUE;
		}

		@Override
		public SMTFunctionSymbol getFalse() {
			return FALSE;
		}

		@Override
		public SMTFunctionSymbol getNot() {
			return NOT;
		}

		@Override
		public SMTFunctionSymbol getImplies() {
			return IMPLIES;
		}

		@Override
		public SMTFunctionSymbol getAnd() {
			return AND;
		}

		@Override
		public SMTFunctionSymbol getOr() {
			return OR;
		}

		@Override
		public SMTFunctionSymbol getXor() {
			return XOR;
		}

		@Override
		public SMTFunctionSymbol getEqual(final SMTSortSymbol[] sort) {
			return new SMTFunctionSymbol(EQUAL, sort, BOOL_SORT, ASSOCIATIVE,
					PREDEFINED, V2_0);
		}

		@Override
		public SMTFunctionSymbol getDistinct(final SMTSortSymbol sort) {
			return new SMTFunctionSymbol(DISTINCT, new SMTSortSymbol[] { sort,
					sort }, BOOL_SORT, ASSOCIATIVE, PREDEFINED, V2_0);
		}

		@Override
		public SMTFunctionSymbol getITE(final SMTSortSymbol sort) {
			return new SMTFunctionSymbol("ite", new SMTSortSymbol[] {
					BOOL_SORT, sort, sort }, sort, !ASSOCIATIVE, PREDEFINED,
					V2_0);
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return BOOL_SORT;
		}
	}

	/**
	 * This class implements a logic using the SMT-LIB integer theory
	 */
	public static class Ints extends SMTTheoryV2_0 implements
			ISMTArithmeticFuns, ISMTArithmeticPreds, ISMTIntegerSort {
		private static final String INTS_THEORY_NAME = "Ints";
		private static final String POW_INT = "PZ";

		/**
		 * Sorts of the integer theory
		 */
		private static final SMTSortSymbol INT_SORT = new SMTSortSymbol(
				SMTSymbol.INT, PREDEFINED, V2_0);
		public static final SMTSortSymbol POW_INT_SORT = new SMTSortSymbol(
				POW_INT, !PREDEFINED, V2_0);

		private static final SMTFunctionSymbol INTS_SET = new SMTFunctionSymbol(
				INTS, new SMTSortSymbol[] {}, POW_INT_SORT, !ASSOCIATIVE,
				!PREDEFINED, V2_0);

		private static final SMTSortSymbol[] SORTS = { INT_SORT, POW_INT_SORT };

		/**
		 * Useful unary and binary integer ranks for predicate and function
		 * declarations
		 */
		private static final SMTSortSymbol[] INT_TAB = { INT_SORT };
		private static final SMTSortSymbol[] INT_INT_TAB = { INT_SORT, INT_SORT };

		/**
		 * Predicates and functions of the integer theory
		 */
		private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
				SMTSymbol.UMINUS, INT_TAB, INT_SORT, !ASSOCIATIVE, PREDEFINED,
				V2_0);
		private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
				SMTSymbol.MINUS, INT_INT_TAB, INT_SORT, !ASSOCIATIVE,
				PREDEFINED, V2_0);
		private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
				SMTSymbol.PLUS, INT_TAB, INT_SORT, ASSOCIATIVE, PREDEFINED,
				V2_0);
		private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
				SMTSymbol.MUL, INT_TAB, INT_SORT, ASSOCIATIVE, PREDEFINED, V2_0);
		private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
				SMTSymbol.LE, INT_INT_TAB, PREDEFINED, ASSOCIATIVE, V2_0);
		private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
				SMTSymbol.LT, INT_INT_TAB, PREDEFINED, ASSOCIATIVE, V2_0);
		private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
				SMTSymbol.GE, INT_INT_TAB, PREDEFINED, ASSOCIATIVE, V2_0);
		private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
				SMTSymbol.GT, INT_INT_TAB, PREDEFINED, ASSOCIATIVE, V2_0);
		private static final SMTPredicateSymbol[] PREDICATES = { LE, LT, GE, GT };
		private static final SMTFunctionSymbol[] FUNCTIONS = { INTS_SET,
				UMINUS, MINUS, PLUS, MUL };

		/**
		 * The sole instance of the integer theory
		 */
		private static final Ints INSTANCE = new Ints();

		protected Ints() {
			super(INTS_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
		}

		public static Ints getInstance() {
			return INSTANCE;
		}

		public static SMTSortSymbol getInt() {
			return INT_SORT;
		}

		public SMTSortSymbol getPowerSetIntegerSort() {
			return POW_INT_SORT;
		}

		public static SMTFunctionSymbol getIntsSet() {
			return INTS_SET;
		}

		@Override
		public SMTSortSymbol getIntegerSort() {
			return INT_SORT;
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
	}
}
