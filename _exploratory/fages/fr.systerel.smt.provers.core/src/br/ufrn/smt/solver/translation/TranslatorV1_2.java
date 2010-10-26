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

import fr.systerel.smt.provers.ast.SMTEmpty;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.SMTTerm;

/**
 * This class translate a formula expressed in Event-B syntax to a formula in
 * SMT-LIB syntax.
 */
public class TranslatorV1_2 implements ISimpleVisitor {

	/** The type environment . */
	private TypeEnvironment typeEnvironment;

	/** The built nodes. */
	private Stack<SMTNode<?>> stack;

	/** The SMT factory. */
	private SMTFactory sf;

	/** The Bound identifier list. */
	private ArrayList<String> bids;

	/** The list of names already used (Free identifiers + others) list. */
	private ArrayList<String> fids;

	/**
	 * This method translates the given predicate into an SMT Node.
	 */
	//TODO remplacer SMTNode<?> par SMTNode<Formula>
	public static SMTNode<?> translate(TypeEnvironment typeEnvironment,
			Predicate predicate) { // TODO remplacer Predicate par Formula?
		final TranslatorV1_2 translator = new TranslatorV1_2(typeEnvironment,
				predicate);
		predicate.accept(translator);
		return translator.getSMTNode();
	}

	/**
	 * Builds a new visitor.
	 */
	private TranslatorV1_2(TypeEnvironment typeEnvironment,
			ArrayList<String> fids) {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
		this.typeEnvironment = typeEnvironment;
		this.bids = new ArrayList<String>();
		this.fids = fids;
	}

	/**
	 * This constructor extracts free identifiers from the given predicate
	 */
	private TranslatorV1_2(TypeEnvironment typeEnvironment, Predicate predicate) {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
		this.typeEnvironment = typeEnvironment;
		this.bids = new ArrayList<String>();
		this.fids = new ArrayList<String>();
		for (FreeIdentifier ident : predicate.getFreeIdentifiers()) {
			this.fids.add(ident.getName());
		}
	}

	/**
	 * Returns the SMT Node on the top of the stack
	 */
	private SMTNode<?> getSMTNode() {
		if (stack.size() == 1) {
			return stack.pop();
		} else {
			return new SMTEmpty();
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
			assert false;
			return;
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
			assert false;
			return;
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
			assert false;
			return;
		}
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		assert false;
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		assert false;
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		assert false;
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
			throw new IllegalArgumentException("The operation \'exponential\' is not supported yet");
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
			assert false;
			return;
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
			assert false;
			return;
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
			assert false;
			return;
		}
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		BoundIdentDecl[] tempIdentDeclTab = new BoundIdentDecl[1];
		tempIdentDeclTab[0] = boundIdentDecl;

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		Set<String> fidsSet = new HashSet<String>(fids);
		String[] newNames = QuantifiedUtil.resolveIdents(tempIdentDeclTab,
				fidsSet);

		if (newNames.length != 1) {
			assert false;
			return;
		}

		fids.add(newNames[0]);
		bids.add(newNames[0]);

		stack.push(sf.makeBoundIdentDecl(SMTNode.BOUND_IDENTIFIER_DECL,
				newNames[0], boundIdentDecl.getType()));
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier expression) {
		String identifier = bids.get(bids.size() - expression.getBoundIndex()
				- 1);
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
			assert false;
			return;
		}
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		assert false;
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {

		BoundIdentDecl[] tempIdentDeclTab = predicate.getBoundIdentDecls();

		// add bound idents identifier in the list, if exists in the list a new
		// name is computed
		Set<String> fidsSet = new HashSet<String>(fids);
		String[] newNames = QuantifiedUtil.resolveIdents(tempIdentDeclTab,
				fidsSet);

		SMTTerm[] children1 = toTermArray(convert(predicate
				.getBoundIdentDecls()));
		SMTFormula[] children2 = toFormulaArray(convert(predicate
				.getPredicate()));

		switch (predicate.getTag()) {
		case Formula.FORALL:
			stack.push(sf.makeQuantifiedPred(
					SMTNode.QUANTIFIED_PRED_FORALL_DECL, children1, children2));
			break;
		case Formula.EXISTS:
			stack.push(sf.makeQuantifiedPred(
					SMTNode.QUANTIFIED_PRED_EXISTS_DECL, children1, children2));
			break;
		default:
			assert false;
			break;

		}

		// remove added bound idents identifier of the list
		for (int i = 0; i < newNames.length; i++) {
			bids.remove(newNames[i]);
		}
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		final SMTTerm[] children = toTermArray(convert(predicate.getLeft(),
				predicate.getRight()));
		switch (predicate.getTag()) {
		case Formula.NOTEQUAL:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "=",
					children, true));
			break;
		case Formula.EQUAL:
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
			stack.push(sf.makeArithmeticFormula(SMTNode.LT, children));
			break;
		case Formula.LE:
			stack.push(sf.makeArithmeticFormula(SMTNode.LE, children));
			break;
		case Formula.GT:
			stack.push(sf.makeArithmeticFormula(SMTNode.GT, children));
			break;
		case Formula.GE:
			stack.push(sf.makeArithmeticFormula(SMTNode.GE, children));
			break;
		case Formula.IN:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "in",
					children, false));
			break;
		case Formula.NOTIN:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "in",
					children, true));
			break;
		case Formula.SUBSET:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subset",
					children, false));
			break;
		case Formula.SUBSETEQ:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subseteq",
					children, false));
			break;
		case Formula.NOTSUBSET:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subset",
					children, true));
			break;
		case Formula.NOTSUBSETEQ:
			stack.push(sf.makeMacroFormula(SMTNode.MACRO_FORMULA, "subseteq",
					children, true));
			break;
		default:
			assert false;
			return;
		}
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		// TODO
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		assert false;
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			SMTTerm[] children = toTermArray(convert(expression.getChild()));
			stack.push(sf.makeArithmeticTerm(SMTNode.UNARY_MINUS, children));
			break;
		default:
			assert false;
			return;
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
			assert false;
			return;
		}
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		assert false;
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		// Do nothing.
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate perdicate) {
		// Do nothing.
	}
}