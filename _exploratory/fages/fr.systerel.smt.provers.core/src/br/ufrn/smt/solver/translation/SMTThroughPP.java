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
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignaturePP;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class does the SMT translation through ppTrans. ppTrans is called first,
 * to reduce an Event-B sequent to Predicate Calculus. Then the SMT translation
 * is done.
 */
public class SMTThroughPP extends TranslatorV1_2 {
	/**
	 * An instance of <code>SMTThroughPP</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	protected SMTSignaturePP signature;

	public SMTThroughPP() {
		super();
	}

	public SMTThroughPP(final SMTSignaturePP signature) {
		this.signature = signature;
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
	 * @return the SMT-LIB benchmark built from the translation of the given
	 *         Event-B sequent
	 */
	public static SMTBenchmark translateToSmtLibBenchmark(
			final String lemmaName, final List<Predicate> hypotheses,
			final Predicate goal) {
		return new SMTThroughPP().translate(lemmaName, hypotheses, goal);
	}

	@Override
	/**
	 * This is the translation method for the ppTrans approach of SMT translation.
	 */
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {

		/**
		 * PP translation
		 */
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		/**
		 * SMT translation
		 */
		// translates the signature
		this.translateSignature(ppTranslatedHypotheses, ppTranslatedGoal);

		// translates each hypothesis
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		for (Predicate hypothesis : hypotheses) {
			this.clearFormula();
			translatedAssumptions.add(this.translate(hypothesis));
		}

		// translates the goal
		this.clearFormula();
		final SMTFormula smtFormula = this.translate(goal);

		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
	}

	/**
	 * This is the pp translation method.
	 */
	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to predicate calculus
		 */
		ppProof.translate();

