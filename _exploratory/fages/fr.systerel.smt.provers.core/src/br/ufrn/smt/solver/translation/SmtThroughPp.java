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
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSignaturePP;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class does the SMT translation through ppTrans. ppTrans is called first,
 * to reduce an Event-B sequent to Predicate Calculus. Then the SMT translation
 * is done.
 */
public class SmtThroughPp extends TranslatorV1_2 {

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
		return new SmtThroughPp().translate(lemmaName, hypotheses, goal);
	}

	@Override
	/**
	 * This is the translation method for the ppTrans approach of SMT translation.
	 */
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {

		// pp translation
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		// smt translation
		final SMTSignature signature = translateSignature(
				ppTranslatedHypotheses, ppTranslatedGoal);
		final SMTBenchmark benchmark = translate(lemmaName, signature,
				ppTranslatedHypotheses, ppTranslatedGoal);

		return benchmark;
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

	/**
	 * This translating method
	 */
	protected static SMTBenchmark translate(final String lemmaName,
			final SMTSignature signature, final List<Predicate> hypotheses,
			final Predicate goal) {
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		final SMTFormula smtFormula = translate(goal);
		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
	}

	/**
	 * This method translates the given predicate into an SMT Formula.
	 */
	public static SMTFormula translate(Predicate predicate) {
		final TranslatorV1_2 translator = new SmtThroughPp();
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/**
	 * This methods builds the SMT-LIB signature of a sequent given as its set
	 * of hypotheses and its goal. The method also fill the variables typeMap
	 * and varMap.
	 * 
	 * @param logicName
	 *            the SMT-LIB logic
	 * @param hypotheses
	 *            the set of hypotheses of the sequent
	 * @param goal
	 *            the goal of the sequent
	 * @return the SMT-LIB signature of the sequent
	 */
	// FIXME Use the given logic, notably to take account of the sorts defined
	// by this logic
	private SMTSignature translateSignature(final String logicName,
			final List<Predicate> hypotheses, final Predicate goal) {
		final SMTSignature signature = new SMTSignaturePP(logicName);
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
			 * gets a fresh name for the variable to be typed, adds it to the
			 * variable names mapping (and adds it to the signature symbols set)
			 */
			final String smtVarName;
			if (!varMap.containsKey(varName)) {
				smtVarName = signature.freshVar(varName);
				this.varMap.put(varName, smtVarName);
			} else {
				smtVarName = this.varMap.get(varName);
			}

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = this.translateTypeName(signature, varType);
				this.typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = this.typeMap.get(varType);
			}

			/**
			 * do the same for each base type used to define this type into an
			 * SMT-LIB sort
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = this.translateTypeName(
							signature, baseType);
					this.typeMap.put(baseType, baseSort);
					signature.addConstant(smtVarName, baseSort);
				}
			}

			/**
			 * adds the typing item (<code>x ⦂ S</code>) to the signature as a
			 * constant (<code>extrafuns</code> SMT-LIB section, with a sort but
			 * no argument: <code>(x S)</code>).
			 */
			signature.addConstant(smtVarName, smtSortSymbol);
		}

		return signature;
	}

	private static Set<Type> getBaseTypes(Set<Type> baseTypes, final Type type) {
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null) {
			baseTypes.add(type);
			return baseTypes;
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Adds any base type
		 * among source and target types, and calls recursively
		 * <code>getBaseType</code> on types that are not base types.
		 */
		else if (type.getSource() != null) {
			final Type sourceType = type.getSource();
			final Type targetType = type.getTarget();

			/**
			 * Neither the source type nor the target type are base types.
			 */
			if (sourceType.getBaseType() != null
					&& targetType.getBaseType() != null) {
				return getBaseTypes(
						getBaseTypes(baseTypes, sourceType.getBaseType()),
						targetType.getBaseType());
			}
			/**
			 * The target type is a base type.
			 */
			else if (sourceType.getBaseType() != null) {
				baseTypes.add(targetType);
				return getBaseTypes(baseTypes, sourceType.getBaseType());
			}
			/**
			 * The source type is a base type.
			 */
			else {
				baseTypes.add(sourceType);
				return getBaseTypes(baseTypes, targetType.getBaseType());
			}
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseType</code> on alpha.
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
	public SMTSignature translateSignature(final List<Predicate> hypotheses,
			final Predicate goal) {
		return translateSignature("UNKNOWN", hypotheses, goal);
	}

	@Override
	protected SMTSortSymbol translateTypeName(SMTSignature signature, Type type) {
		return signature.freshSort("MS");
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
		if (expression.getType() instanceof BooleanType) {
			this.smtNode = sf.makePropAtomIdentifier(expression.getName());
		} else {
			this.smtNode = sf.makeConstantIdentifier(expression.getName());
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
			final SMTTerm[] children;
			// TODO When membership translation implemented
			// if (tag == Formula.IN || tag == Formula.NOTIN) {
			// children = smtTerms(predicate.getRight(), predicate.getLeft());
			// } else {
			children = smtTerms(predicate.getLeft(), predicate.getRight());
			// }
			switch (predicate.getTag()) {
			case Formula.NOTEQUAL:
				this.smtNode = sf.makeNotEqual(children);
				break;
			case Formula.EQUAL:
				// Check Type of equality members
				// final Type type = predicate.getLeft().getType();

				// FIXME is this correct? why was this done?
				// if (type instanceof IntegerType) {
				this.smtNode = sf.makeEqual(children);
				// } else { // FIXME document this... should be as above in all
				// cases
				// this.smtNode = sf.makeMacroFormula(SMTNode.MACRO_FORMULA,
				// "=", children, false);
				// }
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
			// TODO when membership translation implemented
			/*
			 * case Formula.IN: break; case Formula.NOTIN: break;
			 */
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
