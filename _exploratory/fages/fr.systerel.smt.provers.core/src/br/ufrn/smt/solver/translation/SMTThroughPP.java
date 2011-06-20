/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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

import static fr.systerel.smt.provers.ast.SMTFactory.makeBool;
import static fr.systerel.smt.provers.ast.SMTFactory.makeInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTBenchmarkPP;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFactoryPP;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignaturePP;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory;
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class does the SMT translation through ppTrans. ppTrans is called first,
 * to reduce an Event-B sequent to Predicate Calculus. Then the SMT translation
 * is done.
 */
// TODO check if it is possible to use AutoRewriterImpl without warnings
@SuppressWarnings("restriction")
public class SMTThroughPP extends TranslatorV1_2 {
	/**
	 * The SMT factory used by the translator to make SMT symbols
	 */
	private final SMTFactory sf;

	private Gatherer gatherer;
	/**
	 * An instance of <code>SMTThroughPP</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	private SMTSignaturePP signature;

	/**
	 * In order to translate memberships, the approach implemented in this class
	 * defines some new predicates. <code>msTypeMap</code> is a map between each
	 * Event-B type used in a membership and the corresponding SMT-LIB
	 * actualPredicate symbols.
	 */
	private final Map<Type, SMTPredicateSymbol> msTypeMap = new HashMap<Type, SMTPredicateSymbol>();

	private final Map<FreeIdentifier, SMTPredicateSymbol> monadicSetsMap = new HashMap<FreeIdentifier, SMTPredicateSymbol>();
	/**
	 * This list contains the terms of the current membership being translated
	 * in the translation process.
	 */
	// FIXME Seems to be unsafe, to be deleted if possible
	private final List<SMTTerm> membershipPredicateTerms = new ArrayList<SMTTerm>();

	/**
	 * Constructor of a PP approach translator of Event-B to SMT-LIB
	 * 
	 * @param solver
	 *            Solver which will be used to discharge the sequent
	 */
	public SMTThroughPP(final String solver) {
		super(solver);
		sf = SMTFactoryPP.getInstance();
	}

	/**
	 * This class is used to traverse the Event-B sequent in order to gather
	 * some informations needed to proceed with the translation, such as : - the
	 * appearing of occurrences of the Event-B integer symbol; - the appearing
	 * of elements of the bool theory; - the need for using the True predicate;
	 * - the list of the sets to be translated into monadic membership
	 * predicates.
	 **/
	private static class Gatherer extends DefaultVisitor {
		private boolean integerFound = false;
		private boolean boolTheory = false;
		private boolean usesTruePredicate = false;
		private final Set<FreeIdentifier> identsNotForMonadicPreds = new HashSet<FreeIdentifier>();
		private final Set<FreeIdentifier> setsForMonadicPreds = new HashSet<FreeIdentifier>();
		private final Set<Type> boundSetsTypes = new HashSet<Type>();

		public static Gatherer gatherFrom(final List<Predicate> hypotheses,
				final Predicate goal,
				final Map<FreeIdentifier, SMTPredicateSymbol> monadicSetsMap) {
			final Gatherer gatherer = new Gatherer();

			for (final Predicate hypothesis : hypotheses) {
				hypothesis.accept(gatherer);
			}
			goal.accept(gatherer);

			gatherer.removeIdentsFromSetsForMonadicPreds();
			gatherer.setMonadicSetsMapKeys(monadicSetsMap);

			return gatherer;
		}

		private void setMonadicSetsMapKeys(
				final Map<FreeIdentifier, SMTPredicateSymbol> monadicSetsMap) {
			final Iterator<FreeIdentifier> monadicSetsIterator = setsForMonadicPreds
					.iterator();
			while (monadicSetsIterator.hasNext()) {
				monadicSetsMap.put(monadicSetsIterator.next(), null);
			}
		}

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

