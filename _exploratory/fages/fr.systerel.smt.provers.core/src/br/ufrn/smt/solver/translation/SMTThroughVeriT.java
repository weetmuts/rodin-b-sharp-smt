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

import static fr.systerel.smt.provers.ast.SMTMacros.PAIR_SORT;

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

import fr.systerel.smt.provers.ast.SMTAtom;
import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTEnumMacro;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTLIBUnderlyingLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.SMTMacros;
import fr.systerel.smt.provers.ast.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSetComprehensionMacro;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignatureVerit;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTTheory.VeritPredefinedTheory;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.ast.SMTVarSymbol;
import fr.systerel.smt.provers.ast.SMTVeritCardFormula;
import fr.systerel.smt.provers.ast.SMTVeritFiniteFormula;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * 
 */
// FIXME this class must be entirely refactored
public class SMTThroughVeriT extends TranslatorV1_2 {
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
			final Predicate goal) throws TranslationException {
		SMTBenchmark smtB = new SMTThroughVeriT().translate(lemmaName,
				hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			final Predicate predicate) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		translator.translateSignature(logic, new ArrayList<Predicate>(0),
				predicate);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

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
	 * @param varName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @return the translated {@link SMTSortSymbol} of one of the types of a
	 *         CartesianProduct Type.
	 * 
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

	private SMTSortSymbol makeSort(Type varType) {
		if (varType.getSource() != null) {

			SMTSortSymbol sortSymbol = parsePairTypes(varType.getSource(),
					varType.getTarget());
			typeMap.put(varType, sortSymbol);
			return sortSymbol;

		} else if (varType.getBaseType() != null) {

			if (varType.getBaseType().getSource() != null
					|| varType.getBaseType().getBaseType() != null) {
				throw new IllegalArgumentException("Type " + varType.toString()
						+ ": Sets of sets are not supported yet");
			} else {
				SMTSortSymbol sortSymbol = SMTFactory.makeVeriTSortSymbol(
						varType.getBaseType().toString(), signature);
				this.signature.addSort(sortSymbol);
				typeMap.put(varType, sortSymbol);
				return sortSymbol;
			}

		} else {
			SMTSortSymbol smtSortSymbol = SMTFactory.makeVeriTSortSymbol(
					varType.toString(), signature);
			this.signature.addSort(smtSortSymbol);
			typeMap.put(varType, smtSortSymbol);
			return smtSortSymbol;
		}
	}

	public void addPairSortAndFunctions() {
		this.signature.addSort(PAIR_SORT);
		SMTSortSymbol[] argSorts = {};
		final String symbolName = "pair 's 't";
		SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(symbolName,
				argSorts, PAIR_SORT, !SMTFunctionSymbol.ASSOCIATIVE,
				!SMTFunctionSymbol.PREDEFINED);

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
			makeSort(varType);
		}
		// Check if it's necessary to add the sort and function pair and if it
		// was not already inserted.
		if (insertPairDecl) {
			addPairSortAndFunctions();
		}

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
				SMTSortSymbol sortSymbol = parsePairTypes(varType.getSource(),
						varType.getTarget());
				SMTSortSymbol[] sorts = { sortSymbol };
				SMTPredicateSymbol predSymbol = new SMTPredicateSymbol(
						freshVarName, sorts, false);
				signature.addPred(predSymbol);
				varMap.put(varName, predSymbol);
				typeMap.put(varType, sortSymbol);
				insertPairDecl = true;
			} else if (varType.getBaseType() != null) {
				parseBaseTypes(varName, varType);
			} else {
				SMTSortSymbol smtSortSymbol = SMTFactory.makeVeriTSortSymbol(
						varType.toString(), signature);
				final SMTFunctionSymbol smtConstant;
				smtConstant = signature.freshConstant(varName, smtSortSymbol);
				this.signature.addSort(smtSortSymbol);
				this.signature.addConstant(smtConstant);
				typeMap.put(varType, smtSortSymbol);
				varMap.put(varName, smtConstant);
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
	protected SMTSymbol translateTypeName(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void clearFormula() {
		signature.getAdditionalAssumptions().clear();
		smtNode = null;
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.INTEGER:
			smtNode = sf.makeVeriTTerm(
					signature.getLogic().getIntegerSortCst(), signature);
			break;
		case Formula.NATURAL:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.NAT,
					signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.NAT));
			break;
		case Formula.NATURAL1:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.NAT1,
					signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.NAT1));
			break;
		case Formula.BOOL:
			smtNode = sf.makeVeriTTerm(signature.getLogic().getBooleanCste(),
					signature);
			break;
		case Formula.EMPTYSET:
			if (expression.getType().getSource() instanceof ProductType
					|| expression.getType().getTarget() instanceof ProductType) {
				throw new IllegalArgumentException("Type: "
						+ expression.getType().toString()
						+ ": power set of power set is not supported yet");
			} else {
				SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.EMPTY,
						signature);

				smtNode = sf.makeMacroTerm(SMTMacros
						.getMacroSymbol(SMTVeriTOperator.EMPTY));
			}
			break;
		case Formula.KPRED:
			/*
			 * TODO Check rule and implement it
			 */
			break;
		case Formula.KSUCC:
			/*
			 * TODO Check rule and implement it
			 */
			break;
		case Formula.KPRJ1_GEN:
			/*
			 * TODO Check rule and implement it
			 */
			break;
		case Formula.KPRJ2_GEN:
			/*
			 * TODO Check rule and implement it
			 */
			break;
		case Formula.KID_GEN:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.ID,
					signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.ID));
			break;
		case Formula.TRUE:
			/**
			 * TODO Check the rules to see how do implement this.
			 */
			// this.smtNode = sf.makePTrue(this.signature); // FIXME Use boolean
			// value when BOOL_SORT theory implemented
			break;
		case Formula.FALSE:
			/**
			 * TODO Check the rules to see how do implement this.
			 */
			// this.smtNode = sf.makePFalse(this.signature); // FIXME Use
			// boolean value when BOOL_SORT theory implemented
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

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
			smtNode = sf.makeVeriTTermOperatorApplication(VeritPredefinedTheory
					.getInstance().getDivision(), children, signature);
			break;
		case Formula.MOD:
			/**
			 * It'`s added the function ((mod Int Int Int)) in the signature
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
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.RANGE_INTEGER, signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RANGE_INTEGER),
					children);
			break;
		case Formula.RANSUB:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.RANGE_SUBSTRACTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.RANGE_SUBSTRACTION),
					children);
			break;

		case Formula.RANRES:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.RANGE_RESTRICTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.RANGE_RESTRICTION),
					children);
			break;

		case Formula.REL:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.RELATION,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RELATION),
					children);
			break;

		case Formula.TREL:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_RELATION, signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_RELATION),
					children);
			break;

		case Formula.SREL:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.SURJECTIVE_RELATION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.SURJECTIVE_RELATION),
					children);
			break;

		case Formula.STREL:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_SURJECTIVE_RELATION, signature);

			smtNode = sf
					.makeMacroTerm(
							SMTMacros
									.getMacroSymbol(SMTVeriTOperator.TOTAL_SURJECTIVE_RELATION),
							children);
			break;

		case Formula.PFUN:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.PARTIAL_FUNCTION, signature);

			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.PARTIAL_FUNCTION),
							children);
			break;

		case Formula.TFUN:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_FUNCTION, signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_FUNCTION),
					children);
			break;
		case Formula.PINJ:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.PARTIAL_INJECTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.PARTIAL_INJECTION),
					children);
			break;

		case Formula.TINJ:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_INJECTION, signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_INJECTION),
					children);
			break;

		case Formula.PSUR:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.PARTIAL_SURJECTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.PARTIAL_SURJECTION),
					children);
			break;

		case Formula.TSUR:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_SURJECTION, signature);

			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.TOTAL_SURJECTION),
							children);
			break;

		case Formula.TBIJ:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.TOTAL_BIJECTION, signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_BIJECTION),
					children);
			break;

		case Formula.SETMINUS:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.SETMINUS,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SETMINUS),
					children);
			break;

		case Formula.CPROD:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.CARTESIAN_PRODUCT, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.CARTESIAN_PRODUCT),
					children);
			break;

		case Formula.DPROD:
			// FIXME There is no implementation for DPROD in Verit SMT-LIB
			break;

		case Formula.PPROD:
			// FIXME There is no implementation for PPROD in Verit SMT-LIB
			break;

		case Formula.DOMRES:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.DOMAIN_RESTRICTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.DOMAIN_RESTRICTION),
					children);
			break;

		case Formula.DOMSUB:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.DOMAIN_SUBSTRACTION, signature);

			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.DOMAIN_SUBSTRACTION),
					children);
			break;

		case Formula.FUNIMAGE:
			// FIXME There is no implementation for FUNIMAGE in Verit SMT-LIB
			break;

		case Formula.RELIMAGE:
			SMTMacros.addPredefinedMacroInSignature(
					SMTVeriTOperator.RELATIONAL_IMAGE, signature);

			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.RELATIONAL_IMAGE),
							children);
			break;

		case Formula.MAPSTO:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.MAPSTO,
					signature);

			smtNode = sf
					.makeMacroTerm(
							SMTMacros.getMacroSymbol(SMTVeriTOperator.MAPSTO),
							children);
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

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

	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String bidName = boundIdentifiers.get(boundIdentifiers.size()
				- expression.getBoundIndex() - 1);
		smtNode = qVarMap.get(bidName);
	}

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
				this.smtNode = sf.makeEqual(children);
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
				SMTMacros.addPredefinedMacroInSignature(
						SMTVeriTOperator.NOT_EQUAL, signature);

				this.smtNode = sf.makeMacroAtom(
						SMTMacros.getMacroSymbol(SMTVeriTOperator.NOT_EQUAL),
						children, signature);
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
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.IN,
					signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeVeriTMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.IN), children,
					signature);
			break;
		}

		case Formula.SUBSET: {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.SUBSET,
					signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSET),
					children, signature);
			break;
		}
		case Formula.SUBSETEQ: {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.SUBSETEQ,
					signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSETEQ),
					children, signature);
			break;
		}
		case Formula.NOTSUBSET: {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.SUBSET,
					signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSET),
					children, signature);
			SMTFormula[] subsetFormula = { (SMTFormula) smtNode };
			smtNode = sf.makeNot(subsetFormula);
			break;
		}
		case Formula.NOTSUBSETEQ: {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.SUBSETEQ,
					signature);

			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSETEQ),
					children, signature);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		// TODO
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		// TODO Auto-generated method stub
	}

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
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.BUNION,
					signature);

			smtNode = sf
					.makeMacroTerm(
							SMTMacros.getMacroSymbol(SMTVeriTOperator.BUNION),
							children);
			break;
		case Formula.BINTER:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.BINTER,
					signature);

			smtNode = sf
					.makeMacroTerm(
							SMTMacros.getMacroSymbol(SMTVeriTOperator.BINTER),
							children);
			break;
		case Formula.FCOMP:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.FCOMP,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.FCOMP), children);
			break;
		case Formula.BCOMP:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.BCOMP,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.BCOMP), children);
			break;
		case Formula.OVR:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.OVR,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.OVR), children);
			break;
		default:
			throw new IllegalTagException(tag);
		}

	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		switch (expression.getTag()) {
		case Formula.CSET:
			this.printPointInQuantifiedOperator = true;

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
				expressionSymbol = makeSort(expression.getType());
			}

			// obtaining fresh name for the variables
			String macroName = signature.freshCstName(SMTMacroSymbol.CSET);
			String lambdaName = signature.freshCstName(SMTMacroSymbol.ELEM);

			SMTVarSymbol lambdaVar = new SMTVarSymbol(lambdaName,
					expressionSymbol, false);

			// Creating the macro
			SMTSetComprehensionMacro macro = SMTMacros
					.makeSetComprehensionMacro(macroName, termChildren,
							lambdaVar, formulaChild, expressionTerm[0],
							signature);

			this.signature.addMacro(macro);
			SMTMacroSymbol macroSymbol = SMTMacros.makeMacroSymbol(macroName);
			this.smtNode = sf.makeMacroTerm(macroSymbol);
			this.printPointInQuantifiedOperator = false;
			break;
		default:
			throw new IllegalArgumentException(
					"It's not possible yet to translate quantified union and quantified interesection to SMT-LIB");
		}
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		SMTTerm[] children = {};
		if (expression.getChildCount() == 0) {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.EMPTY,
					signature);
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.EMPTY), children);
		} else {
			children = smtTerms(expression.getMembers());
			String macroName = signature.freshCstName(SMTMacroSymbol.ENUM);
			String varName1 = signature.freshCstName(SMTMacroSymbol.ELEM);
			if (expression.getMembers()[0].getType() instanceof ProductType) {

				String varName2 = signature.freshCstName(SMTMacroSymbol.ELEM);
				SMTSortSymbol sortSymbol1 = makeSort(expression.getType()
						.getSource());
				SMTSortSymbol sortSymbol2 = makeSort(expression.getType()
						.getTarget());

				SMTVarSymbol var1 = new SMTVarSymbol(varName1, sortSymbol1,
						false);
				SMTVarSymbol var2 = new SMTVarSymbol(varName2, sortSymbol2,
						false);

				SMTPairEnumMacro macro = SMTMacros.makePairEnumerationMacro(
						macroName, var1, var2, children, signature);
				this.signature.addMacro(macro);
			} else {
				SMTSortSymbol sortSymbol = makeSort(expression.getType());
				SMTVarSymbol var = new SMTVarSymbol(varName1, sortSymbol, false);

				SMTEnumMacro macro = SMTMacros.makeEnumMacro(macroName, var,
						children);
				this.signature.addMacro(macro);
			}
			SMTMacroSymbol symbol = SMTMacros.makeMacroSymbol(macroName);
			smtNode = sf.makeMacroTerm(symbol);
		}
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.KCARD: {

			// Creating card macro and to the signature
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.CARD,
					signature);

			// Creating the name for the 'f' and 'k' variables in SMT-LIB (rule
			// 25)
			String kVarName = signature.freshCstName("card_k");
			String fVarName = signature.freshCstName("card_f");

			SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(kVarName,
					SMTMacros.EMPTY_SORT, Ints.getInt(), false, false);

			Type type = expression.getChild().getType();
			SMTSortSymbol expressionSort = typeMap.get(type);
			if (expressionSort == null) {
				expressionSort = makeSort(type);
			}
			SMTSortSymbol[] sorts = { expressionSort };

			SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(fVarName,
					sorts, Ints.getInt(), false, false);

			signature.addConstant(kVarSymbol);
			signature.addConstant(fVarSymbol);

			// Creating the macro operator 'finite'
			SMTMacroSymbol cardSymbol = SMTMacros
					.getMacroSymbol(SMTVeriTOperator.CARD);

			// Creating the new assumption (card p t k f) and saving it.
			SMTFormula cardFormula = new SMTVeritCardFormula(cardSymbol,
					fVarSymbol, kVarSymbol, children);

			signature.addAdditionalAssumption(cardFormula);

			SMTTerm kTerm = sf.makeVeriTTerm(kVarSymbol, signature);

			smtNode = kTerm;

			break;
		}
		case Formula.POW: {
			// TODO
			break;
		}
		case Formula.POW1: {
			// TODO
			break;
		}
		case Formula.KUNION: {
			// TODO
			break;
		}
		case Formula.KINTER: {
			// TODO
			break;
		}
		case Formula.KDOM:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.DOM,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.DOM), children);
			break;
		case Formula.KRAN: {
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.RANGE,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RANGE), children);
			break;
		}
		case Formula.KMIN: {

			// Creating ismin macro and add it to the signature
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.ISMIN,
					signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			String mVarName = signature.freshCstName("ismin_var");

			// Creating the constant 'm'
			SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(mVarName,
					SMTMacros.EMPTY_SORT, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismin'
			SMTMacroSymbol isMinSymbol = SMTMacros
					.getMacroSymbol(SMTVeriTOperator.ISMIN);

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
			SMTFormula isMinFormula = new SMTAtom(isMinSymbol, minChildrenTerms);
			signature.addAdditionalAssumption(isMinFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.KMAX: {

			// Creating ismax macro and adding it to the signature
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.ISMAX,
					signature);

			// Creating the name for the 'm' variable in SMT-LIB (rule 22)
			String mVarName = signature.freshCstName("ismax_var");

			// Creating the constant 'm'
			SMTFunctionSymbol mVarSymbol = new SMTFunctionSymbol(mVarName,
					SMTMacros.EMPTY_SORT, Ints.getInt(), false, false);
			signature.addConstant(mVarSymbol);

			// Creating the macro operator 'ismax'
			SMTMacroSymbol isMaxSymbol = SMTMacros
					.getMacroSymbol(SMTVeriTOperator.ISMAX);

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
			SMTFormula isMaxFormula = new SMTAtom(isMaxSymbol, maxChildrenTerms);
			signature.addAdditionalAssumption(isMaxFormula);

			smtNode = mVarTerm;

			break;
		}
		case Formula.CONVERSE:
			SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.INV,
					signature);

			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.INV), children);
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

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		smtNode = sf.makeVeriTTerm(varMap.get(identifierExpression.getName()),
				this.signature);
	}

	/**
	 * This method translates an Event-B literal predicate into an SMT node.
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
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		SMTTerm[] children = smtTerms(predicate.getExpression());

		// Creating ismin macro and adding it to the signature
		SMTMacros.addPredefinedMacroInSignature(SMTVeriTOperator.FINITE,
				signature);

		// Creating the name for the 'p','f' and 'k' variables in SMT-LIB (rule
		// 24)
		String pVarName = signature.freshCstName("finite_p");
		String kVarName = signature.freshCstName("finite_k");
		String fVarName = signature.freshCstName("finite_f");

		// Creating the constant 'p'
		SMTPredicateSymbol pVarSymbol = new SMTPredicateSymbol(pVarName,
				SMTMacros.EMPTY_SORT);
		SMTFunctionSymbol kVarSymbol = new SMTFunctionSymbol(kVarName,
				SMTMacros.EMPTY_SORT, Ints.getInt(), false, false);

		Type type = predicate.getExpression().getType();
		SMTSortSymbol expressionSort = typeMap.get(type);
		if (expressionSort == null) {
			expressionSort = makeSort(type);
		}
		SMTSortSymbol[] sorts = { expressionSort };

		SMTFunctionSymbol fVarSymbol = new SMTFunctionSymbol(fVarName, sorts,
				Ints.getInt(), false, false);

		signature.addPred(pVarSymbol);
		signature.addConstant(kVarSymbol);
		signature.addConstant(fVarSymbol);

		// Creating the macro operator 'finite'
		SMTMacroSymbol finiteSymbol = SMTMacros
				.getMacroSymbol(SMTVeriTOperator.FINITE);

		// Creating the new assumption (finite p t k f) and saving it.
		SMTFormula finiteFormula = new SMTVeritFiniteFormula(finiteSymbol,
				pVarSymbol, fVarSymbol, kVarSymbol, children);

		signature.addAdditionalAssumption(finiteFormula);

		SMTFormula pFormula = sf.makeAtom(pVarSymbol, SMTFactory.EMPTY_TERM,
				signature);

		smtNode = pFormula;
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		// TODO Auto-generated method stub

	}

	/**
	 * Just for tests.
	 * 
	 * @param typeEnvironment
	 * 
	 */
	public static SMTSignature translateSMTSignature(
			ITypeEnvironment typeEnvironment) {
		SMTThroughVeriT translator = new SMTThroughVeriT();
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
