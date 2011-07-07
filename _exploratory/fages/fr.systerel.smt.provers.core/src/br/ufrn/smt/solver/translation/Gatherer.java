/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *     Vitor Alcantar de Almeida - implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

/**
 * This class is used to traverse the Event-B sequent in order to gather some
 * informations needed to proceed with the translation, such as:
 * <ul>
 * <li>the appearing of occurrences of the Event-B integer symbol;</li>
 * <li>the appearing of elements of the bool theory;</li>
 * <li>the need for using the True predicate;</li>
 * <li>the list of the sets to be translated into monadic membership predicates.
 * </li>
 * </ul>
 **/
public class Gatherer extends DefaultVisitor {
	private boolean atomicIntegerExpFound = false;
	private boolean boolTheory = false;
	private boolean atomicBoolExpFound = false;
	private boolean usesTruePredicate = false;
	private final Set<FreeIdentifier> setsForMonadicPreds = new HashSet<FreeIdentifier>();
	private final Set<Type> boundSetsTypes = new HashSet<Type>();

	/**
	 * This method recursively traverses the type tree to check if it contains
	 * the boolean type.
	 */
	private boolean booleanTypeInTypeTree(final Type rightType) {
		final boolean isAProductType = rightType instanceof ProductType;
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (rightType.getSource() == null && rightType.getTarget() == null
				&& rightType.getBaseType() == null && !isAProductType) {
			return rightType instanceof BooleanType;
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) rightType;
			return booleanTypeInTypeTree(product.getLeft())
					|| booleanTypeInTypeTree(product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (rightType.getSource() != null) {
			return booleanTypeInTypeTree(rightType.getSource())
					|| booleanTypeInTypeTree(rightType.getTarget());
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha.
		 */
		else if (rightType.getBaseType() != null) {
			return booleanTypeInTypeTree(rightType.getBaseType());
		}

		/**
		 * This case should not be reached because Event-B types given as
		 * arguments are well-formed.
		 */
		else {
			throw new IllegalArgumentException(Messages.Misformed_EventB_Types);
		}
	}

	/**
	 * This method is used to gather the monadic preds from the relational
	 * predicate. If the right side of the relation is a free identifier, then
	 * this identifier is added to the monadic preds set. Else if it is bound
	 * identifier, the type of the bound identifier is added to the set of bound
	 * types.
	 * 
	 * @param pred
	 *            the relational predicate
	 */
	private void gatherMonadicPreds(final RelationalPredicate pred) {
		/**
		 * Code for of membership predicate optimization
		 */
		final Expression right = pred.getRight();

		assert right instanceof FreeIdentifier
				|| right instanceof BoundIdentifier;

		if (right instanceof FreeIdentifier) {
			if (right.getType().getSource() == null) {
				final FreeIdentifier rightSet = (FreeIdentifier) right;
				setsForMonadicPreds.add(rightSet);
			}
		} else if (right instanceof BoundIdentifier) {
			boundSetsTypes.add(((BoundIdentifier) right).getType());
		}
	}

	/**
	 * This method extracts all the setsForMonadicPreds that will be translated
	 * in optimized membership predicates, that is, the setsForMonadicPreds
	 * complying with the following rules:
	 * 
	 * <ul>
	 * <li>The set only occur on the right-hand side of membership predicates;
	 * <li>No bound variable occurs in the right-hand side of similar membership
	 * predicates;
	 * </ul>
	 * 
	 * Then these setsForMonadicPreds are used as operator with one argument,
	 * instead of creating a fresh membership predicate where the set is one of
	 * the arguments.
	 */
	private void removeIdentsFromSetsForMonadicPreds() {
		/**
		 * Removal of all bounded variables from the map of monadic
		 * setsForMonadicPreds.
		 */
		final Iterator<FreeIdentifier> setsForMonadicPredsIterator = setsForMonadicPreds.iterator();
		while (setsForMonadicPredsIterator.hasNext()) {
			final FreeIdentifier set = setsForMonadicPredsIterator.next();
			if (boundSetsTypes.contains(set.getType())) {
				setsForMonadicPreds.remove(set);
			}
		}
	}

	/**
	 * This method executes the traversal in the hpoytheses and predicates to
	 * process the informations described in {@link Gatherer}. It also makes the
	 * mapping of each free identifier to its correlated predicate symbol
	 * 
	 * @param hypotheses
	 *            The hypotheses
	 * @param goal
	 *            the goal
	 * @return a new gatherer with the results of the traversal.
	 */
	public static Gatherer gatherFrom(final List<Predicate> hypotheses,
			final Predicate goal) {
		final Gatherer gatherer = new Gatherer();

		for (final Predicate hypothesis : hypotheses) {
			hypothesis.accept(gatherer);
		}
		goal.accept(gatherer);

		gatherer.removeIdentsFromSetsForMonadicPreds();

		return gatherer;
	}

	/**
	 * returns true if the integer set is found in the PO.
	 * 
	 * @return true if the integer is found in the PO, false otherwise.
	 */
	public boolean foundAtomicIntegerExp() {
		return atomicIntegerExpFound;
	}

	/**
	 * returns true if the Bool Theory is used in the PO.
	 * 
	 * @return true if the Bool Theory is used, false otherwise.
	 */
	public boolean usesBoolTheory() {
		return boolTheory;
	}

	/**
	 * returns true if the True predicate needs to be used in the PO.
	 * 
	 * @return true if the True predicate needs to be used, false otherwise.
	 */
	public boolean usesTruePredicate() {
		return usesTruePredicate;
	}

	/**
	 * returns true if the atomic expression BOOL was found in the sequent
	 */
	public boolean foundAtomicBoolExp() {
		return atomicBoolExpFound;
	}

	/**
	 * returns the gathered sets for which monadic membership predicates should
	 * be used
	 * 
	 * @return the set of identifiers
	 */
	public Set<FreeIdentifier> getSetsForMonadicPreds() {
		return setsForMonadicPreds;
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(final BoundIdentDecl ident) {
		if (ident.getType() instanceof BooleanType) {
			boolTheory = true;
			usesTruePredicate = true;
			return false;
		}
		return true;
	}

	@Override
	public boolean visitINTEGER(final AtomicExpression expr) {
		atomicIntegerExpFound = true;
		return true;
	}

	/**
	 * If one of the predicates has a BOOL set, set <code>boolTheory</code>
	 * <i>true</i> and stop visiting.
	 */
	@Override
	public boolean visitBOOL(final AtomicExpression expr) {
		boolTheory = true;
		atomicBoolExpFound = true;
		return true;
	}

	@Override
	public boolean enterIN(final RelationalPredicate membershipPredicate) {
		gatherMonadicPreds(membershipPredicate);
		if (booleanTypeInTypeTree(membershipPredicate.getRight().getType())) {
			boolTheory = true;
			usesTruePredicate = true;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * If one of the predicates has a TRUE constant, set <code>boolTheory</code>
	 * <i>true</i> and stop visiting.
	 */
	@Override
	public boolean visitTRUE(final AtomicExpression expr) {
		boolTheory = true;
		return true;
	}
}