		private void gatherMonadicSets(final RelationalPredicate pred) {
			/**
			 * Code for of membership predicate optimization
			 */
			final Expression right = pred.getRight();

			assert right instanceof FreeIdentifier
					|| right instanceof BoundIdentifier;

			if (right instanceof FreeIdentifier) {
				final FreeIdentifier rightSet = (FreeIdentifier) right;
				if (right.getType().getSource() == null) {
					setsForMonadicPreds.add(rightSet);
				}
			} else if (right instanceof BoundIdentifier) {
				boundSetsTypes.add(((BoundIdentifier) right).getType());
			}
		}

		/**
		 * This method extracts all the setsForMonadicPreds that will be
		 * translated in optimized membership predicates, that is, the
		 * setsForMonadicPreds complying with the following rules:
		 * 
		 * <ul>
		 * <li>The set only occur on the right-hand side of membership
		 * predicates;
		 * <li>No bound variable occurs in the right-hand side of similar
		 * membership predicates;
		 * </ul>
		 * 
		 * Then these setsForMonadicPreds are used as operator with one
		 * argument, instead of creating a fresh membership predicate where the
		 * set is one of the arguments.
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

		public boolean foundInteger() {
			return integerFound;
		}

		public boolean usesTruePredicate() {
			return usesTruePredicate;
		}

		public boolean usesBoolTheory() {
			return boolTheory;
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
			gatherMonadicSets(pred);
			return checkBooleanElementsInMembershipPredicate(pred);
		}

		@Override
		public boolean enterEQUAL(final RelationalPredicate pred) {
			final Expression right = pred.getRight();
			final Expression left = pred.getLeft();

			if (right instanceof FreeIdentifier) {
				final FreeIdentifier rightIdent = (FreeIdentifier) right;
				identsNotForMonadicPreds.add(rightIdent);
			}

			if (left instanceof FreeIdentifier) {
				final FreeIdentifier leftIdent = (FreeIdentifier) left;
				identsNotForMonadicPreds.add(leftIdent);
			}

			return true;
		}

		/**
		 * If one of the predicates has a TRUE constant, set
		 * <code>boolTheory</code> <i>true</i> and stop visiting.
		 */
		@Override
		public boolean visitTRUE(final AtomicExpression expr) {
			boolTheory = true;
			return true;
		}
	}