		return ppProof;
	}

	public static SMTFormula translate(final SMTSignaturePP signature,
			final Predicate predicate) {
		final SMTThroughPP translator = new SMTThroughPP(signature);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/**
	 * This method translates the given predicate into an SMT Formula.
	 */
	private SMTFormula translate(final Predicate predicate) {
		predicate.accept(this);
		return this.getSMTFormula();
	}

	/**
	 * This method builds the SMT-LIB signature of a sequent given as its set of
	 * hypotheses and its goal. The method also fill the variables typeMap and
	 * varMap.
	 * 
	 * @param logicName
	 *            the SMT-LIB logic
	 * @param hypotheses
	 *            the set of hypotheses of the sequent
	 * @param goal
	 *            the goal of the sequent
	 */
	// FIXME Use the given logic, notably to take account of the symbols defined
	// by this logic
	private void translateSignature(final String logicName,
			final List<Predicate> hypotheses, final Predicate goal) {
		this.signature = new SMTSignaturePP(logicName);
		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);

		/**
		 * For each membership of the type environment,
		 */
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final Type varType = iter.getType();

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = this.translateTypeName(varType);
				this.typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = this.typeMap.get(varType);
			}

			/**
			 * gets a fresh name for the variable to be typed, adds it to the
			 * variable names mapping (and adds it to the signature symbols set)
			 */
			final String smtVarName;
			if (!varMap.containsKey(varName)) {
				smtVarName = this.signature.freshCstName(varName);
				this.varMap.put(varName, smtVarName);
			} else {
				smtVarName = this.varMap.get(varName);
			}

			/**
			 * do the same for each base type used to define this type into an
			 * SMT-LIB sort
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = this
							.translateTypeName(baseType);
					this.typeMap.put(baseType, baseSort);
				}
			}

			/**
			 * adds the typing item (<code>x ⦂ S</code>) to the signature as a
			 * constant (<code>extrafuns</code> SMT-LIB section, with a sort but
			 * no argument: <code>(x S)</code>).
			 */
			this.signature.addConstant(smtVarName, smtSortSymbol);
		}
	}

	private static Set<Type> getBaseTypes(Set<Type> baseTypes, final Type type) {
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
			ProductType product = (ProductType) type;
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
			// FIXME Check that this case really should not be reached, if it is
			// right, delete this case, otherwise, add an exception or implement
			// this case.
			return null;
		}
	}

	@Override
	public void translateSignature(final List<Predicate> hypotheses,
			final Predicate goal) {
		this.translateSignature(SMTLogic.UNKNOWN, hypotheses, goal);
	}

	@Override
	protected SMTSortSymbol translateTypeName(Type type) {
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null) {
			return this.signature.freshSort(type.toString());
		}
		return this.signature.freshSort();
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getChildren());
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			this.smtNode = sf.makePlus(children);
			break;
		case Formula.MUL:
			this.smtNode = sf.makeMul(children);
			break;
		default:
			/**
			 * BUNION, BINTER, BCOMP, FCOMP and OVR tags cannot be produced by
			 * ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getChildren());
		switch (predicate.getTag()) {
		case Formula.LAND:
			this.smtNode = sf.makeAnd(children);
			break;
		case Formula.LOR:
			this.smtNode = sf.makeOr(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.TRUE:
			this.smtNode = sf.makePTrue(); // FIXME Use boolean value when BOOL
											// theory implemented
			break;
		case Formula.FALSE:
			this.smtNode = sf.makePFalse();
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
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"'becomes equal to' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"'becomes member of' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"'becomes such that' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getLeft(),
				expression.getRight());
		switch (expression.getTag()) {
		case Formula.MINUS:
			this.smtNode = sf.makeMinus(children);
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
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		final SMTFormula[] children = smtFormulas(predicate.getLeft(),
				predicate.getRight());
		switch (predicate.getTag()) {
		case Formula.LIMP:
			this.smtNode = sf.makeImplies(children);
			break;
		case Formula.LEQV:
			this.smtNode = sf.makeIff(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		final SMTFormula predicate = smtFormula(expression.getPredicate());
		switch (expression.getTag()) {
		case Formula.KBOOL:
			this.smtNode = predicate; // FIXME Is that right?
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitFreeIdentifier(final FreeIdentifier expression) {
		final String smtName = this.varMap.get(expression.getName());
		if (expression.getType() instanceof BooleanType) {
			final SMTPredicateSymbol predSymbol = this.signature
					.getPredicateSymbol(smtName, SMTFactory.EMPTY_SORT);
			this.smtNode = sf.makePropAtom(predSymbol);
		} else {
			final SMTFunctionSymbol funSymbol = this.signature
					.getFunctionSymbol(smtName, SMTFactory.EMPTY_SORT,
							this.typeMap.get(expression.getType()));
			this.smtNode = sf.makeConstant(funSymbol);
		}
	}

	@Override
	public void visitIntegerLiteral(final IntegerLiteral expression) {
		this.smtNode = sf.makeNumeral(expression.getValue());
	}

	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			this.smtNode = sf.makePTrue();
			break;
		case Formula.BFALSE:
			this.smtNode = sf.makePFalse();
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		final int tag = predicate.getTag();
		if (tag == Formula.EQUAL
				&& predicate.getRight().getType() instanceof BooleanType) {
			final SMTFormula[] children = smtFormulas(predicate.getLeft(),
					predicate.getRight());
			this.smtNode = sf.makeIff(children);
		} else {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			switch (tag) {
			case Formula.NOTEQUAL:
				this.smtNode = sf.makeNotEqual(children);
				break;
			case Formula.EQUAL:
				this.smtNode = sf.makeEqual(children);
				break;
			case Formula.LT:
				this.smtNode = sf.makeLesserThan(children);
				break;
			case Formula.LE:
				this.smtNode = sf.makeLesserEqual(children);
				break;
			case Formula.GT:
				this.smtNode = sf.makeGreaterThan(children);
				break;
			case Formula.GE:
				this.smtNode = sf.makeGreaterEqual(children);
				break;
			case Formula.IN:
				final SMTSortSymbol[] argSorts = { children[0].getSort(),
						children[1].getSort() }; // FIXME will it be right for
													// the following example
													// <code>a→b→d ∈ s</code>?
				SMTPredicateSymbol predSymbol = this.signature
						.getMembershipPredicateSymbol(argSorts);
				if (predSymbol == null) {
					this.signature.addMembershipPredicateSymbol(argSorts);
				}
				predSymbol = this.signature
						.getMembershipPredicateSymbol(argSorts);
				this.smtNode = sf.makeAtom(predSymbol, children);
				break;
			case Formula.NOTIN:
				break;
			case Formula.MAPSTO:
				//TODO case a ↦ b.
				break;
			default:
				/**
				 * SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ cannot be
				 * produced by ppTrans.
				 */
				throw new IllegalTagException(tag);
			}
		}
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		// TODO
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Simple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			this.smtNode = sf.makeUMinus(children);
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		final SMTFormula[] children = new SMTFormula[] { smtFormula(predicate
				.getChild()) };
		switch (predicate.getTag()) {
		case Formula.NOT:
			this.smtNode = sf.makeNot(children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Multiple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate perdicate) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		// TODO Auto-generated method stub

	}
}
