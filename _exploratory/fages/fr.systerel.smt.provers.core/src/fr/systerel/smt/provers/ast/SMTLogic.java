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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import static fr.systerel.smt.provers.ast.SMTSymbol.LOGIC;
import static fr.systerel.smt.provers.ast.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.List;

import fr.systerel.smt.provers.ast.SMTTheory.Booleans;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * The SMT logics.
 */
public class SMTLogic {
	public static String UNKNOWN = "UNKNOWN";

	/** The logic name and symbols */
	private final String name;
	private final SMTTheory[] theories;

	// TODO add fields needed to print a complete logic (language, extensions,
	// notes)

	public SMTLogic(final String name, final SMTTheory[] theories) {
		this.name = name;
		this.theories = theories.clone();
	}

	// TODO could be factorised with benchmarkCmdOpening and may be some other
	// similar methods
	private void logicCmdOpening(final StringBuilder sb) {
		sb.append(OPAR);
		sb.append(LOGIC);
		sb.append(SPACE);
		sb.append(name);
		sb.append("\n");
	}

	private void theoriesSection(final StringBuilder sb) {
		for (final SMTTheory theory : theories) {
			sb.append(" :");
			sb.append(THEORY);
			sb.append(SPACE);
			sb.append(theory.getName());
			sb.append("\n");
		}
	}

	public final String getName() {
		return name;
	}

	public final SMTTheory[] getTheories() {
		return theories.clone();
	}

	public final List<SMTSortSymbol> getSorts() {
		final List<SMTSortSymbol> sorts = new ArrayList<SMTSortSymbol>();
		for (final SMTTheory theory : theories) {
			sorts.addAll(theory.getSorts());
		}
		return sorts;
	}

	public final List<SMTPredicateSymbol> getPredicates() {
		final List<SMTPredicateSymbol> predicates = new ArrayList<SMTPredicateSymbol>();
		for (final SMTTheory theory : theories) {
			predicates.addAll(theory.getPredicates());
		}
		return predicates;
	}

	public final List<SMTFunctionSymbol> getFunctions() {
		final List<SMTFunctionSymbol> functions = new ArrayList<SMTFunctionSymbol>();
		for (final SMTTheory theory : theories) {
			functions.addAll(theory.getFunctions());
		}
		return functions;
	}

