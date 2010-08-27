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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

import fr.systerel.smt.provers.astV1_2.SMTCommandsFactory;
import fr.systerel.smt.provers.astV1_2.SMTFactory;
import fr.systerel.smt.provers.astV1_2.SMTFormula;
import fr.systerel.smt.provers.astV1_2.SMTNode;
import fr.systerel.smt.provers.astV1_2.SMTTerm;

/**
 * This class translate a formula expressed in Event-B syntax to a formula in
 * SMT-LIB syntax.
 */
public class VisitorV2_0 implements ISimpleVisitor {

	/** The built nodes. */
	private Stack<SMTNode<?>> stack;

	/** The SMT factory. */
	private SMTFactory sf;
	
	/** The SMT factory. */
	private SMTCommandsFactory sfc;

	/**
	 * Builds a new visitor.
	 */
	public VisitorV2_0() {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
		sfc = SMTCommandsFactory.getDefault();
	}

	/**
	 * Gets the built formula in SMT-LIB format.
	 * 
	 * @return the string representation of the built formula
	 */
	public String getSMTNode() {
		if (!stack.isEmpty())
			return stack.pop().toString();
		else
			return "";
	}

	/**
	 * Resets the visitor.
	 */
	public void reset() {
		stack.clear();
	}

	/**
	 * Converts Event-B formulas in SMT-LIB format.
	 * 
	 * @param formulas
	 *            the formulas to be converted
	 * @return the built SMT node
	 */
	private List<SMTNode<?>> convert(Formula<?>... formulas) {
		for (int i = 0; i < formulas.length; i++)
			formulas[i].accept(this);

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

	public void visitAssociativeExpression(AssociativeExpression expression) {
		SMTTerm[] children = toTermArray(convert(expression.getChildren()));
		switch (expression.getTag()) {
		case Formula.PLUS:
			stack.push(sf.makeArithmeticTerm(SMTNode.PLUS, children));
			break;
		case Formula.MUL:
			stack.push(sf.makeArithmeticTerm(SMTNode.MUL, children));
			break;
		default:
			assert false;
			return;
		}
	}

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

	public void visitAtomicExpression(AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.TRUE:
			stack.push(sf.makeBoolean(SMTNode.TRUE));
			break;
		case Formula.FALSE:
			stack.push(sf.makeBoolean(SMTNode.FALSE));
			break;
		case Formula.BOOL:
			break;
		default:
			assert false;
			return;
		}
	}

	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		assert false;
	}

	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		assert false;
	}

	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		assert false;
	}

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
		default:
			assert false;
			return;
		}
	}

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

	public void visitBoolExpression(BoolExpression expression) {
		switch (expression.getTag()) {
		case Formula.KBOOL:
			expression.getPredicate().accept(this);
			stack.push(sf.makeITETerm((SMTFormula) stack.pop(), sf
					.makeBoolean(SMTNode.TRUE), sf.makeBoolean(SMTNode.FALSE)));
			break;
		default:
			assert false;
			return;
		}
	}

	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		assert false;
	}

	public void visitBoundIdentifier(BoundIdentifier expression) {
		assert false;
	}

	public void visitFreeIdentifier(FreeIdentifier expression) {
		stack.push(sf.makeIdentifier(expression.getName()));
	}

	public void visitIntegerLiteral(IntegerLiteral expression) {
		stack.push(sf.makeNumeral(expression.getValue()));
	}

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

	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		assert false;
	}

	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		assert false;
	}

	public void visitRelationalPredicate(RelationalPredicate predicate) {
		SMTTerm[] children = null;
		children = toTermArray(convert(predicate.getLeft(), predicate.getRight()));
		switch (predicate.getTag()) {
		case Formula.EQUAL:
			stack.push(sf.makeArithmeticFormula(SMTNode.EQUAL, children));
			break;
		case Formula.LT:
			stack.push(sfc.makeAssertCommand(new SMTFormula[] {
							sf.makeArithmeticFormula(SMTNode.LT, children)}));
			break;
		case Formula.LE:
			stack.push(sf.makeArithmeticFormula(SMTNode.LE, children));
			break;
		case Formula.GT:
			SMTFormula[] f = new SMTFormula[] {
					sf.makeArithmeticFormula(SMTNode.GT, children)};
			stack.push(sfc.makeAssertCommand(f));
			break;
		case Formula.GE:
			stack.push(sf.makeArithmeticFormula(SMTNode.GE, children));
			break;
		default:
			assert false;
			return;
		}
	}

	public void visitSetExtension(SetExtension expression) {
		// TODO
	}

	public void visitSimplePredicate(SimplePredicate predicate) {
		assert false;
	}

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

	public void visitMultiplePredicate(MultiplePredicate predicate) {
		assert false;
	}
	
	public void visitExtendedExpression(ExtendedExpression expression) {
		// Do nothing.
	}
	
	public void visitExtendedPredicate(ExtendedPredicate perdicate) {
		// Do nothing.
	}
}