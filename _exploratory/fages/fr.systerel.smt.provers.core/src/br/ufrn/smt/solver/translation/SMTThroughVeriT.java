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
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
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
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.QuantifiedUtil;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTLIBUnderlyingLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTMacros;
import fr.systerel.smt.provers.ast.SMTPairSortSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignatureVerit;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
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

	/* The Bound identifier list. */
	private ArrayList<String> boundIdentifers;

	/* The list of names already used (Free identifiers + others) list. */
	private ArrayList<String> freeIdentifiers;

	public static SMTFormula translate(Predicate predicate,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/*
	 * This method translates the given predicate into an SMT Node.
	 */
	public static IdentifiersAndSMTStorage translate1(Predicate predicate,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		final SMTThroughVeriT translator = new SMTThroughVeriT();
		predicate.accept(translator);
		IdentifiersAndSMTStorage iSMT = new IdentifiersAndSMTStorage(
				translator.getSMTFormula(), translator.getBoundIdentifers(),
				translator.getFreeIdentifiers());
		return iSMT;
	}

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
			signature.addSort(varName, !SMTFunctionSymbol.PREDEFINED);
		} else if (varType.getBaseType().getSource() != null
				|| varType.getBaseType().getBaseType() != null) {
			throw new IllegalArgumentException("Variable: " + varName
					+ ", Type " + varType.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol(varType
					.getBaseType().toString());
			this.signature.addSort(sortSymbol.toString(),
					!SMTFunctionSymbol.PREDEFINED);
			this.signature.addPred(varName, sortSymbol);
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
	 * @param varName The name of the variable
	 * @param type The type of the variable
	 * @return the translated {@link SMTSortSymbol} of one of the types of a CartesianProduct Type.
	 * 
	 * @see #parsePairTypes(String, Type, Type)
	 */
	private SMTSortSymbol parseOneOfPairTypes(String varName, Type type) {
		if (type.getSource() != null) {
			return parsePairTypes(varName, type.getSource(), type.getTarget());
		} else if (type.getBaseType() != null) {
			throw new IllegalArgumentException("Variable: " + varName
					+ ", Type " + type.toString()
					+ ": Sets of sets are not supported yet");
		} else {
			return sf.makeVeriTSortSymbol(type.toString());
		}
	}

	/**
	 * This method translates a CartesianProductType. It translates both the source and the target types.
	 * 
	 * @param varName The name of the variable.
	 * @param sourceType The source type of the variable type.
	 * @param targetType The target type of the variable type.
	 * @return the translated @link {@link SMTPairSortSymbol} of the variable type.
	 */
	private SMTPairSortSymbol parsePairTypes(String varName, Type sourceType,
			Type targetType) {
		SMTSortSymbol sourceSymbol = parseOneOfPairTypes(varName, sourceType);
		SMTSortSymbol targetSymbol = parseOneOfPairTypes(varName, targetType);
		return sf.makePairSortSymbol(varName, sourceSymbol, targetSymbol);
	}

	@Override
	public void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		this.signature = new SMTSignatureVerit(logic);
		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);
		translateSignature(typeEnvironment);
	}

	/**
	 * This method translates the signature.
	 * 
	 * @param typeEnvironment The Event-B Type Environment for the translation.
	 */
	public void translateSignature(ITypeEnvironment typeEnvironment) {
		boolean insertPairDecl = false;

		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final Type varType = iter.getType();

			if (varName.equals(varType.toString())) {
				this.signature.addSort(varType.toString(),
						!SMTSymbol.PREDEFINED);
			}

			else if (varType.getSource() != null) {

				SMTPairSortSymbol sortSymbol = parsePairTypes(varName,
						varType.getSource(), varType.getTarget());
				this.signature.addPairPred(varName, sortSymbol);
				insertPairDecl = true;

			} else if (varType.getBaseType() != null) {
				parseBaseTypes(varName, varType);
			} else {
				SMTSortSymbol smtSortSymbol = sf.makeVeriTSortSymbol(varType
						.toString());
				final SMTFunctionSymbol smtConstant;
				smtConstant = signature.freshConstant(varName, smtSortSymbol);
				this.signature.addSort(smtSortSymbol.toString(),
						!SMTFunctionSymbol.PREDEFINED);
				this.signature.addConstant(smtConstant);
			}
		}
		if (insertPairDecl) {
			this.signature.addSort("(Pair 's 't)", !SMTSymbol.PREDEFINED);
			SMTSortSymbol sortSymbol = sf.makeVeriTSortSymbol("(Pair 's 't)");
			SMTSortSymbol[] argSorts = {};
			SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(
					"pair 's 't", argSorts, sortSymbol,
					!SMTFunctionSymbol.ASSOCIATIVE,
					!SMTFunctionSymbol.PREDEFINED);

			signature.addConstant(functionSymbol);
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

		// FIXME: Just for tests, the SMTFormula shall not be implemented

		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
	}

	/**
	 * This method translates one predicate.
	 * @param predicate The Rodin predicate to be translated.
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
	 * This method parses hypothesis and goal predicates and translates them
	 * into SMT nodes
	 */
	public static SMTBenchmark translate(final String lemmaName,
			final SMTSignature signature, final List<Predicate> hypotheses,
			final Predicate goal) {

		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		HashSet<String> boundIdentifiers = new HashSet<String>();
		HashSet<String> freeIdentifiers = new HashSet<String>();

		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();

		for (Predicate assumption : hypotheses) {
			IdentifiersAndSMTStorage iSMT = translate1(assumption, a, b);

			boundIdentifiers.addAll(iSMT.getBoundIdentifiers());
			freeIdentifiers.addAll(iSMT.getFreeIdentifiers());
			translatedAssumptions.add(iSMT.getSmtFormula());

			a = new ArrayList<String>(boundIdentifiers);
			b = new ArrayList<String>(freeIdentifiers);
		}

		final SMTFormula smtFormula = translate(goal, a, b);
		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
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
	 * @param boundIdentifers
	 */
	public void setBoundIdentifers(ArrayList<String> boundIdentifers) {
		this.boundIdentifers = boundIdentifers;
	}

	/**
	 * Get the free identifiers of the translator
	 * @return
	 */
	public ArrayList<String> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	/**
	 *  Set the free identifiers of the translator
	 * @param freeIdentifiers
	 */
	public void setFreeIdentifiers(ArrayList<String> freeIdentifiers) {
		this.freeIdentifiers = freeIdentifiers;
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.TRUE:
			// this.smtNode = sf.makePTrue(this.signature); // FIXME Use boolean
			// value when BOOL_SORT theory implemented
			break;
		case Formula.FALSE:
			// this.smtNode = sf.makePFalse(this.signature); // FIXME Use
			// boolean value when BOOL_SORT theory implemented
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
			// this.smtNode = sf.makeMinus(children);
			break;
		case Formula.DIV:
			throw new IllegalArgumentException(
					"The operation \'divise\' is not supported yet");
		case Formula.MOD:
			throw new IllegalArgumentException(
					"The operation \'modulo\' is not supported yet");
		case Formula.EXPN:
			throw new IllegalArgumentException(
					"The operation \'exponential\' is not supported yet");
			/*
			 * case Formula.UPTO: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "range", children, false);
			 * break; case Formula.RANSUB: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "rans", children, false);
			 * break; case Formula.RANRES: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "ranr", children, false);
			 * break; case Formula.DOMSUB: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "doms", children, false);
			 * break; case Formula.DOMRES: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "domr", children, false);
			 * break; case Formula.SETMINUS: this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "setminus", children,
			 * false); break; case Formula.MAPSTO: // TO CHANGE this.smtNode =
			 * sf.makeMacroTerm(SMTNode.MACRO_TERM, "pair", children, false);
			 * break;
			 */
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final BoundIdentDecl[] decls = new BoundIdentDecl[1];
		decls[0] = boundIdentDecl;

		// add bound idents identifier in the list, if exists in the list a new
		// identifier is computed
		final Set<String> fidsSet = new HashSet<String>(this.freeIdentifiers);
		final String[] newNames = QuantifiedUtil.resolveIdents(decls, fidsSet);

		if (newNames.length != 1) { // FIXME Why is that?
			throw new IllegalStateException();
		}

		this.freeIdentifiers.add(newNames[0]);
		this.boundIdentifers.add(newNames[0]);

		// FIXME must create a new SMTVarSymbol
		// this.smtNode = sf.makeQuantifiedVariable(newNames[0],
		// boundIdentDecl.getType());
	}

	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String identifier = boundIdentifers.get(boundIdentifers.size()
				- expression.getBoundIndex() - 1);
		// this.smtNode = sf.makeVar(identifier, expression.getType());
	}

	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();

		// add bound idents identifier in the list, if exists in the list a new
		// identifier is computed
		final Set<String> fidsSet = new HashSet<String>(freeIdentifiers);
		final String[] newNames = QuantifiedUtil.resolveIdents(decls, fidsSet);

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

		// remove added bound idents identifier of the list
		for (int i = 0; i < newNames.length; i++) {
			this.boundIdentifers.remove(newNames[i]);
		}
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
		}
		case Formula.NOTEQUAL: {
			// this.smtNode = sf.makeNotEqual(children);
			break;
		}
		case Formula.LT: {

			// this.smtNode = sf.makeLesserThan(children);
			break;
		}
		case Formula.LE: {
			// this.smtNode = sf.makeLesserEqual(children);
			break;
		}
		case Formula.GT: {
			// this.smtNode = sf.makeGreaterThan(children);
			break;
		}
		case Formula.GE: {
			// this.smtNode = sf.makeGreaterEqual(children);
			break;
			// TODO when membership translation implemented
			/*
			 * case Formula.IN: break; case Formula.NOTIN: break;
			 */
		}
		default: {
			/**
			 * SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ cannot be produced by
			 * ppTrans.
			 */
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
		// TODO Auto-generated method stub

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
			this.signature.addMacro(SMTMacros.BUNION_MACRO);
			smtNode = sf.makeMacroTerm(SMTVeriTOperator.BUNION, children);
			break;
		case Formula.BINTER:
			this.signature.addMacro(SMTMacros.BINTER_MACRO);
			smtNode = sf.makeMacroTerm(SMTVeriTOperator.BINTER, children);
			break;
		case Formula.FCOMP:
			this.signature.addMacro(SMTMacros.FCOMP);
			smtNode = sf.makeMacroTerm(SMTVeriTOperator.FCOMP, children);
			break;
		case Formula.OVR:
			this.signature.addMacro(SMTMacros.REL_OVR);
			smtNode = sf.makeMacroTerm(SMTVeriTOperator.OVR, children);
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
	public void visitIntegerLiteral(IntegerLiteral expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		smtNode = sf.makeConstant(
				this.signature.getSMTVariable(identifierExpression.getName()),
				this.signature);
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		// TODO Auto-generated method stub

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
	public void visitUnaryPredicate(UnaryPredicate predicate) {
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
	 * @param signature The signature to be set in the translator.
	 */
	public void setSignature(SMTSignatureVerit signature) {
		this.signature = signature;
	}
}
