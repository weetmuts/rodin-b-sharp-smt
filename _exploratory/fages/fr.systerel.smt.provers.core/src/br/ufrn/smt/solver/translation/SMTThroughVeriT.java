/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *     Vitor Alcantara de Almeida - implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import static fr.systerel.smt.provers.ast.SMTFactory.makeEqual;
import static fr.systerel.smt.provers.ast.SMTFactoryVeriT.makeMacroTerm;
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
import fr.systerel.smt.provers.ast.SMTBenchmarkVeriT;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFactoryVeriT;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTLIBUnderlyingLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
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
 * This class implements the transalation from Event-B predicates to Extended
 * SMT-LIB formulas.
 * <p>
 * The Extended SMT-LIB formulas contains macros and other elements that does
 * not belong to the standard SMT-LIB and is accepted only by the VeriT solver
 * which pre-process these formulas to the standard SMT-LIB.
 * <p>
 * The SMT-LIB version of the translation is 1.2
 */
public class SMTThroughVeriT extends TranslatorV1_2 {

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

	/**
	 * An instance of the SMTFactoryVeriT
	 */
	final private SMTFactoryVeriT sf;

	public SMTThroughVeriT(final String solver) {
		super(solver);
		sf = SMTFactoryVeriT.getInstance();
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
	 * This method is called by {@link #parsePairTypes(Type)} to translate each
	 * type of the product type.
	 * 
	 * @param type
	 *            one type of the product type
	 * @param parentType
	 *            the product type that holds this type
	 * @return the translated sort of the event-B type
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
	 * @param type
	 *            the ProductType type
	 * @return The translated sort symbol from productType.
	 */
	private SMTSortSymbol parsePairTypes(final Type type) {
		final SMTSortSymbol sourceSymbol = parseOneOfPairTypes(
				type.getSource(), type);
		final SMTSortSymbol targetSymbol = parseOneOfPairTypes(
				type.getTarget(), type);
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
		// it was not already inserted.
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
			final List<Predicate> hypotheses, final Predicate goal) {

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

		final SMTBenchmarkVeriT benchmark = new SMTBenchmarkVeriT(lemmaName,
				signature, translatedAssumptions, smtFormula);
		benchmark.removeUnusedSymbols();
		return benchmark;
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
					addPairSortAndFunctions();
					typeMap.put(baseType, sortSymbol);
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
					+ ": Sets of sets are not supported yet");
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
		typeMap.put(type.getLeft(), left);
		final SMTSortSymbol right = translateTypeName(type.getRight());
		typeMap.put(type.getLeft(), right);
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

	/**
	 * Given the elements of a comprehension set, this method creates the macro
	 * corresponding to the comprehension set and returns the macro term that
	 * represents the CSET macro.
	 * 
	 * @param macroName
	 *            the name of the macro
	 * @param formulaChild
	 *            the formula of the comprehension set
	 * @param expressionTerm
	 *            the translated expression of the comprehension set
	 * @param expressionSort
	 *            the sort of the expression of the comprehension set
	 * @param termChildren
	 *            the bound identifiers of the comprehension set
	 * @return the macro term of the translated comprehension set
	 */
	private SMTTerm translateQuantifiedExpression(final String macroName,
			final SMTFormula formulaChild, final SMTTerm expressionTerm,
			final SMTSortSymbol expressionSort, final SMTTerm... termChildren) {

		// obtaining fresh name for the variables
		final String lambdaName = signature.freshCstName(SMTMacroSymbol.ELEM);

		final SMTVarSymbol lambdaVar = new SMTVarSymbol(lambdaName,
				expressionSort, false);

		// Creating the macro
		final SMTSetComprehensionMacro macro = makeSetComprehensionMacro(
				macroName, termChildren, lambdaVar, formulaChild,
				expressionTerm, signature);

		signature.addMacro(macro);
		final SMTMacroSymbol macroSymbol = makeMacroSymbol(macroName,
				VeritPredefinedTheory.getInstance().getBooleanSort());
		return makeMacroTerm(macroSymbol);
	}

	/**
	 * This method translates an Event-B atomic expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.KPRED:
			smtNode = translateKPREDorKSUCC(SMTOperator.MINUS,
					SMTMacroSymbol.PRED);
			break;
		case Formula.KSUCC:
			smtNode = translateKPREDorKSUCC(SMTOperator.PLUS,
					SMTMacroSymbol.SUCC);
			break;
		case Formula.INTEGER:
			smtNode = SMTFactoryVeriT.makeMacroTerm(getMacroSymbol(INTEGER,
					signature));
			break;
		case Formula.NATURAL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(getMacroSymbol(NAT,
					signature));
			break;
		case Formula.NATURAL1:
			smtNode = SMTFactoryVeriT.makeMacroTerm(getMacroSymbol(NAT1,
					signature));
			break;
		case Formula.EMPTYSET:
			if (expression.getType().getSource() instanceof ProductType
					|| expression.getType().getTarget() instanceof ProductType) {
				throw new IllegalArgumentException("Type: "
						+ expression.getType().toString()
						+ ": power set of power set is not supported yet");
			} else {
				smtNode = SMTFactoryVeriT.makeMacroTerm(SMTMacroFactory
						.getMacroSymbol(EMPTY, signature));
			}
			break;
		case Formula.KID_GEN:
			smtNode = SMTFactoryVeriT.makeMacroTerm(getMacroSymbol(ID,
					signature));
			break;
		case Formula.BOOL:
			throw new IllegalArgumentException(
					"Sort BOOL is not implemented yet");
		case Formula.KPRJ1_GEN:
			throw new IllegalArgumentException(
					"prj1 (KPRJ1_GEN) is not implemented yet");
		case Formula.KPRJ2_GEN:
			throw new IllegalArgumentException(
					"prj2 (KPRJ2_GEN) is not implemented yet");
		case Formula.TRUE:
			throw new IllegalArgumentException(
					"TRUE value (TRUE)is not implemented yet");
		case Formula.FALSE:
			throw new IllegalArgumentException(
					"false value (FALSE) is not implemented yet");
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * 
	 */
	private SMTTerm translateKPREDorKSUCC(final SMTOperator operator,
			final String macroSymbol) {
		// Making x
		final String x = signature.freshCstName("x");
		final SMTSortSymbol xSort = SMTFactoryVeriT.makePairSortSymbol(
				Ints.getInt(), Ints.getInt());

		final SMTTerm xFun = sf.makeVar(x, Ints.getInt());

		// Making 1
		final SMTNumeral plusOrMinusUmNumeral = SMTFactory
				.makeNumeral(BigInteger.ONE);

		// Making x + 1
		final SMTTerm plusOrMinusTerm = sf.makeMinus(
				(SMTFunctionSymbol) signature.getLogic().getOperator(operator),
				signature, xFun, plusOrMinusUmNumeral);

		// Making x |-> x + 1
		final SMTTerm[] mapstoTerm = new SMTTerm[1];
		mapstoTerm[0] = SMTFactoryVeriT.makeMacroTerm(
				getMacroSymbol(MAPSTO, signature), xFun, plusOrMinusTerm);

		// Making Int
		final SMTMacroSymbol intS = SMTMacroFactory.getMacroSymbol(INTEGER,
				signature);
		final SMTTerm intT = makeMacroTerm(intS);

		// making (x in Int)
		final SMTFormula inFormula = SMTFactoryVeriT.makeMacroAtom(
				getMacroSymbol(IN, signature), xFun, intT);

		final String macroName = signature.freshCstName(macroSymbol);

		return translateQuantifiedExpression(macroName, inFormula,
				mapstoTerm[0], xSort, xFun);
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
					SMTSymbol.MOD, Ints.getInt(), false, false,
					Ints.getIntIntTab());
			signature.addConstant(VERIT_MOD);
			smtNode = sf.makeVeriTTermOperatorApplication(VERIT_MOD, children,
					signature);
			break;
		case Formula.EXPN:
			smtNode = sf.makeExpn((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.EXPN), signature, children);
			break;
		case Formula.UPTO:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RANGE_INTEGER, signature), children);
			break;
		case Formula.RANSUB:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RANGE_SUBSTRACTION, signature), children);
			break;

		case Formula.RANRES:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RANGE_RESTRICTION, signature), children);
			break;

		case Formula.REL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RELATION, signature), children);
			break;

		case Formula.TREL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_RELATION, signature), children);
			break;

		case Formula.SREL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(SURJECTIVE_RELATION, signature), children);
			break;

		case Formula.STREL:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_SURJECTIVE_RELATION, signature),
					children);
			break;

		case Formula.PFUN:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(PARTIAL_FUNCTION, signature), children);
			break;

		case Formula.TFUN:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_FUNCTION, signature), children);
			break;
		case Formula.PINJ:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(PARTIAL_INJECTION, signature), children);
			break;

		case Formula.TINJ:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_INJECTION, signature), children);
			break;

		case Formula.PSUR:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(PARTIAL_SURJECTION, signature), children);
			break;

		case Formula.TSUR:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_SURJECTION, signature), children);
			break;

		case Formula.TBIJ:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(TOTAL_BIJECTION, signature), children);
			break;

		case Formula.SETMINUS:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(SETMINUS, signature), children);
			break;

		case Formula.CPROD:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(CARTESIAN_PRODUCT, signature), children);
			break;

		case Formula.DPROD:
			throw new IllegalArgumentException(
					"Operator direct product (DPROD) is not implemented yet");

		case Formula.PPROD:
			throw new IllegalArgumentException(
					"Operator parallel product (PPROD) is not implemented yet");

		case Formula.DOMRES:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(DOMAIN_RESTRICTION, signature), children);
			break;

		case Formula.DOMSUB:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(DOMAIN_SUBSTRACTION, signature), children);
			break;

		case Formula.FUNIMAGE:
			throw new IllegalArgumentException(
					"function application (FUNIMAGE) is not implemented yet");

		case Formula.RELIMAGE:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RELATIONAL_IMAGE, signature), children);
			break;

		case Formula.MAPSTO:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(MAPSTO, signature), children);
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
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

	public static boolean isPairType(final Type type) {
		if (type instanceof ProductType) {
			return true;
		}
		if (type.getBaseType() != null) {
			return isPairType(type.getBaseType());
		}
		return false;
	}

	private SMTFormula translateEqual(final RelationalPredicate predicate) {
		final SMTTerm[] children = smtTerms(predicate.getLeft(),
				predicate.getRight());
		final Type leftType = predicate.getLeft().getType();
		if (leftType instanceof BooleanType) {
			final SMTFormula[] childrenFormulas = sf
					.convertVeritTermsIntoFormulas(children);
			return SMTFactory.makeIff(childrenFormulas);
		} else if (isPairType(leftType)) {
			SMTMacroFactory.addPairEqualityAxiomsInSignature(signature);
		}
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
			smtNode = SMTFactory.makeNot(translateEqual(predicate));
			break;

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
			smtNode = translateRelationalPredicateMacro(IN, predicate);
			break;
		}
		case Formula.SUBSET: {
			smtNode = translateRelationalPredicateMacro(SUBSET, predicate);
			break;
		}
		case Formula.SUBSETEQ: {
			smtNode = translateRelationalPredicateMacro(SUBSETEQ, predicate);
			break;
		}
		case Formula.NOTSUBSET: {
			final SMTFormula subset = translateRelationalPredicateMacro(SUBSET,
					predicate);
			smtNode = SMTFactory.makeNot(subset);
			break;
		}
		case Formula.NOTSUBSETEQ: {
			final SMTFormula subseteq = translateRelationalPredicateMacro(
					SUBSETEQ, predicate);
			smtNode = SMTFactory.makeNot(subseteq);
			break;
		}
		default: {
			throw new IllegalTagException(predicate.getTag());
		}
		}
	}

	/**
	 * @param predicate
	 */
	private SMTFormula translateRelationalPredicateMacro(
			final SMTVeriTOperator operator, final RelationalPredicate predicate) {
		final SMTTerm[] children = smtTerms(predicate.getLeft(),
				predicate.getRight());
		return SMTFactoryVeriT.makeMacroAtom(
				getMacroSymbol(operator, signature), children);
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
			smtNode = translatePACO(BUNION, expression, expressions);
			break;
		case Formula.BINTER:
			smtNode = translatePACO(BINTER, expression, expressions);
			break;
		case Formula.FCOMP:
			smtNode = translatePACO(FCOMP, expression, expressions);
			break;
		case Formula.BCOMP:
			smtNode = translatePACO(BCOMP, expression, expressions);
			break;
		case Formula.OVR:
			smtNode = translatePACO(OVR, expression, expressions);
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
	 * @return the translated SMTTerm
	 */
	private SMTTerm translatePACO(final SMTVeriTOperator operator,
			final AssociativeExpression expression,
			final Expression[] expressions) {
		SMTTerm[] children;
		SMTTerm macroTerm;
		if (expressions.length == 2) {
			children = smtTerms(expression.getChildren());
			macroTerm = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(operator, signature), children);
		} else {
			children = smtTerms(expressions[0], expressions[1]);
			macroTerm = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(operator, signature), children);
			for (int i = 2; i < expressions.length; i++) {
				macroTerm = SMTFactoryVeriT.makeMacroTerm(
						getMacroSymbol(operator, signature), macroTerm,
						smtTerm(expressions[i]));
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
		throw new IllegalArgumentException(
				"'Translation of Boolean Expression is not implemented yet");
	}

	/**
	 * This method translates an Event-B bool quantified expression into an
	 * Extended SMT node.
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		switch (expression.getTag()) {
		case Formula.CSET:
			smtNode = translateCSET(expression);
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
	 * Translate a comprehension set
	 * 
	 * @param expression
	 *            the quantified expression
	 * @return the macro term of the translated comprehension set
	 */
	private SMTTerm translateCSET(final QuantifiedExpression expression) {
		boundIdentifiersMarker.push(boundIdentifiers.size());

		// Translating the children
		final SMTTerm[] termChildren = smtTerms(expression.getBoundIdentDecls());
		final SMTFormula formulaChild = smtFormula(expression.getPredicate());

		final Expression qExp = expression.getExpression();

		final SMTTerm[] expressionTerm = smtTerms(qExp);

		// obtaining the expression type
		SMTSortSymbol expressionSymbol = typeMap.get(expression.getType());
		if (expressionSymbol == null) {
			expressionSymbol = translateTypeName(expression.getType());
		}
		final String macroName = signature.freshCstName(SMTMacroSymbol.CSET);

		final int top = boundIdentifiersMarker.pop();
		boundIdentifiers.subList(top, boundIdentifiers.size()).clear();

		return translateQuantifiedExpression(macroName, formulaChild,
				expressionTerm[0], expressionSymbol, termChildren);
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
			smtNode = SMTFactoryVeriT.makeMacroTerm(getMacroSymbol(EMPTY,
					signature));
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
			smtNode = SMTFactoryVeriT.makeMacroTerm(symbol);
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
			final SMTMacroSymbol cardSymbol = getMacroSymbol(CARD, signature);

			// Creating the new assumption (card p t k f) and saving it.
			final SMTFormula cardFormula = new SMTVeritCardFormula(cardSymbol,
					fVarSymbol, kVarSymbol, children);

			signature.addAdditionalAssumption(cardFormula);

			final SMTTerm kTerm = sf.makeVeriTConstantTerm(kVarSymbol,
					signature);

			smtNode = kTerm;

			break;
		}
		case Formula.KDOM:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(DOM, signature), children);
			break;
		case Formula.KRAN: {
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(RANGE, signature), children);
			break;
		}
		case Formula.KMIN: {
			smtNode = translateKMINorKMAX(SMTVeriTOperator.ISMIN, "ismin_var",
					children);
			break;
		}
		case Formula.KMAX: {
			smtNode = translateKMINorKMAX(SMTVeriTOperator.ISMAX, "ismax_var",
					children);
			break;
		}
		case Formula.CONVERSE:
			smtNode = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(INV, signature), children);
			break;
		case Formula.UNMINUS:
			smtNode = sf.makeUMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.UMINUS), signature, children);
			break;
		case Formula.POW: {
			throw new IllegalArgumentException(
					"It's not possible yet to translate  PowerSet unary expression (POW) to SMT-LIB yet");
		}
		case Formula.POW1: {
			throw new IllegalArgumentException(
					"It's not possible yet to translate  PowerSet1 unary expression (POW1) to SMT-LIB yet");
		}
		case Formula.KUNION: {
			throw new IllegalArgumentException(
					"It's not possible yet to translate generalized union (KUNION) to SMT-LIB yet");
		}
		case Formula.KINTER: {
			throw new IllegalArgumentException(
					"It's not possible yet to translate generalized inter (KINTER) to SMT-LIB yet");
		}
		default: {
			throw new IllegalTagException(expression.getTag());
		}
		}
	}

	/**
	 * @param children
	 */
	private SMTTerm translateKMINorKMAX(final SMTVeriTOperator operator,
			final String constantName, final SMTTerm[] children) {
		// Creating the name for the 'm' variable in SMT-LIB (rule 22)
		final String mVarName = signature.freshCstName(constantName);

		// Creating the constant 'm'
		final SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(mVarName,
				Ints.getInt(), false, false);
		signature.addConstant(mVarSymbol);

		// Creating the macro operator 'ismin'
		final SMTMacroSymbol opSymbol = getMacroSymbol(operator, signature);

		// Creating the term 'm'
		final SMTTerm mVarTerm = SMTFactory.makeFunApplication(mVarSymbol,
				signature);

		// Adding the term 'm' to the other children
		final SMTTerm[] minChildrenTerms = new SMTTerm[children.length + 1];
		for (int i = 0; i < children.length; i++) {
			minChildrenTerms[i + 1] = children[i];
		}
		minChildrenTerms[0] = mVarTerm;

		// Creating the new assumption (ismin m t) and saving it.
		final SMTFormula isMinFormula = SMTFactoryVeriT.makeMacroAtom(opSymbol,
				minChildrenTerms);
		signature.addAdditionalAssumption(isMinFormula);

		return mVarTerm;
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

	/**
	 * There are two different ways to translate multiple predicates.
	 * 
	 * <ol>
	 * <li>
	 * <li>1st: The sets are all singletons. In that case, the translation is
	 * done as follows:
	 * <p>
	 * 
	 * <ol>
	 * <li>translate each expression E0 ·· En</li>
	 * <li>create n fresh variables X, that Xn = sort of smt(Et), where 0 ≤ t ≤
	 * n</li>
	 * <li>add n assumptions:
	 * 
	 * <p>
	 * asumption (+) (= X1 smt(E1))
	 * <p>
	 * ·
	 * <p>
	 * ·
	 * <p>
	 * ·
	 * <p>
	 * asumption (+) (= Xn smt(En))
	 * <p>
	 * (It’s added no assumption for E0)</li>
	 * <li>add more two assumptions:
	 * <ul>
	 * <li>assumption (+) (distinct X1 ... Xn)</li>
	 * <li>assumption (+) (= smt(E0) (union Xn ... Xn))</li>
	 * </ol>
	 * </li>
	 * 
	 * <p>
	 * </li>
	 * <li>
	 * 2nd: There is at least one set which is not singleton. In this case, the
	 * predicate is expanded to a predicate with simpler mathematical operators
	 * and the translated. This expansion is already implemented in Rodin.</li>
	 * </ol>
	 */
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
		smtNode = createUnionAssumptionForParition(newVars, e0);
	}

	/**
	 * Create and return the assumption:
	 * <p>
	 * (= smt(E0) (union Xn ... Xn))
	 * 
	 * @param newVars
	 *            the terms Xn ... Xn
	 * @param e0
	 *            the term E0
	 * @return the assumption as described above
	 * @see #visitMultiplePredicate(MultiplePredicate)
	 */
	private SMTFormula createUnionAssumptionForParition(
			final List<SMTTerm> newVars, final SMTTerm e0) {
		assert !newVars.isEmpty();
		SMTTerm unionTerm;
		if (newVars.size() == 1) {
			unionTerm = newVars.get(0);
		} else {
			unionTerm = SMTFactoryVeriT.makeMacroTerm(
					getMacroSymbol(BUNION, signature), newVars.get(0),
					newVars.get(1));
			for (int i = 2; i < newVars.size(); i++) {
				unionTerm = SMTFactoryVeriT.makeMacroTerm(
						getMacroSymbol(BUNION, signature), unionTerm,
						newVars.get(i));
			}
		}
		return makeEqual(e0, unionTerm);
	}

	/**
	 * Create and add the assumption:
	 * 
	 * <p>
	 * (= Xn smt(En))
	 * 
	 * @param x
	 *            The name of the nth-term x
	 * @param type
	 *            the type of the term x.
	 * @param e0
	 *            the term smt(En)
	 * @return the assumption as described above
	 * @see #visitMultiplePredicate(MultiplePredicate)
	 */
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
	 * Creates and returns the assumption:
	 * 
	 * <p>
	 * assumption (+) (distinct X1 ... Xn)
	 * 
	 * @param newVars
	 *            the terms X1 ... Xn
	 * @see #visitMultiplePredicate(MultiplePredicate)
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
		final SMTMacroSymbol finiteSymbol = getMacroSymbol(FINITE, signature);

		// Creating the new assumption (finite p t k f) and saving it.
		final SMTFormula finiteFormula = new SMTVeritFiniteFormula(
				finiteSymbol, pVarSymbol, fVarSymbol, kVarSymbol, children);

		signature.addAdditionalAssumption(finiteFormula);

		final SMTFormula pFormula = SMTFactory.makeAtom(pVarSymbol, signature);

		smtNode = pFormula;
	}

	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"It's not possible yet to translate extended expression to SMT-LIB yet");

	}

	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
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
