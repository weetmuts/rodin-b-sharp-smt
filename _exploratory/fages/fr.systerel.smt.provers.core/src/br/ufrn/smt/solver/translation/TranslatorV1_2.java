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
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;

import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class is a translator from Event-B syntax into SMT-LIB syntax.
 */
public abstract class TranslatorV1_2 extends Translator {

	/**
	 * TODO Finish this comment When the translator finishes translating a
	 * quantified predicate, it deletes all the bound identifiers of that
	 * predicate. In nested quantified predicates, the translator must delete
	 * the bound identifier declarations inside
	 */
	protected Stack<Integer> boundIdentifiersMarker = new Stack<Integer>();

	protected List<String> boundIdentifiers = new ArrayList<String>();
	protected final Map<String, SMTVar> qVarMap = new HashMap<String, SMTVar>();

	/**
	 * Extracts the type environment of a Predicate needed to build an SMT-LIB
	 * benchmark's signature, that is, free identifiers and given types.
	 */
	private static void extractPredicateTypenv(
			final ITypeEnvironment typeEnvironment, final Predicate predicate) {
		for (FreeIdentifier id : predicate.getFreeIdentifiers()) {
			typeEnvironment.add(id);
		}
		for (GivenType type : predicate.getGivenTypes()) {
			typeEnvironment.addGivenSet(type.getName());
		}
	}

	class SMTFormulaInspector extends DefaultInspector<Type> {
		@Override
		public void inspect(BoundIdentDecl decl, IAccumulator<Type> accumulator) {
			accumulator.add(decl.getType());
		}
	}

	/**
	 * This method takes a copy of the BoundIdentDecl types in the hypotheses
	 * and goal
	 */
	List<Type> getBoundIDentDeclTypes(List<Predicate> hypotheses, Predicate goal) {
		final IFormulaInspector<Type> BID_TYPE_INSPECTOR = new SMTFormulaInspector();
		final List<Type> typesFound = new ArrayList<Type>();
		for (Predicate p : hypotheses) {
			typesFound.addAll(p.inspect(BID_TYPE_INSPECTOR));
		}
		typesFound.addAll(goal.inspect(BID_TYPE_INSPECTOR));

		return typesFound;
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
	protected SMTTerm smtTerm(Formula<?> formula) {
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
	protected SMTFormula smtFormula(Formula<?> formula) {
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
	protected SMTTerm[] smtTerms(Formula<?> left, Formula<?> right) {
		return new SMTTerm[] { smtTerm(left), smtTerm(right) };
	}

	/**
	 * SMT translation of two Event-B formulas (left and right children of a
	 * node) which are formulas in SMT-LIB V1.2 language.
	 */
	protected SMTFormula[] smtFormulas(Formula<?> left, Formula<?> right) {
		return new SMTFormula[] { smtFormula(left), smtFormula(right) };
	}

	/**
	 * SMT translation of a set of Event-B formulas which are terms in SMT-LIB
	 * V1.2 language.
	 */
	protected SMTTerm[] smtTerms(Formula<?>... formulas) {
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
	protected SMTFormula[] smtFormulas(Formula<?>... formulas) {
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
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getChildren());
		switch (predicate.getTag()) {
		case Formula.LAND:
			smtNode = sf.makeAnd(children);
			break;
		case Formula.LOR:
			smtNode = sf.makeOr(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	/**
	 * This method translates an Event-B binary predicate into an SMT node 
	 */
	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getLeft(),
				predicate.getRight());
		switch (predicate.getTag()) {
		case Formula.LIMP:
			smtNode = sf.makeImplies(children);
			break;
		case Formula.LEQV:
			smtNode = sf.makeIff(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}

	}

	/**
	 * This method translates an Event-B unary predicate into an SMT node.
	 */
	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		final SMTFormula[] children = new SMTFormula[] { smtFormula(predicate
				.getChild()) };
		switch (predicate.getTag()) {
		case Formula.NOT:
			smtNode = sf.makeNot(children);
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
		smtNode = sf.makeNumeral(expression.getValue());
	}

}