/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
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

import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunDecl;
import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.SMTPredDecl;
import fr.systerel.smt.provers.ast.SMTSort;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class translate a formula expressed in Event-B syntax to a formula in
 * SMT-LIB syntax.
 */
public class TranslatorV1_2 extends Translator implements ISimpleVisitor {

	/** The SMT factory. */
	private SMTFactory sf;

	/** The Bound identifier list. */
	private ArrayList<String> boundIdentifers;

	/** The list of names already used (Free identifiers + others) list. */
	private ArrayList<String> freeIdentifiers;

	private SMTNode<?> smtNode;

	public static Benchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {
		final Signature signature = translateSignature(hypotheses, goal);
		final Sequent sequent = translateSequent(signature, hypotheses, goal);
		return new Benchmark(lemmaName, signature, sequent);
	}

	public ArrayList<String> getBoundIdentifers() {
		return boundIdentifers;
	}

	public void setBoundIdentifers(ArrayList<String> boundIdentifers) {
		this.boundIdentifers = boundIdentifers;
	}

	public ArrayList<String> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	public void setFreeIdentifiers(ArrayList<String> freeIdentifiers) {
		this.freeIdentifiers = freeIdentifiers;
	}

	public static Signature translateSignature(
			final List<Predicate> hypotheses, final Predicate goal) {
		return translateSignature("UNKNOWN", hypotheses, goal);
	}

	public static Signature translateSignature(final String logicName,
			final List<Predicate> hypotheses, final Predicate goal) {
		final List<SMTSort> sorts = new ArrayList<SMTSort>();
		final List<SMTPredDecl> preds = new ArrayList<SMTPredDecl>();
		final List<SMTFunDecl> funs = new ArrayList<SMTFunDecl>();
		final HashMap<String, String> singleQuotVars = new HashMap<String, String>();
		boolean insertPairDecl = false;

		final ITypeEnvironment typeEnvironment = Signature
				.extractTypeEnvironment(hypotheses, goal);

		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final Type varType = iter.getType();

			if (varName.contains("\'")) {
				final String freshName = Signature.giveFreshVar(varName,
						typeEnvironment);
				singleQuotVars.put(varName, freshName);
			}

			if (varName.equals(varType.toString())) {
				sorts.add(new SMTSort(varType.toString()));
			}

			// Regra 6
			else if (varType.getSource() != null) {

				if (varType.getSource().getSource() != null
						|| varType.getSource().getBaseType() != null) {
					// TODO: Insert an Error message and abort, cartesian
					// product of cartesian product || cartesian product of
					// power type is not implemeted yet
					System.err
							.println("Cartesian product of cartesian product || Cartesian product of power type is not implemented yet");
				}

				final SMTSort sort1 = new SMTSort(
						getSMTAtomicExpressionFormat(varType.getSource()
								.toString()));
				final SMTSort sort2 = new SMTSort(
						getSMTAtomicExpressionFormat(varType.getTarget()
								.toString()));
				final SMTSort pair = new SMTSort("(Pair " + sort1.toString()
						+ " " + sort2.toString() + ")");
				preds.add(new SMTPredDecl(varName, pair));

				if (!insertPairDecl) {
					sorts.add(new SMTSort("(Pair 's 't)"));
				}
				if (!sorts.contains(sort1)) {
					sorts.add(sort1);
				}
				if (!sorts.contains(sort2)) {
					sorts.add(sort2);
				}
				insertPairDecl = true;

			} else if (varType.getBaseType() != null) {
				if (varName.equals(varType.getBaseType().toString())) {
					sorts.add(new SMTSort(varName));
				} else {
					preds.add(new SMTPredDecl(varName, new SMTSort(
							getSMTAtomicExpressionFormat(varType.getBaseType()
									.toString()))));
				}
			} else {
				funs.add(new SMTFunDecl(varName, new SMTSort(
						getSMTAtomicExpressionFormat(varType.toString()))));
			}
		}
		if (insertPairDecl) {
			funs.add(new SMTFunDecl("pair 's 't", new SMTSort("(Pair 's 't)")));
		}

