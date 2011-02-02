/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.EMPTY_SORT;
import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;

/**
 * The SMT logics.
 */
public class SMTLogic {
	public static String UNKNOWN = "UNKNOWN";

	/** The logic name and symbols */
	private final String name;
	private final SMTSortSymbol[] sorts;
	private final SMTPredicateSymbol[] predicates;
	private final SMTFunctionSymbol[] functions;

	public SMTLogic(final String name, final SMTSortSymbol[] sorts,
			final SMTPredicateSymbol[] predicates,
			final SMTFunctionSymbol[] functions) {
		this.name = name;
		this.sorts = sorts;
		this.predicates = predicates;
		this.functions = functions;
	}

	public final String getName() {
		return name;
	}

	public final SMTSortSymbol[] getSorts() {
		return sorts;
	}

	public final SMTPredicateSymbol[] getPredicates() {
		return predicates;
	}

	public final SMTFunctionSymbol[] getFunctions() {
		return functions;
	}

	public SMTFunctionSymbol getUMinus() {
		return null;
	}

	public SMTFunctionSymbol getPlus() {
		return null;
	}

	public SMTFunctionSymbol getMul() {
		return null;
	}

	public SMTFunctionSymbol getMinus() {
		return null;
	}

	public SMTFunctionSymbol getTrue() {
		return null;
	}

	public SMTFunctionSymbol getFalse() {
		return null;
	}

	public SMTPredicateSymbol getEqual(final SMTSortSymbol sort) {
		return null;
	}

	public SMTPredicateSymbol getLesserThan() {
		return null;
	}

	public SMTPredicateSymbol getLesserEqual() {
		return null;
	}

	public SMTPredicateSymbol getGreaterThan() {
		return null;
	}

	public SMTPredicateSymbol getGreaterEqual() {
		return null;
	}

	public SMTSortSymbol getIntegerSort() {
		return null;
	}

	public SMTFunctionSymbol getIntegerCste() {
		return null;
	}

	public SMTSortSymbol getBooleanSort() {
		return null;
	}

