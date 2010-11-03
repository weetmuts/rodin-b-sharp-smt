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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
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
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class translate a formula expressed in Event-B syntax to a formula in
 * SMT-LIB syntax.
 */
public class TranslatorV1_2 implements ISimpleVisitor {

	/** The built nodes. */
	private Stack<SMTNode<?>> stack;

	/** The SMT factory. */
	private SMTFactory sf;

	/** The Bound identifier list. */
	private ArrayList<String> boundIdentifers;

	/** The list of names already used (Free identifiers + others) list. */
	private ArrayList<String> freeIdentifiers;

	/**
	 * This method translates the given predicate into an SMT Node.
	 */
	public static SMTFormula translate(Predicate predicate) {
		final TranslatorV1_2 translator = new TranslatorV1_2(predicate);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/**
	 * Builds a new visitor.
	 */
	private TranslatorV1_2(ArrayList<String> freeIdentifiers) {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
		this.boundIdentifers = new ArrayList<String>();
		this.freeIdentifiers = freeIdentifiers;
	}

	/**
	 * This constructor extracts free identifiers from the given predicate
	 */
	private TranslatorV1_2(Predicate predicate) {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
		this.boundIdentifers = new ArrayList<String>();
		this.freeIdentifiers = new ArrayList<String>();
		for (FreeIdentifier ident : predicate.getFreeIdentifiers()) {
			this.freeIdentifiers.add(ident.getName());
		}
	}

	/**
	 * Returns the SMT Node on the top of the stack
	 */
	private SMTFormula getSMTFormula() {
		if (stack.size() == 1) {
			final SMTNode<?> node = stack.pop();
			if (node instanceof SMTFormula) {
				return (SMTFormula)node;
			} else {
				throw new IllegalArgumentException(Messages.TranslatorV1_2_translation_error);
			}
		} else {
			throw new IllegalStateException(Messages.TranslatorV1_2_stack_error);
		}
	}

	/**
	 * Converts Event-B formulas in SMT-LIB format.
	 * 
	 * @param formulas
	 *            the formulas to be converted
	 * @return the built SMT node
	 */
	private List<SMTNode<?>> convert(Formula<?>... formulas) {
		for (Formula<?> formula : formulas)
			formula.accept(this);

		List<SMTNode<?>> nodes = new ArrayList<SMTNode<?>>(formulas.length);
		for (int i = 0; i < formulas.length; i++)
			nodes.add(0, stack.pop());

		return nodes;
	}

	private SMTTerm[] toTermArray(List<SMTNode<?>> nodes) {
		return nodes.toArray(new SMTTerm[nodes.size()]);
	}

	private SMTFormula[] toFormulaArray(List<SMTNode<?>> nodes) {
		return nodes.toArray(new SMTFormula[nodes.size()]);
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		SMTTerm[] children = toTermArray(convert(expression.getChildren()));
		switch (expression.getTag()) {
		case Formula.PLUS:
			stack.push(sf.makeArithmeticTerm(SMTNode.PLUS, children));
			break;
		case Formula.MUL:
			stack.push(sf.makeArithmeticTerm(SMTNode.MUL, children));
			break;
		case Formula.BUNION:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO, "union", children,
					false));
			break;
		case Formula.BINTER:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO, "inter", children,
					false));
			break;
		case Formula.BCOMP:
			// TODO
			break;
		case Formula.FCOMP:
			// TODO
			break;
		case Formula.OVR:
			// TODO
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		SMTFormula[] children = toFormulaArray(convert(predicate.getChildren()));
		switch (predicate.getTag()) {
		case Formula.LAND:
			stack.push(sf.makeConnectiveFormula(SMTNode.AND, children));
			break;
		case Formula.LOR:
			stack.push(sf.makeConnectiveFormula(SMTNode.OR, children));
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.TRUE:
			stack.push(sf.makeBoolean(SMTNode.TRUE));
			break;
		case Formula.FALSE:
			stack.push(sf.makeBoolean(SMTNode.FALSE));
			break;
		case Formula.INTEGER:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO, "Int", null, false));
			break;
		case Formula.NATURAL:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO, "Nat", null, false));
			break;
		case Formula.NATURAL1:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO, "Nat1", null, false));
			break;
		case Formula.BOOL:
			break;
		case Formula.EMPTYSET:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO, "emptyset", null, false));
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
		SMTTerm[] children = toTermArray(convert(expression.getLeft(),
				expression.getRight()));
		switch (expression.getTag()) {
		case Formula.MINUS:
			stack.push(sf.makeArithmeticTerm(SMTNode.MINUS, children));
			break;
		case Formula.DIV:
			stack.push(sf.makeArithmeticTerm(SMTNode.DIV, children));
			break;
		case Formula.MOD:
			stack.push(sf.makeArithmeticTerm(SMTNode.MODULO, children));
			break;
		case Formula.EXPN:
			throw new IllegalArgumentException(
					"The operation \'exponential\' is not supported yet");
		case Formula.UPTO:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "range", children,
					false));
			break;
		case Formula.RANSUB:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "rans", children,
					false));
			break;
		case Formula.RANRES:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "ranr", children,
					false));
			break;
		case Formula.DOMSUB:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "doms", children,
					false));
			break;
		case Formula.DOMRES:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "domr", children,
					false));
			break;
		case Formula.SETMINUS:
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "setminus",
					children, false));
			break;
		case Formula.MAPSTO:
			// TO CHANGE
			stack.push(sf.makeMacroTerm(SMTNode.MACRO_TERM, "Pair", children,
					false));
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		SMTFormula[] children = toFormulaArray(convert(predicate.getLeft(),
				predicate.getRight()));
		switch (predicate.getTag()) {
		case Formula.LIMP:
			stack.push(sf.makeConnectiveFormula(SMTNode.IMPLIES, children));
			break;
		case Formula.LEQV:
			stack.push(sf.makeConnectiveFormula(SMTNode.IFF, children));
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		switch (expression.getTag()) {
		case Formula.KBOOL:
			expression.getPredicate().accept(this);
			stack.push(sf.makeITETerm((SMTFormula) stack.pop(),
					sf.makeBoolean(SMTNode.TRUE), sf.makeBoolean(SMTNode.FALSE)));
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final BoundIdentDecl[] tempIdentDeclTab = new BoundIdentDecl[1];
		tempIdentDeclTab[0] = boundIdentDecl;

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		final Set<String> fidsSet = new HashSet<String>(this.freeIdentifiers);
		final String[] newNames = QuantifiedUtil.resolveIdents(
				tempIdentDeclTab, fidsSet);

		if (newNames.length != 1) { // FIXME Why is that?
			throw new IllegalStateException();
		}

		this.freeIdentifiers.add(newNames[0]);
		this.boundIdentifers.add(newNames[0]);

		stack.push(sf.makeQuantifiedVariable(SMTNode.QUANTIFIED_VARIABLE,
				newNames[0], boundIdentDecl.getType()));
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier expression) {
		String identifier = boundIdentifers.get(boundIdentifers.size()
				- expression.getBoundIndex() - 1);
		stack.push(sf.makeIdentifier(identifier));
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier expression) {
		stack.push(sf.makeIdentifier(expression.getName()));
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		stack.push(sf.makeNumeral(expression.getValue()));
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			stack.push(sf.makePropAtom(SMTNode.PTRUE));
			break;
		case Formula.BFALSE:
			stack.push(sf.makePropAtom(SMTNode.PFALSE));
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {

		BoundIdentDecl[] tempIdentDeclTab = predicate.getBoundIdentDecls();

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		Set<String> fidsSet = new HashSet<String>(freeIdentifiers);
		String[] newNames = QuantifiedUtil.resolveIdents(tempIdentDeclTab,
				fidsSet);

		SMTTerm[] children1 = toTermArray(convert(predicate
				.getBoundIdentDecls()));
		SMTFormula[] children2 = toFormulaArray(convert(predicate
				.getPredicate()));

		switch (predicate.getTag()) {
		case Formula.FORALL:
			stack.push(sf.makeQuantifiedPred(SMTNode.FORALL, children1,
					children2));
			break;
		case Formula.EXISTS:
			stack.push(sf.makeQuantifiedPred(SMTNode.EXISTS, children1,
					children2));
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}

		// remove added bound idents identifier of the list
		for (int i = 0; i < newNames.length; i++) {
			boundIdentifers.remove(newNames[i]);
		}
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		final SMTTerm[] children;
		switch (predicate.getTag()) {
		case Formula.NOTEQUAL:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "=",
					children, true));
			break;
		case Formula.EQUAL:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			FormulaFactory ff = FormulaFactory.getDefault();

			// Check Type of equality members
			if (predicate.getLeft().getType().equals(ff.makeIntegerType())) {
				stack.push(sf.makeArithmeticFormula(SMTNode.EQUAL, children));
			} else {
				stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "=",
						children, false));
			}
			break;
		case Formula.LT:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeArithmeticFormula(SMTNode.LT, children));
			break;
		case Formula.LE:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeArithmeticFormula(SMTNode.LE, children));
			break;
		case Formula.GT:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeArithmeticFormula(SMTNode.GT, children));
			break;
		case Formula.GE:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeArithmeticFormula(SMTNode.GE, children));
			break;
		case Formula.IN:
			children = toTermArray(convert(predicate.getRight(),
					predicate.getLeft()));
			stack.push(sf.makeMacroFormula(SMTNode.IN, "", children, false));
			break;
		case Formula.NOTIN:
			children = toTermArray(convert(predicate.getRight(),
					predicate.getLeft()));
			stack.push(sf.makeMacroFormula(SMTNode.IN, "", children, true));
			break;
		case Formula.SUBSET:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subset",
					children, false));
			break;
		case Formula.SUBSETEQ:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subseteq",
					children, false));
			break;
		case Formula.NOTSUBSET:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subset",
					children, true));
			break;
		case Formula.NOTSUBSETEQ:
			children = toTermArray(convert(predicate.getLeft(),
					predicate.getRight()));
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subseteq",
					children, true));
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
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
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			SMTTerm[] children = toTermArray(convert(expression.getChild()));
			stack.push(sf.makeArithmeticTerm(SMTNode.UNARY_MINUS, children));
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.NOT:
			SMTFormula[] children = toFormulaArray(convert(predicate.getChild()));
			stack.push(sf.makeConnectiveFormula(SMTNode.NOT, children));
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