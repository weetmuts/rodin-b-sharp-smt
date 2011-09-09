/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.theories;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;
import static org.eventb.smt.ast.symbols.SMTSymbol.LOGIC;
import static org.eventb.smt.ast.symbols.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.List;

import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTTheory.Booleans;
import org.eventb.smt.ast.theories.SMTTheory.Ints;


/**
 * The SMT logics.
 * 
 * TODO: Implement methods/classes to define new logics/theories. For that it is
 * necessary to discover and standardize how to add new logics/theories to the
 * solvers.
 */
public class SMTLogic {
	public static String UNKNOWN = "UNKNOWN";

	/** The logic name and symbols */
	private final String name;
	protected final SMTTheory[] theories;

	// TODO add fields needed to print a complete logic (language, extensions,
	// notes)

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
			return null;
		case EXPN:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFunsExtended) theory).getExpn();
				}
			}
			return null;
		case MOD:
			for (final SMTTheory theory : theories) {
				if (theory instanceof ISMTArithmeticFunsExtended) {
					return ((ISMTArithmeticFunsExtended) theory).getMod();
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
				SMTSymbol.UMINUS), DIV(SMTSymbol.DIV), EXPN(SMTSymbol.EXPN), MOD(
				SMTSymbol.MOD);

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
	 * This class represents the SMT underlying logic used by the PP approach.
	 * It differs from the standard underlying logic.
	 */
	public static class SMTLogicPP extends SMTLogic {
		public SMTLogicPP(final String name, final SMTTheory... theories) {
			super(name, theories);
		}

		public SMTSortSymbol getPowerSetIntegerSort() {
			for (final SMTTheory theory : theories) {
				if (theory instanceof Ints) {
					return ((Ints) theory).getPowerSetIntegerSort();
				}
			}
			return null;
		}

		public SMTSortSymbol getPowerSetBooleanSort() {
			for (final SMTTheory theory : theories) {
				if (theory instanceof Booleans) {
					return ((Booleans) theory).getPowerSetBooleanSort();
				}
			}
			return null;
		}
	}

	/**
	 * "Version 1.2 of the SMT-LIB format adopts as its underlying logic a basic
	 * many-sorted version of first-order logic with equality. This logic allows
	 * the definition of sorts and of sorted symbols but does not allow more
	 * sophisticated constructs such as subsorts, sort constructors, explicit
	 * sort declarations for terms, and so on."
	 */
	public static class SMTLIBUnderlyingLogic extends SMTLogicPP {
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

	public static class SMTLogicVeriT extends SMTLogic {
		public SMTLogicVeriT(final String name, final SMTTheory... theories) {
			super(name, theories);
		}
	}

	/**
	 * This class represents the SMT underlying logic used by veriT. It differs
	 * from the standard underlying logic.
	 */
	public static class VeriTSMTLIBUnderlyingLogic extends SMTLogicVeriT {

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
	 * returns the integer sort constant symbol.
	 * 
	 * @return the integer sort constant symbol.
	 */
	public SMTFunctionSymbol getIntsSet() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof Ints) {
				return Ints.getIntsSet();
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
	public SMTFunctionSymbol getBoolsSet() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof Booleans) {
				return Booleans.getBoolsSet();
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

	public SMTFunctionSymbol getTrueConstant() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof VeriTBooleans) {
				return VeriTBooleans.getInstance().getTrueConstant();
			}
		}
		return null;
	}

	public SMTFunctionSymbol getFalseConstant() {
		for (final SMTTheory theory : theories) {
			if (theory instanceof VeriTBooleans) {
				return VeriTBooleans.getInstance().getFalseConstant();
			}
		}
		return null;
	}
}
