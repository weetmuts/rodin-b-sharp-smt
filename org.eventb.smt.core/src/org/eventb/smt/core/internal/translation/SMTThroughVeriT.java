/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - Code refactoring and SMT-LIB 2.0 implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.translation;

import static org.eventb.smt.core.internal.ast.SMTFactory.makeEqual;
import static org.eventb.smt.core.internal.ast.SMTFactoryVeriT.makeITE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.core.internal.ast.SMTBenchmarkVeriT;
import org.eventb.smt.core.internal.ast.SMTFactory;
import org.eventb.smt.core.internal.ast.SMTFactoryPP;
import org.eventb.smt.core.internal.ast.SMTFactoryVeriT;
import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.ast.SMTSignatureV2_0Verit;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.SMTVar;
import org.eventb.smt.core.internal.ast.macros.SMTMacroFactoryV2_0;
import org.eventb.smt.core.internal.ast.macros.SMTMacroFactoryV2_0.SMTVeriTOperatorV2_0;
import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.theories.Logic;
import org.eventb.smt.core.internal.ast.theories.Logic.AUFLIAV2_0VeriT;
import org.eventb.smt.core.internal.ast.theories.Logic.AUFLIAv2_0;
import org.eventb.smt.core.internal.ast.theories.Logic.QF_AUFLIAv2_0;
import org.eventb.smt.core.internal.ast.theories.Logic.QF_AUFLIAv2_0VeriT;
import org.eventb.smt.core.internal.ast.theories.Logic.SMTOperator;
import org.eventb.smt.core.internal.ast.theories.Theory;
import org.eventb.smt.core.internal.ast.theories.TheoryV2_0;
import org.eventb.smt.core.internal.ast.theories.VeriTBooleansV2_0;

/**
 * This class implements the translation from Event-B predicates to Extended
 * SMT-LIB formulas.
 * <p>
 * The Extended SMT-LIB formulas contains macros and other elements that does
 * not belong to the standard SMT-LIB and is accepted only by the VeriT solver
 * which pre-process these formulas to the standard SMT-LIB.
 * <p>
 * 
 */
public class SMTThroughVeriT extends Translator {

	/**
	 * This variable stores additional assumptions produced by the translation
	 * of min,max, finite and cardinality operators
	 */
	private final Set<SMTFormula> additionalAssumptions = new HashSet<SMTFormula>();

	/**
	 * An instance of <code>SMTThroughVeriT</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	private SMTSignature signature;

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTSignature translateTE(final Logic logic,
			final Predicate predicate) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		final ISimpleSequent sequent = SimpleSequents.make(noHypothesis,
				predicate, predicate.getFactory());
		translator.determineLogic(sequent);
		translator.translateSignature(logic, sequent);
		return translator.getSignature();
	}

	/**
	 * An instance of the SMTFactoryVeriT
	 */
	final private SMTFactoryVeriT sf;

	public SMTThroughVeriT() {
		sf = SMTFactoryVeriT.getInstance();
	}

