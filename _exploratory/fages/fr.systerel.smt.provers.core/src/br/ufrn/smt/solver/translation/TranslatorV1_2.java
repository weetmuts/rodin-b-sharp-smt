/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;

import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class is a translator from Event-B syntax into SMT-LIB version 1.2
 * syntax.
 */
public abstract class TranslatorV1_2 extends Translator {
	protected static final boolean IN_GOAL = true;

	/**
	 * The target solver of the translation. It is used to check which
	 * translation must be used depending of the solver
	 */
	protected String solver;

	/**
	 * When the translator finishes translating a quantified predicate, it
	 * deletes all the bound identifiers of that predicate. In nested quantified
	 * predicates, the translator must delete the bound identifier declarations
	 * inside.
	 */
	protected Stack<Integer> boundIdentifiersMarker = new Stack<Integer>();

	/**
	 * This variable stores the name of bound identifiers of the actual
	 * predicate being translated.
	 */
	protected List<String> boundIdentifiers = new ArrayList<String>();

	/**
	 * This variable maps names to SMT bound variables.
	 */
	protected final Map<String, SMTVar> qVarMap = new HashMap<String, SMTVar>();

	/**
	 * Constructs the translator with the solver
	 * 
	 * @param solver
	 *            the target solver of the translated SMT-LIB file
	 */
	public TranslatorV1_2(final String solver) {
		this.solver = solver;
	}

	/**
	 * Extracts the type environment of a Predicate needed to build an SMT-LIB
	 * benchmark's signature, that is, free identifiers and given types.
	 * 
	 * @param typeEnvironment
	 *            The type environment that will store the predicates.
	 * @param predicate
	 *            the predicate on which its type environment will be extracted.
	 */
	private static void extractPredicateTypenv(
			final ITypeEnvironment typeEnvironment, final Predicate predicate) {
		for (final FreeIdentifier id : predicate.getFreeIdentifiers()) {
			typeEnvironment.add(id);
		}
		for (final GivenType type : predicate.getGivenTypes()) {
			typeEnvironment.addGivenSet(type.getName());
		}
	}

