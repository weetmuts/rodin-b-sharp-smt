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
	private boolean integerFound = false;
	private boolean boolTheory = false;
	private boolean usesTruePredicate = false;
	private final Set<FreeIdentifier> identsNotForMonadicPreds = new HashSet<FreeIdentifier>();
	private final Set<FreeIdentifier> setsForMonadicPreds = new HashSet<FreeIdentifier>();
	private final Set<Type> boundSetsTypes = new HashSet<Type>();

	private final static Gatherer DEFAULT_INSTANCE = new Gatherer();

	/**
	 * This method checks if the were found Boolean Elements in the relational
	 * predicate. If yes, it returns false and determine that the True predicate
	 * must be used.
	 * 
	 * @param pred
	 *            the relational predicate.
	 * @return true if there is no boolean element, false otherwise.
	 */
	private boolean checkBooleanElementsInMembershipPredicate(
			final RelationalPredicate pred) {
		if (pred.getLeft().getType() instanceof BooleanType
				|| pred.getRight().getType() instanceof BooleanType) {
			usesTruePredicate = true;
			boolTheory = true;
			return false;
		}
		return true;
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
		for (final FreeIdentifier set : setsForMonadicPreds) {
			if (boundSetsTypes.contains(set.getType())) {
				identsNotForMonadicPreds.add(set);
			}
		}
		setsForMonadicPreds.removeAll(identsNotForMonadicPreds);
	}

	public static Gatherer getInstance() {
		return DEFAULT_INSTANCE;
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
	 * return true if the integer set is found in the PO.
	 * 
	 * @return true if the integer is found in the PO, false otherwise.
	 */
	public boolean foundInteger() {
		return integerFound;
	}

	/**
	 * return true if the True predicate needs to be used in the PO.
	 * 
	 * @return true if the True predicate needs to be used, false otherwise.
	 */
	public boolean usesTruePredicate() {
		return usesTruePredicate;
	}

	/**
	 * return true if the Bool Theory is used in the PO.
	 * 
	 * @return true if the Bool Theory is used, false otherwise.
	 */
	public boolean usesBoolTheory() {
		return boolTheory;
	}

	/**
	 * return the gathered sets for which monadic membership predicates should be used
	 * 
	 * @return the set of identifiers
	 */
	public Set<FreeIdentifier> getSetsForMonadicPreds() {
		return setsForMonadicPreds;
	}

	@Override
	public boolean visitINTEGER(final AtomicExpression expr) {
		integerFound = true;
		return true;
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

	/**
	 * If one of the predicates has a BOOL set, set <code>boolTheory</code>
	 * <i>true</i> and stop visiting.
	 */
	@Override
	public boolean visitBOOL(final AtomicExpression expr) {
		boolTheory = true;
		return true;
	}

	@Override
	public boolean enterIN(final RelationalPredicate pred) {
		gatherMonadicPreds(pred);
		return checkBooleanElementsInMembershipPredicate(pred);
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
