/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.macros;

import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTQuantifiedFormula;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * <p>
 * This class is used to store macros generated by the modified version of rules
 * 18 and 20. of the article Integration of SMT-Solvers in B and Event-B
 * Development Environments, from author DEHARBE, David, 2010.
 * </p>
 * 
 * 
 * </p>The rule 18 specify the translation of sets defined in intention and the
 * rule 20 specify the translation lambda terms. But, Rodin uses only one
 * structure to define both. So, the plugin produces only one type of macro for
 * both cases. Explaining technically the implementation:</p>
 * 
 * </p>The structure in Rodin for sets defined in intention and lambda
 * abstractions, labeled in Rodin as comprehension set, is:</p>
 * 
 * <code>{X1,...,Xn . P(X1...Xn) | E(X1...Xn) }</code>
 * 
 * <p>
 * where:
 * </p>
 * 
 * <ul>
 * <li>X1 … Xn are bound identifiers</li>
 * <li>P() is an Event-B predicate that contains the bound identifiers X1 … Xn</li>
 * <li>E() is an Event-B expression that contains the bound identifiers X1 … Xn</li>
 * </ul>
 * 
 * 
 * </p>The translation to Extended SMT-LIB of this structure is:</p>
 * 
 * <code>(lambda (?u S) . (exists (?x1 S1)...(?xn Sn)(and (= ?u smtE(X1,...,Xn)
 * smtP(X1,...Xn)))))</code>
 * 
 * <p>
 * where:
 * <p>
 * 
 * <ul>
 * <li>S is smtSort(E())</li>
 * <li>?u is a fresh smt variable with sort S</li>
 * <li>x1 = smt(X1), … , xn = smt(Xn)</li>
 * <li>S1 = smtSort(X1), … Sn = smtSort(Xn)</li>
 * <li>smtE = smt(E)</li>
 * <li>smtU = smt(U)</li>
 * </ul>
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class SMTSetComprehensionMacro extends SMTMacro {

	/**
	 * the ?u variable defined in {@link SMTSetComprehensionMacro}
	 */
	final SMTVarSymbol lambdaVar;

	/**
	 * The ?x1 ... ?xn variables defined in {@link SMTSetComprehensionMacro}
	 */
	final SMTVarSymbol[] qVars;

	/**
	 * The smtP formula defined in {@link SMTSetComprehensionMacro}
	 */
	final SMTFormula formula;

	/**
	 * The smtE term defined in {@link SMTSetComprehensionMacro}
	 */
	final SMTTerm expression;

	/**
	 * Initializes the class with the necessary parameters to create the set
	 * comprehension macro.
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param quantifiedVariables
	 *            The boundVariables of the set comprehension macro
	 * @param lambdaVar
	 *            the ?u var defined in {@link SMTSetComprehensionMacro}
	 * @param formula
	 *            The <code>smtP</code> formula defined in
	 *            {@link SMTSetComprehensionMacro}
	 * @param expression
	 *            The <code>smtE</code> expression defined in
	 *            {@link SMTSetComprehensionMacro}
	 * @param precedence
	 *            The precedence of the macro. See {@link SMTMacro} for more
	 *            details.
	 */
	SMTSetComprehensionMacro(final String macroName, final SMTVarSymbol[] quantifiedVariables,
			final SMTVarSymbol lambdaVar, final SMTFormula formula, final SMTTerm expression, final int precedence) {
		super(macroName, precedence);
		qVars = quantifiedVariables;
		this.lambdaVar = lambdaVar;
		this.formula = formula;
		this.expression = expression;
	}

	public SMTFormula getFormula() {
		return formula;
	}

	public SMTTerm getExpression() {
		return expression;
	}

	/**
	 * Retrieve the lambda variable.
	 * 
	 * @return the lambda variable
	 */
	public SMTVarSymbol getLambdaVar() {
		return lambdaVar;
	}

	/**
	 * get the bound identifiers
	 * 
	 * @return the bound identifiers
	 */
	public SMTVarSymbol[] getqVars() {
		return qVars;
	}

	@Override
	public void toString(final StringBuilder sb, final int offset) {
		sb.append(super.getMacroName());
		sb.append(" (");
		lambdaVar.toString(sb);
		sb.append(") ");
		sb.append("(exists (");
		for (final SMTVarSymbol qVar : qVars) {
			qVar.toString(sb);
		}
		sb.append(") (and (= ");
		sb.append(lambdaVar.getName());
		sb.append(" ");
		expression.toString(sb, offset);
		sb.append(") ");
		if (formula instanceof SMTQuantifiedFormula) {
			((SMTQuantifiedFormula) formula).toString(sb, offset, true);
		} else {
			formula.toString(sb, offset, true);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}
}
