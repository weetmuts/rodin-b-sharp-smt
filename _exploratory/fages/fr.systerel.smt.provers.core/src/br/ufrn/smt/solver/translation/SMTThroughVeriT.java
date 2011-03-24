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

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTLIBUnderlyingLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.SMTMacros;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignatureVerit;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTTheory.VeritPredefinedTheory;
import fr.systerel.smt.provers.ast.SMTVar;
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

	/* The Bound identifier list. */
	private ArrayList<String> boundIdentifers;

	/* The list of names already used (Free identifiers + others) list. */
	private ArrayList<String> freeIdentifiers;

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
		if (varType.getBaseType().toString().equals(varName)) {
			SMTSortSymbol sort = sf.makeVeriTSortSymbol(varName);
			signature.addSort(sort);
			typeMap.put(varType, sort);
		} else if (varType.getBaseType().getSource() != null
				|| varType.getBaseType().getBaseType() != null) {
			throw new IllegalArgumentException("Variable: " + varName
					+ ", Type " + varType.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol(varType
					.getBaseType().toString());
			this.signature.addSort(sortSymbol);
			typeMap.put(varType, sortSymbol);

			SMTPredicateSymbol predSymbol = sf.makeVeriTPredSymbol(varName,
					sortSymbol);
			this.signature.addPred(predSymbol);
			varMap.put(varName, predSymbol);
		}
	}

	/**
	 * This method translates each type of CartesianProduct Types. It must be
	 * called only by {@link #parsePairTypes(String, Type, Type)}. It applies
	 * the following rules:
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
			return sf.makeVeriTSortSymbol(type.toString());
		}
	}

	/**
	 * This method translates a CartesianProductType. It translates both the
	 * source and the target types.
	 * 
	 * @param varName
	 *            The name of the variable.
	 * @param sourceType
	 *            The source type of the variable type.
	 * @param targetType
	 *            The target type of the variable type.
	 * @return the translated @link {@link SMTPairSortSymbol} of the variable
	 *         type.
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

		translateSignature(bIterator);
	}

	/**
	 * This method extracts types of bound ident declarations and adds them into
	 * the signature
	 * 
	 * @param iter
	 *            The iterator which contains the types of bound ident
	 *            declarations
	 */
	private void translateSignature(Iterator<Type> iter) {
		boolean usingPairs = false;

		while (iter.hasNext()) {
			final Type varType = iter.next();

			if (varType.getSource() != null) {

				SMTSortSymbol sortSymbol = parsePairTypes(varType.getSource(),
						varType.getTarget());
				typeMap.put(varType, sortSymbol);
				usingPairs = true;

			} else if (varType.getBaseType() != null) {

				if (varType.getBaseType().getSource() != null
						|| varType.getBaseType().getBaseType() != null) {
					throw new IllegalArgumentException("Type "
							+ varType.toString()
							+ ": Sets of sets are not supported yet");
				} else {
					SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol(varType
							.getBaseType().toString());
					this.signature.addSort(sortSymbol);
					typeMap.put(varType, sortSymbol);
				}

			} else {
				SMTSortSymbol smtSortSymbol = sf.makeVeriTSortSymbol(varType
						.toString());
				this.signature.addSort(smtSortSymbol);
				typeMap.put(varType, smtSortSymbol);
			}
		}
		if (!insertPairDecl && usingPairs) {
			final String sortSymbolName = "(Pair 's 't)";
			SMTSortSymbol smtSortSymbol = sf
					.makeVeriTSortSymbol(sortSymbolName);
			this.signature.addSort(smtSortSymbol);
			SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol(sortSymbolName);
			SMTSortSymbol[] argSorts = {};
			final String symbolName = "pair 's 't";
			SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(
					symbolName, argSorts, sortSymbol,
					!SMTFunctionSymbol.ASSOCIATIVE,
					!SMTFunctionSymbol.PREDEFINED);

			signature.addConstant(functionSymbol);
			varMap.put(symbolName, functionSymbol);
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
			final Type varType = iter.getType();

			if (varName.equals(varType.toString())) {
				final SMTSortSymbol sort = signature.freshSort(varType
						.toString());
				this.signature.addSort(sort);
				typeMap.put(varType, sort);
			}

			else if (varType.getSource() != null) {

				SMTSortSymbol sortSymbol = parsePairTypes(varType.getSource(),
						varType.getTarget());
				this.signature.addPred(varName, sortSymbol);
				typeMap.put(varType, sortSymbol);
				insertPairDecl = true;

			} else if (varType.getBaseType() != null) {
				parseBaseTypes(varName, varType);
			} else {
				SMTSortSymbol smtSortSymbol = sf.makeVeriTSortSymbol(varType
						.toString());
				final SMTFunctionSymbol smtConstant;
				smtConstant = signature.freshConstant(varName, smtSortSymbol);
				this.signature.addSort(smtSortSymbol);
				this.signature.addConstant(smtConstant);
				typeMap.put(varType, smtSortSymbol);
				varMap.put(varName, smtConstant);
			}
		}
		if (insertPairDecl) {
			final String sortSymbolName = "(Pair 's 't)";
			SMTSortSymbol smtSortSymbol = sf
					.makeVeriTSortSymbol(sortSymbolName);
			this.signature.addSort(smtSortSymbol);
			SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol(sortSymbolName);
			SMTSortSymbol[] argSorts = {};
			final String symbolName = "pair 's 't";
			SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(
					symbolName, argSorts, sortSymbol,
					!SMTFunctionSymbol.ASSOCIATIVE,
					!SMTFunctionSymbol.PREDEFINED);

			signature.addConstant(functionSymbol);
			varMap.put(symbolName, functionSymbol);
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
			translatedAssumptions.add(translate(hypothesis));
		}

		// translates the goal
		clearFormula();
		final SMTFormula smtFormula = translate(goal);

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

	/**
	 * 
	 * @return the bound identifiers saved in the translator.
	 */
	public ArrayList<String> getBoundIdentifers() {
		return boundIdentifers;
	}

	/**
	 * Set the bound identifiers in the translator
	 * 
	 * @param boundIdentifers
	 */
	public void setBoundIdentifers(ArrayList<String> boundIdentifers) {
		this.boundIdentifers = boundIdentifers;
	}

	/**
	 * Get the free identifiers of the translator
	 * 
	 * @return
	 */
	public ArrayList<String> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	/**
	 * Set the free identifiers of the translator
	 * 
	 * @param freeIdentifiers
	 */
	public void setFreeIdentifiers(ArrayList<String> freeIdentifiers) {
		this.freeIdentifiers = freeIdentifiers;
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.INTEGER:
			smtNode = sf.makeVeriTTerm(
					signature.getLogic().getIntegerSortCst(), signature);
		case Formula.NATURAL:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.NAT));
		case Formula.NATURAL1:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.NAT1));
		case Formula.BOOL:
			smtNode = sf.makeVeriTTerm(signature.getLogic().getBooleanCste(),
					signature);
		case Formula.EMPTYSET:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.PARTIAL_FUNCTION));
		case Formula.KPRED:
			/*
			 * TODO Check rule and implement it
			 */
		case Formula.KSUCC:
			/*
			 * TODO Check rule and implement it
			 */
		case Formula.KPRJ1_GEN:
			/*
			 * TODO Check rule and implement it
			 */
		case Formula.KPRJ2_GEN:
			/*
			 * TODO Check rule and implement it
			 */
		case Formula.KID_GEN:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.ID));

		case Formula.TRUE:
			// this.smtNode = sf.makePTrue(this.signature); // FIXME Use boolean
			// value when BOOL_SORT theory implemented

			/**
			 * TODO Check the rules to see how do implement this.
			 */
			break;
		case Formula.FALSE:
			// this.smtNode = sf.makePFalse(this.signature); // FIXME Use
			// boolean value when BOOL_SORT theory implemented

			/**
			 * TODO Check the rules to see how do implement this.
			 */

			break;
		// FIXME Must be put in the SMTSignature
		/*
		 * case Formula.INTEGER: this.smtNode = sf.makeMacroTerm(SMTNode.MACRO,
		 * "Int", null, false); break; case Formula.NATURAL: this.smtNode =
		 * sf.makeMacroTerm(SMTNode.MACRO, "Nat", null, false); break; case
		 * Formula.NATURAL1: this.smtNode = sf.makeMacroTerm(SMTNode.MACRO,
		 * "Nat1", null, false); break; case Formula.BOOL: break; case
		 * Formula.EMPTYSET: this.smtNode = sf.makeMacroTerm(SMTNode.MACRO,
		 * "emptyset", null, false); break;
		 */
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
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RANGE_INTEGER),
					children);
			break;
		case Formula.RANSUB:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.RANGE_SUBSTRACTION),
					children);
			break;

		case Formula.RANRES:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.RANGE_RESTRICTION),
					children);
			break;

		case Formula.REL:
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RELATION),
					children);
			break;

		case Formula.TREL:
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_RELATION),
					children);
			break;

		case Formula.SREL:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.SURJECTIVE_RELATION),
					children);
			break;

		case Formula.STREL:
			smtNode = sf
					.makeMacroTerm(
							SMTMacros
									.getMacroSymbol(SMTVeriTOperator.TOTAL_SURJECTIVE_RELATION),
							children);
			break;

		case Formula.PFUN:
			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.PARTIAL_FUNCTION),
							children);
			break;

		case Formula.TFUN:
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_FUNCTION),
					children);
			break;
		// FIXME
		case Formula.PINJ:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.PARTIAL_INJECTION),
					children);
			break;

		case Formula.TINJ:
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_INJECTION),
					children);
			break;

		case Formula.PSUR:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.PARTIAL_SURJECTION),
					children);
			break;

		case Formula.TSUR:
			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.TOTAL_SURJECTION),
							children);
			break;

		case Formula.TBIJ:
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.TOTAL_BIJECTION),
					children);
			break;

		case Formula.SETMINUS:
			this.signature
					.addMacro(SMTMacroSymbol.SETMINUS, SMTMacros.SETMINUS);
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SETMINUS),
					children);
			break;

		case Formula.CPROD:
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
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.DOMAIN_RESTRICTION),
					children);
			break;

		case Formula.DOMSUB:
			smtNode = sf.makeMacroTerm(SMTMacros
					.getMacroSymbol(SMTVeriTOperator.DOMAIN_SUBSTRACTION),
					children);
			break;

		case Formula.FUNIMAGE:
			// FIXME There is no implementation for FUNIMAGE in Verit SMT-LIB
			break;

		case Formula.RELIMAGE:
			smtNode = sf
					.makeMacroTerm(SMTMacros
							.getMacroSymbol(SMTVeriTOperator.RELATIONAL_IMAGE),
							children);
			break;

		case Formula.MAPSTO:
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
			this.smtNode = sf.makeForAll(termChildren, formulaChild);
			break;
		case Formula.EXISTS:
			this.smtNode = sf.makeExists(termChildren, formulaChild);
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
				this.smtNode = sf.makeNotEqual(children);
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
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeVeriTMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.IN), children,
					signature);
			break;
		}

		case Formula.SUBSET: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSET),
					children, signature);
			break;
		}
		case Formula.SUBSETEQ: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeMacroAtom(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.SUBSETEQ),
					children, signature);
			break;
		}
		case Formula.NOTSUBSET: {
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
			this.signature.addMacro(SMTMacroSymbol.BUNION,
					SMTMacros.BUNION_MACRO);
			smtNode = sf
					.makeMacroTerm(
							SMTMacros.getMacroSymbol(SMTVeriTOperator.BUNION),
							children);
			break;
		case Formula.BINTER:
			this.signature.addMacro(SMTMacroSymbol.BINTER,
					SMTMacros.BINTER_MACRO);
			smtNode = sf
					.makeMacroTerm(
							SMTMacros.getMacroSymbol(SMTVeriTOperator.BINTER),
							children);
			break;
		case Formula.FCOMP:
			this.signature
					.addMacro(SMTMacroSymbol.FCOMP, SMTMacros.FCOMP_MACRO);
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.FCOMP), children);
			break;
		case Formula.OVR:
			this.signature
					.addMacro(SMTMacroSymbol.OVR, SMTMacros.REL_OVR_MACRO);
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.OVR), children);
			break;
		default:
			/**
			 * , BCOMP tag cannot be produced by VeriT pre-processing.
			 */
			throw new IllegalTagException(tag);
		}

	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		SMTTerm[] children = {};
		if (expression.getChildCount() == 0) {
			if (expression.getType() instanceof ProductType) {
				this.signature.addMacro(SMTMacroSymbol.EMPTY_PAIR,
						SMTMacros.EMPTYSET_PAIR_MACRO);
				smtNode = sf.makeMacroTerm(
						SMTMacros.getMacroSymbol(SMTVeriTOperator.EMPTY_PAIR),
						children);
			} else {
				this.signature.addMacro(SMTMacroSymbol.EMPTY,
						SMTMacros.EMPTYSET_MACRO);
				smtNode = sf.makeMacroTerm(
						SMTMacros.getMacroSymbol(SMTVeriTOperator.EMPTY),
						children);
			}
		} else {
			children = smtTerms(expression.getMembers());
			SMTMacros.addEnumerationMacroInSignature(signature, children);
		}
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.KCARD: {
			// TODO
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
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.DOM), children);
			break;
		case Formula.KRAN: {
			smtNode = sf.makeMacroTerm(
					SMTMacros.getMacroSymbol(SMTVeriTOperator.RANGE_INTEGER),
					children);
			break;
		}
		case Formula.KMIN: {
			// TODO
			break;
		}
		case Formula.KMAX: {
			// TODO
			break;
		}
		case Formula.CONVERSE:
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
		smtNode = sf.makeVeriTTerm(
				this.signature.getSMTSymbol(identifierExpression.getName()),
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
		// TODO Auto-generated method stub

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
	 * @return
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
	public SMTSignatureVerit getSignature() {
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