	/**
	 * This method recursively traverses the type tree to list all base types
	 * used to define it. That is, those which can be extracted from powerset
	 * types and product types.
	 * 
	 * @param baseTypes
	 *            the actual list of extracted base types
	 * @param type
	 *            the type from which base types must be extracted
	 * @return the list of base types extracted from this type
	 */
	private static Set<Type> getBaseTypes(final Set<Type> baseTypes,
			final Type type) {
		final boolean isAProductType = type instanceof ProductType;
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null && !isAProductType) {
			baseTypes.add(type);
			return baseTypes;
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) type;
			return getBaseTypes(getBaseTypes(baseTypes, product.getLeft()),
					product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (type.getSource() != null) {
			return getBaseTypes(getBaseTypes(baseTypes, type.getSource()),
					type.getTarget());
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha.
		 */
		else if (type.getBaseType() != null) {
			return getBaseTypes(baseTypes, type.getBaseType());
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
	 * This is the pp translation method.
	 */
	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to actualPredicate
		 * calculus
		 */
		ppProof.translate();

		return ppProof;
	}

	private SMTSignature getSignature() {
		return signature;
	}

	/**
	 * determines the logic
	 * 
	 * @param hypotheses
	 *            the hypotheses
	 * @param goal
	 *            the goal
	 * @return the logic that will be used in the benchmark
	 */
	@Override
	protected SMTLogic determineLogic(final List<Predicate> hypotheses,
			final Predicate goal) {
		gatherer = Gatherer.gatherFrom(hypotheses, goal, monadicSetsMap);

		if (gatherer.usesBoolTheory()) {
			return new SMTLogic(SMTLogic.UNKNOWN, Ints.getInstance(),
					Booleans.getInstance());
		}
		return SMTLogic.SMTLIBUnderlyingLogic.getInstance();
	}

	/**
	 * Rewrites the predicate using a basic auto-rewriter of Event-B. The
	 * rewriting is done until the fixpoint is reached.
	 */
	private Predicate recursiveAutoRewrite(Predicate pred) {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final IFormulaRewriter rewriter = new AutoRewriterImpl(ff, Level.L2);

		Predicate resultPred;
		resultPred = pred.rewrite(rewriter);
		while (resultPred != pred) {
			pred = resultPred;
			resultPred = pred.rewrite(rewriter);
		}
		return resultPred;
	}

	/**
	 * Rewrites the predicates using a basic auto-rewriter for the Event-B
	 * sequent prover.
	 */
	private List<Predicate> recursiveAutoRewriteAll(final List<Predicate> preds) {
		final List<Predicate> rewritedPreds = new ArrayList<Predicate>();
		for (final Predicate pred : preds) {
			rewritedPreds.add(recursiveAutoRewrite(pred));
		}
		return rewritedPreds;
	}

	/**
	 * Translate the type of the bound identifiers
	 * 
	 * @param hypotheses
	 *            the hypotheses
	 * @param goal
	 *            the goal
	 */
	private void translateBoundIdentTypes(final List<Predicate> hypotheses,
			final Predicate goal) {
		final List<Type> biTypes = BidTypeInspector.getBoundIDentDeclTypes(
				hypotheses, goal);

		final Iterator<Type> bIterator = biTypes.iterator();

		while (bIterator.hasNext()) {

			final Type varType = bIterator.next();

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * do the same for each base type used to define this type into an
			 * SMT-LIB sort
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}
		}
	}

	/**
	 * Given a actualPredicate <code>a ∈ s</code>, with <code>a ⦂ S</code> and
	 * <code>s ⦂ ℙ(S)</code>.
	 * <p>
	 * If this actualPredicate is in accordance with the rules of optimization
	 * of translation setsForMonadicPreds, the membership is translated to:
	 * 
	 * (s a)
	 * 
	 * <p>
	 * else the type of <code>a</code> is mapped to the SMT-LIB actualPredicate:
	 * <code>(p S PS)</code>.
	 * 
	 * @param membershipPredicate
	 *            The membership actualPredicate that will be translated.
	 */
	private void translateMemberShipPredicate(
			final RelationalPredicate membershipPredicate) {

		final Expression left = membershipPredicate.getLeft();
		final Expression right = membershipPredicate.getRight();

		// Translate monadic setsForMonadicPreds (special case)
		if (right instanceof FreeIdentifier) {
			final FreeIdentifier rightSet = (FreeIdentifier) right;
			if (monadicSetsMap.containsKey(rightSet)) {
				translateInMonadicMembershipPredicate(left, rightSet);
				return;
			}
		}

		translateInClassicMembershipPredicate(left, right);
	}

	private void translateInClassicMembershipPredicate(final Expression left,
			final Expression right) {
		final SMTTerm[] children = smtTerms(left, right);

		if (left.getTag() != Formula.MAPSTO) {
			membershipPredicateTerms.add(0, children[0]);
		}
		membershipPredicateTerms.add(children[1]);

		final int numberOfArguments = membershipPredicateTerms.size();
		final SMTSortSymbol[] argSorts = new SMTSortSymbol[numberOfArguments];
		for (int i = 0; i < numberOfArguments; i++) {
			argSorts[i] = membershipPredicateTerms.get(i).getSort();
		}

		final Type leftType = left.getType();

		final SMTPredicateSymbol predSymbol = getMembershipPredicateSymbol(
				leftType, argSorts);

		final SMTTerm[] args = membershipPredicateTerms
				.toArray(new SMTTerm[numberOfArguments]);

		smtNode = SMTFactory.makeAtom(predSymbol, args, signature);
		membershipPredicateTerms.clear();
	}

	private void translateInMonadicMembershipPredicate(
			final Expression leftExpression, final FreeIdentifier rightSet) {
		final SMTTerm leftTerm = smtTerm(leftExpression);
		SMTPredicateSymbol monadicMembershipPredicate = monadicSetsMap
				.get(rightSet);

		if (monadicMembershipPredicate == null) {
			// TODO add tests for this
			monadicMembershipPredicate = signature.freshPredicateSymbol(
					rightSet.getName(), leftTerm.getSort());
			monadicSetsMap.put(rightSet, monadicMembershipPredicate);
		}

		smtNode = SMTFactory.makeAtom(monadicMembershipPredicate,
				new SMTTerm[] { leftTerm }, signature);
		membershipPredicateTerms.clear();
	}

	/**
	 * Creates a fresh membership predicate for the given type and argument
	 * sorts if it doesn't already exists in msTypeMap. As we use unique names
	 * for each rank, we don't need to specify the name of the needed predicate.
	 * 
	 * @param type
	 *            Event-B type to be mapped.
	 * @param argSorts
	 *            SMT sorts of the arguments of the membership predicate.
	 * @return the needed membership predicate.
	 */
	private SMTPredicateSymbol getMembershipPredicateSymbol(final Type type,
			final SMTSortSymbol... argSorts) {
		SMTPredicateSymbol membershipPredicateSymbol = msTypeMap.get(type);
		if (membershipPredicateSymbol == null) {
			membershipPredicateSymbol = signature.freshPredicateSymbol(
					SMTPredicateSymbol.MS_PREDICATE_NAME, argSorts);
			msTypeMap.put(type, membershipPredicateSymbol);
		}
		assert membershipPredicateSymbol != null;
		return membershipPredicateSymbol;
	}

	/**
	 * This method is used to check if the symbol must be added as predicate or
	 * a function symbol.
	 * 
	 * @param type
	 * @return true if the type is of sort Bool and the actual translation is
	 *         not using the TRUE pred symbol. Otherwise returns false
	 */
	private boolean isBoolTheoryAndDoesNotUseTruePred(final Type type) {
		if (!gatherer.usesTruePredicate()) {
			if (type instanceof BooleanType) {
				for (final SMTTheory theories : signature.getLogic()
						.getTheories()) {
					if (theories instanceof Booleans) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param left
	 * @param right
	 */
	private SMTFormula translateTruePredWithId(final Expression left,
			final Expression right) {
		final SMTFormula leftFormula = translateTruePred(left);
		final SMTFormula rightFormula = translateTruePred(right);

		return SMTFactory
				.makeIff(new SMTFormula[] { leftFormula, rightFormula });
	}

	private SMTFormula translateTruePred(final Expression expr) {
		if (expr.getTag() == Formula.TRUE) {
			throw new IllegalArgumentException(
					Messages.TRUE_On_Both_Sides_Of_Boolean_Equality_error);
		} else {
			if (gatherer.usesTruePredicate()) {
				final SMTTerm term = smtTerm(expr);
				return SMTFactory.makeAtom(signature.getLogic().getTrue(),
						new SMTTerm[] { term }, signature);
			}
		}
		return smtFormula(expr);
	}

	/**
	 * Generate the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ ℤ</code>
	 * 
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateIntegerAxiom() {
		// gets the event-B integer type
		final Type integerType = FormulaFactory.getDefault().makeIntegerType();

		// creates the quantified variable with a fresh name
		final String varName = signature.freshSymbolName("x"); //$NON-NLS-1$
		final SMTSortSymbol intSort = signature.getLogic().getIntegerSort();
		final SMTTerm smtVar = sf.makeVar(varName, intSort);

		// creates the integer constant
		final SMTTerm intConstant = SMTFactory.makeConstant(signature
				.getLogic().getIntsSet(), signature);

		// gets the membership symbol
		final SMTPredicateSymbol membershipPredSymbol = getMembershipPredicateSymbol(
				integerType, intSort, intSort);

		// creates the membership formula
		final SMTFormula membershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, new SMTTerm[] { smtVar, intConstant },
				signature);

		// returns the quantified formula
		return SMTFactory.makeForAll(new SMTTerm[] { smtVar },
				membershipFormula);
	}

	/**
	 * Generate the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ BOOL</code>
	 * 
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateBoolAxiom() {
		// gets the event-B boolean type
		final Type booleanType = FormulaFactory.getDefault().makeBooleanType();

		// creates the quantified variable with a fresh name
		final String varName = signature.freshSymbolName("x"); //$NON-NLS-1$
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();
		final SMTTerm smtVar = sf.makeVar(varName, boolSort);

		// creates the boolean constant
		final SMTTerm boolConstant = SMTFactory.makeConstant(signature
				.getLogic().getBoolsSet(), signature);

		// gets the membership symbol
		final SMTPredicateSymbol membershipPredSymbol = getMembershipPredicateSymbol(
				booleanType, boolSort, boolSort);

		// creates the membership formula
		final SMTFormula membershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, new SMTTerm[] { smtVar, boolConstant },
				signature);

		// returns the quantified formula
		return SMTFactory.makeForAll(new SMTTerm[] { smtVar },
				membershipFormula);
	}

	/**
	 * Generate the SMT-LIB formula for this event-B formula:
	 * <code>∀x, y·(x = TRUE ⇔ y = TRUE) ⇔ x = y</code>
	 * 
	 * @return the SMTFormula representing the translated axiom
	 */
	private SMTFormula generateTrueAxiom() {
		// creates the quantified boolean variables with fresh names
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();
		final String xName = signature.freshSymbolName("x");
		final String yName = signature.freshSymbolName("y");
		final SMTTerm xTerm = sf.makeVar(xName, boolSort);
		final SMTTerm yTerm = sf.makeVar(yName, boolSort);

		// creates the formula <code>x = TRUE ⇔ y = TRUE</code>
		final SMTPredicateSymbol truePredSymbol = signature.getLogic()
				.getTrue();
		final SMTFormula trueX = SMTFactory.makeAtom(truePredSymbol,
				new SMTTerm[] { xTerm }, signature);
		final SMTFormula trueY = SMTFactory.makeAtom(truePredSymbol,
				new SMTTerm[] { yTerm }, signature);
		final SMTFormula trueXEqvTrueY = SMTFactory.makeIff(new SMTFormula[] {
				trueX, trueY });

		// creates the formula <code>x = y</code>
		final SMTFormula xEqualY = SMTFactory.makeEqual(new SMTTerm[] { xTerm,
				yTerm });

		// creates the formula <code>(x = TRUE ⇔ y = TRUE) ⇔ x = y</code>
		final SMTFormula equivalence = SMTFactory.makeIff(new SMTFormula[] {
				trueXEqvTrueY, xEqualY });

		// returns the quantified formula
		return SMTFactory.makeForAll(new SMTTerm[] { xTerm, yTerm },
				equivalence);
	}

	/**
	 * This method links some symbols of the logic to the main Event-B symbols.
	 */
	private void linkLogicSymbols() {
		final SMTLogic logic = signature.getLogic();
		final FormulaFactory ff = FormulaFactory.getDefault();

		typeMap.put(ff.makeIntegerType(), logic.getIntegerSort());
		typeMap.put(ff.makeBooleanType(), logic.getBooleanSort());
	}

	/**
	 * This method translates the given actualPredicate into an SMT Formula.
	 * 
	 */
	private SMTFormula translate(final Predicate predicate) {
		predicate.accept(this);
		return getSMTFormula();
	}

	/**
	 * @param lemmaName
	 *            the name of the lemma
	 * @param ppTranslatedHypotheses
	 *            the PP translated hypotheses
	 * @param ppTranslatedGoal
	 *            the PP translated formula
	 * @param logic
	 *            the used logic
	 * @return the SMTBenchmark of the translation
	 */
	private SMTBenchmark translate(final String lemmaName,
			final List<Predicate> ppTranslatedHypotheses,
			final Predicate ppTranslatedGoal, final SMTLogic logic) {
		translateSignature(logic, ppTranslatedHypotheses, ppTranslatedGoal);

		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		if (gatherer.foundInteger()) {
			translatedAssumptions.add(generateIntegerAxiom());
		}

		for (final SMTTheory t : signature.getLogic().getTheories()) {
			if (t instanceof Booleans) {
				translatedAssumptions.add(generateBoolAxiom());
				if (gatherer.usesTruePredicate()) {
					translatedAssumptions.add(generateTrueAxiom());
				}
			}
		}

		// translates each hypothesis
		for (final Predicate hypothesis : ppTranslatedHypotheses) {
			clearFormula();
			/**
			 * ignoring TRUE hypotheses generated by PP
			 */
			if (hypothesis.getTag() != Formula.BTRUE) {
				translatedAssumptions.add(translate(hypothesis));
			}
		}
		// translates the formula
		clearFormula();
		final SMTFormula smtFormula = translate(ppTranslatedGoal);

		final SMTBenchmarkPP benchmark = new SMTBenchmarkPP(lemmaName,
				signature, translatedAssumptions, smtFormula);
		benchmark.removeUnusedSymbols();
		return benchmark;
	}

	/**
	 * This method translates an Event-B type into an SMT-LIB sort. It gives the
	 * sort a fresh name if necessary, to avoid any name collision.
	 */
	@Override
	protected SMTSortSymbol translateTypeName(final Type type) {
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null) {
			return signature.freshSort(type.toString());
		}
		return signature.freshSort();
	}

	/**
	 * This method builds the SMT-LIB signature of a sequent given as its set of
	 * hypotheses and its goal. The method also fill the variables
	 * <code>typeMap</code>, <code>varMap</code> and <code>msTypeMap</code>.
	 * 
	 * @param logic
	 *            the SMT-LIB logic
	 * @param hypotheses
	 *            the set of hypotheses of the sequent
	 * @param goal
	 *            the goal of the sequent
	 */
	@Override
	protected void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		signature = new SMTSignaturePP(logic);

		linkLogicSymbols();

		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);

		translateTypeEnvironment(typeEnvironment);
		translateBoundIdentTypes(hypotheses, goal);
	}

	public void translateTypeEnvironment(final ITypeEnvironment typeEnvironment) {

		/**
		 * For each membership of the type environment,
		 */
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			Type varType = iter.getType();
			boolean parseConstant = true;

			/**
			 * check if the the variable is a monadic set. If so, translate the
			 * base type of it
			 */
			for (final FreeIdentifier monadicSet : monadicSetsMap.keySet()) {
				if (monadicSet.getName().equals(varName)
						&& monadicSet.getType().equals(varType)) {
					varType = iter.getType().getBaseType();
					parseConstant = false;
					break;
				}
			}

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * gets a fresh name for the type of the variable to be typed, adds
			 * it to the type names mapping (and adds it to the signature
			 * symbols set)
			 * 
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}

			if (parseConstant) {
				/**
				 * gets a fresh name for the variable to be typed, adds it to
				 * the variable names mapping (and adds it to the signature
				 * symbols set)
				 */
				final SMTFunctionSymbol smtConstant;
				if (!varMap.containsKey(varName)) {
					if (isBoolTheoryAndDoesNotUseTruePred(varType)) {
						final SMTPredicateSymbol predSymbol = signature
								.freshPredicateSymbol(varName);
						varMap.put(varName, predSymbol);
						continue;
					} else {
						/**
						 * adds the typing item (<code>x ⦂ S</code>) to the
						 * signature as a constant (<code>extrafuns</code>
						 * SMT-LIB section, with a sort but no argument:
						 * <code>(x S)</code>).
						 */
						smtConstant = signature.freshConstant(varName,
								smtSortSymbol);
						varMap.put(varName, smtConstant);
					}
				}
			}
		}
	}

