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

import static fr.systerel.smt.provers.ast.SMTFactory.EMPTY_SORT;
import static fr.systerel.smt.provers.ast.SMTFactory.makeDistinct;
import static fr.systerel.smt.provers.ast.SMTFactory.makeEqual;
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
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.INV;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.ISMAX;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.ISMIN;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.MAPSTO;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.NAT;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.NAT1;
import static fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator.NOT_EQUAL;
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
import static fr.systerel.smt.provers.ast.macros.SMTMacroFactory.PAIR_SORT;
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

	public SMTThroughVeriT(String solver) {
		super(solver);
	}

	/**
	 * An instance of <code>SMTThroughVeriT</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	protected SMTSignatureVerit signature;

	/**
	 * This variable is used to control if it's necessary to print a point ( . )
	 * after declaration of variables in a existencial predicate. The point is
	 * necessary when the predicate being translated is inside a macro (it can
	 * be produced by set intension translation rules of lambda abstraction
	 * translation rules)
	 */
	private boolean printPointInQuantifiedOperator = false;

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
			final Predicate goal, String solver) throws TranslationException {
		SMTBenchmark smtB = new SMTThroughVeriT(solver).translate(lemmaName,
				hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			final Predicate predicate, String solver) {
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
	protected SMTLogic determineLogic() {
		return SMTLogic.VeriTSMTLIBUnderlyingLogic.getInstance();
	}

	/**
	 * This boolean is used to check if it's necessary to add the polymorphic
	 * sort Pair and the polymorphic function pair in the signature.
	 */
	private boolean insertPairDecl = false;

	/**
	 * This method translates powerset types. It applies the following rules:
	 * 
	 * 1: The BaseType name is equal the Var name: add a new sort with the Var
	 * name. 2: The BaseType is a CartesianType or a power set of another type:
	 * throw an {@link IllegalArgumentException}. Default: Add a new predicate
	 * with the same name as Var name and the first argument as the BaseType.
	 * 
	 * @param varName
	 *            The name of the variable
	 * @param varType
	 *            The BaseType of the variable
	 */
	private void parseBaseTypes(String varName, Type varType) {
		if (varType.getBaseType().getSource() != null
				|| varType.getBaseType().getBaseType() != null) {
			throw new IllegalArgumentException("Variable: " + varName
					+ ", Type " + varType.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			SMTSortSymbol sortSymbol = SMTFactory.makeVeriTSortSymbol(varType
					.getBaseType().toString(), signature);
			this.signature.addSort(sortSymbol);
			typeMap.put(varType, sortSymbol);

			String newVarName = signature.freshCstName(varName);

			SMTPredicateSymbol predSymbol = sf.makeVeriTPredSymbol(newVarName,
					sortSymbol);
			this.signature.addPred(predSymbol);
			varMap.put(varName, predSymbol);
		}
	}

	/**
	 * This method translates each type of CartesianProduct Types. It must be
	 * called only by {@link #parsePairTypes(Type, Type)}. It applies the
	 * following rules:
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
	 * @see #parsePairTypes(Type, Type)
	 */
	private SMTSortSymbol parseOneOfPairTypes(Type type) {
		if (type.getSource() != null) {
			return parsePairTypes(type.getSource(), type.getTarget());
		} else if (type.getBaseType() != null) {
			throw new IllegalArgumentException(", Type " + type.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			return SMTFactory.makeVeriTSortSymbol(type.toString(), signature);
		}
	}

	/**
	 * This method translates a ProductType. It translates both the source and
	 * the target types.
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return The translated sort symbol from productType.
	 */
	private SMTSortSymbol parsePairTypes(Type sourceType, Type targetType) {
		SMTSortSymbol sourceSymbol = parseOneOfPairTypes(sourceType);
		SMTSortSymbol targetSymbol = parseOneOfPairTypes(targetType);
		return sf.makePairSortSymbol(sourceSymbol, targetSymbol);
	}

	@Override
	public void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		this.signature = new SMTSignatureVerit(logic);
		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);
		translateSignature(typeEnvironment);

		List<Type> biTypes = getBoundIDentDeclTypes(hypotheses, goal);
		Iterator<Type> bIterator = biTypes.iterator();

		extractTypeFromBoundIdentDecl(bIterator);
	}

	/**
	 * This method adds the sort (Pair 's 't) and the fun (pair 's 't (Pair 's
	 * 't)) to the signature
	 */
	public void addPairSortAndFunctions() {
		this.signature.addSort(PAIR_SORT);
		SMTSortSymbol[] argSorts = {};
		final String symbolName = "pair 's 't";
		SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(symbolName,
				argSorts, PAIR_SORT, !SMTFunctionSymbol.ASSOCIATIVE,
				!SMTSymbol.PREDEFINED);

		signature.addConstant(functionSymbol);
		varMap.put(symbolName, functionSymbol);
	}

	/**
	 * This method extracts types of bound ident declarations and adds them into
	 * the signature
	 * 
	 * @param iter
	 *            The iterator which contains the types of bound ident
	 *            declarations
	 */
	private void extractTypeFromBoundIdentDecl(Iterator<Type> iter) {
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
	 * @param varType
	 *            The type of the variable
	 */
	private void translatePairTypeSymbol(String varName, String freshVarName,
			Type varType) {
		SMTSortSymbol sortSymbol = parsePairTypes(varType.getSource(),
				varType.getTarget());
		SMTSortSymbol[] sorts = { sortSymbol };
		SMTPredicateSymbol predSymbol = new SMTPredicateSymbol(freshVarName,
				sorts, false);
		signature.addPred(predSymbol);
		varMap.put(varName, predSymbol);
		typeMap.put(varType, sortSymbol);
		insertPairDecl = true;
	}

	/**
	 * Translate variables into functions
	 * 
	 * @param varName
	 *            the name of variable
	 * @param varType
	 *            the type of variable
	 */
	private void translateFunSymbol(String varName, Type varType) {
		SMTSortSymbol smtSortSymbol = SMTFactory.makeVeriTSortSymbol(
				varType.toString(), signature);
		final SMTFunctionSymbol smtConstant;
		smtConstant = signature.freshConstant(varName, smtSortSymbol);
		this.signature.addSort(smtSortSymbol);
		this.signature.addConstant(smtConstant);
		typeMap.put(varType, smtSortSymbol);
		varMap.put(varName, smtConstant);
	}

	/**
	 * This method translates the signature.
	 * 
	 * @param typeEnvironment
	 *            The Event-B Type Environment for the translation.
	 */
	public void translateSignature(ITypeEnvironment typeEnvironment) {
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final String freshVarName = signature.freshCstName(varName);
			final Type varType = iter.getType();

			if (varType.getSource() != null) {
				translatePairTypeSymbol(varName, freshVarName, varType);
			} else if (varType.getBaseType() != null) {
				parseBaseTypes(varName, varType);
			} else {
				translateFunSymbol(varName, varType);
			}
		}
		if (insertPairDecl) {
			addPairSortAndFunctions();
		}

	}

	@Override
	protected SMTBenchmark translate(String lemmaName,
			List<Predicate> hypotheses, Predicate goal)
			throws TranslationException {

		final SMTLogic logic = determineLogic();

		/**
		 * SMT translation
		 */
		// translates the signature
		translateSignature(logic, hypotheses, goal);

		// translates each hypothesis
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		for (Predicate hypothesis : hypotheses) {
			clearFormula();
			SMTFormula translatedFormula = translate(hypothesis);
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
	private SMTFormula translate(Predicate predicate) {
		predicate.accept(this);
		return getSMTFormula();
	}

	@Override
	protected SMTSortSymbol translateTypeName(Type type) {
		if (type.getSource() != null) {

			SMTSortSymbol sortSymbol = parsePairTypes(type.getSource(),
					type.getTarget());
			typeMap.put(type, sortSymbol);
			return sortSymbol;

		} else if (type.getBaseType() != null) {

			if (type.getBaseType().getSource() != null
					|| type.getBaseType().getBaseType() != null) {
				throw new IllegalArgumentException("Type " + type.toString()
						+ ": Sets of sets are not supported yet");
			} else {
				SMTSortSymbol sortSymbol = SMTFactory.makeVeriTSortSymbol(type
						.getBaseType().toString(), signature);
				this.signature.addSort(sortSymbol);
				typeMap.put(type, sortSymbol);
				return sortSymbol;
			}

		} else {
			SMTSortSymbol smtSortSymbol = SMTFactory.makeVeriTSortSymbol(
					type.toString(), signature);
			this.signature.addSort(smtSortSymbol);
			typeMap.put(type, smtSortSymbol);
			return smtSortSymbol;
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

	private void translateQuantifiedExpression(String macroName,
			SMTTerm[] termChildren, SMTFormula formulaChild,
			SMTTerm[] expressionTerm, SMTSortSymbol expressionSymbol) {
		this.printPointInQuantifiedOperator = true;

		// obtaining fresh name for the variables
		String lambdaName = signature.freshCstName(SMTMacroSymbol.ELEM);

		SMTVarSymbol lambdaVar = new SMTVarSymbol(lambdaName, expressionSymbol,
				false);

		// Creating the macro
		SMTSetComprehensionMacro macro = makeSetComprehensionMacro(macroName,
				termChildren, lambdaVar, formulaChild, expressionTerm[0],
				signature);

		this.signature.addMacro(macro);
		SMTMacroSymbol macroSymbol = makeMacroSymbol(macroName);
		this.smtNode = sf.makeMacroTerm(macroSymbol);
		this.printPointInQuantifiedOperator = false;
	}

	/**
	 * This method translates an Event-B atomic expression into an Extended SMT
	 * node.
	 */
	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.KPRED:

			// obtaining the expression type
			final SMTSortSymbol expressionSymbol = Ints.getInt();

			// Making x - 1
			final String x = signature.freshCstName("x");
			new SMTFunctionSymbol(x, EMPTY_SORT, Ints.getInt(), !ASSOCIATIVE,
					!PREDEFINED);
			final SMTTerm xFun = sf.makeVar(x, expressionSymbol);
			final SMTTerm[] xFuns = { xFun };
			final BigInteger b = BigInteger.ONE;
			final SMTNumeral minusUmNumeral = sf.makeNumeral(b);
			final SMTTerm[] minusChildren = { xFun, minusUmNumeral };
			final SMTTerm[] minusTerm = { sf.makeMinus(
					(SMTFunctionSymbol) signature.getLogic().getOperator(
							SMTOperator.MINUS), minusChildren, signature) };

			// Making true (Z in Int)
			final SMTFormula xInInt = sf.makePTrue(this.signature);
			String macroName = signature.freshCstName(SMTMacroSymbol.PRED);
			translateQuantifiedExpression(macroName, xFuns, xInInt, minusTerm,
					expressionSymbol);

			break;
		case Formula.INTEGER:
			smtNode = sf.makePTrue(this.signature);
			break;
		case Formula.NATURAL:
			addPredefinedMacroInSignature(NAT, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(NAT));
			break;
		case Formula.NATURAL1:
			addPredefinedMacroInSignature(NAT1, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(NAT1));
			break;
		case Formula.BOOL:
			smtNode = sf.makeVeriTConstantTerm(signature.getLogic()
					.getBooleanCste(), signature);
			break;
		case Formula.EMPTYSET:
			if (expression.getType().getSource() instanceof ProductType
					|| expression.getType().getTarget() instanceof ProductType) {
				throw new IllegalArgumentException("Type: "
						+ expression.getType().toString()
						+ ": power set of power set is not supported yet");
			} else {
				addPredefinedMacroInSignature(EMPTY, signature);

				smtNode = sf.makeMacroTerm(SMTMacroFactory
						.getMacroSymbol(EMPTY));
			}
			break;
		case Formula.KSUCC:

			// obtaining the expression type
			final SMTSortSymbol expressionSymbolKS = Ints.getInt();

			// Making x - 1
			final String xKS = signature.freshCstName("x");
			new SMTFunctionSymbol(xKS, EMPTY_SORT, Ints.getInt(), !ASSOCIATIVE,
					!PREDEFINED);
			final SMTTerm xFunKS = sf.makeVar(xKS, expressionSymbolKS);
			final SMTTerm[] xFunsKS = { xFunKS };
			final BigInteger bKS = BigInteger.ONE;
			final SMTNumeral plusUmNumeral = sf.makeNumeral(bKS);
			final SMTTerm[] plusChildren = { xFunKS, plusUmNumeral };
			final SMTTerm[] plusTerm = { sf.makePlus(
					(SMTFunctionSymbol) signature.getLogic().getOperator(
							SMTOperator.PLUS), plusChildren, signature) };

			// Making true (Z in Int)
			final SMTFormula xInIntKS = sf.makePTrue(this.signature);
			String macroNameKS = signature.freshCstName(SMTMacroSymbol.SUCC);
			translateQuantifiedExpression(macroNameKS, xFunsKS, xInIntKS,
					plusTerm, expressionSymbolKS);
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

			smtNode = sf.makeMacroTerm(getMacroSymbol(ID));
			break;
		case Formula.TRUE:
			/**
			 * TODO Check the rules to see how do implement this.
			 */
			// this.smtNode = sf.makePTrue(this.signature); // FIXME Use boolean
			// value when BOOL_SORT theory implemented
			throw new IllegalArgumentException(
					"TRUE value (TRUE)is not implemented yet");
		case Formula.FALSE:
			/**
			 * TODO Check the rules to see how do implement this.
			 */
			// this.smtNode = sf.makePFalse(this.signature); // FIXME Use
			// boolean value when BOOL_SORT theory implemented
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
	public void visitBinaryExpression(BinaryExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getLeft(),
				expression.getRight());
		switch (expression.getTag()) {
		case Formula.MINUS:
			smtNode = sf.makeMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MINUS), children, signature);
			break;
		case Formula.DIV:
			if (solver.equals(SMTSolver.Z3.toString())) {
				smtNode = sf.makeDiv((SMTFunctionSymbol) signature.getLogic()
						.getOperator(SMTOperator.DIV_Z3), children, signature);
			} else {
				smtNode = sf.makeDiv((SMTFunctionSymbol) signature.getLogic()
						.getOperator(SMTOperator.DIV), children, signature);
			}
			break;
		case Formula.MOD:
			/**
			 * It's added the function ((mod Int Int Int)) in the signature
			 */
			SMTFunctionSymbol VERIT_MOD = new SMTFunctionSymbol(
					SMTMacroSymbol.MOD, Ints.getIntIntTab(), Ints.getInt(),
					false, false);

			this.signature.addConstant(VERIT_MOD);
			smtNode = sf.makeVeriTTermOperatorApplication(VERIT_MOD, children,
					signature);
			break;
		case Formula.EXPN:
			throw new IllegalArgumentException(
					"The operation \'exponential\' is not supported yet");

		case Formula.UPTO:
			addPredefinedMacroInSignature(RANGE_INTEGER, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RANGE_INTEGER), children);
			break;
		case Formula.RANSUB:
			addPredefinedMacroInSignature(RANGE_SUBSTRACTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RANGE_SUBSTRACTION),
					children);
			break;

		case Formula.RANRES:
			addPredefinedMacroInSignature(RANGE_RESTRICTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RANGE_RESTRICTION),
					children);
			break;

		case Formula.REL:
			addPredefinedMacroInSignature(RELATION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RELATION), children);
			break;

		case Formula.TREL:
			addPredefinedMacroInSignature(TOTAL_RELATION, signature);

			smtNode = sf
					.makeMacroTerm(getMacroSymbol(TOTAL_RELATION), children);
			break;

		case Formula.SREL:
			addPredefinedMacroInSignature(SURJECTIVE_RELATION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(SURJECTIVE_RELATION),
					children);
			break;

		case Formula.STREL:
			addPredefinedMacroInSignature(TOTAL_SURJECTIVE_RELATION, signature);

			smtNode = sf.makeMacroTerm(
					getMacroSymbol(TOTAL_SURJECTIVE_RELATION), children);
			break;

		case Formula.PFUN:
			addPredefinedMacroInSignature(PARTIAL_FUNCTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(PARTIAL_FUNCTION),
					children);
			break;

		case Formula.TFUN:
			addPredefinedMacroInSignature(TOTAL_FUNCTION, signature);

			smtNode = sf
					.makeMacroTerm(getMacroSymbol(TOTAL_FUNCTION), children);
			break;
		case Formula.PINJ:
			addPredefinedMacroInSignature(PARTIAL_INJECTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(PARTIAL_INJECTION),
					children);
			break;

		case Formula.TINJ:
			addPredefinedMacroInSignature(TOTAL_INJECTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(TOTAL_INJECTION),
					children);
			break;

		case Formula.PSUR:
			addPredefinedMacroInSignature(PARTIAL_SURJECTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(PARTIAL_SURJECTION),
					children);
			break;

		case Formula.TSUR:
			addPredefinedMacroInSignature(TOTAL_SURJECTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(TOTAL_SURJECTION),
					children);
			break;

		case Formula.TBIJ:
			addPredefinedMacroInSignature(TOTAL_BIJECTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(TOTAL_BIJECTION),
					children);
			break;

		case Formula.SETMINUS:
			addPredefinedMacroInSignature(SETMINUS, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(SETMINUS), children);
			break;

		case Formula.CPROD:
			addPredefinedMacroInSignature(CARTESIAN_PRODUCT, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(CARTESIAN_PRODUCT),
					children);
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

			smtNode = sf.makeMacroTerm(getMacroSymbol(DOMAIN_RESTRICTION),
					children);
			break;

		case Formula.DOMSUB:
			addPredefinedMacroInSignature(DOMAIN_SUBSTRACTION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(DOMAIN_SUBSTRACTION),
					children);
			break;

		case Formula.FUNIMAGE:
			// FIXME There is no implementation for FUNIMAGE in Verit SMT-LIB
			throw new IllegalArgumentException(
					"function application (FUNIMAGE) is not implemented yet");

		case Formula.RELIMAGE:
			addPredefinedMacroInSignature(RELATIONAL_IMAGE, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RELATIONAL_IMAGE),
					children);
			break;

		case Formula.MAPSTO:
			addPredefinedMacroInSignature(MAPSTO, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(MAPSTO), children);
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
			this.smtNode = sf.makeForAll(termChildren, formulaChild,
					this.printPointInQuantifiedOperator);
			break;
		case Formula.EXISTS:
			this.smtNode = sf.makeExists(termChildren, formulaChild,
					this.printPointInQuantifiedOperator);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}

		int top = boundIdentifiersMarker.pop();

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
				this.smtNode = sf.makeIff(childrenFormulas);
			} else {
				this.smtNode = makeEqual(children);
			}
			break;
		}
		case Formula.NOTEQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			if (predicate.getLeft().getType() instanceof BooleanType) {
				final SMTFormula[] childrenFormulas = sf
						.convertVeritTermsIntoFormulas(children);
				this.smtNode = sf.makeNotIff(childrenFormulas);
			} else {
				addPredefinedMacroInSignature(NOT_EQUAL, signature);

				this.smtNode = SMTFactory.makeMacroAtom(
						getMacroSymbol(NOT_EQUAL), children);
			}
			break;
		}
		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessThan((SMTPredicateSymbol) signature.getLogic()
					.getOperator(SMTOperator.LT), children, this.signature);
			break;
		}
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.LE), children,
					this.signature);
			break;
		}
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterThan((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GT), children,
					this.signature);
			break;
		}
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GE), children,
					this.signature);
			break;
		}
		case Formula.IN: {
			addPredefinedMacroInSignature(IN, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeVeriTMacroAtom(getMacroSymbol(IN), children,
					signature);
			break;
		}

		case Formula.SUBSET: {
			addPredefinedMacroInSignature(SUBSET, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory
					.makeMacroAtom(getMacroSymbol(SUBSET), children);
			break;
		}
		case Formula.SUBSETEQ: {
			addPredefinedMacroInSignature(SUBSETEQ, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory.makeMacroAtom(getMacroSymbol(SUBSETEQ),
					children);
			break;
		}
		case Formula.NOTSUBSET: {
			addPredefinedMacroInSignature(SUBSET, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory
					.makeMacroAtom(getMacroSymbol(SUBSET), children);
			SMTFormula[] subsetFormula = { (SMTFormula) smtNode };
			smtNode = sf.makeNot(subsetFormula);
			break;
		}
		case Formula.NOTSUBSETEQ: {
			addPredefinedMacroInSignature(SUBSETEQ, signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory.makeAtom(getMacroSymbol(SUBSETEQ), children,
					signature);
			SMTFormula[] subsetFormula = { (SMTFormula) smtNode };
			smtNode = sf.makeNot(subsetFormula);
			break;
		}
		default: {
			throw new IllegalTagException(predicate.getTag());
		}
		}
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"BecomesEqualTo assignment is not implemented yet");
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"BecomesMemberOf assignment is not implemented yet");
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"BecomesSuchThat assignment is not implemented yet");
	}

	/**
	 * This method translates an Event-B associative expression into an Extended
	 * SMT node.
	 */
	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
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
		case Formula.BUNION:
			addPredefinedMacroInSignature(BUNION, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(BUNION), children);
			break;
		case Formula.BINTER:
			addPredefinedMacroInSignature(BINTER, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(BINTER), children);
			break;
		case Formula.FCOMP:
			addPredefinedMacroInSignature(FCOMP, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(FCOMP), children);
			break;
		case Formula.BCOMP:
			addPredefinedMacroInSignature(BCOMP, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(BCOMP), children);
			break;
		case Formula.OVR:
			addPredefinedMacroInSignature(OVR, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(OVR), children);
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
	public void visitBoolExpression(BoolExpression expression) {
		// TODO Implement rule 21
		throw new IllegalArgumentException(
				"'Translation of Boolean Expression is not implemented yet");
	}

	/**
	 * This method translates an Event-B bool quantified expression into an
	 * Extended SMT node.
	 */
	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
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
			String macroName = signature.freshCstName(SMTMacroSymbol.CSET);
			translateQuantifiedExpression(macroName, termChildren,
					formulaChild, expressionTerm, expressionSymbol);
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
	public void visitSetExtension(SetExtension expression) {
		// FIXME: Refactor this method
		SMTTerm[] children = {};
		if (expression.getChildCount() == 0) {
			addPredefinedMacroInSignature(EMPTY, signature);
			smtNode = sf.makeMacroTerm(getMacroSymbol(EMPTY), children);
		} else {
			children = smtTerms(expression.getMembers());
			String macroName = signature.freshCstName(SMTMacroSymbol.ENUM);
			String varName1 = signature.freshCstName(SMTMacroSymbol.ELEM);
			if (expression.getMembers()[0].getType() instanceof ProductType) {

				Set<String> l = new HashSet<String>();
				l.add(varName1);
				String varName2 = signature
						.freshCstName(SMTMacroSymbol.ELEM, l);
				SMTSortSymbol sortSymbol1 = translateTypeName(expression
						.getType().getSource());
				SMTSortSymbol sortSymbol2 = translateTypeName(expression
						.getType().getTarget());

				SMTVarSymbol var1 = new SMTVarSymbol(varName1, sortSymbol1,
						false);
				SMTVarSymbol var2 = new SMTVarSymbol(varName2, sortSymbol2,
						false);

				SMTPairEnumMacro macro = SMTMacroFactory
						.makePairEnumerationMacro(macroName, var1, var2,
								children, signature);
				this.signature.addMacro(macro);
			} else {
				SMTSortSymbol sortSymbol = translateTypeName(expression
						.getType());
				SMTVarSymbol var = new SMTVarSymbol(varName1, sortSymbol, false);

				SMTEnumMacro macro = makeEnumMacro(macroName, var, children);
				this.signature.addMacro(macro);
			}
			SMTMacroSymbol symbol = makeMacroSymbol(macroName);
			smtNode = sf.makeMacroTerm(symbol);
		}
	}

	/**
	 * This method translates an Event-B unary expression into an SMT node.
	 */
	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		// FIXME Refactor this method
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.KCARD: {

			// Creating card macro and to the signature
			addPredefinedMacroInSignature(CARD, signature);

			// Creating the name for the 'f' and 'k' variables in SMT-LIB (rule
			// 25)
			String kVarName = signature.freshCstName("card_k");
			String fVarName = signature.freshCstName("card_f");

			SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(kVarName,
					EMPTY_SORT, Ints.getInt(), false, false);

			Type type = expression.getChild().getType();
			SMTSortSymbol expressionSort = typeMap.get(type);
			if (expressionSort == null) {
				expressionSort = translateTypeName(type);
			}
			SMTSortSymbol[] sorts = { expressionSort };

			SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(fVarName,
					sorts, Ints.getInt(), false, false);

			signature.addConstant(kVarSymbol);
			signature.addConstant(fVarSymbol);

			// Creating the macro operator 'finite'
			SMTMacroSymbol cardSymbol = getMacroSymbol(CARD);

			// Creating the new assumption (card p t k f) and saving it.
			SMTFormula cardFormula = new SMTVeritCardFormula(cardSymbol,
					fVarSymbol, kVarSymbol, children);

			signature.addAdditionalAssumption(cardFormula);

			SMTTerm kTerm = sf.makeVeriTConstantTerm(kVarSymbol, signature);

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

			smtNode = sf.makeMacroTerm(getMacroSymbol(DOM), children);
			break;
		case Formula.KRAN: {
			addPredefinedMacroInSignature(RANGE, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(RANGE), children);
			break;
		}
		case Formula.KMIN: {
			// TODO Refactor

			// Creating ismin macro and add it to the signature
			addPredefinedMacroInSignature(ISMIN, signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			String mVarName = signature.freshCstName("ismin_var");

			// Creating the constant 'm'
			SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(mVarName,
					EMPTY_SORT, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismin'
			SMTMacroSymbol isMinSymbol = getMacroSymbol(ISMIN);

			// Creating the term 'm'
			SMTTerm mVarTerm = sf.makeFunApplication(mVarSymbol,
					SMTFactory.EMPTY_TERM, signature);

			// Adding the term 'm' to the other children
			SMTTerm[] minChildrenTerms = new SMTTerm[children.length + 1];
			for (int i = 0; i < children.length; i++) {
				minChildrenTerms[i + 1] = children[i];
			}
			minChildrenTerms[0] = mVarTerm;

			// Creating the new assumption (ismin m t) and saving it.
			SMTFormula isMinFormula = SMTFactory.makeMacroAtom(isMinSymbol,
					minChildrenTerms);
			signature.addAdditionalAssumption(isMinFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.KMAX: {
			// TODO Refactor

			// Creating ismax macro and adding it to the signature
			addPredefinedMacroInSignature(ISMAX, signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			String mVarName = signature.freshCstName("ismax_var");

			// Creating the constant 'm'
			SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(mVarName,
					EMPTY_SORT, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismax'
			SMTMacroSymbol isMaxSymbol = getMacroSymbol(ISMAX);

			// Creating the term 'm'
			SMTTerm mVarTerm = sf.makeFunApplication(mVarSymbol,
					SMTFactory.EMPTY_TERM, signature);

			// Adding the term 'm' to the other children
			SMTTerm[] maxChildrenTerms = new SMTTerm[children.length + 1];
			for (int i = 0; i < children.length; i++) {
				maxChildrenTerms[i + 1] = children[i];
			}
			maxChildrenTerms[0] = mVarTerm;

			// Creating the new assumption (ismax m t) and saving it.
			SMTFormula isMaxFormula = SMTFactory.makeMacroAtom(isMaxSymbol,
					maxChildrenTerms);

			signature.addAdditionalAssumption(isMaxFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.CONVERSE:
			addPredefinedMacroInSignature(INV, signature);

			smtNode = sf.makeMacroTerm(getMacroSymbol(INV), children);
			break;
		case Formula.UNMINUS:
			smtNode = sf.makeUMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.UMINUS), children, signature);
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
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		smtNode = sf.makeVeriTConstantTerm(
				varMap.get(identifierExpression.getName()), this.signature);
	}

	/**
	 * This method translates an Event-B literal predicate into an Extended SMT
	 * node.
	 */
	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			smtNode = sf.makePTrue(this.signature);
			break;
		case Formula.BFALSE:
			smtNode = sf.makePFalse(this.signature);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		Predicate expandedPredicate = Expanders.expandPARTITION(predicate,
				FormulaFactory.getDefault());
		smtNode = smtFormula(expandedPredicate);
		return;

		/*
		 * FIXME: Using expandPartition to translate multiple predicate. The
		 * problem of translating it to the simple case (only singletons), is
		 * that the macro union is not working with more than two arguments
		 */

		// Expression[] expressions = predicate.getChildren();
		// for (int i = 1; i < expressions.length; i++) {
		// if (expressions[i].getChildCount() != 1) {
		// // Translate the case where the child sets are not
		// // singleton
		// Predicate expandedPredicate = Expanders.expandPARTITION(
		// predicate, FormulaFactory.getDefault());
		// smtNode = smtFormula(expandedPredicate);
		// return;
		// }
		// }
		// SMTTerm e0 = smtTerm(predicate.getChildren()[0]);
		//
		// // Translation of special case where all child sets are singleton
		// Set<String> usedNames = new HashSet<String>();
		// List<SMTTerm> newVars = new ArrayList<SMTTerm>();
		// for (int i = 1; i < expressions.length; i++) {
		// SMTTerm expTerm = smtTerm(expressions[i]);
		//
		// String x = signature.freshCstName("set", usedNames);
		// usedNames.add(x);
		// newVars.add(addEqualAssumption(x, expressions[i].getType(),
		// expTerm));
		// }
		// addDistinctAssumption(newVars);
		// setNodeWithUnionAssumption(newVars, e0);
	}

	private void setNodeWithUnionAssumption(List<SMTTerm> newVars, SMTTerm e0) {
		// TODO Auto-generated method stub
		addPredefinedMacroInSignature(BUNION, signature);
		SMTTerm unionTerm = sf.makeMacroTerm(getMacroSymbol(BUNION),
				newVars.toArray(new SMTTerm[newVars.size()]));
		SMTTerm[] equalTerms = { e0, unionTerm };

		this.smtNode = makeEqual(equalTerms);
	}

	private SMTTerm addEqualAssumption(String x, Type type, SMTTerm e0) {
		SMTSortSymbol sort = typeMap.get(type);
		if (sort == null) {
			sort = this.translateTypeName(type);
		}

		SMTPredicateSymbol symbol = sf.makeVeriTPredSymbol(x, sort);

		this.signature.addPred(symbol);

		SMTTerm xTerm = sf.makeVeriTConstantTerm(symbol, signature);

		SMTTerm[] args = { xTerm, e0 };
		signature.addAdditionalAssumption(SMTFactory.makeEqual(args));
		return xTerm;
	}

	private void addDistinctAssumption(List<SMTTerm> newVars) {
		signature.addAdditionalAssumption(makeDistinct(newVars
				.toArray(new SMTTerm[newVars.size()])));
	}

	/**
	 * This method translates an Event-B simple predicate into an Extended SMT
	 * node.
	 */
	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		SMTTerm[] children = smtTerms(predicate.getExpression());

		// Creating ismin macro and adding it to the signature
		addPredefinedMacroInSignature(FINITE, signature);

		// Creating the name for the 'p','f' and 'k' variables in SMT-LIB (rule
		// 24)
		String pVarName = signature.freshCstName("finite_p");
		String kVarName = signature.freshCstName("finite_k");
		String fVarName = signature.freshCstName("finite_f");

		// Creating the constant 'p'
		SMTPredicateSymbol pVarSymbol = new SMTPredicateSymbol(pVarName,
				EMPTY_SORT, !PREDEFINED);
		SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(kVarName,
				EMPTY_SORT, Ints.getInt(), false, false);

		Type type = predicate.getExpression().getType();
		SMTSortSymbol expressionSort = typeMap.get(type);
		if (expressionSort == null) {
			expressionSort = translateTypeName(type);
		}
		SMTSortSymbol[] sorts = { expressionSort };

		SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(fVarName, sorts,
				Ints.getInt(), false, false);

		signature.addPred(pVarSymbol);
		signature.addConstant(kVarSymbol);
		signature.addConstant(fVarSymbol);

		// Creating the macro operator 'finite'
		SMTMacroSymbol finiteSymbol = getMacroSymbol(FINITE);

		// Creating the new assumption (finite p t k f) and saving it.
		SMTFormula finiteFormula = new SMTVeritFiniteFormula(finiteSymbol,
				pVarSymbol, fVarSymbol, kVarSymbol, children);

		signature.addAdditionalAssumption(finiteFormula);

		SMTFormula pFormula = SMTFactory.makeAtom(pVarSymbol,
				SMTFactory.EMPTY_TERM, signature);

		smtNode = pFormula;
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException(
				"It's not possible yet to translate extended expressionto SMT-LIB yet");

	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
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
			ITypeEnvironment typeEnvironment, String solver) {
		SMTThroughVeriT translator = new SMTThroughVeriT(solver);
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
	public void setSignature(SMTSignatureVerit signature) {
		this.signature = signature;
	}
}
