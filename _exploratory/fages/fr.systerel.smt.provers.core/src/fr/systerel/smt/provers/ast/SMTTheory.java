/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;

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

	// FIXME .clone() needed?
	public List<SMTSortSymbol> getSorts() {
		return Arrays.asList(sorts);
	}

	// FIXME .clone() needed?
	public List<SMTPredicateSymbol> getPredicates() {
		return Arrays.asList(predicates);
	}

	// FIXME .clone() needed?
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
		private static final String INTS = "Ints";

		/**
		 * Sorts of the integer theory
		 */
		private final static SMTSortSymbol INT = new SMTSortSymbol(
				SMTSymbol.INT, PREDEFINED);

		public static SMTSortSymbol getInt() {
			return INT;
		}

		public static SMTSortSymbol[] getIntTab() {
			return INT_TAB;
		}

		private static final SMTSortSymbol[] SORTS = { INT };

		/**
		 * Useful unary and binary integer ranks for predicate and function
		 * declarations
		 */
		private final static SMTSortSymbol[] INT_TAB = { INT };
		private final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

		/**
		 * Predicates and functions of the integer theory
		 */
		private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
				SMTSymbol.EQUAL, PREDEFINED, INT_INT_TAB);

		private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
				SMTSymbol.LT, PREDEFINED, INT_INT_TAB);
		private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
				SMTSymbol.LE, PREDEFINED, INT_INT_TAB);
		private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
				SMTSymbol.GT, PREDEFINED, INT_INT_TAB);
		private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
				SMTSymbol.GE, PREDEFINED, INT_INT_TAB);
		private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE,
				GT, GE };

		/**
		 * Useless declarations private static final SMTFunctionSymbol ZERO =
		 * new SMTFunctionSymbol( "0", SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE,
		 * PREDEFINED); private static final SMTFunctionSymbol ONE = new
		 * SMTFunctionSymbol("1", SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE,
		 * PREDEFINED);
		 **/

		private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
				SMTSymbol.UMINUS, INT, !ASSOCIATIVE, PREDEFINED, INT_TAB);
		private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
				SMTSymbol.MINUS, INT, !ASSOCIATIVE, PREDEFINED, INT_INT_TAB);
		private static final SMTFunctionSymbol DIV = new SMTFunctionSymbol(
				SMTSymbol.DIV, INT, !ASSOCIATIVE, !PREDEFINED, INT_INT_TAB);
		private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
				SMTSymbol.PLUS, INT, ASSOCIATIVE, PREDEFINED, INT_TAB);
		private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
				SMTSymbol.MUL, INT, ASSOCIATIVE, PREDEFINED, INT_TAB);
		private static final SMTFunctionSymbol EXPN = new SMTFunctionSymbol(
				SMTSymbol.EXPN, INT, !ASSOCIATIVE, !PREDEFINED, INT_INT_TAB);
		private static final SMTFunctionSymbol MOD = new SMTFunctionSymbol(
				SMTSymbol.MOD, INT, !ASSOCIATIVE, !PREDEFINED, INT_INT_TAB);
		private static final SMTFunctionSymbol[] FUNCTIONS = { UMINUS, MINUS,
				PLUS, MUL, DIV };

		/**
		 * The sole instance of the integer theory
		 */
		private static final Ints INSTANCE = new Ints();

		protected Ints() {
			super(INTS, SORTS, PREDICATES, FUNCTIONS);
		}

		public static Ints getInstance() {
			return INSTANCE;
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

		public static SMTSortSymbol[] getIntIntTab() {
			return INT_INT_TAB;
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
		private static final String BOOLS = "Bools";

		private final static SMTSortSymbol BOOL = new SMTSortSymbol(
				SMTSymbol.BOOL_SORT, !PREDEFINED);
		private static final SMTSortSymbol[] SORTS = { BOOL };

		private static final SMTSortSymbol[] BOOL_TAB = { BOOL };
		public final static SMTSortSymbol[] BOOL_BOOL_TAB = { BOOL, BOOL };

		private final static SMTPredicateSymbol TRUE = new SMTPredicateSymbol(
				"TRUE", !PREDEFINED, BOOL_TAB);

		private final static SMTPredicateSymbol[] PREDICATES = { TRUE };

		private static final SMTFunctionSymbol BOOL_CSTE = new SMTFunctionSymbol(
				SMTSymbol.BOOL_SORT, BOOL, !ASSOCIATIVE, !PREDEFINED);

		public static SMTFunctionSymbol getBoolCste() {
			return BOOL_CSTE;
		}

		private static final SMTFunctionSymbol[] FUNCTIONS = { BOOL_CSTE };

		private static final Booleans INSTANCE = new Booleans();

		private Booleans() {
			super(BOOLS, SORTS, PREDICATES, FUNCTIONS);
		}

		public static Booleans getInstance() {
			return INSTANCE;
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return BOOL;
		}

		public static SMTPredicateSymbol getTrue() {
			return TRUE;
		}
	}
}
