/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;

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
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
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
import fr.systerel.smt.provers.ast.SMTSignaturePP;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory;
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.ast.SMTVarSymbol;
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
	/**
	 * An instance of <code>SMTThroughPP</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	private SMTSignaturePP signature;
	/**
	 * Map of all the sets that will be translated in optimized membership
	 * predicates to their event-B types.
	 */
	private final Map<String, Type> monadicSets = new HashMap<String, Type>();
	/**
	 * In order to translate memberships, the approach implemented in this class
	 * defines some new predicates. <code>msTypeMap</code> is a map between each
	 * Event-B type used in a membership and the corresponding SMT-LIB
	 * actualPredicate symbols.
	 */
	private final Map<Type, SMTPredicateSymbol> msTypeMap = new HashMap<Type, SMTPredicateSymbol>();
	/**
	 * This list contains the terms of the current membership being translated
	 * in the translation process.
	 */
	// FIXME Seems to be unsafe, to be deleted if possible
	private final List<SMTTerm> membershipPredicateTerms = new ArrayList<SMTTerm>();

	// FIXME remove this field
	private Predicate actualPredicate;

	// FIXME remove this field
	private boolean usesTruePred = false;

	// FIXME remove this field
	private SMTTerm intSetTerm = null;

	// FIXME remove this field
	private SMTFormula intAxiom;

	/**
	 * Constructor of a PP approach translator of Event-B to SMT-LIB
	 * 
	 * @param solver
	 *            Solver which will be used to discharge the sequent
	 * @param predicate
	 */
	public SMTThroughPP(final String solver, final Predicate predicate) {
		super(solver);
		sf = SMTFactoryPP.getInstance();
		actualPredicate = predicate;
	}

	private void setUsesTruePred(final boolean usesTruePred) {
		this.usesTruePred = usesTruePred;
	}

	/**
	 * @param actualPredicate
	 *            the actualPredicate to set
	 */
	private void setActualPredicate(final Predicate actualPredicate) {
		this.actualPredicate = actualPredicate;
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
			// FIXME add an exception "EventBTypeMisFormed"
			return null;
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

	/**
	 * This method extracts all the sets that will be translated in optimized
	 * membership predicates, that is, the sets complying with the following
	 * rules:
	 * 
	 * <ul>
	 * <li>The set only occur on the right-hand side of membership predicates;
	 * <li>No bound variable occurs in the right-hand side of similar membership
	 * predicates;
	 * </ul>
	 * 
	 * Then these sets are used as operator with one argument, instead of
	 * creating a fresh membership predicate where the set is one of the
	 * arguments.
	 * 
	 * @param hypotheses
	 *            The hypotheses of the sequent
	 * @param goal
	 *            The goal of the sequent
	 * @return All the sets to be translated into monadic membership predicates
	 */
	private Map<String, Type> extractMonadicSetsMap(
			final List<Predicate> hypotheses, final Predicate goal) {

		/**
		 * This class is used to get all the relational predicates of the
		 * hypotheses and the goal of the proof. The intention is to extract the
		 * sets that will be translated into monadic membership predicates
		 * 
		 * @author vitor
		 * 
		 */
		class MemberShipPredicateInspector extends
				DefaultInspector<RelationalPredicate> {

			/**
			 * Stores in the accumulator the relational predicates which the tag
			 * is {@link Formula#IN}
			 */
			@Override
			public void inspect(final RelationalPredicate relPredicate,
					final IAccumulator<RelationalPredicate> accumulator) {
				if (relPredicate.getTag() == Formula.IN) {
					accumulator.add(relPredicate);
				}
			}
		}

		/**
		 * Extraction of all membership predicates from hypotheses and goal
		 */
		final MemberShipPredicateInspector membershipPredicateInspector = new MemberShipPredicateInspector();
		final List<RelationalPredicate> membershipPredicates = new ArrayList<RelationalPredicate>();

		for (final Predicate predicate : hypotheses) {
			membershipPredicates.addAll(predicate
					.inspect(membershipPredicateInspector));
		}
		membershipPredicates.addAll(goal.inspect(membershipPredicateInspector));

		/**
		 * Extraction of all membership right-hand side free variables
		 */
		final Map<String, Type> monadicSetsMap = new HashMap<String, Type>();
		final Set<Type> boundSetsTypes = new HashSet<Type>();
		Expression right;

		for (final RelationalPredicate membershipPredicate : membershipPredicates) {
			right = membershipPredicate.getRight();

			assert right instanceof FreeIdentifier
					|| right instanceof BoundIdentifier;

			if (right instanceof FreeIdentifier) {
				final FreeIdentifier rightSet = (FreeIdentifier) right;
				if (right.getType().getSource() == null) {
					monadicSetsMap.put(rightSet.getName(), rightSet.getType());
				}
			} else if (right instanceof BoundIdentifier) {
				boundSetsTypes.add(((BoundIdentifier) right).getType());
			}
		}

		/**
		 * Removal of all bounded variables from the map of monadic sets.
		 */
		for (final String name : monadicSetsMap.keySet()) {
			if (boundSetsTypes.contains(monadicSetsMap.get(name))) {
				monadicSetsMap.remove(name);
			}
		}

		/**
		 * monadicSetsMap is a mapping from sets to their types
		 */
		return monadicSetsMap;
	}

	/**
	 * determines the logic
	 * 
	 * @param hypotheses
	 *            the hypotheses
	 * @param goal
	 *            the goal
	 * @param boolVisitor
	 *            the visitor that checked for boolean symbols in the predicates
	 * @return the logic that will be used in the benchmark
	 */
	private SMTLogic determineLogic(final List<Predicate> hypotheses,
			final Predicate goal, final BoolTheoryVisitor boolVisitor) {
		for (final Predicate h : hypotheses) {
			h.accept(boolVisitor);
			if (boolVisitor.isBoolTheory() && boolVisitor.usesTruePred()) {
				usesTruePred = true;
				return new SMTLogic(SMTLogic.UNKNOWN, Ints.getInstance(),
						Booleans.getInstance());
			}
		}
		goal.accept(boolVisitor);
		if (boolVisitor.isBoolTheory()) {
			if (boolVisitor.usesTruePred()) {
				usesTruePred = true;
				return new SMTLogic(SMTLogic.UNKNOWN, Ints.getInstance(),
						Booleans.getInstance());
			}
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
		final List<Type> biTypes = getBoundIDentDeclTypes(hypotheses, goal);

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
	 * of translation sets, the membership is translated to:
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

		// Translate monadic sets (special case)
		if (right instanceof FreeIdentifier) {
			final FreeIdentifier rightSet = (FreeIdentifier) right;
			if (monadicSets.containsKey(rightSet.getName())) {
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

		final SMTPredicateSymbol predSymbol = createMembershipPredicateSymbol(
				leftType, argSorts);

		final SMTTerm[] args = membershipPredicateTerms
				.toArray(new SMTTerm[numberOfArguments]);

		smtNode = SMTFactory.makeAtom(predSymbol, signature, args);
		membershipPredicateTerms.clear();
	}

	private void translateInMonadicMembershipPredicate(
			final Expression leftExpression, final FreeIdentifier rightSet) {
		final Type leftType = leftExpression.getType();
		final SMTTerm leftTerm = smtTerm(leftExpression);
		SMTPredicateSymbol monadicMembershipPredicate = msTypeMap.get(leftType);

		if (monadicMembershipPredicate == null) {
			// FIXME Check the behavior of this method
			monadicMembershipPredicate = signature.freshPredicateSymbol(
					rightSet.getName(), leftTerm.getSort());
			msTypeMap.put(leftExpression.getType(), monadicMembershipPredicate);
		}

		smtNode = SMTFactory.makeAtom(monadicMembershipPredicate, signature,
				leftTerm);
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
	private SMTPredicateSymbol createMembershipPredicateSymbol(final Type type,
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
		if (!usesTruePred) {
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
	 * This method check if the BOOL set expression is in accordance with the
	 * Bool theory rule, for the PP approach, that concerns the BOOL set.
	 * 
	 * @see BoolSetVisitor
	 * 
	 * @param pred
	 *            The actual hypothesis (or goal) being traversed.
	 * @param atomicExpression
	 *            The BOOL set atomic expression
	 */
	private void checkBoolSet(final Predicate pred,
			final AtomicExpression atomicExpression) {
		for (final SMTTheory theory : signature.getLogic().getTheories()) {
			if (theory instanceof Booleans) {
				final BoolSetVisitor bv = new BoolSetVisitor(atomicExpression);
				pred.accept(bv);
				return;
			}
		}
	}

	/**
	 * @param left
	 * @param right
	 */
	private SMTFormula translateTruePredWithId(final Expression left,
			final Expression right) {
		final SMTFormula leftFor = translateTruePred(left);
		final SMTFormula rightFor = translateTruePred(right);

		return SMTFactory.makeIff(leftFor, rightFor);
	}

	private SMTFormula translateTruePred(final Expression expr) {
		if (expr.getTag() == Formula.TRUE) {
			throw new IllegalArgumentException(
					"Predefined literal TRUE cannot happen in both sides of boolean equality");
		} else {
			if (usesTruePred) {
				final SMTTerm term = smtTerm(expr);
				return SMTFactory.makeAtom(signature.getLogic().getTrue(),
						signature, term);
			}
		}
		return smtFormula(expr);
	}

	/**
	 * Generate the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ ℤ</code>
	 * 
	 * @param type
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateIntegerAxiom(final Type type,
			final SMTTerm intSet) {
		final String symbolName = signature.freshSymbolName("x");
		final SMTSortSymbol intSort = signature.getLogic().getIntegerSort();

		final SMTVarSymbol vs = new SMTVarSymbol(symbolName, intSort,
				!PREDEFINED);
		final SMTTerm term = new SMTVar(vs);

		final SMTPredicateSymbol predSymbol = createMembershipPredicateSymbol(
				type, intSort, intSort);

		final SMTFormula formula = SMTFactory.makeAtom(predSymbol, signature,
				term, intSet);

		return SMTFactory.makeForAll(formula, term);
	}

	/**
	 * Generate the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ BOOL</code>
	 * 
	 * @param type
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateBoolAxiom(final Type type) {
		final String symbolName = signature.freshSymbolName("x");
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();

		final SMTVarSymbol vs = new SMTVarSymbol(symbolName, boolSort,
				!PREDEFINED);
		final SMTTerm term = new SMTVar(vs);
		final SMTTerm termBool = SMTFactory.makeConstant(signature.getLogic()
				.getBooleanCste(), signature);

		final SMTPredicateSymbol predSymbol = createMembershipPredicateSymbol(
				type, boolSort, boolSort);

		final SMTFormula formula = SMTFactory.makeAtom(predSymbol, signature,
				term, termBool);

		return SMTFactory.makeForAll(formula, term);
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
		actualPredicate = predicate;
		predicate.accept(this);
		return getSMTFormula();
	}

	/**
	 * @param lemmaName
	 *            the name of the lemma
	 * @param ppTranslatedHypotheses
	 *            the PP translated hypotheses
	 * @param ppTranslatedGoal
	 *            the PP translated goal
	 * @param logic
	 *            the used logic
	 * @return the SMTBenchmark of the translation
	 */
	private SMTBenchmark translate(final String lemmaName,
			final List<Predicate> ppTranslatedHypotheses,
			final Predicate ppTranslatedGoal, final SMTLogic logic) {
		translateSignature(logic, ppTranslatedHypotheses, ppTranslatedGoal);

		// translates each hypothesis
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		for (final SMTTheory t : signature.getLogic().getTheories()) {
			if (t instanceof Booleans) {
				translatedAssumptions.add(generateBoolAxiom(FormulaFactory
						.getDefault().makeBooleanType()));
			}
		}

		for (final Predicate hypothesis : ppTranslatedHypotheses) {
			clearFormula();
			translatedAssumptions.add(translate(hypothesis));
		}
		// translates the goal
		clearFormula();
		final SMTFormula smtFormula = translate(ppTranslatedGoal);

		if (intAxiom != null) {
			translatedAssumptions.add(0, intAxiom);
		}

		final SMTBenchmarkPP benchmark = new SMTBenchmarkPP(lemmaName,
				signature, translatedAssumptions, smtFormula);
		benchmark.removeUnusedSymbols();
		return benchmark;
	}

	/**
	 * This class checks if the hypotheses or the goal has elements of the
	 * boolean theory
	 * 
	 * @author vitor
	 * 
	 */
	class BoolTheoryVisitor extends DefaultVisitor {

		private boolean boolTheory = false;
		private boolean usesTruePredicate = false;

		boolean usesTruePred() {
			return usesTruePredicate;
		}

		/**
		 * 
		 * @return true if at least one hypothesis or the goal has boolean
		 *         theory elements.
		 */
		boolean isBoolTheory() {
			return boolTheory;
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
			if (pred.getLeft().getType() instanceof BooleanType
					|| pred.getRight().getType() instanceof BooleanType) {
				usesTruePredicate = true;
				boolTheory = true;
				return false;
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
	 * This class visits a actualPredicate and checks if the actualPredicate
	 * agrees with the following rule:
	 * <p>
	 * The predeﬁned set BOOL can only occur in a maplet expression in the
	 * left-hand side of a membership actualPredicate.
	 * 
	 * @see SMTThroughPP#checkBoolSet(Predicate, AtomicExpression)
	 * @author vitor
	 */
	class BoolSetVisitor extends DefaultVisitor {

		private RelationalPredicate membershipPredicate;
		private final AtomicExpression atomicExpression;

		/**
		 * Constructor that stores an atomic expresion which the tag is
		 * <code>BOOL</code>.
		 * 
		 * @param expr
		 */
		public BoolSetVisitor(final AtomicExpression expr) {
			assert expr.getTag() == Formula.BOOL;
			atomicExpression = expr;
		}

		@Override
		/**
		 * This method just stores the relational actualPredicate
		 */
		public boolean enterIN(final RelationalPredicate pred) {
			membershipPredicate = pred;
			return true;
		}

		/**
		 * This method checks, for each MAPSTO expression:
		 * <ul>
		 * <li>If the left or the right of the binary expression correspond to
		 * the stored atomicExpression
		 * <li>If so, check if the parent of the MAPSTO expression is a
		 * membership actualPredicate. If not, throws an exception
		 * <li>else keep traversing the actualPredicate
		 * </ul>
		 */
		@Override
		public boolean enterMAPSTO(final BinaryExpression expr) {
			assert atomicExpression != null;
			if (expr.getLeft().equals(atomicExpression)
					|| expr.getRight().equals(atomicExpression)) {
				if (membershipPredicate.getLeft().equals(expr)) {
					return false;
				} else {
					throw new IllegalArgumentException(
							" The predeﬁned set BOOL can only occur in a maplet expression in the left-hand side of a membership actualPredicate.");
				}
			} else {
				return true;
			}
		}
	}

	/**
	 * This method determines the used logic.
	 */
	@Override
	protected SMTLogic determineLogic(final List<Predicate> hypotheses,
			final Predicate goal) {
		final BoolTheoryVisitor bI = new BoolTheoryVisitor();
		return determineLogic(hypotheses, goal, bI);
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

		monadicSets.putAll(extractMonadicSetsMap(hypotheses, goal));

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
			if (monadicSets.containsKey(varName)) {
				varType = monadicSets.get(varName).getBaseType();
				parseConstant = false;
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
		translateBoundIdentTypes(hypotheses, goal);

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
		// translates the signature
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
		final SMTBenchmark smtB = new SMTThroughPP(solver, null).translate(
				lemmaName, hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			Predicate predicate, final String solver, final boolean usesTruePred) {
		final SMTThroughPP translator = new SMTThroughPP(solver, null);
		predicate = translator.recursiveAutoRewrite(predicate);
		translator.setActualPredicate(predicate);
		translator.setUsesTruePred(usesTruePred);
		translator.translateSignature(logic, new ArrayList<Predicate>(0),
				predicate);
		return translator.translate(predicate);
	}

	/**
	 * This method is used only to test the logic determination
	 */
	public static SMTLogic determineLogic(Predicate goalPredicate) {
		final SMTThroughPP translator = new SMTThroughPP(null, null);
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
					.getOperator(SMTOperator.PLUS), signature, children);
			break;
		case Formula.MUL:
			smtNode = sf.makeMul((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MUL), signature, children);
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
			if (intSetTerm == null) {
				final SMTFunctionSymbol intSymbol = signature.freshConstant(
						"int", signature.getLogic().getIntegerSort());
				intSetTerm = SMTFactory.makeInteger(intSymbol, signature);
				intAxiom = generateIntegerAxiom(expression.getType(),
						intSetTerm);
				smtNode = intSetTerm;
			} else {
				smtNode = intSetTerm;
			}
			break;
		case Formula.BOOL:
			assert actualPredicate != null;
			checkBoolSet(actualPredicate, expression);
			smtNode = sf.makeBool(signature.getLogic().getBooleanCste(),
					signature);
			break;
		case Formula.TRUE:
			// This case is never reached because it is translated in its parent
			// node.
			break;
		default:
			// TODO check that it's true for KPRED, KSUCC, KPRJ1_GEN, KPRJ2_GEN,
			// KID_GEN tags
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
					.getOperator(SMTOperator.MINUS), signature, children);
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
					.getOperator(SMTOperator.DIV), signature, children);
			break;
		case Formula.MOD:
			smtNode = sf.makeMod((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MOD), signature, children);
			break;
		case Formula.EXPN:
			smtNode = sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.EXPN), signature, children);
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
		final SMTFormula pred = smtFormula(expression.getPredicate());
		switch (expression.getTag()) {
		case Formula.KBOOL:
			smtNode = pred; // FIXME Is that right?
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B literal actualPredicate into an SMT
	 * node.
	 */
	@Override
	public void visitLiteralPredicate(final LiteralPredicate pred) {
		switch (pred.getTag()) {
		case Formula.BTRUE:
			smtNode = sf.makePTrue(signature);
			break;
		case Formula.BFALSE:
			smtNode = sf.makePFalse(signature);
			break;
		default:
			throw new IllegalTagException(actualPredicate.getTag());
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
		case Formula.NOTEQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory.makeNotEqual(children);
		}
			break;
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
					.getLogic().getOperator(SMTOperator.GT), signature,
					children);
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
		case Formula.NOTIN:
			// TODO
			break;
		default:
			/**
			 * SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ cannot be produced by
			 * ppTrans.
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
					.getOperator(SMTOperator.UMINUS), signature, children);
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
		// TODO
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
	 * This method translates an Event-B bound identifier into an SMT node.
	 */
	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String bidName = boundIdentifiers.get(boundIdentifiers.size()
				- expression.getBoundIndex() - 1);
		smtNode = qVarMap.get(bidName);
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
		smtNode = SMTFactory.makeAtom((SMTPredicateSymbol) symbol, signature);
	}

	/**
	 * This method translates an Event-B quantified actualPredicate into an SMT
	 * node
	 */
	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		boundIdentifiersMarker.push(boundIdentifiers.size());

		final SMTTerm[] termChildren = smtTerms(predicate.getBoundIdentDecls());
		final SMTFormula formulaChild = smtFormula(predicate.getPredicate());

		switch (predicate.getTag()) {
		case Formula.FORALL:
			smtNode = SMTFactory.makeForAll(formulaChild, termChildren);
			break;
		case Formula.EXISTS:
			smtNode = sf.makeExists(formulaChild, termChildren);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
		final int top = boundIdentifiersMarker.pop();
		boundIdentifiers.subList(top, boundIdentifiers.size()).clear();
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"'becomes equal to' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"'becomes member of' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"'becomes such that' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Simple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Multiple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		throw new IllegalArgumentException(
				"'Extended predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}
}