	public final SMTSortSymbol getIntegerSort() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof ISMTIntegerSort) {
				return ((ISMTIntegerSort) theory).getIntegerSort();
			}
		}
		return null;
	}

	public SMTSortSymbol getBooleanSort() {
		// for (final SMTTheory theory : theories) {
		// if (theory instanceof ISMTBooleanSort) {
		// return ((ISMTBooleanSort) theory).getBooleanSort();
		// }
		// }

		// FIXME: It's temporary, just for verification of function and
		// predicate ranks! It's necessary to add the Boolean Theory later.
		return Booleans.getInstance().getBooleanSort();
	}

	public final SMTSymbol getOperator(final SMTOperator operator) {
		switch (operator) {
		case GE:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticPreds) {
					return ((ISMTArithmeticPreds) theory).getGreaterEqual();
				}
			}
			return null;
		case GT:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticPreds) {
					return ((ISMTArithmeticPreds) theory).getGreaterThan();
				}
			}
			return null;
		case LE:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticPreds) {
					return ((ISMTArithmeticPreds) theory).getLessEqual();
				}
			}
			return null;
		case LT:
			for (final SMTTheory theory : theories) {

				if (theory instanceof ISMTArithmeticPreds) {
					return ((ISMTArithmeticPreds) theory).getLessThan();
				}
			}
			return null;
		case MINUS:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFuns) theory).getMinus();
				}
			}
			return null;
		case MUL:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFuns) theory).getMul();
				}
			}
			return null;
		case PLUS:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFuns) theory).getPlus();
				}
			}
			return null;
		case UMINUS:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFuns) theory).getUMinus();
				}
			}
			return null;
		case DIV:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFunsExtended) theory).getDiv();
				}
			}
			break;
		case DIV_Z3:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFunsExtended) theory).getDivZ3();
				}
			}
			break;
		case EXPN:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFunsExtended) theory).getExpn();
				}
			}
			break;

		}
		return null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		logicCmdOpening(sb);
		theoriesSection(sb);
		sb.append(CPAR);
		return sb.toString();
	}

	/**
	 * This class represents SMT operators.
	 */
	public static enum SMTOperator {
		GE(SMTSymbol.GE), GT(SMTSymbol.GT), LE(SMTSymbol.LE), LT(SMTSymbol.LT), MINUS(
				SMTSymbol.MINUS), MUL(SMTSymbol.MUL), PLUS(SMTSymbol.PLUS), UMINUS(
				SMTSymbol.UMINUS), DIV(SMTSymbol.DIV), DIV_Z3(SMTSymbol.DIV_Z3), EXPN(
				SMTSymbol.EXPN);

		private String symbol;

		SMTOperator(final String symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return symbol;
		}
	}

	/**
	 * This class represents VeriT extended SMT operators.
	 */
	public static enum SMTVeriTOperator {
		GE(SMTSymbol.GE), GT(SMTSymbol.GT), LE(SMTSymbol.LE), LT(SMTSymbol.LT), MINUS(
				SMTSymbol.MINUS), MUL(SMTSymbol.MUL), PLUS(SMTSymbol.PLUS), UMINUS(
				SMTSymbol.UMINUS), BUNION(SMTMacroSymbol.BUNION), BINTER(
				SMTMacroSymbol.BINTER), EMPTY(SMTMacroSymbol.EMPTY), INTER(
				SMTMacroSymbol.INTER), SETMINUS(SMTMacroSymbol.SETMINUS), IN(
				SMTMacroSymbol.IN), SUBSETEQ(SMTMacroSymbol.SUBSETEQ), SUBSET(
				SMTMacroSymbol.SUBSET), RANGE_INTEGER(
				SMTMacroSymbol.RANGE_INTEGER), PROD(SMTMacroSymbol.PROD), DOM(
				SMTMacroSymbol.DOM),
		// RAN(SMTMacroSymbol.RAN),
		IMG(SMTMacroSymbol.IMG), DOMR(SMTMacroSymbol.DOMR), DOMS(
				SMTMacroSymbol.DOMS), INV(SMTMacroSymbol.INV), OVR(
				SMTMacroSymbol.OVR), ID(SMTMacroSymbol.ID), FCOMP(
				SMTMacroSymbol.FCOMP), ENUM(SMTMacroSymbol.ENUM), DIV(
				SMTMacroSymbol.DIV), MOD(SMTMacroSymbol.MOD), RANGE_SUBSTRACTION(
				SMTMacroSymbol.RANGE_SUBSTRACION), RANGE_RESTRICTION(
				SMTMacroSymbol.RANGE_RESTRICTION), RELATION(
				SMTMacroSymbol.RELATION), TOTAL_RELATION(
				SMTMacroSymbol.TOTAL_RELATION), SURJECTIVE_RELATION(
				SMTMacroSymbol.SURJECTIVE_RELATION), TOTAL_SURJECTIVE_RELATION(
				SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION), PARTIAL_FUNCTION(
				SMTMacroSymbol.PARTIAL_FUNCTION), TOTAL_FUNCTION(
				SMTMacroSymbol.TOTAL_FUNCTION), MAPSTO(SMTMacroSymbol.MAPSTO), NAT(
				SMTMacroSymbol.NAT), NAT1(SMTMacroSymbol.NAT1), PARTIAL_INJECTION(
				SMTMacroSymbol.PARTIAL_INJECTION), TOTAL_INJECTION(
				SMTMacroSymbol.TOTAL_INJECTION), PARTIAL_SURJECTION(
				SMTMacroSymbol.PARTIAL_SURJECTION), TOTAL_SURJECTION(
				SMTMacroSymbol.TOTAL_SURJECTION), TOTAL_BIJECTION(
				SMTMacroSymbol.TOTAL_BIJECTION), CARTESIAN_PRODUCT(
				SMTMacroSymbol.CARTESIAN_PRODUCT), DOMAIN_RESTRICTION(
				SMTMacroSymbol.DOMAIN_RESTRICTION), DOMAIN_SUBSTRACTION(
				SMTMacroSymbol.DOMAIN_SUBSTRACTION), RELATIONAL_IMAGE(
				SMTMacroSymbol.RELATIONAL_IMAGE), ISMIN(SMTMacroSymbol.ISMIN), ISMAX(
				SMTMacroSymbol.ISMAX), FINITE(SMTMacroSymbol.FINITE), CARD(
				SMTMacroSymbol.CARD), PAIR(SMTMacroSymbol.PAIR), FUNP(
				SMTMacroSymbol.FUNP), INJP(SMTMacroSymbol.INJP), RANGE(
				SMTMacroSymbol.RANGE), NOT_EQUAL(SMTMacroSymbol.NOT_EQUAL), BCOMP(
				SMTMacroSymbol.BCOMP), INTEGER(SMTMacroSymbol.INT);

		private String symbol;

		SMTVeriTOperator(final String symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return symbol;
		}
	}

	/**
	 * "Version 1.2 of the SMT-LIB format adopts as its underlying logic a basic
	 * many-sorted version of first-order logic with equality. This logic allows
	 * the definition of sorts and of sorted symbols but does not allow more
	 * sophisticated constructs such as subsorts, sort constructors, explicit
	 * sort declarations for terms, and so on."
	 */
	public static class SMTLIBUnderlyingLogic extends SMTLogic {
		private static final SMTTheory[] THEORIES = { SMTTheory.Ints
				.getInstance() };

		private static final SMTLIBUnderlyingLogic INSTANCE = new SMTLIBUnderlyingLogic();

		protected SMTLIBUnderlyingLogic() {
			super(UNKNOWN, THEORIES);
		}

		protected SMTLIBUnderlyingLogic(final String name) {
			super(name, THEORIES);
		}

		public static SMTLIBUnderlyingLogic getInstance() {
			return INSTANCE;
		}
	}

	public static class VeriTSMTLIBUnderlyingLogic extends SMTLogic {

		private static final SMTTheory[] THEORIES = { VeritPredefinedTheory
				.getInstance() };

		private static final VeriTSMTLIBUnderlyingLogic INSTANCE = new VeriTSMTLIBUnderlyingLogic();

		protected VeriTSMTLIBUnderlyingLogic() {
			super(UNKNOWN, THEORIES);
		}

		protected VeriTSMTLIBUnderlyingLogic(final String name) {
			super(name, THEORIES);
		}

		public static VeriTSMTLIBUnderlyingLogic getInstance() {
			return INSTANCE;
		}
	}

	// FIXME provers seems to be unable to use predefined logics
	public static class UFNIA extends SMTLIBUnderlyingLogic {
		private static final String UFNIA = "UFNIA";

		private static final UFNIA INSTANCE = new UFNIA();

		private UFNIA() {
			super(UFNIA);
		}

		public static UFNIA getInstance() {
			return INSTANCE;
		}
	}

	public static class LIA extends SMTLIBUnderlyingLogic {
		private static final String LIA = "LIA";

		private static final LIA INSTANCE = new LIA();

		private LIA() {
			super(LIA);
		}

		public static LIA getInstance() {
			return INSTANCE;
		}
	}

	public static class AUFLIA extends SMTLIBUnderlyingLogic {
		private static final String AUFLIA = "AUFLIA";

		private static final AUFLIA INSTANCE = new AUFLIA();

		private AUFLIA() {
			super(AUFLIA);
		}

		public static AUFLIA getInstance() {
			return INSTANCE;
		}
	}

	public SMTFunctionSymbol getIntegerSortCst() {
		for (SMTTheory theory : theories) {
			if (theory instanceof Ints) {
				SMTSortSymbol integerSort = ((Ints) theory).getIntegerSort();
				SMTSortSymbol[] argSorts = {};
				SMTFunctionSymbol integerSortFunction = new SMTFunctionSymbol(
						"Int", argSorts, integerSort, false, true);
				return integerSortFunction;
			}
		}
		throw new IllegalArgumentException(
				"The Int sort is not declared in the signature of this benchmark");
	}

	public SMTFunctionSymbol getBooleanCste() {
		for (SMTTheory theory : theories) {
			if (theory instanceof VeritPredefinedTheory) {
				SMTSortSymbol boolSort = ((VeritPredefinedTheory) theory)
						.getBooleanSort();
				SMTSortSymbol[] argSorts = {};
				SMTFunctionSymbol boolSortFunction = new SMTFunctionSymbol(
						SMTSymbol.BOOL_SORT, argSorts, boolSort, false, true);
				return boolSortFunction;
			}
		}
		throw new IllegalArgumentException(
				"The Int sort is not declared in the signature of this benchmark");
	}

	public SMTSymbol getIntegerOneSortCst() {
		// TODO Auto-generated method stub
		return null;
	}
}
