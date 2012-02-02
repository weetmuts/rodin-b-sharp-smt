/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.translation;

import static org.eventb.smt.internal.translation.SMTThroughPP.GATHER_SPECIAL_MS_PREDS;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * This class is used to traverse the Event-B sequent in order to gather some
 * informations needed to proceed with the translation, such as:
 * <ul>
 * <li>the appearing of occurrences of the Event-B integer symbol;</li>
 * <li>the appearing of elements of the bool theory;</li>
 * <li>the need for using the True predicate;</li>
 * <li>the list of the sets to be translated into specialised membership
 * predicates.</li>
 * </ul>
 **/
public class Gatherer extends DefaultVisitor {
	private boolean atomicIntegerExpFound = false;
	private boolean boolTheory = false;
	private boolean intTheory = false;
	private boolean atomicBoolExpFound = false;
	private boolean usesTruePredicate = false;
	private boolean quantifierFound = false;
	private boolean uncoveredArithFound = false;
	private final Set<FreeIdentifier> setsForSpecialMSPreds = new HashSet<FreeIdentifier>();
	private final Set<Type> boundSetsTypes = new HashSet<Type>();
	private final Set<FreeIdentifier> leftMSHandSideSets = new HashSet<FreeIdentifier>();

	/**
	 * This method recursively traverses the type tree to check if it contains
	 * the boolean type.
	 */
	private static boolean booleanTypeInTypeTree(final Type type) {
		final boolean isAProductType = type instanceof ProductType;
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null && !isAProductType) {
			return type instanceof BooleanType;
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) type;
			return booleanTypeInTypeTree(product.getLeft())
					|| booleanTypeInTypeTree(product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (type.getSource() != null) {
			return booleanTypeInTypeTree(type.getSource())
					|| booleanTypeInTypeTree(type.getTarget());
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha.
		 */
		else if (type.getBaseType() != null) {
			return booleanTypeInTypeTree(type.getBaseType());
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
	 * This method is used to gather from the relational predicate, the sets for
	 * which specialised membership predicates (monadic or dyadic) should be
	 * used. If the right side of the relation is a free identifier, then this
	 * identifier is added to the specialised membership predicates set. Else if
	 * it is bound identifier, the type of the bound identifier is added to the
	 * set of bound types.
	 * 
	 * @param pred
	 *            the relational predicate
	 */
	private void gatherSetsForSpecialMSPreds(final RelationalPredicate pred) {
		final Expression right = pred.getRight();
		final FreeIdentifier lefts[] = pred.getLeft().getFreeIdentifiers();

		for (final FreeIdentifier ident : lefts) {
			if (ident.getType().getBaseType() != null
					|| ident.getType().getSource() != null) {
				leftMSHandSideSets.add(ident);
			}
		}

		if (right instanceof FreeIdentifier) {
			final FreeIdentifier rightSet = (FreeIdentifier) right;
			setsForSpecialMSPreds.add(rightSet);
		} else if (right instanceof BoundIdentifier) {
			boundSetsTypes.add(((BoundIdentifier) right).getType());
		}
	}

	/**
	 * This method extracts all the setsForSpecialMSPreds that will be
	 * translated in optimised membership predicates, that is, the
	 * setsForSpecialMSPreds complying with the following rules:
	 * 
	 * <ul>
	 * <li>The set only occur on the right-hand side of membership predicates;
	 * <li>No bound variable occurs in the right-hand side of similar membership
	 * predicates;
	 * </ul>
	 * 
	 * Then these setsForSpecialMSPreds are used as operator with one argument,
	 * instead of creating a fresh membership predicate where the set is one of
	 * the arguments.
	 * 
	 * When a set is removed from this set, a general membership predicate
	 * symbol will be used, and the axiom of singleton will be generated. This
	 * axiom contains some quantifiers.
	 */
	private void removeIdentsFromSetsForSpecialMSPreds() {
		/**
		 * Removal of all bounded variables from the setsForSpecialMSPreds.
		 */
		final Iterator<FreeIdentifier> setsForSpecialMSPredsIterator = setsForSpecialMSPreds
				.iterator();
		while (setsForSpecialMSPredsIterator.hasNext()) {
			final FreeIdentifier set = setsForSpecialMSPredsIterator.next();
			if (boundSetsTypes.contains(set.getType())) {
				setsForSpecialMSPredsIterator.remove();
				quantifierFound = true;
			} else if (leftMSHandSideSets.contains(set)) {
				setsForSpecialMSPredsIterator.remove();
				quantifierFound = true;
			}
		}
	}

	/**
	 * This method executes the traversal in the hypotheses and goal of the
	 * given sequent to process the informations described in {@link Gatherer}.
	 * It also makes the mapping of each free identifier to its correlated
	 * predicate symbol.
	 * 
	 * @param sequent
	 *            the sequent of which predicates must be traversed
	 * @return a new gatherer with the results of the traversal.
	 */
	public static Gatherer gatherFrom(final ISimpleSequent sequent) {
		final Gatherer gatherer = new Gatherer();

		for (final ITrackedPredicate trPredicate : sequent.getPredicates()) {
			trPredicate.getPredicate().accept(gatherer);
		}

		gatherer.removeIdentsFromSetsForSpecialMSPreds();
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
	 * returns true if the theory of integers is used in the PO. That is, one of
	 * the arithmetic symbols (negation, subtraction, +, *, <=, <, >=, >) of the
	 * SMT-LIB Ints theory appears (div, mod and abs are currently translated in
	 * uninterpreted symbols).
	 * 
	 * @return true if the theory of integers is used in the PO.
	 */
	public boolean usesIntTheory() {
		return intTheory;
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
	 * @return true if the atomic expression BOOL was found in the sequent
	 */
	public boolean foundAtomicBoolExp() {
		return atomicBoolExpFound;
	}

	/**
	 * @return true if one or more quantifier, or membership were found in the
	 *         sequent
	 */
	public boolean foundQuantifier() {
		return quantifierFound;
	}

	public boolean foundUncoveredArith() {
		return uncoveredArithFound;
	}

	/**
	 * returns the gathered sets for which specialised membership predicates
	 * should be used
	 * 
	 * @return the set of identifiers
	 */
	public Set<FreeIdentifier> getSetsForSpecialMSPreds() {
		return setsForSpecialMSPreds;
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(final BoundIdentDecl ident) {
		quantifierFound = true;
		if (booleanTypeInTypeTree(ident.getType())) {
			boolTheory = true;
			usesTruePredicate = true;
		}
		return true;
	}

	@Override
	public boolean enterGE(RelationalPredicate pred) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterGT(RelationalPredicate pred) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterLE(RelationalPredicate pred) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterLT(RelationalPredicate pred) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterMINUS(BinaryExpression expr) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterMUL(AssociativeExpression expr) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterPLUS(AssociativeExpression expr) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterUNMINUS(UnaryExpression expr) {
		intTheory = true;
		return true;
	}

	@Override
	public boolean enterDIV(BinaryExpression expr) {
		uncoveredArithFound = true;
		return true;
	}

	@Override
	public boolean enterMOD(BinaryExpression expr) {
		uncoveredArithFound = true;
		return true;
	}

	@Override
	public boolean enterEXPN(BinaryExpression expr) {
		uncoveredArithFound = true;
		return true;
	}

	@Override
	public boolean visitFREE_IDENT(final FreeIdentifier ident) {
		if (booleanTypeInTypeTree(ident.getType())) {
			boolTheory = true;
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
		if (GATHER_SPECIAL_MS_PREDS)
			gatherSetsForSpecialMSPreds(membershipPredicate);
		if (booleanTypeInTypeTree(membershipPredicate.getLeft().getType())) {
			boolTheory = true;
			usesTruePredicate = true;
		}
		return true;
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
