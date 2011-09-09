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

import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.symbols.SMTSymbol.BOOLS;
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

	/**
	 * This class implements a logic using the SMT-LIB integer theory
	 */
	public static class Ints extends SMTTheoryV2_0 implements
			ISMTArithmeticFunsExtended, ISMTArithmeticPreds, ISMTIntegerSort {
		private static final String INTS_THEORY_NAME = "Ints";
		private static final String POW_INT = "PZ";

		/**
		 * Sorts of the integer theory
		 */
		private static final SMTSortSymbol INT_SORT = new SMTSortSymbol(
				SMTSymbol.INT, PREDEFINED);
		public static final SMTSortSymbol POW_INT_SORT = new SMTSortSymbol(
				POW_INT, !PREDEFINED);

		private static final SMTFunctionSymbol INTS_SET = new SMTFunctionSymbol(
				INTS, new SMTSortSymbol[] {}, POW_INT_SORT, !ASSOCIATIVE,
				!PREDEFINED);

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
		private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
				SMTSymbol.EQUAL, INT_INT_TAB, PREDEFINED, V2_0);

		private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
				SMTSymbol.LT, INT_INT_TAB, PREDEFINED, V2_0);
		private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
				SMTSymbol.LE, INT_INT_TAB, PREDEFINED, V2_0);
		private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
				SMTSymbol.GT, INT_INT_TAB, PREDEFINED, V2_0);
		private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
				SMTSymbol.GE, INT_INT_TAB, PREDEFINED, V2_0);
		private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE,
				GT, GE };

		/**
		 * Useless declarations private static final SMTFunctionSymbol ZERO =
		 * new SMTFunctionSymbol( "0", SMTFactory.EMPTY_SORT, INT_SORT,
		 * !ASSOCIATIVE, PREDEFINED); private static final SMTFunctionSymbol ONE
		 * = new SMTFunctionSymbol("1", SMTFactory.EMPTY_SORT, INT_SORT,
		 * !ASSOCIATIVE, PREDEFINED);
		 **/

		private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
				SMTSymbol.UMINUS, INT_TAB, INT_SORT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
				SMTSymbol.MINUS, INT_INT_TAB, INT_SORT, !ASSOCIATIVE,
				PREDEFINED);
		private static final SMTFunctionSymbol DIV = new SMTFunctionSymbol(
				SMTSymbol.DIV, INT_INT_TAB, INT_SORT, !ASSOCIATIVE, !PREDEFINED);
		private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
				SMTSymbol.PLUS, INT_TAB, INT_SORT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
				SMTSymbol.MUL, INT_TAB, INT_SORT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol EXPN = new SMTFunctionSymbol(
				SMTSymbol.EXPN, INT_INT_TAB, INT_SORT, !ASSOCIATIVE,
				!PREDEFINED);
		private static final SMTFunctionSymbol MOD = new SMTFunctionSymbol(
				SMTSymbol.MOD, INT_INT_TAB, INT_SORT, !ASSOCIATIVE, !PREDEFINED);
		private static final SMTFunctionSymbol[] FUNCTIONS = { INTS_SET,
				UMINUS, MINUS, PLUS, MUL, DIV, MOD, EXPN };

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

		@Override
		public SMTSymbol getExpn() {
			return EXPN;
		}

		@Override
		public SMTSymbol getMod() {
			return MOD;
		}
	}

	public static class Booleans extends SMTTheory implements ISMTBooleanSort {
		private static final String BOOLS_THEORY_NAME = "Bools";
		private static final String POW_BOOL = "PB";

		private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
				SMTSymbol.BOOL, !PREDEFINED);
		private final static SMTSortSymbol POW_BOOL_SORT = new SMTSortSymbol(
				POW_BOOL, !PREDEFINED);
		private static final SMTSortSymbol[] SORTS = { BOOL_SORT, POW_BOOL_SORT };

		private static final SMTSortSymbol[] BOOL_TAB = { BOOL_SORT };
		public final static SMTSortSymbol[] BOOL_BOOL_TAB = { BOOL_SORT,
				BOOL_SORT };

		private final static SMTPredicateSymbol TRUE = new SMTPredicateSymbol(
				"TRUE", BOOL_TAB, !PREDEFINED, V2_0);

		private final static SMTPredicateSymbol[] PREDICATES = { TRUE };

		private static final SMTFunctionSymbol BOOLS_SET = new SMTFunctionSymbol(
				BOOLS, new SMTSortSymbol[] {}, POW_BOOL_SORT, !ASSOCIATIVE,
				!PREDEFINED);

		private static final SMTFunctionSymbol[] FUNCTIONS = { BOOLS_SET };

		private static final Booleans INSTANCE = new Booleans();

		private Booleans() {
			super(BOOLS_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
		}

		public static Booleans getInstance() {
			return INSTANCE;
		}

		public SMTSortSymbol getPowerSetBooleanSort() {
			return POW_BOOL_SORT;
		}

		public static SMTFunctionSymbol getBoolsSet() {
			return BOOLS_SET;
		}

		public static SMTPredicateSymbol getTrue() {
			return TRUE;
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return BOOL_SORT;
		}
	}
}
