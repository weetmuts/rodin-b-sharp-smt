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

import static fr.systerel.smt.provers.ast.SMTFactory.makeEqual;
import static fr.systerel.smt.provers.ast.SMTFactory.makeMacroTerm;
import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.BCOMP;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.BINTER;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.BUNION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.CARD;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.CARTESIAN_PRODUCT;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.DOM;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.DOMAIN_RESTRICTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.DOMAIN_SUBSTRACTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.EMPTY;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.FCOMP;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.FINITE;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.ID;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.IN;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.INTEGER;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.INV;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.ISMAX;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.ISMIN;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.MAPSTO;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.NAT;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.NAT1;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.OVR;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.PARTIAL_FUNCTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.PARTIAL_INJECTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.PARTIAL_SURJECTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RANGE;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RANGE_INTEGER;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RANGE_RESTRICTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RANGE_SUBSTRACTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RELATION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.RELATIONAL_IMAGE;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.SETMINUS;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.SUBSET;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.SUBSETEQ;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.SURJECTIVE_RELATION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_BIJECTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_FUNCTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_INJECTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_RELATION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_SURJECTION;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.TOTAL_SURJECTIVE_RELATION;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.addPredefinedMacroInSignature;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.getMacroSymbol;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.makeEnumMacro;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.makeMacroSymbol;
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.makeSetComprehensionMacro;

import java.math.BigInteger;
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
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
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
import org.eventb.core.ast.expanders.Expanders;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFactoryVeriT;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTLIBUnderlyingLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTNumeral;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignatureVerit;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.ast.SMTVarSymbol;
import fr.systerel.smt.provers.ast.SMTVeritCardFormula;
import fr.systerel.smt.provers.ast.SMTVeritFiniteFormula;
import fr.systerel.smt.provers.ast.VeritPredefinedTheory;
import fr.systerel.smt.provers.ast.macros.SMTEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.macros.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTSetComprehensionMacro;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * 
 */
public class SMTThroughVeriT extends TranslatorV1_2 {

	private static final SMTTerm[] EMPTY_TERM = {};

	/**
	 * An instance of <code>SMTThroughVeriT</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	protected SMTSignatureVerit signature;

	/**
	 * This boolean is used to check if it's necessary to add the polymorphic
	 * sort Pair and the polymorphic function pair in the signature.
	 */
	private boolean insertPairDecl = false;

	final private SMTFactoryVeriT sf;

	public SMTThroughVeriT(final String solver) {
		super(solver);
		this.sf = SMTFactoryVeriT.getInstance();
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
	 * @throws TranslationException
	 */
	public static SMTBenchmark translateToSmtLibBenchmark(
			final String lemmaName, final List<Predicate> hypotheses,
			final Predicate goal, final String solver)
			throws TranslationException {
		final SMTBenchmark smtB = new SMTThroughVeriT(solver).translate(
				lemmaName, hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			final Predicate predicate, final String solver) {
		final SMTThroughVeriT translator = new SMTThroughVeriT(solver);
		translator.translateSignature(logic, new ArrayList<Predicate>(0),
				predicate);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/**
	 * Determine the logic. In the veriT approach for the translation, it is
	 * returned the solver's own logic.
	 */
	@Override
	protected SMTLogic determineLogic(final List<Predicate> hypotheses,
			final Predicate goal) {
		return SMTLogic.VeriTSMTLIBUnderlyingLogic.getInstance();
	}

	/**
	 * FIXME: Remake this commmentary
	 * 
	 * 1: if the type is a CartesianProduct Type, the same CartesianProduct
	 * translating rules are applied again on it. 2: if the type is a BaseType,
	 * throws an {@link IllegalArgumentException}. Default: It translates and
	 * return the type.
	 * 
	 * @param type
	 *            The type of the variable
	 * @return the translated {@link SMTSortSymbol} of one of the types of a
	 *         CartesianProduct Type.
	 */
	private SMTSortSymbol parseOneOfPairTypes(final Type type,
			final Type parentType) {
		if (type.getBaseType() != null || type.getSource() != null) {
			throw new IllegalArgumentException(", Type "
					+ parentType.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			SMTSortSymbol sortSymbol = typeMap.get(type);
			if (sortSymbol == null) {
				sortSymbol = SMTFactoryVeriT.makeVeriTSortSymbol(
						type.toString(), signature);
				signature.addSort(sortSymbol);
				typeMap.put(type, sortSymbol);
			}
			return sortSymbol;
		}
	}

	/**
	 * This method translates a ProductType. It translates both the source and
	 * the target types.
	 * 
	 * @return The translated sort symbol from productType.
	 */
	private SMTSortSymbol parsePairTypes(final Type parentType) {
		final SMTSortSymbol sourceSymbol = parseOneOfPairTypes(
				parentType.getSource(), parentType);
		final SMTSortSymbol targetSymbol = parseOneOfPairTypes(
				parentType.getTarget(), parentType);
		return SMTFactoryVeriT.makePairSortSymbol(sourceSymbol, targetSymbol);
	}

	@Override
	public void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		signature = new SMTSignatureVerit(logic);
		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);
		translateSignature(typeEnvironment);

		final List<Type> biTypes = getBoundIDentDeclTypes(hypotheses, goal);
		final Iterator<Type> bIterator = biTypes.iterator();

		extractTypeFromBoundIdentDecl(bIterator);
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
		// Checking if it's necessary to add the sort and function pair and if
		// it
		// was not already inserted.
		if (insertPairDecl) {
			addPairSortAndFunctions();
		}
	}

	/**
	 * Translate variables which type is product type.
	 * 
	 * @param varName
	 *            The name of the variable
	 */
	private void translatePredSymbol(final String varName,
			final String freshVarName, final SMTSortSymbol sort) {
		final SMTPredicateSymbol predSymbol = new SMTPredicateSymbol(
				freshVarName, false, sort);
		signature.addPred(predSymbol);
		varMap.put(varName, predSymbol);
		insertPairDecl = true;
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
		signature.addConstant(smtConstant);
		varMap.put(varName, smtConstant);
	}

	/**
	 * This method translates the signature.
	 * 
	 * @param typeEnvironment
	 *            The Event-B Type Environment for the translation.
	 */
	public void translateSignature(final ITypeEnvironment typeEnvironment) {
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final Type varType = iter.getType();
			final SMTSortSymbol sort = translateTypeName(varType);
			final String varName = iter.getName();
			final String freshVarName = signature.freshCstName(varName);
			if (varType.getSource() != null || varType.getBaseType() != null) {
				translatePredSymbol(varName, freshVarName, sort);
			} else {
				translateFunSymbol(varName, sort);
			}
		}
		if (insertPairDecl) {
			addPairSortAndFunctions();
		}

	}

	/**
	 * This method adds the sort (Pair 's 't) and the fun (pair 's 't (Pair 's
	 * 't)) to the signature
	 */
	public void addPairSortAndFunctions() {
		signature.addSort(SMTMacroFactory.PAIR_SORT);
		signature.addConstant(SMTMacroFactory.PAIR_SYMBOL);
	}

	@Override
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal)
			throws TranslationException {

		final SMTLogic logic = determineLogic(hypotheses, goal);

		/**
		 * SMT translation
		 */
		// translates the signature
		translateSignature(logic, hypotheses, goal);

		// translates each hypothesis
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		for (final Predicate hypothesis : hypotheses) {
			clearFormula();
			final SMTFormula translatedFormula = translate(hypothesis);
			translatedAssumptions.addAll(signature.getAdditionalAssumptions());
			translatedAssumptions.add(translatedFormula);
		}

		// translates the goal
		clearFormula();
		final SMTFormula smtFormula = translate(goal);
		translatedAssumptions.addAll(signature.getAdditionalAssumptions());
		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
	}