	/**
	 * This method translates an Event-B bound identifier into an Extended SMT
	 * node.
	 */
	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String bidName = boundIdentifiers.get(boundIdentifiers.size()
				- expression.getBoundIndex() - 1);
		smtNode = qVarMap.get(bidName);
	}

	/**
	 * This class is used to store the type of all the BoundIdentDecls from the
	 * predicate
	 * 
	 * @author vitor
	 * 
	 */
	protected static class BidTypeInspector extends DefaultInspector<Type> {
		/**
		 * This method takes a copy of the BoundIdentDecl types in the
		 * hypotheses and goal
		 */
		public static List<Type> getBoundIDentDeclTypes(
				final List<Predicate> hypotheses, final Predicate goal) {
			final IFormulaInspector<Type> BID_TYPE_INSPECTOR = new BidTypeInspector();
			final List<Type> typesFound = new ArrayList<Type>();
			for (final Predicate p : hypotheses) {
				typesFound.addAll(p.inspect(BID_TYPE_INSPECTOR));
			}
			typesFound.addAll(goal.inspect(BID_TYPE_INSPECTOR));

			return typesFound;
		}

		/**
		 * This method stores in the accumlator the type of the actual
		 * BoundIdentDecl
		 */
		@Override
		public void inspect(final BoundIdentDecl decl,
				final IAccumulator<Type> accumulator) {
			accumulator.add(decl.getType());
		}
	}

	/**
	 * Extracts the type environment of a Event-B sequent
	 */
	protected static ITypeEnvironment extractTypeEnvironment(
			final List<Predicate> hypotheses, final Predicate goal) {
		final FormulaFactory ff = FormulaFactory.getDefault(); // FIXME use real
																// one
		final ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (final Predicate hypothesis : hypotheses) {
			extractPredicateTypenv(typeEnvironment, hypothesis);
		}
		extractPredicateTypenv(typeEnvironment, goal);
		return typeEnvironment;
	}

	/**
	 * SMT translation of an Event-B formula that is a term in SMT-LIB V1.2
	 * language.
	 */
	protected SMTTerm smtTerm(final Formula<?> formula) {
		formula.accept(this);
		if (smtNode instanceof SMTTerm) {
			return (SMTTerm) smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTTerm'.");
		}
	}

	/**
	 * SMT translation of an Event-B formula that is a formula in SMT-LIB V1.2
	 * language.
	 */
	protected SMTFormula smtFormula(final Formula<?> formula) {
		formula.accept(this);
		if (smtNode instanceof SMTFormula) {
			return (SMTFormula) smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTFormula'.");
		}
	}

	/**
	 * SMT translation of two Event-B formulas (left and right children of a
	 * node) which are terms in SMT-LIB V1.2 language.
	 */
	protected SMTTerm[] smtTerms(final Formula<?> left, final Formula<?> right) {
		return new SMTTerm[] { smtTerm(left), smtTerm(right) };
	}

	/**
	 * SMT translation of two Event-B formulas (left and right children of a
	 * node) which are formulas in SMT-LIB V1.2 language.
	 */
	protected SMTFormula[] smtFormulas(final Formula<?> left,
			final Formula<?> right) {
		return new SMTFormula[] { smtFormula(left), smtFormula(right) };
	}

	/**
	 * SMT translation of a set of Event-B formulas which are terms in SMT-LIB
	 * V1.2 language.
	 */
	protected SMTTerm[] smtTerms(final Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTTerm[] smtTerms = new SMTTerm[length];
		for (int i = 0; i < length; i++) {
			smtTerms[i] = smtTerm(formulas[i]);
		}
		return smtTerms;
	}

	/**
	 * SMT translation of a set of Event-B formulas which are formulas in
	 * SMT-LIB V1.2 language.
	 */
	protected SMTFormula[] smtFormulas(final Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTFormula[] smtFormulas = new SMTFormula[length];
		for (int i = 0; i < length; i++) {
			smtFormulas[i] = smtFormula(formulas[i]);
		}
		return smtFormulas;
	}

	/**
	 * This method translates an Event-B associative predicate into an SMT node.
	 */
	@Override
	public void visitAssociativePredicate(final AssociativePredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getChildren());
		switch (predicate.getTag()) {
		case Formula.LAND:
			smtNode = SMTFactory.makeAnd(children);
			break;
		case Formula.LOR:
			smtNode = SMTFactory.makeOr(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	/**
	 * This method translates an Event-B literal actualPredicate into an SMT
	 * node.
	 */
	@Override
	public void visitLiteralPredicate(final LiteralPredicate pred) {
		final int tag = pred.getTag();
		switch (tag) {
		case Formula.BTRUE:
			smtNode = SMTFactory.makePTrue();
			break;
		case Formula.BFALSE:
			smtNode = SMTFactory.makePFalse();
			break;
		default:
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B binary predicate into an SMT node
	 */
	@Override
	public void visitBinaryPredicate(final BinaryPredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getLeft(),
				predicate.getRight());
		switch (predicate.getTag()) {
		case Formula.LIMP:
			smtNode = SMTFactory.makeImplies(children);
			break;
		case Formula.LEQV:
			smtNode = SMTFactory.makeIff(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}

	}

	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		boundIdentifiersMarker.push(boundIdentifiers.size());

		final SMTTerm[] termChildren = smtTerms(predicate.getBoundIdentDecls());
		final SMTFormula formulaChild = smtFormula(predicate.getPredicate());

		switch (predicate.getTag()) {
		case Formula.FORALL:
			smtNode = SMTFactory.makeForAll(termChildren, formulaChild);
			break;
		case Formula.EXISTS:
			smtNode = SMTFactory.makeExists(termChildren, formulaChild);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
		final int top = boundIdentifiersMarker.pop();
		boundIdentifiers.subList(top, boundIdentifiers.size()).clear();
	}

	/**
	 * This method translates an Event-B unary predicate into an SMT node.
	 */
	@Override
	public void visitUnaryPredicate(final UnaryPredicate predicate) {
		final SMTFormula[] children = new SMTFormula[] { smtFormula(predicate
				.getChild()) };
		switch (predicate.getTag()) {
		case Formula.NOT:
			smtNode = SMTFactory.makeNot(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	/**
	 * This method translates an Event-B integer literal into an SMT node.
	 */
	@Override
	public void visitIntegerLiteral(final IntegerLiteral expression) {
		smtNode = SMTFactory.makeNumeral(expression.getValue());
	}

}