	/**
	 * Returns the additional assumptions used for the translation of the
	 * event-B PO
	 * 
	 * @return the additional assumptions used for the translation of the
	 *         event-B PO
	 */
	public Set<SMTFormula> getAdditionalAssumptions() {
		return additionalAssumptions;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final Logic logic,
			final Predicate predicate, final FormulaFactory ff) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		final ISimpleSequent sequent = SimpleSequents.make(noHypothesis,
				predicate, ff);
		translator.translateSignature(logic, sequent);
		try {
			predicate.accept(translator);
		} catch (IllegalArgumentException e) {
			return SMTFactoryPP.makePFalse();
		}
		return translator.getSMTFormula();
	}

	private static class Gatherer extends DefaultVisitor {
		private boolean boolTheory = false;
		private boolean quantifierFound = false;

		/**
		 * This method executes the traversal in the hypotheses and goal to
		 * process the informations described in {@link Gatherer}. It also makes
		 * the mapping of each free identifier to its correlated predicate
		 * symbol
		 * 
		 * @param sequent
		 *            the sequent of which predicates must be traversed by the
		 *            gatherer
		 * @return a new gatherer with the results of the traversal.
		 */
		public static Gatherer gatherFrom(final ISimpleSequent sequent) {
			final Gatherer gatherer = new Gatherer();

			for (final ITrackedPredicate trPredicate : sequent.getPredicates()) {
				trPredicate.getPredicate().accept(gatherer);
			}

			return gatherer;
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
		 * If one of the predicates has a BOOL set, set <code>boolTheory</code>
		 * <i>true</i>
		 */
		@Override
		public boolean visitBOOL(final AtomicExpression expr) {
			boolTheory = true;
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			quantifierFound = true;
			return true;
		}

		/**
		 * If one of the predicates has a TRUE constant, set
		 * <code>boolTheory</code> <i>true</i>
		 */
		@Override
		public boolean visitTRUE(final AtomicExpression expr) {
			boolTheory = true;
			return true;
		}

		/**
		 * If one of the predicates has a FALSE constant, set
		 * <code>boolTheory</code> <i>true</i>
		 */
		@Override
		public boolean visitFALSE(final AtomicExpression expr) {
			boolTheory = true;
			return true;
		}

		/**
		 * If the predicate has a bool expression, set <code>boolTheory</code>
		 * <i>true</i>
		 */
		@Override
		public boolean enterKBOOL(BoolExpression expr) {
			boolTheory = true;
			return true;
		}

		public boolean foundQuantifier() {
			return quantifierFound;
		}
	}

	/**
	 * Determines the logic to be set in the benchmark. A logic setting is
	 * necessary for most of the solvers.
	 * 
	 * @param sequent
	 *            the sequent of which the logic must be determined
	 * @return the logic that will be used in the benchmark
	 */
	@Override
	protected Logic determineLogic(final ISimpleSequent sequent) {
		final Gatherer gatherer = Gatherer.gatherFrom(sequent);
		if (gatherer.usesBoolTheory()) {
			if (gatherer.foundQuantifier()) {
				return new Logic.SMTLogicVeriT(AUFLIAv2_0.getInstance()
						.getName(), TheoryV2_0.Ints.getInstance(),
						VeriTBooleansV2_0.getInstance());
			} else {
				return new Logic.SMTLogicVeriT(QF_AUFLIAv2_0
						.getInstance().getName(),
						TheoryV2_0.Ints.getInstance(),
						VeriTBooleansV2_0.getInstance());
			}

		} else {
			if (gatherer.foundQuantifier()) {
				return AUFLIAV2_0VeriT.getInstance();

			} else {
				return QF_AUFLIAv2_0VeriT.getInstance();
			}
		}
	}

	/**
	 * Once the theory {@link VeriTBooleansV2_0} is added to the logic, it is
	 * created and added as an assumption the SMT format of the following
	 * formula:
	 * 
	 * <p>
	 * (∀x:BOOL · (x = TRUE) ∨ (x = FALSE)) ∧ (TRUE ≠ FALSE)
	 * 
	 * @param logic
	 *            the logic in use.
	 */
	private void addBooleanAssumption(final Logic logic) {
		for (final Theory theory : logic.getTheories()) {
			if (theory instanceof VeriTBooleansV2_0) {
				final String boolVarName = signature.freshSymbolName("elem");
				additionalAssumptions.add(sf
						.makeDefinitionOfElementsOfBooleanFormula_V2_0(
								boolVarName, VeriTBooleansV2_0.getInstance()
										.getBooleanSort(), VeriTBooleansV2_0
										.getInstance().getTrueConstant(),
								VeriTBooleansV2_0.getInstance()
										.getFalseConstant()));
				return;
			}
		}
	}

	@Override
	public void translateSignature(final Logic logic,
			final ISimpleSequent sequent) {
		signature = new SMTSignatureV2_0Verit(logic);

		addBooleanAssumption(logic);

		linkLogicSymbols();

		final ITypeEnvironment typeEnvironment = sequent.getTypeEnvironment();

		translateTypeEnvironment(typeEnvironment);

		final List<Type> biTypes = BidTypeInspector
				.getBoundIDentDeclTypes(sequent);
		final Iterator<Type> bIterator = biTypes.iterator();

		extractTypeFromBoundIdentDecl(bIterator);
	}

	/**
	 * This method links some symbols of the logic to the main Event-B symbols.
	 * It is not being used.
	 */
	private void linkLogicSymbols() {
		// TODO
	}

	/**
	 * This method extracts types of bound ident declarations and adds them into
	 * the signature
	 * 
	 * @param iter
	 *            The iterator which contains the types of bound ident
	 *            declarations
	 */
	private void extractTypeFromBoundIdentDecl(final Iterator<Type> iter) {
		while (iter.hasNext()) {
			final Type varType = iter.next();
			translateTypeName(varType);
		}
	}

	/**
	 * Translate variables which type is product type.
	 * 
	 * @param varName
	 *            The name of the variable
	 */
	private void translatePredSymbol(final String varName,
			final SMTSortSymbol sort) {
		final SMTPredicateSymbol predSymbol = signature.freshPredicateSymbol(
				varName, sort);
		varMap.put(varName, predSymbol);
	}

	/**
	 * Translate variables into functions
	 * 
	 * @param varName
	 *            the name of variable
	 */
	private void translateFunSymbol(final String varName,
			final SMTSortSymbol sort) {
		final SMTFunctionSymbol smtConstant;
		smtConstant = signature.freshConstant(varName, sort);
		varMap.put(varName, smtConstant);
	}

	/**
	 * This method translates the signature.
	 * 
	 * @param typeEnvironment
	 *            The Event-B Type Environment for the translation.
	 */
	public void translateTypeEnvironment(final ITypeEnvironment typeEnvironment) {
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final Type varType = iter.getType();
			final SMTSortSymbol sort = translateTypeName(varType);
			if (varType.getSource() != null || varType.getBaseType() != null) {
				translatePredSymbol(varName, sort);
				if (varType.getSource() != null) {
					((SMTSignatureV2_0Verit) signature)
							.addPairSortAndFunction();
				}
			} else {
				translateFunSymbol(varName, sort);
			}
		}
	}

	@Override
	public BenchmarkResult translate(final String lemmaName,
			final ISimpleSequent sequent) {
		final Logic logic = determineLogic(sequent);
		translateSignature(logic, sequent);

		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		SMTFormula smtFormula = null;
		boolean falseGoalNeeded = true;
		translatedAssumptions.addAll(getAdditionalAssumptions());

		for (final ITrackedPredicate trackedPredicate : sequent.getPredicates()) {
			clearFormula();
			final Predicate predicate = trackedPredicate.getPredicate();
			/**
			 * If it is an hypothesis
			 */
			if (trackedPredicate.isHypothesis()) {
				final SMTFormula translatedFormula = translate(predicate,
						!IN_GOAL);
				translatedAssumptions.addAll(getAdditionalAssumptions());
				translatedAssumptions.add(translatedFormula);
			}
			/**
			 * If it is the goal
			 */
			else {
				falseGoalNeeded = false;
				smtFormula = SMTFactory.makeNot(
						new SMTFormula[] { translate(predicate, IN_GOAL) });
				translatedAssumptions.addAll(getAdditionalAssumptions());
			}
		}
		if (falseGoalNeeded) {
			smtFormula = SMTFactory
					.makeNot(new SMTFormula[] { SMTFactory.makePFalse() });
		}

		final SMTBenchmarkVeriT benchmark = new SMTBenchmarkVeriT(lemmaName,
				signature, translatedAssumptions, smtFormula);
		benchmark.removeUnusedSymbols();
		return new BenchmarkResult(benchmark);
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final Predicate predicate,
			final FormulaFactory ff) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		final ISimpleSequent sequent = SimpleSequents.make(noHypothesis,
				predicate, ff);
		final Logic logic = translator.determineLogic(sequent);
		translator.translateSignature(logic, sequent);
		return translator.translate(predicate, IN_GOAL);
	}

	/**
	 * This method translates one predicate.
	 * 
	 * @param predicate
	 *            The Rodin predicate to be translated.
	 * @return the translated SMT Formula from the predicate
	 */
	private SMTFormula translate(final Predicate predicate, final boolean inGoal) {
		try {
			predicate.accept(this);
		} catch (IllegalArgumentException e) {
			if (inGoal) {
				if (DEBUG) {
					System.err.println("Catched IllegalArgumentException : '"
							+ e.getMessage() + "'.");
					System.err.println("Replacing with \u22a5 in goal.");
				}
				return SMTFactoryPP.makePFalse();
			} else {
				if (DEBUG) {
					System.err.println("Catched IllegalArgumentException : '"
							+ e.getMessage() + "'.");
					System.err.println("Replacing with \u22a4 in hypothesis.");
				}
				return SMTFactoryPP.makePTrue();
			}
		}
		return getSMTFormula();
	}

	@Override
	protected SMTSortSymbol translateTypeName(final Type type) {
		SMTSortSymbol sortSymbol;
		final Type baseType = type.getBaseType();
		if (baseType != null) {
			checkIfIsSetOfSet(baseType, type);
			sortSymbol = typeMap.get(baseType);
			if (sortSymbol == null) {
				if (baseType instanceof ProductType) {
					sortSymbol = translateProductType((ProductType) baseType);
					((SMTSignatureV2_0Verit) signature).addPairSortAndFunction();
					typeMap.put(baseType, sortSymbol);
				} else {
					sortSymbol = signature.freshSort(baseType.toString());
					typeMap.put(baseType, sortSymbol);
				}
			}
		} else {
			sortSymbol = typeMap.get(type);
			if (sortSymbol == null) {
				if (type instanceof ProductType) {
					sortSymbol = translateProductType((ProductType) type);
				} else {
					String freshSortName = type.toString();
					sortSymbol = signature.freshSort(freshSortName);
					typeMap.put(type, sortSymbol);
				}
			}
		}
		return sortSymbol;
	}

	/**
	 * Check if type is set of set.
	 * 
	 * @param type
	 *            the type of the set.
	 * @param parentType
	 *            the parent type of type. It's used for exception output string
	 */
	private void checkIfIsSetOfSet(final Type type, final Type parentType) {
		if (type.getSource() != null || type.getBaseType() != null) {
			throw new IllegalArgumentException("Type " + parentType.toString()
					+ ": sets of sets are not supported yet");
		}
	}

	/**
	 * translate ProductType types
	 * 
	 * @param type
	 *            the ProductType
	 * @return the translated SMT sort symbol
	 */
	private SMTSortSymbol translateProductType(final ProductType type) {
		checkIfIsSetOfSet(type);
		final SMTSortSymbol left = translateTypeName(type.getLeft());
		final SMTSortSymbol right = translateTypeName(type.getRight());
		return SMTFactoryVeriT.makePairSortSymbol(left, right);
	}

	/**
	 * Check if the product type is set of set
	 * 
	 * @param type
	 *            the product type
	 */
	private void checkIfIsSetOfSet(final ProductType type) {
		checkIfIsSetOfSet(type.getLeft(), type);
		checkIfIsSetOfSet(type.getRight(), type);
	}

	/**
	 * Clears the formula and the additional assumptions.
	 */
	@Override
	protected void clearFormula() {
		getAdditionalAssumptions().clear();
		smtNode = null;
	}

	/**
	 * This method translates an Event-B atomic expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		SMTSignatureV2_0Verit sig = (SMTSignatureV2_0Verit) signature;
		switch (expression.getTag()) {
		case Formula.KPRED:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.PRED_OP, sig));
			break;
		case Formula.KSUCC:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.SUCC_OP, sig));
			break;
		case Formula.INTEGER:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.INTEGER_OP, sig));
			break;
		case Formula.NATURAL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.NAT_OP, sig));
			break;
		case Formula.NATURAL1:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.NAT1_OP, sig));
			break;
		case Formula.EMPTYSET:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.EMPTY_OP, sig));
			break;
		case Formula.KID_GEN:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.ID_OP, sig));
			break;
		case Formula.BOOL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactoryV2_0
					.getMacroSymbol(SMTVeriTOperatorV2_0.BOOLS_OP, sig));
			break;
		case Formula.TRUE:
			smtNode = sf.makeTrueConstant(signature.getLogic()
					.getTrueConstant());
			break;
		case Formula.FALSE:
			smtNode = sf.makeFalseConstant(signature.getLogic()
					.getFalseConstant());
			break;
		case Formula.KPRJ1_GEN:
			throw new IllegalArgumentException(
					"prj1 (KPRJ1_GEN) is not implemented yet");
		case Formula.KPRJ2_GEN:
			throw new IllegalArgumentException(
					"prj2 (KPRJ2_GEN) is not implemented yet");
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B binary expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitBinaryExpression(final BinaryExpression expression) {
		throw new IllegalTagException(expression.getTag());
	}

	/**
	 * This method translates an Event-B bound identifier declaration into an
	 * Extended SMT node.
	 */
	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String varName = boundIdentDecl.getName();
		final SMTVar smtVar;
		final String smtVarName = ((SMTSignatureV2_0Verit) signature)
				.freshQVarName(varName);
		final SMTSortSymbol sort = typeMap.get(boundIdentDecl.getType());
		smtVar = (SMTVar) SMTFactory.makeVar(smtVarName, sort);
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
	 * Checks if the type is pair type
	 * 
	 * @param type
	 * @return if type is a pair type
	 */
	public static boolean isPairType(final Type type) {
		if (type instanceof ProductType) {
			return true;
		}
		if (type.getBaseType() != null) {
			return isPairType(type.getBaseType());
		}
		return false;
	}

	/**
	 * Translates the equal (=) operator
	 * 
	 * @param predicate
	 *            the predicate with the equal operator that will be translated
	 * @return the SMT Formula of the predicate
	 */
	private SMTFormula translateEqual(final RelationalPredicate predicate) {
		final SMTTerm[] children = smtTerms(predicate.getLeft(),
				predicate.getRight());
		return makeEqual(children);
	}

	/**
	 * This method translates an Event-B relational predicate into an Extended
	 * SMT node.
	 */
	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.EQUAL:
			smtNode = translateEqual(predicate);
			break;

		case Formula.NOTEQUAL:
			smtNode = SMTFactory.makeNot(new SMTFormula[] { translateEqual(predicate) });
			break;

		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(), predicate.getRight());
			smtNode = sf.makeLessThan((SMTPredicateSymbol) signature.getLogic().getOperator(SMTOperator.LT), children,
					signature);
			break;
		}
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(), predicate.getRight());
			smtNode = sf.makeLessEqual((SMTPredicateSymbol) signature.getLogic().getOperator(SMTOperator.LE), children,
					signature);
			break;
		}
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(), predicate.getRight());
			smtNode = sf.makeGreaterThan((SMTPredicateSymbol) signature.getLogic().getOperator(SMTOperator.GT),
					children, signature);
			break;
		}
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(), predicate.getRight());
			smtNode = sf.makeGreaterEqual((SMTPredicateSymbol) signature.getLogic().getOperator(SMTOperator.GE),
					children, signature);
			break;
		}
		case Formula.IN: {
			smtNode = translateRelationalPredicateMacroV2_0(SMTVeriTOperatorV2_0.IN_OP, predicate);
			break;
		}
		case Formula.SUBSET: {
			smtNode = translateRelationalPredicateMacroV2_0(SMTVeriTOperatorV2_0.SUBSET_OP, predicate);
			break;
		}
		case Formula.SUBSETEQ: {
			smtNode = translateRelationalPredicateMacroV2_0(SMTVeriTOperatorV2_0.SUBSETEQ_OP, predicate);
			break;
		}
		case Formula.NOTSUBSET: {
			final SMTFormula subset = translateRelationalPredicateMacroV2_0(SMTVeriTOperatorV2_0.SUBSET_OP, predicate);
			smtNode = SMTFactory.makeNot(new SMTFormula[] { subset });
			break;
		}
		case Formula.NOTSUBSETEQ: {
			final SMTFormula subseteq = translateRelationalPredicateMacroV2_0(SMTVeriTOperatorV2_0.SUBSETEQ_OP,
					predicate);
			smtNode = SMTFactory.makeNot(new SMTFormula[] { subseteq });
			break;
		}
		default:
			break;
		}
	}

	/**
	 * This method translates relational predicates to version 1.2.
	 * 
	 * @param operator
	 *            a relational operator
	 * @param predicate
	 *            the predicate to be translated
	 * @return the predicate translated to SMT-LIB 2.0
	 */
	private SMTFormula translateRelationalPredicateMacroV2_0(
			final SMTVeriTOperatorV2_0 operator,
			final RelationalPredicate predicate) {
		final SMTTerm[] children = smtTerms(predicate.getLeft(),
				predicate.getRight());
		return SMTFactoryVeriT.makeMacroAtom(SMTMacroFactoryV2_0
				.getMacroSymbol(operator, (SMTSignatureV2_0Verit) signature),
				children, (SMTSignatureV2_0Verit) signature);
	}

	/**
	 * This method is not reached in this translation
	 */
	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"BecomesEqualTo assignment is not implemented yet");
	}

	/**
	 * This method is not reached in this translation
	 */
	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"BecomesMemberOf assignment is not implemented yet");
	}

	/**
	 * This method is not reached in this translation
	 */
	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"BecomesSuchThat assignment is not implemented yet");
	}

	/**
	 * This method translates an Event-B associative expression into an Extended
	 * SMT node.
	 */
	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		SMTTerm[] children;
		final Expression[] expressions = expression.getChildren();
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			children = smtTerms(expressions);
			smtNode = sf.makePlus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.PLUS), children, signature);
			break;
		case Formula.MUL:
			children = smtTerms(expressions);
			smtNode = sf.makeMul((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MUL), children, signature);
			break;
		case Formula.BUNION:
			smtNode = translatePACOV2_0(SMTVeriTOperatorV2_0.BUNION_OP,
					expression, expressions);
			break;
		case Formula.BINTER:
			smtNode = translatePACOV2_0(SMTVeriTOperatorV2_0.BINTER_OP,
					expression, expressions);
			break;
		case Formula.FCOMP:
			smtNode = translatePACOV2_0(SMTVeriTOperatorV2_0.FCOMP_OP,
					expression, expressions);
			break;
		case Formula.BCOMP:
			smtNode = translatePACOV2_0(SMTVeriTOperatorV2_0.BCOMP_OP,
					expression, expressions);
			break;
		case Formula.OVR:
			smtNode = translatePACOV2_0(SMTVeriTOperatorV2_0.OVR_OP,
					expression, expressions);
			break;
		default:
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method is used to translate PACOs:
	 * 
	 * <ul>
	 * <li>Polyadic
	 * <li>Associative
	 * <li>Commutative
	 * <li>Operators
	 * </ul>
	 * 
	 * @param operator
	 *            the operator
	 * @param expression
	 *            the expression
	 * @param expressions
	 *            the children expressions
	 * @return the translated SMTTerm in SMT-LIB 2.0
	 */
	private SMTTerm translatePACOV2_0(final SMTVeriTOperatorV2_0 operator,
			final AssociativeExpression expression,
			final Expression[] expressions) {
		SMTTerm[] children;
		SMTTerm macroTerm;
		SMTSignatureV2_0Verit sig = (SMTSignatureV2_0Verit) signature;

		if (expressions.length == 2) {
			children = smtTerms(expression.getChildren());
			macroTerm = SMTFactoryVeriT
					.makeMacroTerm(
							SMTMacroFactoryV2_0.getMacroSymbol(operator, sig),
							children);
		} else {
			children = smtTerms(expressions[0], expressions[1]);
			macroTerm = SMTFactoryVeriT
					.makeMacroTerm(
							SMTMacroFactoryV2_0.getMacroSymbol(operator, sig),
							children);
			for (int i = 2; i < expressions.length; i++) {
				macroTerm = SMTFactoryVeriT.makeMacroTerm(
						SMTMacroFactoryV2_0.getMacroSymbol(operator, sig),
						macroTerm, smtTerm(expressions[i]));
			}

		}
		return macroTerm;
	}

	/**
	 * This method translates an Event-B bool expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		final SMTFormula child = smtFormula(expression.getPredicate());
		smtNode = makeITE(child,
				sf.makeTrueConstant(signature.getLogic().getTrueConstant()),
				sf.makeFalseConstant(signature.getLogic().getFalseConstant()));
	}

	/**
	 * This method translates an Event-B bool quantified expression into an
	 * Extended SMT node.
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalTagException(expression.getTag());
	}

	/**
	 * This method translates an Event-B bool setextension expression into an
	 * Extended SMT node.
	 * 
	 * It is translated in the following way:
	 * 
	 * <ul>
	 * <li>
	 * for a set defined as: {} (it contains no element), the set extension is
	 * translated to the macro <strong>empty</strong>.</li>
	 * 
	 * <li>for a set defined as: {a1,a2,...,an}, it is translated to:
	 * 
	 * (enum (lambda (?elem Z) . (or (= ?elem a1) (= ?elem a2) ... (= ?elem an)
	 * )))</li>
	 * 
	 * <li>
	 * for a set defined as: {a1↦b1,a2↦b2,...,an↦bn}, it is translated to:
	 * 
	 * (enum (lambda (?elem (Pair X Y)) . (or (= ?elem (pair a1 b1)) (= ?elem
	 * (pair a2 b2)) ... (= ?elem (pair an bn)) )))</li>
	 * </ul>
	 * 
	 */
	@Override
	public void visitSetExtension(final SetExtension expression) {
		// 
	}

	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		throw new IllegalTagException(expression.getTag());
	}

	@Override
	public void visitFreeIdentifier(final FreeIdentifier identifierExpression) {
		smtNode = sf.makeVeriTConstantTerm(
				varMap.get(identifierExpression.getName()), signature);
	}

	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		//
	}

	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		//
	}

	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"It's not possible to translate extended expression to SMT-LIB yet");

	}

	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		throw new IllegalArgumentException(
				"It's not possible to translate extended predicate to SMT-LIB yet");

	}

	/**
	 * 
	 * @return The translator signature.
	 */
	public SMTSignature getSignature() {
		return signature;
	}
}
