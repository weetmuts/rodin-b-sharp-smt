/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - veriT logics
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.theories;

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.LOGIC;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.List;

import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;

/**
 * The SMT logics.
 * 
 * TODO: Implement methods/classes to define new logics/theories. For that it is
 * necessary to discover and standardize how to add new logics/theories to the
 * solvers.
 */
public class Logic {

	public static final String UNKNOWN = "UNKNOWN";

	/** The logic name and symbols */
	private final String name;
	protected final Theory[] theories;

	/**
	 * Constructs a new Logic
	 * 
	 * @param name
	 *            the name of the Logic
	 * @param theories
	 *            the theories used in the logic
	 **/
	public Logic(final String name, final Theory... theories) {
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
		for (final Theory theory : theories) {
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
	public final Theory[] getTheories() {
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
		for (final Theory theory : theories) {
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
		for (final Theory theory : theories) {
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
		for (final Theory theory : theories) {
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
		for (final Theory theory : theories) {
			if (theory instanceof IIntSort) {
				return ((IIntSort) theory).getIntegerSort();
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
		for (final Theory theory : theories) {
			if (theory instanceof IBoolSort) {
				return ((IBoolSort) theory).getBooleanSort();
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
			for (final Theory theory : theories) {
				if (theory instanceof IArithPreds) {
					return ((IArithPreds) theory).getGreaterEqual();
				}
			}
			return null;
		case GT:
			for (final Theory theory : theories) {
				if (theory instanceof IArithPreds) {
					return ((IArithPreds) theory).getGreaterThan();
				}
			}
			return null;
		case LE:
			for (final Theory theory : theories) {
				if (theory instanceof IArithPreds) {
					return ((IArithPreds) theory).getLessEqual();
				}
			}
			return null;
		case LT:
			for (final Theory theory : theories) {

				if (theory instanceof IArithPreds) {
					return ((IArithPreds) theory).getLessThan();
				}
			}
			return null;
		case MINUS:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFuns) {
					return ((IArithFuns) theory).getMinus();
				}
			}
			return null;
		case MUL:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFuns) {
					return ((IArithFuns) theory).getMul();
				}
			}
			return null;
		case PLUS:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFuns) {
					return ((IArithFuns) theory).getPlus();
				}
			}
			return null;
		case UMINUS:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFuns) {
					return ((IArithFuns) theory).getUMinus();
				}
			}
			return null;
		case DIV:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFunsExt) {
					return ((IArithFunsExt) theory).getDiv();
				}
			}
			return null;
		case EXPN:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFunsExt) {
					return ((IArithFunsExt) theory).getExpn();
				}
			}
			return null;
		case MOD:
			for (final Theory theory : theories) {
				if (theory instanceof IArithFunsExt) {
					return ((IArithFunsExt) theory).getMod();
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
	public static class SMTLogicPP extends Logic {
		public SMTLogicPP(final String name, final Theory... theories) {
			super(name, theories);
		}

		public SMTSortSymbol getPowerSetIntegerSort() {
			for (final Theory theory : theories) {
				if (theory instanceof TheoryV2_0.Ints) {
					return ((TheoryV2_0.Ints) theory)
							.getPowerSetIntegerSort();
				}
			}
			return null;
		}

		public SMTSortSymbol getPowerSetBooleanSort() {
			for (final Theory theory : theories) {
				if (theory instanceof TheoryV2_0.Core) {
					return ((TheoryV2_0.Core) theory)
							.getPowerSetBooleanSort();
				}
			}
			return null;
		}
	}

	/**
	 * Unquantified formulas built over a signature of uninterpreted (i.e.,
	 * free) sort and function symbols.
	 * 
	 * @author Yoann Guyot
	 * 
	 */
	public static class QF_UFv2_0 extends SMTLogicPP {
		private static final String QF_UF_LOGIC_NAME = "QF_UF";
		private static final Theory[] THEORIES = { TheoryV2_0.Core
				.getInstance() };

		private static final QF_UFv2_0 INSTANCE = new QF_UFv2_0();

		private QF_UFv2_0() {
			super(QF_UF_LOGIC_NAME, THEORIES);
		}

		public static QF_UFv2_0 getInstance() {
			return INSTANCE;
		}
	}

	/**
	 * Closed quantifier-free linear formulas over the theory of integer arrays
	 * extended with free sort and function symbols.
	 * 
	 * @author Yoann Guyot
	 * 
	 */
	public static class QF_AUFLIAv2_0 extends SMTLogicPP {
		private static final String QF_AUFLIA_LOGIC_NAME = "QF_AUFLIA";
		private static final Theory[] THEORIES = {
				TheoryV2_0.Core.getInstance(),
				TheoryV2_0.Ints.getInstance() };

		private static final QF_AUFLIAv2_0 INSTANCE = new QF_AUFLIAv2_0();

		private QF_AUFLIAv2_0() {
			super(QF_AUFLIA_LOGIC_NAME, THEORIES);
		}

		public static QF_AUFLIAv2_0 getInstance() {
			return INSTANCE;
		}
	}

	/**
	 * Closed formulas over the theory of linear integer arithmetic and arrays
	 * extended with free sort and function symbols but restricted to arrays
	 * with integer indices and values.
	 * 
	 * @author Yoann Guyot
	 * 
	 */
	public static class AUFLIAv2_0 extends SMTLogicPP {
		private static final String AUFLIA_LOGIC_NAME = "AUFLIA";
		private static final Theory[] THEORIES = {
				TheoryV2_0.Core.getInstance(),
				TheoryV2_0.Ints.getInstance() };

		private static final AUFLIAv2_0 INSTANCE = new AUFLIAv2_0();

		private AUFLIAv2_0() {
			super(AUFLIA_LOGIC_NAME, THEORIES);
		}

		public static AUFLIAv2_0 getInstance() {
			return INSTANCE;
		}
	}

	/**
	 * returns the integer sort constant symbol.
	 * 
	 * @return the integer sort constant symbol.
	 */
	public SMTFunctionSymbol getIntsSet() {
		for (final Theory theory : theories) {
			if (theory instanceof TheoryV2_0.Ints) {
				return TheoryV2_0.Ints.getIntsSet();
			}
		}
		return null;
	}

	/**
	 * returns the boolean sort constant symbol.
	 * 
	 * @return the boolean sort constant symbol.
	 */
	public SMTFunctionSymbol getBoolsSet() {
		for (final Theory theory : theories) {
			if (theory instanceof TheoryV2_0.Core) {
				return TheoryV2_0.Core.getBoolsSet();
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
		for (final Theory theory : theories) {
			if (theory instanceof TheoryV2_0.Core) {
				return TheoryV2_0.Core.getTrue();
			}
		}
		return null;
	}

}