	/**
	 * This is the translation method for the ppTrans approach of SMT
	 * translation.
	 * 
	 */
	@Override
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {

		/**
		 * PP translation
		 */
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		@SuppressWarnings("deprecation")
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		@SuppressWarnings("deprecation")
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		/**
		 * PP auto-rewriting
		 */
		final List<Predicate> ppRewritedHypotheses = recursiveAutoRewriteAll(ppTranslatedHypotheses);
		final Predicate ppRewritedGoal = recursiveAutoRewrite(ppTranslatedGoal);

		/**
		 * Logic auto-configuration
		 */
		final SMTLogic logic = determineLogic(ppRewritedHypotheses,
				ppRewritedGoal);

		/**
		 * SMT translation
		 */
		return translate(lemmaName, ppRewritedHypotheses, ppRewritedGoal, logic);
	}

	/**
	 * This is the public translation method
	 * 
	 * @param lemmaName
	 *            the name to be used in the SMT-LIB benchmark
	 * @param hypotheses
	 *            the hypotheses of the Event-B sequent
	 * @param goal
	 *            the goal of the Event-B sequent
	 * @return the SMT-LIB benchmark built over the translation of the given
	 *         Event-B sequent
	 */
	public static SMTBenchmark translateToSmtLibBenchmark(
			final String lemmaName, final List<Predicate> hypotheses,
			final Predicate goal, final String solver) {
		final SMTBenchmark smtB = new SMTThroughPP(solver).translate(lemmaName,
				hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			Predicate predicate, final String solver) {
		final SMTThroughPP translator = new SMTThroughPP(solver);
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		predicate = translator.recursiveAutoRewrite(predicate);
		translator.determineLogic(noHypothesis, predicate);
		translator.translateSignature(logic, noHypothesis, predicate);
		return translator.translate(predicate);
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTSignature translateTE(final SMTLogic logic,
			Predicate predicate, final String solver) {
		final SMTThroughPP translator = new SMTThroughPP(solver);
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		predicate = translator.recursiveAutoRewrite(predicate);
		translator.determineLogic(noHypothesis, predicate);
		translator.translateSignature(logic, noHypothesis, predicate);
		return translator.getSignature();
	}

	/**
	 * This method is used only to test the logic determination
	 */
	public static SMTLogic determineLogic(Predicate goalPredicate) {
		final SMTThroughPP translator = new SMTThroughPP(null);
		goalPredicate = translator.recursiveAutoRewrite(goalPredicate);
		return translator.determineLogic(new ArrayList<Predicate>(0),
				goalPredicate);
	}

	/**
	 * This method translates an Event-B associative expression into an SMT
	 * node.
	 */
	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getChildren());
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			smtNode = sf.makePlus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.PLUS), children, signature);
			break;
		case Formula.MUL:
			smtNode = sf.makeMul((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MUL), children, signature);
			break;
		default:
			/**
			 * BUNION, BINTER, BCOMP, FCOMP and OVR tags cannot be produced by
			 * ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B atomic expression into an SMT node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.INTEGER:
			smtNode = makeInteger(signature.getLogic().getIntsSet(), signature);
			break;
		case Formula.BOOL:
			smtNode = makeBool(signature.getLogic().getBoolsSet(), signature);
			break;
		case Formula.TRUE:
			/**
			 * This case is never reached because it is translated in its parent
			 * node.
			 */
			throw new IllegalTagException(expression.getTag());
		default:
			/**
			 * NATURAL, NATURAL1, EMPTYSET, KPRED, KSUCC, KPRJ1_GEN, KPRJ2_GEN,
			 * KID_GEN,FALSE tags cannot be produced by ppTrans.
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B binary expression into an SMT node.
	 */
	@Override
	public void visitBinaryExpression(final BinaryExpression expression) {
		final Expression left = expression.getLeft();
		final Expression right = expression.getRight();
		final SMTTerm[] children = smtTerms(left, right);
		switch (expression.getTag()) {
		case Formula.MINUS:
			smtNode = sf.makeMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MINUS), children, signature);
			break;
		case Formula.MAPSTO:

			if (left.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(0, children[0]);
			}

			if (right.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(children[1]);
			}
			break;
		case Formula.DIV:
			smtNode = sf.makeDiv((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.DIV), children, signature);
			break;
		case Formula.MOD:
			smtNode = sf.makeMod((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MOD), children, signature);
			break;
		case Formula.EXPN:
			smtNode = sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.EXPN), children, signature);
			break;
		default:
			/**
			 * REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR, TBIJ,
			 * SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
			 * UPTO, FUNIMAGE, RELIMAGE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B bool expression into an SMT node.
	 */
	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		/**
		 * KBOOL cannot be produced by ppTrans.
		 */
		throw new IllegalArgumentException(
				Messages.Incompatible_Formula_With_PPTrans_Production);
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
			smtNode = SMTFactory.makePTrue(signature);
			break;
		case Formula.BFALSE:
			smtNode = SMTFactory.makePFalse(signature);
			break;
		default:
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B relational actualPredicate into an SMT
	 * node.
	 */
	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		final int tag = predicate.getTag();
		Expression left;
		Expression right;
		switch (tag) {
		case Formula.EQUAL: {
			left = predicate.getLeft();
			right = predicate.getRight();

			if (left.getTag() == Formula.TRUE) {
				smtNode = translateTruePred(right);
				break;
			} else if (right.getTag() == Formula.TRUE) {
				smtNode = translateTruePred(left);
				break;
			} else if (left.getType() instanceof BooleanType) {
				smtNode = translateTruePredWithId(left, right);
				break;
			}

			final SMTTerm[] children = smtTerms(left, right);
			smtNode = SMTFactory.makeEqual(children);
			break;
		}
		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessThan((SMTPredicateSymbol) signature.getLogic()
					.getOperator(SMTOperator.LT), children, signature);
		}
			break;
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.LE), children,
					signature);
		}
			break;
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterThan((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GT), children,
					signature);
		}
			break;
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GE), children,
					signature);
		}
			break;
		case Formula.IN:
			translateMemberShipPredicate(predicate);
			break;
		default:
			/**
			 * NOTEQUAL, NOTIN, SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ
			 * cannot be produced by ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B unary expression into an SMT node.
	 */
	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			smtNode = sf.makeUMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.UMINUS), children, signature);
			break;
		default:
			/**
			 * KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID,
			 * KMIN, KMAX, CONVERSE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitSetExtension(final SetExtension expression) {
		throw new IllegalArgumentException(
				Messages.Incompatible_Formula_With_PPTrans_Production);
	}

	/**
	 * This method translates an Event-B bound identifier declaration into an
	 * SMT node.
	 */
	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String varName = boundIdentDecl.getName();
		final SMTVar smtVar;

		final String smtVarName = signature.freshSymbolName(varName);
		final SMTSortSymbol sort = typeMap.get(boundIdentDecl.getType());
		smtVar = (SMTVar) sf.makeVar(smtVarName, sort);
		if (!qVarMap.containsKey(varName)) {
			qVarMap.put(varName, smtVar);
			boundIdentifiers.add(varName);
		} else {
			qVarMap.put(smtVarName, smtVar);
			boundIdentifiers.add(smtVarName);
		}
		smtNode = smtVar;
	}

	/**
	 * This method translates an Event-B free identifier into an SMT node.
	 */
	@Override
	public void visitFreeIdentifier(final FreeIdentifier expression) {
		final SMTSymbol symbol = varMap.get(expression.getName());

		if (symbol instanceof SMTFunctionSymbol) {
			smtNode = SMTFactory.makeConstant((SMTFunctionSymbol) symbol,
					signature);
			return;
		}
		smtNode = SMTFactory.makeAtom((SMTPredicateSymbol) symbol,
				new SMTTerm[] {}, signature);
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"'becomes equal to' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"'becomes member of' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"'becomes such that' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Simple predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Multiple predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		throw new IllegalArgumentException(
				"'Extended predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}
}