	public SMTFunctionSymbol getBooleanCste() {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	public static class SMTLIBUnderlyingLogic extends SMTLogic {
		private final static IntsTheory ints = IntsTheory.getInstance();
		private final static BoolsTheory bools = BoolsTheory.getInstance();

		private static final SMTSortSymbol[] SORTS = new SMTSortSymbol[ints
				.getSorts().length + bools.getSorts().length];
		static {
			System.arraycopy(ints.getSorts(), 0, SORTS, 0,
					ints.getSorts().length);
			System.arraycopy(bools.getSorts(), 0, SORTS,
					ints.getSorts().length, bools.getSorts().length);
		}

		private static final SMTPredicateSymbol[] PREDICATES = new SMTPredicateSymbol[ints
				.getPredicates().length + bools.getPredicates().length];
		static {
			System.arraycopy(ints.getPredicates(), 0, PREDICATES, 0,
					ints.getPredicates().length);
			System.arraycopy(bools.getPredicates(), 0, PREDICATES,
					ints.getPredicates().length, bools.getPredicates().length);
		}

		private static final SMTFunctionSymbol[] FUNCTIONS = new SMTFunctionSymbol[ints
				.getFunctions().length + bools.getFunctions().length];
		static {
			System.arraycopy(ints.getFunctions(), 0, FUNCTIONS, 0,
					ints.getFunctions().length);
			System.arraycopy(bools.getFunctions(), 0, FUNCTIONS,
					ints.getFunctions().length, bools.getFunctions().length);
		}

		private static final SMTLIBUnderlyingLogic INSTANCE = new SMTLIBUnderlyingLogic();

		protected SMTLIBUnderlyingLogic() {
			super(UNKNOWN, SORTS, PREDICATES, FUNCTIONS);
		}

		public static SMTLIBUnderlyingLogic getInstance() {
			return INSTANCE;
		}

		@Override
		public SMTSortSymbol getIntegerSort() {
			return ints.getIntegerSort();
		}

		@Override
		public SMTFunctionSymbol getIntegerCste() {
			return ints.getIntegerCste();
		}

		@Override
		public SMTFunctionSymbol getUMinus() {
			return ints.getUMinus();
		}

		@Override
		public SMTFunctionSymbol getPlus() {
			return ints.getPlus();
		}

		@Override
		public SMTFunctionSymbol getMul() {
			return ints.getMul();
		}

		@Override
		public SMTFunctionSymbol getMinus() {
			return ints.getMinus();
		}

		@Override
		public SMTPredicateSymbol getEqual(final SMTSortSymbol sort) {
			SMTPredicateSymbol equal = ints.getEqual(sort);
			if (equal != null) {
				return equal;
			} else {
				return bools.getEqual(sort);
			}
		}

		@Override
		public SMTPredicateSymbol getLesserThan() {
			return ints.getLesserThan();
		}

		@Override
		public SMTPredicateSymbol getLesserEqual() {
			return ints.getLesserEqual();
		}

		@Override
		public SMTPredicateSymbol getGreaterThan() {
			return ints.getGreaterThan();
		}

		@Override
		public SMTPredicateSymbol getGreaterEqual() {
			return ints.getGreaterEqual();
		}

		@Override
		public SMTFunctionSymbol getTrue() {
			return bools.getTrue();
		}

		@Override
		public SMTFunctionSymbol getFalse() {
			return bools.getFalse();
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return bools.getBooleanSort();
		}

		@Override
		public SMTFunctionSymbol getBooleanCste() {
			return bools.getBooleanCste();
		}
	}

	/**
	 * This class implements a logic using the SMT-LIB integer theory
	 */
	public static class IntsTheory extends SMTLogic {
		/**
		 * Sorts of the integer theory
		 */
		private final static SMTSortSymbol INT = new SMTSortSymbol(
				SMTSymbol.INT, PREDEFINED);
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

		private static final SMTFunctionSymbol INT_CSTE = new SMTFunctionSymbol(
				SMTSymbol.INT, EMPTY_SORT, INT, !ASSOCIATIVE, PREDEFINED);
		/**
		 * Useless declarations private static final SMTFunctionSymbol ZERO =
		 * new SMTFunctionSymbol( "0", SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE,
		 * PREDEFINED); private static final SMTFunctionSymbol ONE = new
		 * SMTFunctionSymbol("1", SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE,
		 * PREDEFINED);
		 **/
		private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
				SMTSymbol.UMINUS, INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
				SMTSymbol.MINUS, INT_INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
				SMTSymbol.PLUS, INT_INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
				SMTSymbol.MUL, INT_INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol[] FUNCTIONS = { INT_CSTE,
				UMINUS, MINUS, PLUS, MUL };

		/**
		 * The sole instance of the integer theory
		 */
		private static final IntsTheory INSTANCE = new IntsTheory();

		protected IntsTheory() {
			super(UNKNOWN, SORTS, PREDICATES, FUNCTIONS);
		}

		protected IntsTheory(final String logicName) {
			super(logicName, SORTS, PREDICATES, FUNCTIONS);
		}

		public static IntsTheory getInstance() {
			return INSTANCE;
		}

		@Override
		public SMTSortSymbol getIntegerSort() {
			return INT;
		}

		@Override
		public SMTFunctionSymbol getIntegerCste() {
			return INT_CSTE;
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
		public SMTPredicateSymbol getEqual(final SMTSortSymbol sort) {
			if (sort.equals(INT)) {
				return EQUAL;
			} else {
				return null;
			}
		}

		@Override
		public SMTPredicateSymbol getLesserThan() {
			return LT;
		}

		@Override
		public SMTPredicateSymbol getLesserEqual() {
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

	public static class BoolsTheory extends SMTLogic {
		private static final String BOOLS = "Bools";

		private final static SMTSortSymbol BOOL = new SMTSortSymbol(
				SMTSymbol.BOOL, PREDEFINED);
		private static final SMTSortSymbol[] SORTS = { BOOL };

		public final static SMTSortSymbol[] BOOL_BOOL_TAB = { BOOL, BOOL };

		private final static SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
				SMTSymbol.EQUAL, BOOL_BOOL_TAB, PREDEFINED);
		private final static SMTPredicateSymbol[] PREDICATES = { EQUAL };

		private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol(
				"TRUE", EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED);
		private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
				"FALSE", EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED);

		private static final SMTFunctionSymbol BOOL_CSTE = new SMTFunctionSymbol(
				SMTSymbol.BOOL, EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol[] FUNCTIONS = { BOOL_CSTE, TRUE,
				FALSE };

		private static final BoolsTheory INSTANCE = new BoolsTheory();

		private BoolsTheory() {
			super(BOOLS, SORTS, PREDICATES, FUNCTIONS);
		}

		public static BoolsTheory getInstance() {
			return INSTANCE;
		}

		@Override
		public SMTFunctionSymbol getBooleanCste() {
			return BOOL_CSTE;
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
		public SMTPredicateSymbol getEqual(final SMTSortSymbol sort) {
			if (sort.equals(BOOL)) {
				return EQUAL;
			} else {
				return null;
			}
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return BOOL;
		}
	}

	// FIXME provers seems to be unable to use predefined logics
	public static class UFNIA extends IntsTheory {
		private static final String UFNIA = "UFNIA";

		private static final UFNIA INSTANCE = new UFNIA();

		private UFNIA() {
			super(UFNIA);
		}

		public static UFNIA getInstance() {
			return INSTANCE;
		}
	}

	public static class LIA extends IntsTheory {
		private static final String LIA = "LIA";

		private static final LIA INSTANCE = new LIA();

		private LIA() {
			super(LIA);
		}

		public static LIA getInstance() {
			return INSTANCE;
		}
	}

	public static class AUFLIA extends IntsTheory {
		private static final String AUFLIA = "AUFLIA";

		private static final AUFLIA INSTANCE = new AUFLIA();

		private AUFLIA() {
			super(AUFLIA);
		}

		public static AUFLIA getInstance() {
			return INSTANCE;
		}
	}
}
