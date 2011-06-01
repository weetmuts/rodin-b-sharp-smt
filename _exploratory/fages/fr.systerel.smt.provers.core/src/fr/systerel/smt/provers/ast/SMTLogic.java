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

	/**
	 * Constructs a new SMTLogic
	 * 
	 * @param name
	 *            the name of the SMTLogic
	 * @param theories
	 *            the theories used in the logic
	 **/
	public SMTLogic(final String name, final SMTTheory... theories) {
		this.name = name;
		this.theories = theories.clone();
	}

	/**
	 * appends the string representation of the theories section to the String
	 * Builder.
	 * 
	 * @param sb
	 *            the builder that will receive the string representation of the
	 *            theories section.
	 */
	private void theoriesSection(final StringBuilder sb) {
		for (final SMTTheory theory : theories) {
			sb.append(" :");
			sb.append(THEORY);
			sb.append(SPACE);
			sb.append(theory.getName());
			sb.append("\n");
		}
	}

	/**
	 * returns the name of the logic.
	 * 
	 * @return the name of the logic.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * returns the used theories of this logic.
	 * 
	 * @return the used theories of this logic.
	 */
	public final SMTTheory[] getTheories() {
		return theories.clone();
	}

	/**
	 * returns the defined sorts of all the included theories of the instance of
	 * the logic.
	 * 
	 * @return the sorts as explained above.
	 */
	public final List<SMTSortSymbol> getSorts() {
		final List<SMTSortSymbol> sorts = new ArrayList<SMTSortSymbol>();
		for (final SMTTheory theory : theories) {
			sorts.addAll(theory.getSorts());
		}
		return sorts;
	}

	/**
	 * returns the defined predicates of all the included theories of the
	 * instance of the logic.
	 * 
	 * @return the predicates as explained above.
	 */
	public final List<SMTPredicateSymbol> getPredicates() {
		final List<SMTPredicateSymbol> predicates = new ArrayList<SMTPredicateSymbol>();
		for (final SMTTheory theory : theories) {
			predicates.addAll(theory.getPredicates());
		}
		return predicates;
	}

	/**
	 * returns the defined functions of all the included theories of the
	 * instance of the logic.
	 * 
	 * @return the functions as explained above.
	 */
	public final List<SMTFunctionSymbol> getFunctions() {
		final List<SMTFunctionSymbol> functions = new ArrayList<SMTFunctionSymbol>();
		for (final SMTTheory theory : theories) {
			functions.addAll(theory.getFunctions());
		}
		return functions;
	}

	/**
	 * returns the integer sort if the logic contains a theory that defines the
	 * integer sort, otherwise it returns null.
	 * 
	 * @return the integer sort as defined above.
	 */
	public final SMTSortSymbol getIntegerSort() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof ISMTIntegerSort) {
				return ((ISMTIntegerSort) theory).getIntegerSort();
			}
		}
		return null;
	}

	/**
	 * returns the boolean sort if the logic contains a theory that defines the
	 * boolean sort, otherwise it returns null.
	 * 
	 * @return the boolean sort as defined above.
	 */
	public SMTSortSymbol getBooleanSort() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof ISMTBooleanSort) {
				return ((ISMTBooleanSort) theory).getBooleanSort();
			}
		}
		return null;
	}

	/**
	 * Given the operator value, it returns the corresponding SMT symbol.
	 * 
	 * @param operator
	 *            the operator code
	 * @return the corresponding SMT Symbol
	 */
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
				if (theory instanceof ISMTArithmeticFuns) {
					return ((ISMTArithmeticFuns) theory).getMinus();
				}
			}
			return null;
		case MUL:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFuns) {
					return ((ISMTArithmeticFuns) theory).getMul();
				}
			}
			return null;
		case PLUS:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFuns) {
					return ((ISMTArithmeticFuns) theory).getPlus();
				}
			}
			return null;
		case UMINUS:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFuns) {
					return ((ISMTArithmeticFuns) theory).getUMinus();
				}
			}
			return null;
		}
		return null;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	/**
	 * Appends in the StringBuilder a string representation of this logic.
	 * 
	 * @param sb
	 *            the StringBuilder that will receive the string representation.
	 */
	public void toString(final StringBuilder sb) {
		SMTBenchmark.smtCmdOpening(sb, LOGIC, name);
		theoriesSection(sb);
		sb.append(CPAR);
	}

	/**
	 * This class represents SMT operators.
	 */
	public static enum SMTOperator {
		GE(SMTSymbol.GE), GT(SMTSymbol.GT), LE(SMTSymbol.LE), LT(SMTSymbol.LT), MINUS(
				SMTSymbol.MINUS), MUL(SMTSymbol.MUL), PLUS(SMTSymbol.PLUS), UMINUS(
				SMTSymbol.UMINUS);

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
				SMTMacroSymbol.FCOMP), ENUM(SMTMacroSymbol.ENUM), RANGE_SUBSTRACTION(
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

		/**
		 * The symbol string.
		 */
		private String symbol;

		/**
		 * THe Constructor of the enumeration
		 * 
		 * @param symbol
		 *            the String value of the operator.
		 */
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

	/**
	 * This class represents the SMT underlying logic used by veriT. It differs
	 * from the standard underlying logic.
	 */
	public static class VeriTSMTLIBUnderlyingLogic extends SMTLogic {

		/**
		 * The theories used by the veriT logic.
		 */
		private static final SMTTheory[] THEORIES = { VeritPredefinedTheory
				.getInstance() };

		/**
		 * The instance of the underlying logic.
		 */
		private static final VeriTSMTLIBUnderlyingLogic INSTANCE = new VeriTSMTLIBUnderlyingLogic();

		/**
		 * The constructor of the logic.
		 */
		private VeriTSMTLIBUnderlyingLogic() {
			super(UNKNOWN, THEORIES);
		}

		/**
		 * returns an instance of the VeriT logic.
		 * 
		 * @return an instance of the VeriT logic.
		 */
		public static VeriTSMTLIBUnderlyingLogic getInstance() {
			return INSTANCE;
		}
	}

	/**
	 * This class represents the UFNIA logic
	 * 
	 */
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

	/**
	 * This class represents the LIA logic
	 * 
	 */
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

	/**
	 * This class represents the AUFLIA logic.
	 */
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

	/**
	 * returns the integer sort constant symbol.
	 * 
	 * @return the integer sort constant symbol.
	 */
	public SMTFunctionSymbol getIntegerSortCst() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof Ints) {
				final SMTSortSymbol integerSort = ((Ints) theory)
						.getIntegerSort();
				final SMTSortSymbol[] argSorts = {};
				final SMTFunctionSymbol integerSortFunction = new SMTFunctionSymbol(
						"Int", integerSort, false, true, argSorts);
				return integerSortFunction;
			}
		}
		throw new IllegalArgumentException(
				"The Int sort is not declared in the signature of this benchmark");
	}

	/**
	 * returns the boolean sort constant symbol.
	 * 
	 * @return the boolean sort constant symbol.
	 */
	public SMTFunctionSymbol getBooleanCste() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof VeritPredefinedTheory) {
				return VeritPredefinedTheory.getBoolFunction();
			} else if (theory instanceof Booleans) {
				return Booleans.getBoolCste();
			}
		}
		return null;
	}

	/**
	 * returns the {@code true} predicate symbol.
	 * 
	 * @return the {@code true} predicate symbol.
	 */
	public SMTPredicateSymbol getTrue() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof Booleans) {
				return Booleans.getTrue();
			}
		}
		return null;
	}
}
