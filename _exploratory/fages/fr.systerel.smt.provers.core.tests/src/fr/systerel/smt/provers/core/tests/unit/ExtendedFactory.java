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
package fr.systerel.smt.provers.core.tests.unit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Simple formula factory with two extended operators (one for expressions, one
 * for predicates). To be used for tests only.
 * 
 * @author Laurent Voisin
 */
public class ExtendedFactory {

	private static final IFormulaExtension foo = new IExpressionExtension() {

		@Override
		public Predicate getWDPredicate(final IExtendedFormula formula,
				final IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return "foo";
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public String getId() {
			return "foo";
		}

		@Override
		public String getGroupId() {
			return "foo";
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public void addPriorities(final IPriorityMediator mediator) {
			// Do nothing
		}

		@Override
		public void addCompatibilities(final ICompatibilityMediator mediator) {
			// Do nothing
		}

		@Override
		public boolean verifyType(final Type proposedType,
				final Expression[] childExprs, final Predicate[] childPreds) {
			return proposedType instanceof IntegerType;
		}

		@Override
		public Type typeCheck(final ExtendedExpression expression,
				final ITypeCheckMediator tcMediator) {
			final Type intType = tcMediator.makeIntegerType();
			tcMediator.sameType(expression.getType(), intType);
			return intType;
		}

		@Override
		public Type synthesizeType(final Expression[] childExprs,
				final Predicate[] childPreds, final ITypeMediator mediator) {
			return mediator.makeIntegerType();
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}
	};

	private static final IFormulaExtension bar = new IPredicateExtension() {

		@Override
		public String getSyntaxSymbol() {
			return "bar";
		}

		@Override
		public Predicate getWDPredicate(final IExtendedFormula formula,
				final IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return "bar";
		}

		@Override
		public String getGroupId() {
			return "bar";
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_PREDICATE;
		}

		@Override
		public Object getOrigin() {
			return this;
		}

		@Override
		public void addCompatibilities(final ICompatibilityMediator mediator) {
			// Do nothing
		}

		@Override
		public void addPriorities(final IPriorityMediator mediator) {
			// Do nothing
		}

		@Override
		public void typeCheck(final ExtendedPredicate predicate,
				final ITypeCheckMediator tcMediator) {
			final Expression child = predicate.getChildExpressions()[0];
			tcMediator.sameType(child.getType(), tcMediator.makeIntegerType());
		}
	};

	private static final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>(
			Arrays.asList(foo, bar));

	public static final FormulaFactory eff = FormulaFactory
			.getInstance(extensions);

}
