/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.translation;

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
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTFactory;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTNode;
import org.eventb.smt.ast.SMTTerm;
import org.eventb.smt.ast.SMTVar;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.provers.internal.core.IllegalTagException;

/**
 * This class is a translator from Event-B syntax to SMT-LIB syntax.
 */
public abstract class Translator implements ISimpleVisitor {
	protected static final boolean IN_GOAL = true;

	/**
	 * typeMap is a map between Event-B types encountered during the translation
	 * process and SMT-LIB sorts assigned to them. This map is built using an
	 * SMT-LIB Signature that provides fresh type names.
	 */
	protected HashMap<Type, SMTSortSymbol> typeMap = new HashMap<Type, SMTSortSymbol>();
	/**
	 * varMap is a map between Event-B variable names encountered during the
	 * translation process and SMT-LIB symbol names assigned to them. This map
	 * is built using an SMT-LIB Signature that provides fresh type names.
	 */
	protected HashMap<String, SMTSymbol> varMap = new HashMap<String, SMTSymbol>();

	public static boolean DEBUG = false;
	public static boolean DEBUG_DETAILS = false;

	protected SMTNode<?> smtNode;

	protected SMTLIBVersion smtlibVersion;

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

	public Translator(final SMTLIBVersion smtlibVersion) {
		this.smtlibVersion = smtlibVersion;
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
				final ISimpleSequent sequent) {
			final IFormulaInspector<Type> BID_TYPE_INSPECTOR = new BidTypeInspector();
			final List<Type> typesFound = new ArrayList<Type>();

			for (final ITrackedPredicate trackedPredicate : sequent
					.getPredicates()) {
				final Predicate predicate = trackedPredicate.getPredicate();
				typesFound.addAll(predicate.inspect(BID_TYPE_INSPECTOR));
			}

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
	 * This is the translation method. An Event-B sequent is given to this
	 * method as hypotheses and goal. Must be called by a public static method.
	 */
	protected abstract SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method takes an Event-B type and returns the equivalent in SMT-LIB.
	 */
	protected abstract SMTSymbol translateTypeName(final Type type);

	/**
	 * Determines and returns the SMT-LIB logic to use in order to discharge the
	 * current sequent.
	 */
	protected abstract SMTLogic determineLogic(final ISimpleSequent sequent);

	/**
	 * This method extracts the type environment from the Event-B sequent and
	 * builds the SMT-LIB signature to use.
	 */
	protected abstract void translateSignature(final SMTLogic logic,
			final ISimpleSequent sequent);

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
	 * This method returns the current SMT node.
	 */
	protected SMTFormula getSMTFormula() {
		if (smtNode instanceof SMTFormula) {
			return (SMTFormula) smtNode;
		} else {
			throw new IllegalArgumentException(Messages.Translation_error);
		}
	}

	/**
	 * Clears the formula
	 */
	protected void clearFormula() {
		smtNode = null;
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
			smtNode = SMTFactory.makeAnd(children, smtlibVersion);
			break;
		case Formula.LOR:
			smtNode = SMTFactory.makeOr(children, smtlibVersion);
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
			smtNode = SMTFactory.makeImplies(children, smtlibVersion);
			break;
		case Formula.LEQV:
			smtNode = SMTFactory.makeIff(children, smtlibVersion);
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
			smtNode = SMTFactory.makeForAll(termChildren, formulaChild,
					smtlibVersion);
			break;
		case Formula.EXISTS:
			smtNode = SMTFactory.makeExists(termChildren, formulaChild,
					smtlibVersion);
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
			smtNode = SMTFactory.makeNot(children, smtlibVersion);
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
		smtNode = SMTFactory.makeNumeral(expression.getValue(), smtlibVersion);
	}
}