	/**
	 * This method translates one predicate.
	 * 
	 * @param predicate
	 *            The Rodin predicate to be translated.
	 * @return the translated SMT Formula from the predicate
	 */
	private SMTFormula translate(final Predicate predicate) {
		predicate.accept(this);
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
				} else {
					sortSymbol = SMTFactoryVeriT.makeVeriTSortSymbol(
							baseType.toString(), signature);
					signature.addSort(sortSymbol);
					typeMap.put(baseType, sortSymbol);
				}
			}
		} else {
			sortSymbol = typeMap.get(type);
			if (sortSymbol == null) {
				if (type instanceof ProductType) {
					sortSymbol = translateProductType((ProductType) type);
				} else {
					sortSymbol = SMTFactoryVeriT.makeVeriTSortSymbol(
							type.toString(), signature);
					signature.addSort(sortSymbol);
					typeMap.put(type, sortSymbol);
				}
			}
		}
		return sortSymbol;
	}

	/**
	 * @param type
	 */
	private void checkIfIsSetOfSet(final Type type, final Type parentType) {
		if (type.getSource() != null || type.getBaseType() != null) {
			throw new IllegalArgumentException("Type " + parentType.toString()
					+ ": Sets of sets are not supported yet");
		}
	}

	private SMTSortSymbol translateProductType(final ProductType type) {
		checkIfIsSetOfSet(type);
		final SMTSortSymbol left = translateTypeName(type.getLeft());
		final SMTSortSymbol right = translateTypeName(type.getRight());
		return SMTFactoryVeriT.makePairSortSymbol(left, right);
	}

	/**
	 * @param type
	 */
	private void checkIfIsSetOfSet(final ProductType type) {
		checkIfIsSetOfSet(type.getLeft(), type);
		if (type.getLeft() instanceof ProductType) {
			throw new IllegalArgumentException("Type " + type.toString()
					+ ": Sets of sets are not supported yet");
		}
		checkIfIsSetOfSet(type.getRight(), type);
		if (type.getRight() instanceof ProductType) {
			throw new IllegalArgumentException("Type " + type.toString()
					+ ": Sets of sets are not supported yet");
		}
	}

	/**
	 * Clears the formula and the additional assumptions.
	 */
	@Override
	protected void clearFormula() {
		signature.getAdditionalAssumptions().clear();
		smtNode = null;
	}

	private void translateQuantifiedExpression(final String macroName,
			final SMTFormula formulaChild, final SMTTerm[] expressionTerm,
			final SMTSortSymbol expressionSymbol, final SMTTerm... termChildren) {
		// obtaining fresh name for the variables
		final String lambdaName = signature.freshCstName(SMTMacroSymbol.ELEM);

		final SMTVarSymbol lambdaVar = new SMTVarSymbol(lambdaName,
				expressionSymbol, false);

		// Creating the macro
		final SMTSetComprehensionMacro macro = makeSetComprehensionMacro(
				macroName, termChildren, lambdaVar, formulaChild,
				expressionTerm[0], signature);

		signature.addMacro(macro);
		final SMTMacroSymbol macroSymbol = makeMacroSymbol(macroName,
				VeritPredefinedTheory.getInstance().getBooleanSort());
		smtNode = makeMacroTerm(macroSymbol, EMPTY_TERM);
	}

	/**
	 * This method translates an Event-B atomic expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {

		final String x;
		final SMTTerm xFun;
		final SMTSortSymbol xSort;
		final BigInteger b;
		final SMTNumeral plusOrMinusUmNumeral;
		final SMTTerm plusOrMinusTerm;
		final SMTTerm[] mapstoTerm;
		final SMTMacroSymbol intS;
		final SMTTerm intT;
		final SMTFormula inFormula;
		final String macroName;

		switch (expression.getTag()) {
		case Formula.KPRED:
			// Making x
			x = signature.freshCstName("x");
			xSort = SMTFactoryVeriT.makePairSortSymbol(Ints.getInt(),
					Ints.getInt());

			new SMTFunctionSymbol(x, xSort, !ASSOCIATIVE, !PREDEFINED);
			xFun = sf.makeVar(x, Ints.getInt());

			// Making 1
			b = BigInteger.ONE;
			plusOrMinusUmNumeral = SMTFactory.makeNumeral(b);

			// Making x + 1
			plusOrMinusTerm = sf.makeMinus((SMTFunctionSymbol) signature
					.getLogic().getOperator(SMTOperator.MINUS), signature,
					xFun, plusOrMinusUmNumeral);

			// Making x |-> x + 1
			addPredefinedMacroInSignature(MAPSTO, signature);

			mapstoTerm = new SMTTerm[1];
			mapstoTerm[0] = SMTFactory.makeMacroTerm(getMacroSymbol(MAPSTO),
					xFun, plusOrMinusTerm);

			// Making Int
			intS = SMTMacroFactory.getMacroSymbol(INTEGER);
			intT = makeMacroTerm(intS, EMPTY_TERM);
			addPredefinedMacroInSignature(INTEGER, signature);

			// making (x in Int)
			addPredefinedMacroInSignature(IN, signature);
			inFormula = sf.makeVeriTMacroAtom(getMacroSymbol(IN), xFun, intT);

			macroName = signature.freshCstName(SMTMacroSymbol.PRED);

			translateQuantifiedExpression(macroName, inFormula, mapstoTerm,
					xSort, xFun);
			break;
		case Formula.KSUCC:

			// Making x
			x = signature.freshCstName("x");
			xSort = SMTFactoryVeriT.makePairSortSymbol(Ints.getInt(),
					Ints.getInt());

			new SMTFunctionSymbol(x, xSort, !ASSOCIATIVE, !PREDEFINED);
			xFun = sf.makeVar(x, Ints.getInt());

			// Making 1
			b = BigInteger.ONE;
			plusOrMinusUmNumeral = SMTFactory.makeNumeral(b);

			// Making x + 1
			plusOrMinusTerm = sf.makePlus((SMTFunctionSymbol) signature
					.getLogic().getOperator(SMTOperator.PLUS), signature, xFun,
					plusOrMinusUmNumeral);

			// Making x |-> x + 1
			addPredefinedMacroInSignature(MAPSTO, signature);

			mapstoTerm = new SMTTerm[1];
			mapstoTerm[0] = SMTFactory.makeMacroTerm(getMacroSymbol(MAPSTO),
					xFun, plusOrMinusTerm);

			// Making Int
			intS = SMTMacroFactory.getMacroSymbol(INTEGER);
			intT = makeMacroTerm(intS, EMPTY_TERM);
			addPredefinedMacroInSignature(INTEGER, signature);

			// making (x in Int)
			addPredefinedMacroInSignature(IN, signature);

			inFormula = sf.makeVeriTMacroAtom(getMacroSymbol(IN), xFun, intT);

			macroName = signature.freshCstName(SMTMacroSymbol.SUCC);

			translateQuantifiedExpression(macroName, inFormula, mapstoTerm,
					xSort, xFun);
			break;
		case Formula.INTEGER:
			addPredefinedMacroInSignature(INTEGER, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(INTEGER),
					EMPTY_TERM);
			break;
		case Formula.NATURAL:
			addPredefinedMacroInSignature(NAT, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(NAT), EMPTY_TERM);
			break;
		case Formula.NATURAL1:
			addPredefinedMacroInSignature(NAT1, signature);

			smtNode = SMTFactory
					.makeMacroTerm(getMacroSymbol(NAT1), EMPTY_TERM);
			break;
		case Formula.BOOL:
			throw new IllegalArgumentException(
					"Sort BOOL is not implemented yet");
		case Formula.EMPTYSET:
			if (expression.getType().getSource() instanceof ProductType
					|| expression.getType().getTarget() instanceof ProductType) {
				throw new IllegalArgumentException("Type: "
						+ expression.getType().toString()
						+ ": power set of power set is not supported yet");
			} else {
				addPredefinedMacroInSignature(EMPTY, signature);

				smtNode = SMTFactory.makeMacroTerm(
						SMTMacroFactory.getMacroSymbol(EMPTY), EMPTY_TERM);
			}
			break;
		case Formula.KPRJ1_GEN:
			/*
			 * TODO Check rule and implement it
			 */
			throw new IllegalArgumentException(
					"prj1 (KPRJ1_GEN) is not implemented yet");
		case Formula.KPRJ2_GEN:
			/*
			 * TODO Check rule and implement it
			 */
			throw new IllegalArgumentException(
					"prj2 (KPRJ2_GEN) is not implemented yet");
		case Formula.KID_GEN:
			addPredefinedMacroInSignature(ID, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(ID), EMPTY_TERM);
			break;
		case Formula.TRUE:
			// FIXME Use boolean value when BOOL_SORT theory implemented
			throw new IllegalArgumentException(
					"TRUE value (TRUE)is not implemented yet");
		case Formula.FALSE:
			// FIXME Use boolean value when BOOL_SORT theory implemented
			throw new IllegalArgumentException(
					"false value (FALSE) is not implemented yet");
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
		final SMTTerm[] children = smtTerms(expression.getLeft(),
				expression.getRight());
		switch (expression.getTag()) {
		case Formula.MINUS:
			smtNode = sf.makeMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MINUS), signature, children);
			break;
		case Formula.DIV:
			smtNode = sf.makeDiv((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.DIV), signature, children);
			break;
		case Formula.MOD:
			/**
			 * It's added the function ((mod Int Int Int)) in the signature
			 */
			final SMTFunctionSymbol VERIT_MOD = new SMTFunctionSymbol(
					SMTMacroSymbol.MOD, Ints.getInt(), false, false,
					Ints.getIntIntTab());

			signature.addConstant(VERIT_MOD);
			smtNode = sf.makeVeriTTermOperatorApplication(VERIT_MOD, children,
					signature);
			break;
		case Formula.EXPN:
			/**
			 * Translation of exponentiation:
			 * 
			 * forall x n . n > 0 => exp(x, n) = x * exp(x, n-1)
			 * 
			 * forall x . x ≠ 0 => exp(x, 0) = 1
			 * 
			 * forall n . n > 0 => exp(0, n) = 0
			 */

			signature.addConstant((SMTFunctionSymbol) VeritPredefinedTheory
					.getInstance().getExpn());

			signature.addAdditionalAssumption(makeZeroCaseOfExpnAxioms());
			signature.addAdditionalAssumption(makeOneCaseOfExpnAxioms());
			signature.addAdditionalAssumption(makeRecursionCaseOfExpnAxioms());

			smtNode = sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.EXPN), signature, children);
			break;
		case Formula.UPTO:
			addPredefinedMacroInSignature(RANGE_INTEGER, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(RANGE_INTEGER),
					children);
			break;
		case Formula.RANSUB:
			addPredefinedMacroInSignature(RANGE_SUBSTRACTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(RANGE_SUBSTRACTION), children);
			break;

		case Formula.RANRES:
			addPredefinedMacroInSignature(RANGE_RESTRICTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(RANGE_RESTRICTION), children);
			break;

		case Formula.REL:
			addPredefinedMacroInSignature(RELATION, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(RELATION),
					children);
			break;

		case Formula.TREL:
			addPredefinedMacroInSignature(TOTAL_RELATION, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(TOTAL_RELATION),
					children);
			break;

		case Formula.SREL:
			addPredefinedMacroInSignature(SURJECTIVE_RELATION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(SURJECTIVE_RELATION), children);
			break;

		case Formula.STREL:
			addPredefinedMacroInSignature(TOTAL_SURJECTIVE_RELATION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(TOTAL_SURJECTIVE_RELATION), children);
			break;

		case Formula.PFUN:
			addPredefinedMacroInSignature(PARTIAL_FUNCTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(PARTIAL_FUNCTION), children);
			break;

		case Formula.TFUN:
			addPredefinedMacroInSignature(TOTAL_FUNCTION, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(TOTAL_FUNCTION),
					children);
			break;
		case Formula.PINJ:
			addPredefinedMacroInSignature(PARTIAL_INJECTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(PARTIAL_INJECTION), children);
			break;

		case Formula.TINJ:
			addPredefinedMacroInSignature(TOTAL_INJECTION, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(TOTAL_INJECTION),
					children);
			break;

		case Formula.PSUR:
			addPredefinedMacroInSignature(PARTIAL_SURJECTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(PARTIAL_SURJECTION), children);
			break;

		case Formula.TSUR:
			addPredefinedMacroInSignature(TOTAL_SURJECTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(TOTAL_SURJECTION), children);
			break;

		case Formula.TBIJ:
			addPredefinedMacroInSignature(TOTAL_BIJECTION, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(TOTAL_BIJECTION),
					children);
			break;

		case Formula.SETMINUS:
			addPredefinedMacroInSignature(SETMINUS, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(SETMINUS),
					children);
			break;

		case Formula.CPROD:
			addPredefinedMacroInSignature(CARTESIAN_PRODUCT, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(CARTESIAN_PRODUCT), children);
			break;

		case Formula.DPROD:
			// FIXME There is no implementation for DPROD in Verit SMT-LIB
			throw new IllegalArgumentException(
					"Operator direct product (DPROD) is not implemented yet");

		case Formula.PPROD:
			// FIXME There is no implementation for PPROD in Verit SMT-LIB
			throw new IllegalArgumentException(
					"Operator parallel product (PPROD) is not implemented yet");

		case Formula.DOMRES:
			addPredefinedMacroInSignature(DOMAIN_RESTRICTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(DOMAIN_RESTRICTION), children);
			break;

		case Formula.DOMSUB:
			addPredefinedMacroInSignature(DOMAIN_SUBSTRACTION, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(DOMAIN_SUBSTRACTION), children);
			break;

		case Formula.FUNIMAGE:
			// FIXME There is no implementation for FUNIMAGE in Verit SMT-LIB
			throw new IllegalArgumentException(
					"function application (FUNIMAGE) is not implemented yet");

		case Formula.RELIMAGE:
			addPredefinedMacroInSignature(RELATIONAL_IMAGE, signature);

			smtNode = SMTFactory.makeMacroTerm(
					getMacroSymbol(RELATIONAL_IMAGE), children);
			break;

		case Formula.MAPSTO:
			addPredefinedMacroInSignature(MAPSTO, signature);

			smtNode = SMTFactory
					.makeMacroTerm(getMacroSymbol(MAPSTO), children);
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * forall x . x ≠ 0 => exp(x, 0) = 1
	 * 
	 * @return the one case base formula
	 */
	private SMTFormula makeOneCaseOfExpnAxioms() {
		// TODO Refactor
		// addPredefinedMacroInSignature(NOT_EQUAL, signature);

		// making the boundIdentifier
		final String symbolName = signature.freshCstName("x");
		final SMTVarSymbol varSymbol = new SMTVarSymbol(symbolName,
				VeritPredefinedTheory.getInt(), !PREDEFINED);
		final SMTVar var = new SMTVar(varSymbol);

		// making the moreOrEqual terms
		final SMTTerm numeralZero = SMTFactory.makeNumeral(BigInteger.ZERO);
		final SMTTerm numeralOne = SMTFactory.makeNumeral(BigInteger.ONE);

		// making the moreOrEqual formula
		final SMTFormula impliesFstArgument = SMTFactory.makeNotEqual(var,
				numeralZero);

		// making the expn fun application
		final SMTTerm[] equalTerms = {
				sf.makeFunApplication((SMTFunctionSymbol) signature.getLogic()
						.getOperator(SMTOperator.EXPN), signature, var,
						numeralZero), numeralOne };

		// making the Equal formula
		final SMTFormula impliesSndArgument = makeEqual(equalTerms);

		// making the implies formula
		return SMTFactory.makeForAll(
				SMTFactory.makeImplies(impliesFstArgument, impliesSndArgument),
				var);
	}

	/**
	 * forall x n . n > 0 => exp(x, n) = x * exp(x, n-1)
	 * 
	 * @return the recursion case base formula
	 */
	private SMTFormula makeRecursionCaseOfExpnAxioms() {
		// TODO: Refactor
		final String n = signature.freshCstName("n");
		final String x = signature.freshCstName("x");

		final SMTTerm nVar = sf.makeVar(n, VeritPredefinedTheory.getInt());
		final SMTTerm xVar = sf.makeVar(x, VeritPredefinedTheory.getInt());

		final SMTFormula greaterThan = makeGreaterThan(nVar, BigInteger.ZERO);
		final SMTTerm expnAppl1 = makeExpnAppl(xVar, nVar);
		final SMTTerm nMinusOne = makeMinus(nVar, BigInteger.ONE);
		final SMTTerm expnAppl2 = makeExpnAppl(xVar, nMinusOne);
		final SMTTerm multTerm = makeMul(xVar, expnAppl2);
		final SMTFormula equalFormula = makeEqualFormula(expnAppl1, multTerm);
		final SMTFormula impliesFormula = makeImpliesFormula(greaterThan,
				equalFormula);

		return SMTFactory.makeForAll(impliesFormula, nVar, xVar);
	}

	private SMTFormula makeImpliesFormula(final SMTFormula greaterThan,
			final SMTFormula equalFormula) {
		return SMTFactory.makeImplies(greaterThan, equalFormula);
	}

	private SMTFormula makeEqualFormula(final SMTTerm expnAppl1,
			final SMTTerm multTerm) {
		return makeEqual(expnAppl1, multTerm);
	}

	private SMTTerm makeMul(final SMTTerm xVar, final SMTTerm expnAppl2) {
		return sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
				.getOperator(SMTOperator.EXPN), signature, xVar, expnAppl2);
	}

	private SMTTerm makeExpnAppl(final SMTTerm xVar, final SMTTerm nVar) {
		return sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
				.getOperator(SMTOperator.EXPN), signature, xVar, nVar);
	}

	private SMTTerm makeMinus(final SMTTerm nVar, final BigInteger numeral) {
		return sf.makeMinus((SMTFunctionSymbol) signature.getLogic()
				.getOperator(SMTOperator.MINUS), signature, nVar, SMTFactory
				.makeNumeral(numeral));
	}

	private SMTFormula makeGreaterThan(final SMTTerm nVar,
			final BigInteger numeral) {
		return sf.makeGreaterThan((SMTPredicateSymbol) signature.getLogic()
				.getOperator(SMTOperator.GT), signature, nVar, SMTFactory
				.makeNumeral(numeral));
	}

	/**
	 * forall n . n > 0 => exp(0, n) = 0
	 * 
	 * @return the zero case base formula
	 */
	private SMTFormula makeZeroCaseOfExpnAxioms() {
		// TODO: Refactor
		// making the boundIdentifier
		final String symbolName = signature.freshCstName("n");
		final SMTVarSymbol varSymbol = new SMTVarSymbol(symbolName,
				VeritPredefinedTheory.getInt(), !PREDEFINED);
		final SMTVar var = new SMTVar(varSymbol);

		// making the moreOrEqual terms
		final SMTTerm numeralZero = SMTFactory.makeNumeral(BigInteger.ZERO);

		// making the moreOrEqual formula
		final SMTFormula impliesFstArgument = sf.makeGreaterThan(
				(SMTPredicateSymbol) signature.getLogic().getOperator(
						SMTOperator.GT), signature, var, numeralZero);

		// making the expn fun application
		final SMTTerm[] equalTerms = {
				sf.makeFunApplication((SMTFunctionSymbol) signature.getLogic()
						.getOperator(SMTOperator.EXPN), signature, numeralZero,
						var), numeralZero };

		// making the Equal formula
		final SMTFormula impliesSndArgument = makeEqual(equalTerms);

		// making the implies formula
		return SMTFactory.makeForAll(
				SMTFactory.makeImplies(impliesFstArgument, impliesSndArgument),
				var);
	}

	/**
	 * This method translates an Event-B bound identifier declaration into an
	 * Extended SMT node.
	 */
	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String varName = boundIdentDecl.getName();
		final SMTVar smtVar;

		final Set<String> symbolNames = new HashSet<String>();
		for (final SMTSymbol function : varMap.values()) {
			symbolNames.add(function.getName());
		}
		for (final SMTVar var : qVarMap.values()) {
			symbolNames.add(var.getSymbol().getName());
		}
		final String smtVarName = signature.freshSymbolName(symbolNames,
				varName);
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
	 * This method translates an Event-B quantified predicate into an Extended
	 * SMT node
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
	 * This method translates an Event-B relational predicate into an Extended
	 * SMT node.
	 */
	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.EQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			if (predicate.getLeft().getType() instanceof BooleanType) {
				final SMTFormula[] childrenFormulas = sf
						.convertVeritTermsIntoFormulas(children);
				smtNode = SMTFactory.makeIff(childrenFormulas);
			} else {
				smtNode = makeEqual(children);
			}
			break;
		}
		case Formula.NOTEQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			if (predicate.getLeft().getType() instanceof BooleanType) {
				final SMTFormula[] childrenFormulas = sf
						.convertVeritTermsIntoFormulas(children);
				smtNode = sf.makeNotIff(childrenFormulas);
			} else {
				smtNode = SMTFactory.makeNotEqual(children);
			}
			break;
		}
		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessThan((SMTPredicateSymbol) signature.getLogic()
					.getOperator(SMTOperator.LT), children, signature);
			break;
		}
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.LE), children,
					signature);
			break;
		}
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterThan((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GT), signature,
					children);
			break;
		}
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GE), children,
					signature);
			break;
		}
		case Formula.IN: {
			addPredefinedMacroInSignature(IN, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeVeriTMacroAtom(getMacroSymbol(IN), children);
			break;
		}

		case Formula.SUBSET: {
			addPredefinedMacroInSignature(SUBSET, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactoryVeriT.makeMacroAtom(getMacroSymbol(SUBSET),
					children);
			break;
		}
		case Formula.SUBSETEQ: {
			addPredefinedMacroInSignature(SUBSETEQ, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactoryVeriT.makeMacroAtom(getMacroSymbol(SUBSETEQ),
					children);
			break;
		}
		case Formula.NOTSUBSET: {
			addPredefinedMacroInSignature(SUBSET, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactoryVeriT.makeMacroAtom(getMacroSymbol(SUBSET),
					children);
			smtNode = SMTFactory.makeNot((SMTFormula) smtNode);
			break;
		}
		case Formula.NOTSUBSETEQ: {
			addPredefinedMacroInSignature(SUBSETEQ, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactoryVeriT.makeMacroAtom(getMacroSymbol(SUBSETEQ),
					children);
			smtNode = SMTFactory.makeNot((SMTFormula) smtNode);
			break;
		}
		default: {
			throw new IllegalTagException(predicate.getTag());
		}
		}
	}

	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"BecomesEqualTo assignment is not implemented yet");
	}

	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"BecomesMemberOf assignment is not implemented yet");
	}

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
		SMTTerm macroTerm;
		switch (tag) {
		case Formula.PLUS:
			children = smtTerms(expressions);
			smtNode = sf.makePlus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.PLUS), signature, children);
			break;
		case Formula.MUL:
			children = smtTerms(expressions);
			smtNode = sf.makeMul((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MUL), signature, children);
			break;
		case Formula.BUNION:
			addPredefinedMacroInSignature(BUNION, signature);
			if (expressions.length == 2) {
				children = smtTerms(expression.getChildren());
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BUNION),
						children);
			} else {
				children = smtTerms(expressions[0], expressions[1]);
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BUNION),
						children);
				for (int i = 2; i < expressions.length; i++) {
					macroTerm = SMTFactory.makeMacroTerm(
							getMacroSymbol(BUNION), macroTerm,
							smtTerm(expressions[i]));
				}

			}
			smtNode = macroTerm;
			break;
		case Formula.BINTER:
			addPredefinedMacroInSignature(BINTER, signature);
			if (expressions.length == 2) {
				children = smtTerms(expression.getChildren());
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BINTER),
						children);
			} else {
				children = smtTerms(expressions[0], expressions[1]);
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BINTER),
						children);
				for (int i = 2; i < expressions.length; i++) {
					macroTerm = SMTFactory.makeMacroTerm(
							getMacroSymbol(BINTER), macroTerm,
							smtTerm(expressions[i]));
				}

			}
			smtNode = macroTerm;
			break;
		case Formula.FCOMP:
			addPredefinedMacroInSignature(FCOMP, signature);
			if (expressions.length == 2) {
				children = smtTerms(expression.getChildren());
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(FCOMP),
						children);
			} else {
				children = smtTerms(expressions[0], expressions[1]);
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(FCOMP),
						children);
				for (int i = 2; i < expressions.length; i++) {
					macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(FCOMP),
							macroTerm, smtTerm(expressions[i]));
				}

			}
			smtNode = macroTerm;
			break;
		case Formula.BCOMP:
			addPredefinedMacroInSignature(BCOMP, signature);
			if (expressions.length == 2) {
				children = smtTerms(expression.getChildren());
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BCOMP),
						children);
			} else {
				children = smtTerms(expressions[0], expressions[1]);
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BCOMP),
						children);
				for (int i = 2; i < expressions.length; i++) {
					macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BCOMP),
							macroTerm, smtTerm(expressions[i]));
				}

			}
			smtNode = macroTerm;
			break;
		case Formula.OVR:
			addPredefinedMacroInSignature(OVR, signature);
			if (expressions.length == 2) {
				children = smtTerms(expression.getChildren());
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(OVR),
						children);
			} else {
				children = smtTerms(expressions[0], expressions[1]);
				macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(OVR),
						children);
				for (int i = 2; i < expressions.length; i++) {
					macroTerm = SMTFactory.makeMacroTerm(getMacroSymbol(OVR),
							macroTerm, smtTerm(expressions[i]));
				}

			}
			smtNode = macroTerm;
			break;
		default:
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B bool expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		// TODO Implement rule 21
		throw new IllegalArgumentException(
				"'Translation of Boolean Expression is not implemented yet");
	}

	/**
	 * This method translates an Event-B bool quantified expression into an
	 * Extended SMT node.
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		// FIXME Refactor this method
		switch (expression.getTag()) {
		case Formula.CSET:

			// Translating the children
			final SMTTerm[] termChildren = smtTerms(expression
					.getBoundIdentDecls());
			final SMTFormula formulaChild = smtFormula(expression
					.getPredicate());
			final SMTTerm[] expressionTerm = smtTerms(expression
					.getExpression());

			// obtaining the expression type
			SMTSortSymbol expressionSymbol = typeMap.get(expression.getType());
			if (expressionSymbol == null) {
				expressionSymbol = translateTypeName(expression.getType());
			}
			final String macroName = signature
					.freshCstName(SMTMacroSymbol.CSET);
			translateQuantifiedExpression(macroName, formulaChild,
					expressionTerm, expressionSymbol, termChildren);
			break;
		case Formula.QUNION:
			throw new IllegalArgumentException(
					"It's not possible to translated quantified union (QUNION) to SMT-LIB yet");
		case Formula.QINTER:
			throw new IllegalArgumentException(
					"It's not possible yet to translate quantified intersection (QINTER) to SMT-LIB yet");
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B bool setextension expression into an
	 * Extended SMT node.
	 */
	@Override
	public void visitSetExtension(final SetExtension expression) {
		// FIXME: Refactor this method
		SMTTerm[] children = {};
		if (expression.getChildCount() == 0) {
			addPredefinedMacroInSignature(EMPTY, signature);
			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(EMPTY));
		} else {
			children = smtTerms(expression.getMembers());
			final String macroName = signature
					.freshCstName(SMTMacroSymbol.ENUM);
			final String varName = signature.freshCstName(SMTMacroSymbol.ELEM);

			final Type setExtensionType = expression.getMembers()[0].getType();
			if (setExtensionType instanceof ProductType) {

				final SMTSortSymbol pairSort = parsePairTypes(expression
						.getType());

				final SMTVarSymbol var = new SMTVarSymbol(varName, pairSort,
						false);

				final SMTPairEnumMacro macro = SMTMacroFactory
						.makePairEnumerationMacro(macroName, var, children,
								signature);
				signature.addMacro(macro);
			} else {
				SMTSortSymbol sortSymbol = typeMap.get(expression.getType());
				if (sortSymbol == null) {
					sortSymbol = translateTypeName(expression.getType());
				}
				final SMTVarSymbol var = new SMTVarSymbol(varName, sortSymbol,
						false);

				final SMTEnumMacro macro = makeEnumMacro(macroName, var,
						children);
				signature.addMacro(macro);
			}
			final SMTMacroSymbol symbol = makeMacroSymbol(macroName,
					VeritPredefinedTheory.getInstance().getBooleanSort());
			smtNode = SMTFactory.makeMacroTerm(symbol, EMPTY_TERM);
		}
	}

	/**
	 * This method translates an Event-B unary expression into an SMT node.
	 */
	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		// FIXME Refactor this method
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.KCARD: {

			// Creating card macro and to the signature
			addPredefinedMacroInSignature(CARD, signature);

			// Creating the name for the 'f' and 'k' variables in SMT-LIB (rule
			// 25)
			final String kVarName = signature.freshCstName("card_k");
			final String fVarName = signature.freshCstName("card_f");

			final SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(
					kVarName, Ints.getInt(), false, false);

			final Type type = expression.getChild().getType();
			SMTSortSymbol expressionSort = typeMap.get(type);
			if (expressionSort == null) {
				expressionSort = translateTypeName(type);
			}
			final SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(
					fVarName, Ints.getInt(), false, false, expressionSort);

			signature.addConstant(kVarSymbol);
			signature.addConstant(fVarSymbol);

			// Creating the macro operator 'finite'
			final SMTMacroSymbol cardSymbol = getMacroSymbol(CARD);

			// Creating the new assumption (card p t k f) and saving it.
			final SMTFormula cardFormula = new SMTVeritCardFormula(cardSymbol,
					fVarSymbol, kVarSymbol, children);

			signature.addAdditionalAssumption(cardFormula);

			final SMTTerm kTerm = sf.makeVeriTConstantTerm(kVarSymbol,
					signature);

			smtNode = kTerm;

			break;
		}
		case Formula.POW: {
			// TODO Implement this translation
			throw new IllegalArgumentException(
					"It's not possible yet to translate  PowerSet unary expression (POW) to SMT-LIB yet");
		}
		case Formula.POW1: {
			// TODO Implement this translationTODO
			throw new IllegalArgumentException(
					"It's not possible yet to translate  PowerSet1 unary expression (POW1) to SMT-LIB yet");
		}
		case Formula.KUNION: {
			// TODO Implement this translationTODO
			throw new IllegalArgumentException(
					"It's not possible yet to translate generalized union (KUNION) to SMT-LIB yet");
		}
		case Formula.KINTER: {
			// TODO Implement this translationTODO
			throw new IllegalArgumentException(
					"It's not possible yet to translate generalized inter (KINTER) to SMT-LIB yet");
		}
		case Formula.KDOM:
			addPredefinedMacroInSignature(DOM, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(DOM), children);
			break;
		case Formula.KRAN: {
			addPredefinedMacroInSignature(RANGE, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(RANGE), children);
			break;
		}
		case Formula.KMIN: {
			// TODO Refactor

			// Creating ismin macro and add it to the signature
			addPredefinedMacroInSignature(ISMIN, signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			final String mVarName = signature.freshCstName("ismin_var");

			// Creating the constant 'm'
			final SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(
					mVarName, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismin'
			final SMTMacroSymbol isMinSymbol = getMacroSymbol(ISMIN);

			// Creating the term 'm'
			final SMTTerm mVarTerm = sf.makeFunApplication(mVarSymbol,
					signature);

			// Adding the term 'm' to the other children
			final SMTTerm[] minChildrenTerms = new SMTTerm[children.length + 1];
			for (int i = 0; i < children.length; i++) {
				minChildrenTerms[i + 1] = children[i];
			}
			minChildrenTerms[0] = mVarTerm;

			// Creating the new assumption (ismin m t) and saving it.
			final SMTFormula isMinFormula = SMTFactoryVeriT.makeMacroAtom(
					isMinSymbol, minChildrenTerms);
			signature.addAdditionalAssumption(isMinFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.KMAX: {
			// TODO Refactor

			// Creating ismax macro and adding it to the signature
			addPredefinedMacroInSignature(ISMAX, signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			final String mVarName = signature.freshCstName("ismax_var");

			// Creating the constant 'm'
			final SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(
					mVarName, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismax'
			final SMTMacroSymbol isMaxSymbol = getMacroSymbol(ISMAX);

			// Creating the term 'm'
			final SMTTerm mVarTerm = sf.makeFunApplication(mVarSymbol,
					signature);

			// Adding the term 'm' to the other children
			final SMTTerm[] maxChildrenTerms = new SMTTerm[children.length + 1];
			for (int i = 0; i < children.length; i++) {
				maxChildrenTerms[i + 1] = children[i];
			}
			maxChildrenTerms[0] = mVarTerm;

			// Creating the new assumption (ismax m t) and saving it.
			final SMTFormula isMaxFormula = SMTFactoryVeriT.makeMacroAtom(
					isMaxSymbol, maxChildrenTerms);

			signature.addAdditionalAssumption(isMaxFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.CONVERSE:
			addPredefinedMacroInSignature(INV, signature);

			smtNode = SMTFactory.makeMacroTerm(getMacroSymbol(INV), children);
			break;
		case Formula.UNMINUS:
			smtNode = sf.makeUMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.UMINUS), signature, children);
			break;
		default: {
			throw new IllegalTagException(expression.getTag());
		}
		}
	}

	/**
	 * This method translates an Event-B free identifier into an Extended SMT
	 * node.
	 */
	@Override
	public void visitFreeIdentifier(final FreeIdentifier identifierExpression) {
		smtNode = sf.makeVeriTConstantTerm(
				varMap.get(identifierExpression.getName()), signature);
	}

	/**
	 * This method translates an Event-B literal predicate into an Extended SMT
	 * node.
	 */
	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			smtNode = sf.makePTrue(signature);
			break;
		case Formula.BFALSE:
			smtNode = sf.makePFalse(signature);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		final Expression[] expressions = predicate.getChildren();
		for (int i = 1; i < expressions.length; i++) {
			if (expressions[i].getChildCount() != 1) {
				// Translate the case where the child sets are not
				// singleton
				final Predicate expandedPredicate = Expanders.expandPARTITION(
						predicate, FormulaFactory.getDefault());
				smtNode = smtFormula(expandedPredicate);
				return;
			}
		}
		final SMTTerm e0 = smtTerm(predicate.getChildren()[0]);

		// Translation of special case where all child sets are singleton
		final Set<String> usedNames = new HashSet<String>();
		final List<SMTTerm> newVars = new ArrayList<SMTTerm>();
		for (int i = 1; i < expressions.length; i++) {
			final SMTTerm expTerm = smtTerm(expressions[i]);

			final String x = signature.freshCstName("set", usedNames);
			usedNames.add(x);
			newVars.add(addEqualAssumption(x, expressions[i].getType(), expTerm));
		}
		addDistinctAssumption(newVars);
		setNodeWithUnionAssumption(newVars, e0);
	}

	private void setNodeWithUnionAssumption(final List<SMTTerm> newVars,
			final SMTTerm e0) {
		assert !newVars.isEmpty();
		addPredefinedMacroInSignature(BUNION, signature);

		SMTTerm unionTerm;

		if (newVars.size() == 1) {
			unionTerm = newVars.get(0);
		} else {
			unionTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BUNION),
					newVars.get(0), newVars.get(1));
			for (int i = 2; i < newVars.size(); i++) {
				unionTerm = SMTFactory.makeMacroTerm(getMacroSymbol(BUNION),
						unionTerm, newVars.get(i));
			}
		}
		smtNode = makeEqual(e0, unionTerm);
	}

	private SMTTerm addEqualAssumption(final String x, final Type type,
			final SMTTerm e0) {
		SMTSortSymbol sort = typeMap.get(type);
		if (sort == null) {
			sort = translateTypeName(type);
		}

		final SMTPredicateSymbol symbol = sf.makeVeriTPredSymbol(x, sort);

		signature.addPred(symbol);

		final SMTTerm xTerm = sf.makeVeriTConstantTerm(symbol, signature);

		signature.addAdditionalAssumption(SMTFactory.makeEqual(xTerm, e0));
		return xTerm;
	}

	/**
	 * 
	 * @param newVars
	 */
	private void addDistinctAssumption(final List<SMTTerm> newVars) {
		signature.addAdditionalAssumption(SMTFactoryVeriT.makeDistinct(newVars
				.toArray(new SMTTerm[newVars.size()])));
	}

	/**
	 * This method translates an Event-B simple predicate into an Extended SMT
	 * node.
	 */
	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		final SMTTerm[] children = smtTerms(predicate.getExpression());

		// Creating ismin macro and adding it to the signature
		addPredefinedMacroInSignature(FINITE, signature);

		// Creating the name for the 'p','f' and 'k' variables in SMT-LIB (rule
		// 24)
		final String pVarName = signature.freshCstName("finite_p");
		final String kVarName = signature.freshCstName("finite_k");
		final String fVarName = signature.freshCstName("finite_f");

		// Creating the constant 'p'
		final SMTPredicateSymbol pVarSymbol = new SMTPredicateSymbol(pVarName,
				!PREDEFINED);
		final SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(kVarName,
				Ints.getInt(), false, false);

		final Type type = predicate.getExpression().getType();
		SMTSortSymbol expressionSort = typeMap.get(type);
		if (expressionSort == null) {
			expressionSort = translateTypeName(type);
		}
		final SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(fVarName,
				Ints.getInt(), false, false, expressionSort);

		signature.addPred(pVarSymbol);
		signature.addConstant(kVarSymbol);
		signature.addConstant(fVarSymbol);

		// Creating the macro operator 'finite'
		final SMTMacroSymbol finiteSymbol = getMacroSymbol(FINITE);

		// Creating the new assumption (finite p t k f) and saving it.
		final SMTFormula finiteFormula = new SMTVeritFiniteFormula(
				finiteSymbol, pVarSymbol, fVarSymbol, kVarSymbol, children);

		signature.addAdditionalAssumption(finiteFormula);

		final SMTFormula pFormula = SMTFactory.makeAtom(pVarSymbol, signature);

		smtNode = pFormula;
	}

	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException(
				"It's not possible yet to translate extended expressionto SMT-LIB yet");

	}

	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException(
				"It's not possible yet to translate extended predicate to SMT-LIB yet");

	}

	/**
	 * Just for tests.
	 * 
	 * @param typeEnvironment
	 * 
	 */
	public static SMTSignature translateSMTSignature(
			final ITypeEnvironment typeEnvironment, final String solver) {
		final SMTThroughVeriT translator = new SMTThroughVeriT(solver);
		translator.setSignature(new SMTSignatureVerit(SMTLIBUnderlyingLogic
				.getInstance()));
		translator.translateSignature(typeEnvironment);
		return translator.getSignature();
	}

	/**
	 * 
	 * @return The translator signature.
	 */
	public SMTSignature getSignature() {
		return signature;
	}

	/**
	 * 
	 * @param signature
	 *            The signature to be set in the translator.
	 */
	public void setSignature(final SMTSignatureVerit signature) {
		this.signature = signature;
	}
}
