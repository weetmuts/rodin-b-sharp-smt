/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.SMTSymbol.BOOLS;
import static org.eventb.smt.ast.SMTSymbol.INTS;
import static org.eventb.smt.ast.SMTSymbol.PREDEFINED;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents SMT Theories
 */
public class SMTTheory {
	private final String name;
	private final SMTSortSymbol[] sorts;
	private final SMTPredicateSymbol[] predicates;
	private final SMTFunctionSymbol[] functions;

	// TODO add fields needed to print a complete theory (definition, axioms,
	// notes)

	protected SMTTheory(final String name, final SMTSortSymbol[] sorts,
			final SMTPredicateSymbol[] predicates,
			final SMTFunctionSymbol[] functions) {
		this.name = name;
		this.sorts = sorts.clone();
		this.predicates = predicates.clone();
		this.functions = functions.clone();
	}

	public String getName() {
		return name;
	}

	public List<SMTSortSymbol> getSorts() {
		return Arrays.asList(sorts);
	}

	public List<SMTPredicateSymbol> getPredicates() {
		return Arrays.asList(predicates);
	}

	public List<SMTFunctionSymbol> getFunctions() {
		return Arrays.asList(functions);
	}

	@Override
	public String toString() {
		return "SMTTheory [name=" + name + ", sorts=" + Arrays.toString(sorts)
				+ ", predicates=" + Arrays.toString(predicates)
				+ ", functions=" + Arrays.toString(functions) + "]";
	}

	/**
	 * This class implements a logic using the SMT-LIB integer theory
	 */
	public static class Ints extends SMTTheory implements
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
				SMTSymbol.EQUAL, INT_INT_TAB, PREDEFINED);

		private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
				SMTSymbol.LT, INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
				SMTSymbol.LE, INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
				SMTSymbol.GT, INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
				SMTSymbol.GE, INT_INT_TAB, PREDEFINED);
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
				"TRUE", BOOL_TAB, !PREDEFINED);

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