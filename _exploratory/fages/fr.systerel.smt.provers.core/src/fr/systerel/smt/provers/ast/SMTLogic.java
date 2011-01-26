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

	/** The logic identifier. */
	protected final String name;
	protected final SMTSortSymbol[] sorts;
	protected final SMTPredicateSymbol[] predicates;
	protected final SMTFunctionSymbol[] functions;

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

	public SMTPredicateSymbol getEqual() {
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

	public SMTSortSymbol getBooleanSort() {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	public static class IntsTheory extends SMTLogic {
		private static final IntsTheory INSTANCE = new IntsTheory();

		private final static SMTSortSymbol INT = new SMTSortSymbol("Int",
				PREDEFINED);
		private static final SMTSortSymbol[] SORTS = { INT };

		public final static SMTSortSymbol[] INT_TAB = { INT };
		public final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

		private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
				"=", INT_INT_TAB, PREDEFINED);

		private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
				"<", INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
				"<=", INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
				">", INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
				">=", INT_INT_TAB, PREDEFINED);
		private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE,
				GT, GE };

		private static final SMTFunctionSymbol ZERO = new SMTFunctionSymbol(
				"0", SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol ONE = new SMTFunctionSymbol("1",
				SMTFactory.EMPTY_SORT, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
				"~", INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
				"-", INT_INT_TAB, INT, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
				"+", INT_INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol("*",
				INT_INT_TAB, INT, ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol[] FUNCTIONS = { ZERO, ONE,
				UMINUS, MINUS, PLUS, MUL };

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
		public SMTPredicateSymbol getEqual() {
			return EQUAL;
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
		private static final BoolsTheory INSTANCE = new BoolsTheory();

		private static final String BOOLS = "Bools";

		private final static SMTSortSymbol BOOL = new SMTSortSymbol("Bool",
				PREDEFINED);
		private static final SMTSortSymbol[] SORTS = { BOOL };

		public final static SMTSortSymbol[] BOOL_BOOL_TAB = { BOOL, BOOL };

		private final static SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
				"=", BOOL_BOOL_TAB, PREDEFINED);
		private final static SMTPredicateSymbol[] PREDICATES = { EQUAL };

		private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol(
				"TRUE", EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED);
		private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
				"FALSE", EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED);
		private static final SMTFunctionSymbol[] FUNCTIONS = { TRUE, FALSE };

		private BoolsTheory() {
			super(BOOLS, SORTS, PREDICATES, FUNCTIONS);
		}

		public static BoolsTheory getInstance() {
			return INSTANCE;
		}

		@Override
		public SMTPredicateSymbol getEqual() {
			return EQUAL;
		}

		@Override
		public SMTSortSymbol getBooleanSort() {
			return BOOL;
		}
	}

	// FIXME provers seems to be unable to use predefined logics
	public static class UFNIA extends IntsTheory {
		private static final UFNIA INSTANCE = new UFNIA();

		private static final String UFNIA = "UFNIA";

		private UFNIA() {
			super(UFNIA);
		}

		public static UFNIA getInstance() {
			return INSTANCE;
		}
	}

	public static class LIA extends IntsTheory {
		private static final LIA INSTANCE = new LIA();

		private static final String LIA = "LIA";

		private LIA() {
			super(LIA);
		}

		public static LIA getInstance() {
			return INSTANCE;
		}
	}

	public static class AUFLIA extends IntsTheory {
		private static final AUFLIA INSTANCE = new AUFLIA();

		private static final String AUFLIA = "AUFLIA";

		private AUFLIA() {
			super(AUFLIA);
		}

		public static AUFLIA getInstance() {
			return INSTANCE;
		}
	}
}