		return new Signature(logicName, sorts, preds, funs);
	}

	/**
	 * This method parses hypothesis and goal predicates and translates them
	 * into SMT nodes
	 */
	public static Sequent translateSequent(final Signature signature,
			final List<Predicate> hypotheses, final Predicate goal) {
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
		return new Sequent(signature, translatedAssumptions, smtFormula);
	}

	/**
	 * This method translates the given predicate into an SMT Node.
	 */
	public static IdentifiersAndSMTStorage translate1(Predicate predicate,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		final TranslatorV1_2 translator = new TranslatorV1_2(predicate,
				boundIdentifiers, freeIdentifiers);
		predicate.accept(translator);
		IdentifiersAndSMTStorage iSMT = new IdentifiersAndSMTStorage(
				translator.getSMTFormula(), translator.getBoundIdentifers(),
				translator.getFreeIdentifiers());
		return iSMT;
	}

	/**
	 * This method translates the given predicate into an SMT Node.
	 */
	public static SMTFormula translate(Predicate predicate,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		final TranslatorV1_2 translator = new TranslatorV1_2(predicate,
				boundIdentifiers, freeIdentifiers);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	public static SMTFormula translate(Predicate predicate) {
		final TranslatorV1_2 translator = new TranslatorV1_2(predicate);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	// TODO to be moved into the Translator used by veriT approach
	public static String getSMTAtomicExpressionFormat(String atomicExpression) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return "Int";
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else if (atomicExpression.equals("TRUE")) {
			return "true";
		} else if (atomicExpression.equals("FALSE")) {
			return "false";
		} else if (atomicExpression.equals("\u2205")) {
			return "emptyset";
		}
		return atomicExpression;
	}

	/**
	 * This constructor extracts free identifiers from the given predicate
	 */
	private TranslatorV1_2(Predicate predicate) {
		freeIdentifiers = new ArrayList<String>();
		boundIdentifers = new ArrayList<String>();
		this.sf = SMTFactory.getDefault();
		for (FreeIdentifier ident : predicate.getFreeIdentifiers()) {
			this.freeIdentifiers.add(ident.getName());
		}
	}

	TranslatorV1_2(Predicate predicate, ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		this.sf = SMTFactory.getDefault();
		this.boundIdentifers = boundIdentifiers;
		this.freeIdentifiers = freeIdentifiers;
		for (FreeIdentifier ident : predicate.getFreeIdentifiers()) {
			this.freeIdentifiers.add(ident.getName());
		}
	}

	private SMTFormula getSMTFormula() {
		if (this.smtNode instanceof SMTFormula) {
			return (SMTFormula) this.smtNode;
		} else {
			throw new IllegalArgumentException(
					Messages.TranslatorV1_2_translation_error);
		}
	}

	private SMTTerm smtTerm(Formula<?> formula) {
		formula.accept(this);
		if (this.smtNode instanceof SMTTerm) {
			return (SMTTerm) this.smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTTerm'.");
		}
	}

	private SMTFormula smtFormula(Formula<?> formula) {
		formula.accept(this);
		if (this.smtNode instanceof SMTFormula) {
			return (SMTFormula) this.smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTFormula'.");
		}
	}

	private SMTTerm[] smtTerms(Formula<?> left, Formula<?> right) {
		return new SMTTerm[] { smtTerm(left), smtTerm(right) };
	}

	private SMTFormula[] smtFormulas(Formula<?> left, Formula<?> right) {
		return new SMTFormula[] { smtFormula(left), smtFormula(right) };
	}

	private SMTTerm[] smtTerms(Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTTerm[] smtTerms = new SMTTerm[length];
		for (int i = 0; i < length; i++) {
			smtTerms[i] = smtTerm(formulas[i]);
		}
		return smtTerms;
	}

	private SMTFormula[] smtFormulas(Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTFormula[] smtFormulas = new SMTFormula[length];
		for (int i = 0; i < length; i++) {
			smtFormulas[i] = smtFormula(formulas[i]);
		}
		return smtFormulas;
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getChildren());
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			this.smtNode = sf.makeArithmeticTerm(SMTNode.PLUS, children);
			break;
		case Formula.MUL:
			this.smtNode = sf.makeArithmeticTerm(SMTNode.MUL, children);
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
			this.smtNode = sf.makeConnectiveFormula(SMTNode.AND, children);
			break;
		case Formula.LOR:
			this.smtNode = sf.makeConnectiveFormula(SMTNode.OR, children);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.TRUE:
			this.smtNode = sf.makeBoolean(SMTNode.TRUE);
			break;
		case Formula.FALSE:
			this.smtNode = sf.makeBoolean(SMTNode.FALSE);
			break;
		case Formula.INTEGER:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO, "Int", null, false);
			break;
		case Formula.NATURAL:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO, "Nat", null, false);
			break;
		case Formula.NATURAL1:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO, "Nat1", null, false);
			break;
		case Formula.BOOL:
			break;
		case Formula.EMPTYSET:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO, "emptyset", null,
					false);
			break;
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
			this.smtNode = sf.makeArithmeticTerm(SMTNode.MINUS, children);
			break;
		case Formula.DIV:
			this.smtNode = sf.makeArithmeticTerm(SMTNode.DIV, children);
			break;
		case Formula.MOD:
			this.smtNode = sf.makeArithmeticTerm(SMTNode.MODULO, children);
			break;
		case Formula.EXPN:
			throw new IllegalArgumentException(
					"The operation \'exponential\' is not supported yet");
		case Formula.UPTO:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "range",
					children, false);
			break;
		case Formula.RANSUB:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "rans",
					children, false);
			break;
		case Formula.RANRES:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "ranr",
					children, false);
			break;
		case Formula.DOMSUB:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "doms",
					children, false);
			break;
		case Formula.DOMRES:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "domr",
					children, false);
			break;
		case Formula.SETMINUS:
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "setminus",
					children, false);
			break;
		case Formula.MAPSTO:
			// TO CHANGE
			this.smtNode = sf.makeMacroTerm(SMTNode.MACRO_TERM, "pair",
					children, false);
			break;
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
			this.smtNode = sf.makeConnectiveFormula(SMTNode.IMPLIES, children);
			break;
		case Formula.LEQV:
			this.smtNode = sf.makeConnectiveFormula(SMTNode.IFF, children);
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
			this.smtNode = predicate;
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final BoundIdentDecl[] decls = new BoundIdentDecl[1];
		decls[0] = boundIdentDecl;

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		final Set<String> fidsSet = new HashSet<String>(this.freeIdentifiers);
		final String[] newNames = QuantifiedUtil.resolveIdents(decls, fidsSet);

		if (newNames.length != 1) { // FIXME Why is that?
			throw new IllegalStateException();
		}

		this.freeIdentifiers.add(newNames[0]);
		this.boundIdentifers.add(newNames[0]);

		this.smtNode = sf.makeQuantifiedVariable(SMTNode.QUANTIFIED_VARIABLE,
				newNames[0], boundIdentDecl.getType());
	}

	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String identifier = boundIdentifers.get(boundIdentifers.size()
				- expression.getBoundIndex() - 1);
		this.smtNode = sf.makeIdentifier("?" + identifier);
	}

	@Override
	public void visitFreeIdentifier(final FreeIdentifier expression) {
		if (expression.getType() instanceof BooleanType) {
			this.smtNode = sf.makeAtomicFormula(sf.makeIdentifier(expression
					.getName()));
		} else {
			this.smtNode = sf.makeIdentifier(expression.getName());
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
			this.smtNode = sf.makePropAtom(SMTNode.PTRUE);
			break;
		case Formula.BFALSE:
			this.smtNode = sf.makePropAtom(SMTNode.PFALSE);
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
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		final Set<String> fidsSet = new HashSet<String>(freeIdentifiers);
		final String[] newNames = QuantifiedUtil.resolveIdents(decls, fidsSet);

		final SMTTerm[] termChildren = smtTerms(predicate.getBoundIdentDecls());
		final SMTFormula[] formulaChildren = new SMTFormula[] { smtFormula(predicate
				.getPredicate()) };

		switch (predicate.getTag()) {
		case Formula.FORALL:
			this.smtNode = sf.makeQuantifiedPred(SMTNode.FORALL, termChildren,
					formulaChildren);
			break;
		case Formula.EXISTS:
			this.smtNode = sf.makeQuantifiedPred(SMTNode.EXISTS, termChildren,
					formulaChildren);
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
		final int tag = predicate.getTag();
		if (tag == Formula.EQUAL
				&& predicate.getRight().getType() instanceof BooleanType) {
			final SMTFormula[] children = smtFormulas(predicate.getLeft(),
					predicate.getRight());
			this.smtNode = sf.makeConnectiveFormula(SMTNode.IFF, children);
		} else {
			final SMTTerm[] children;
			if (tag == Formula.IN || tag == Formula.NOTIN) {
				children = smtTerms(predicate.getRight(), predicate.getLeft());
			} else {
				children = smtTerms(predicate.getLeft(), predicate.getRight());
			}
			switch (predicate.getTag()) {
			case Formula.NOTEQUAL:
				this.smtNode = sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "=",
						children, true);
				break;
			case Formula.EQUAL:
				// Check Type of equality members
				final Type type = predicate.getLeft().getType();
				if (type instanceof IntegerType) {
					this.smtNode = sf
							.makeAtomicFormula(SMTNode.EQUAL, children);
				} else { // FIXME document this... should be as above in all
							// cases
					this.smtNode = sf.makeMacroFormula(SMTNode.MACRO_FORMULA,
							"=", children, false);
				}
				break;
			case Formula.LT:
				this.smtNode = sf.makeAtomicFormula(SMTNode.LT, children);
				break;
			case Formula.LE:
				this.smtNode = sf.makeAtomicFormula(SMTNode.LE, children);
				break;
			case Formula.GT:
				this.smtNode = sf.makeAtomicFormula(SMTNode.GT, children);
				break;
			case Formula.GE:
				this.smtNode = sf.makeAtomicFormula(SMTNode.GE, children);
				break;
			case Formula.IN:
				this.smtNode = sf.makeMacroFormula(SMTNode.IN, "", children,
						false);
				break;
			case Formula.NOTIN:
				this.smtNode = sf.makeMacroFormula(SMTNode.IN, "", children,
						true);
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
			this.smtNode = sf.makeArithmeticTerm(SMTNode.UNARY_MINUS, children);
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
			this.smtNode = sf.makeConnectiveFormula(SMTNode.NOT, children);
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
}