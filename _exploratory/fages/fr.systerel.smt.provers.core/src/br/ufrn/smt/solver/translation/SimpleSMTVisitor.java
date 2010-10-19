/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.Hashtable;
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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTNode;

public class SimpleSMTVisitor implements ISimpleVisitor {

	/** The built nodes. */
	private Stack<SMTNode<?>> stack;

	/** The SMT factory. */
	private SMTFactory sf;

	/**
	 * Builds a new visitor.
	 */
	public SimpleSMTVisitor() {
		stack = new Stack<SMTNode<?>>();
		sf = SMTFactory.getDefault();
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

	private SMTFormula[] toFormulaArray(List<SMTNode<?>> nodes) {
		return nodes.toArray(new SMTFormula[nodes.size()]);
	}

	//TODO: New change made by Vitor
	private String getBoundVarFromDeBrujinIndex(int index)
	{
		return indexesOfboundIdentifiers.get(indexesOfboundIdentifiers.size() - (1 + index));
	}
	//END-TOJDO
	
	
	
	
	long minimalFiniteValue = 0;
	long minimalEnumValue = 0;
	long minimalElemvalue = 0;

	ArrayList<String> isNecessaryInterrogation = new ArrayList<String>();
	Hashtable<String, String> funs = new Hashtable<String, String>();
	Hashtable<String, String> preds = new Hashtable<String, String>();
	Hashtable<String, String> singleQuotVars = new Hashtable<String, String>();
	ArrayList<String> sorts = new ArrayList<String>();
	ArrayList<String> assumptions = new ArrayList<String>();
	ArrayList<String> macros = new ArrayList<String>();
	ArrayList<String> indexesOfboundIdentifiers = new ArrayList<String>();

	private StringBuffer smtFormula = new StringBuffer();
	private boolean isNecessaryAllMacros = false;
	private String notImplementedOperation = "";

	public SimpleSMTVisitor(long minimalFiniteValue2, long minimalEnumValue2,
			long minimalElemvalue2, Hashtable<String, String> singleQuotVars,
			ArrayList<String> indexesOfboundIdentifiers) {
		this.minimalFiniteValue = minimalFiniteValue2;
		this.minimalEnumValue = minimalEnumValue2;
		this.minimalElemvalue = minimalElemvalue2;
		this.singleQuotVars = singleQuotVars;

		this.indexesOfboundIdentifiers = indexesOfboundIdentifiers;
	}
	
	public SimpleSMTVisitor(RodinToSMTPredicateParser parser) {
		funs = parser.getTypeEnvironment().getFuns();
		sorts = parser.getTypeEnvironment().getSorts();
		preds = parser.getTypeEnvironment().getPreds();
		assumptions = parser.getAssumptions();
		macros = parser.getMacros();
		singleQuotVars = parser.getTypeEnvironment().getSingleQuotVars();
		minimalElemvalue = parser.getMinimalElemvalue();
		minimalEnumValue = parser.getMinimalEnumValue();
		minimalFiniteValue = parser.getMinimalFiniteValue();
	}	
	
	public long getMinimalFiniteValue() {
		return minimalFiniteValue;
	}

	public void setMinimalFiniteValue(long minimalFiniteValue) {
		this.minimalFiniteValue = minimalFiniteValue;
	}

	public long getMinimalEnumValue() {
		return minimalEnumValue;
	}

	public void setMinimalEnumValue(long minimalEnumValue) {
		this.minimalEnumValue = minimalEnumValue;
	}

	public long getMinimalElemvalue() {
		return minimalElemvalue;
	}

	public void setMinimalElemvalue(long minimalElemvalue) {
		this.minimalElemvalue = minimalElemvalue;
	}

	public Hashtable<String, String> getFuns() {
		return funs;
	}

	public void setFuns(Hashtable<String, String> funs) {
		this.funs = funs;
	}

	public Hashtable<String, String> getPreds() {
		return preds;
	}

	public void setPreds(Hashtable<String, String> preds) {
		this.preds = preds;
	}

	public Hashtable<String, String> getSingleQuotVars() {
		return singleQuotVars;
	}

	public void setSingleQuotVars(Hashtable<String, String> singleQuotVars) {
		this.singleQuotVars = singleQuotVars;
	}

	public ArrayList<String> getSorts() {
		return sorts;
	}

	public void setSorts(ArrayList<String> sorts) {
		this.sorts = sorts;
	}

	public ArrayList<String> getAssumptions() {
		return assumptions;
	}

	public void setAssumptions(ArrayList<String> assumptions) {
		this.assumptions = assumptions;
	}

	public boolean isNecessaryAllMacros() {
		return isNecessaryAllMacros;
	}

	public void setNecessaryAllMacros(boolean isNecessaryAllMacros) {
		this.isNecessaryAllMacros = isNecessaryAllMacros;
	}

//	public SimpleSMTVisitor(SimpleSMTVisitor visitor) {
//
//	}
	//TODO: Change made by Vitor
	public SimpleSMTVisitor(SimpleSMTVisitor visitor)
    {
        minimalFiniteValue = 0L;
        minimalEnumValue = 0L;
        minimalElemvalue = 0L;
        isNecessaryInterrogation = new ArrayList<String>();
        funs = new Hashtable<String, String>();
        preds = new Hashtable<String, String>();
        singleQuotVars = new Hashtable<String, String>();
        sorts = new ArrayList<String>();
        assumptions = new ArrayList<String>();
        macros = new ArrayList<String>();
        indexesOfboundIdentifiers = new ArrayList<String>();
        smtFormula = new StringBuffer();
        isNecessaryAllMacros = false;
        notImplementedOperation = "";
    }
	//END-TODO

	public String getNotImplementedOperation() {
		return notImplementedOperation;
	}

	public void setNotImplementedOperation(String notImplementedOperation) {
		this.notImplementedOperation = notImplementedOperation;
	}

	public String getSmtFormula() {
		return smtFormula.toString();
	}

	private String createSet(FreeIdentifier fr, SetExtension se) {
		StringBuffer sb = new StringBuffer();
		Pair<String, Long> setEl = getValidName("elem", minimalElemvalue);
		minimalElemvalue = setEl.getValue();
		sb.append("(forall (? " + setEl.getKey() + " " + "(or");
		for (int i = 0; i < se.getMembers().length; i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
					minimalEnumValue, minimalElemvalue, singleQuotVars,
					indexesOfboundIdentifiers);
			se.getMembers()[i].accept(smv);
			if (!getDataFromVisitor(smv)) {
				return "";
			}
			String setElement = smv.getSmtFormula();
			sb.append("(= + ?" + setEl.getKey() + " " + setElement + ")");
		}
		sb.append("))");
		return sb.toString();
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();

		ArrayList<Expression> exp = new ArrayList<Expression>(
				assignment.getExpressions().length);
		for (int i = 0; i < assignment.getExpressions().length; i++) {
			System.out.println("uma linha");
			if (assignment.getExpressions()[i] instanceof SetExtension) {
				for (int j = 0; j < identifiers.length; j++) {
					String createdSetString = createSet(identifiers[j],
							(SetExtension) assignment.getExpressions()[i]);
					if ((j + 1) == identifiers.length
							&& (i + 1) == assignment.getExpressions().length) {
						// smtFormula = smtFormula + createdSetString;
						smtFormula.append(createdSetString);
					} else {
						assumptions.add(createdSetString);
					}
				}
			} else {
				exp.add(assignment.getExpressions()[i]);
			}
		}

		// smtFormula = smtFormula + "(=";
		smtFormula.append("(=");

		for (int i = 0; i < identifiers.length; i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
					minimalEnumValue, minimalElemvalue, singleQuotVars,
					indexesOfboundIdentifiers);
			identifiers[i].accept(smv);
			// getDataFromVisitor(smv);
			if (!getDataFromVisitor(smv)) {
				return;
			}
			// smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		for (int i = 0; i < exp.size(); i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
					minimalEnumValue, minimalElemvalue, singleQuotVars,
					indexesOfboundIdentifiers);
			exp.get(i).accept(smv);
			// getDataFromVisitor(smv);
			if (!getDataFromVisitor(smv)) {
				return;
			}
			// smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		// smtFormula = smtFormula + ")";
		smtFormula.append(")");
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		
		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();

		// smtFormula = smtFormula + "(=";
		smtFormula.append("(=");

		for (int i = 0; i < identifiers.length; i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
					minimalEnumValue, minimalElemvalue, singleQuotVars,
					indexesOfboundIdentifiers);
			identifiers[i].accept(smv);
			// getDataFromVisitor(smv);
			if (!getDataFromVisitor(smv)) {
				return;
			}
			// smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		Expression expression = assignment.getSet();
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		expression.accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		// smtFormula = smtFormula + smv.getSmtFormula();
		// smtFormula = smtFormula + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		StringBuffer sb = new StringBuffer();
		sb.append("(lambda");
		FreeIdentifier[] assignedIdentifiers = assignment
				.getAssignedIdentifiers();
		for (int i = 0; i < assignedIdentifiers.length; i++) {
			sb.append("(?"
					+ assignedIdentifiers[i].getName()
					+ " "
					+ TypeEnvironment
							.getSMTAtomicExpressionFormat(assignedIdentifiers[i]
									.getType().toString()) + ")");
		}
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		assignment.getCondition().accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		// smtFormula += sb.toString() + smv.getSmtFormula();
		smtFormula.append(sb.toString() + smv.getSmtFormula());

	}

	private String verifyBoundedQuotedVar(String name) {
		if (name.lastIndexOf('\'') > 0) {
			int countofQuots = name.length() - name.lastIndexOf('\'');
			// String alternativeName = name.substring(0,name.lastIndexOf('\''))
			// + "_" + countofQuots;
			String alternativeName = name.replaceAll("'", "_" + countofQuots
					+ "_");
			while (true) {
				if (funs.containsKey(alternativeName)
						|| preds.containsKey(alternativeName)
						|| sorts.contains(alternativeName)) {
					alternativeName = name.replaceAll("'", "_" + ++countofQuots
							+ "_");
					continue;
				} else {
					break;
				}
			}
			this.singleQuotVars.put(name, alternativeName);
			return alternativeName;
		}
		return name;
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		String subVar = this.singleQuotVars
				.get(boundIdentDecl.getName().trim());
		if (subVar == null) {
			subVar = boundIdentDecl.getName();

		}
		subVar = "?" + subVar;
		subVar = verifyBoundedQuotedVar(subVar);
		indexesOfboundIdentifiers.add(subVar.trim());
		smtFormula.append("("
				+ subVar
				+ " "
				+ TypeEnvironment.getSMTAtomicExpressionFormat(boundIdentDecl
						.getType().toString()) + ")");
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		int operatorTag = expression.getTag();
		String operator = "";
		if (operatorTag == Formula.BUNION) {
			operator = "union";
		} else if (operatorTag == Formula.BINTER) {
			operator = "inter";
		} else if (operatorTag == Formula.FCOMP) {
			isNecessaryAllMacros = true;
			operator = "comp";
		} else if (operatorTag == Formula.OVR) {
			isNecessaryAllMacros = true;
			operator = "ovr";
		} else if (operatorTag == Formula.PLUS) {
			operator = "+";
		} else if (operatorTag == Formula.MUL) {
			operator = "*";
		} else if (operatorTag == Formula.BCOMP) {
			// notImplementedOperation = "Backward Composition";
			notImplementedOperation = "Translation of Backward Composition like in "
					+ expression.toString() + " is not implemented yet";
		}

		Expression[] expressions = expression.getChildren();

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		expressions[0].accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		// smtFormula += "(" + operator + smv.getSmtFormula();
		smtFormula.append("(" + operator + smv.getSmtFormula());
		smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue,
				minimalElemvalue, singleQuotVars, indexesOfboundIdentifiers);
		expressions[1].accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		// smtFormula += smv.getSmtFormula() + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
		for (int i = 2; i < expressions.length; i++) {
			smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue,
					minimalElemvalue, singleQuotVars, indexesOfboundIdentifiers);
			expressions[i].accept(smv);
			// getDataFromVisitor(smv);
			if (!getDataFromVisitor(smv)) {
				return;
			}
			// smtFormula = "(" + operator + smtFormula + smv.getSmtFormula() +
			// ")";
			StringBuffer temp = new StringBuffer("(" + operator
					+ smtFormula.toString() + smv.getSmtFormula() + ")");
			smtFormula = temp;
		}

	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		int vTag = expression.getTag();
		if (vTag == Formula.INTEGER) {
			// smtFormula = smtFormula + " Int ";
			smtFormula.append(" Int ");
		} else if (vTag == Formula.NATURAL) {
			// smtFormula = smtFormula + " Nat ";
			smtFormula.append(" Nat ");
		} else if (vTag == Formula.NATURAL1) {
			// smtFormula = smtFormula + " Nat1 ";
			smtFormula.append(" Nat1 ");
		} else if (vTag == Formula.BOOL) {
			// smtFormula = smtFormula + " Bool ";
			smtFormula.append(" Bool ");
		} else if (vTag == Formula.TRUE) {
			//smtFormula.append(" True ");
			//TODO Changes made by Vitor
			smtFormula.append(" true ");
			
		} else if (vTag == Formula.FALSE) {
			//smtFormula.append(" False ");
			//TODO Changes made by Vitor
			smtFormula.append(" false ");
			
		} else if (vTag == Formula.EMPTYSET) {
			// smtFormula = smtFormula + " emptyset ";
			smtFormula.append(" emptyset ");
		} else if (vTag == Formula.KPRED) {
			// smtFormula = smtFormula + " pred ";
			smtFormula.append(" pred ");
			notImplementedOperation = "Translation of pred like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.KSUCC) {
			// smtFormula = smtFormula + " succ ";
			smtFormula.append(" succ ");
			notImplementedOperation = "Translation of succ in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.KPRJ1_GEN) {
			// smtFormula = smtFormula + " prj1 ";
			smtFormula.append(" prj1 ");
			notImplementedOperation = "Translation of prj1 in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.KPRJ2_GEN) {
			// smtFormula = smtFormula + " prj2 ";
			smtFormula.append(" prj2 ");
			notImplementedOperation = "Translation of prj2 in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.KID_GEN) {
			// smtFormula = smtFormula + " id ";
			smtFormula.append(" id ");
			notImplementedOperation = "Translation of id in "
					+ expression.toString() + " is not implemented yet";
		}

	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		String operator = "";
		int vTag = expression.getTag();
		if (vTag == Formula.MAPSTO) {
			//operator = "pair";
			//TODO: Changes made by Vitor
			operator = "Pair";
		} else if (vTag == Formula.REL) {
			operator = "rel";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TREL) {
			notImplementedOperation = "total relation";
			notImplementedOperation = "Translation of Total Relation like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.SREL) {
			notImplementedOperation = "surjective relation";
			notImplementedOperation = "Translation of Surjective Relation like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.STREL) {
			notImplementedOperation = "total surjective relation";
			notImplementedOperation = "Translation of Total Surjective Relation like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.PFUN) {
			operator = "pfun";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TFUN) {
			operator = "tfun";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.PINJ) {
			operator = "pinj";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TINJ) {
			operator = "tinj";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.PSUR) {
			operator = "psur";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TSUR) {
			operator = "tsur";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TBIJ) {
			operator = "bij";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.SETMINUS) {
			operator = "setminus";
		} else if (vTag == Formula.CPROD) {
			operator = "cartesianproduct";
		} else if (vTag == Formula.DPROD) {
			notImplementedOperation = "Translation of Direct Product like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.PPROD) {
			notImplementedOperation = "parallel product";
			notImplementedOperation = "Translation of Parallel Product like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.DOMRES) {
			operator = "domr";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.DOMSUB) {
			operator = "doms";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.RANRES) {
			operator = "ranr";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.RANSUB) {
			operator = "rans";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.UPTO) {
			operator = "range";
		} else if (vTag == Formula.MINUS) {
			operator = "-";
		} else if (vTag == Formula.DIV) {
			operator = "/";
		} else if (vTag == Formula.MOD) {
			operator = "mod";
		} else if (vTag == Formula.EXPN) {
			notImplementedOperation = "exponentiation";
			notImplementedOperation = "Translation of Exponentiation like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.FUNIMAGE) {
			notImplementedOperation = "FUNIMAGE: function application";
			notImplementedOperation = "Translation of image of function like in "
					+ expression.toString() + " is not implemented yet";
		} else if (vTag == Formula.RELIMAGE) {
			notImplementedOperation = "RELIMATION: relational image";
			notImplementedOperation = "Translation of Relational Image like in "
					+ expression.toString() + " is not implemented yet";
		}
		Expression exp = expression.getLeft();
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		exp.accept(leftVisitor);
		// getDataFromVisitor(leftVisitor);
		if (!getDataFromVisitor(leftVisitor)) {
			return;
		}
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(
				minimalFiniteValue, minimalEnumValue, minimalElemvalue,
				singleQuotVars, indexesOfboundIdentifiers);
		exp = expression.getRight();
		exp.accept(rightVisitor);
		// getDataFromVisitor(rightVisitor);
		if (!getDataFromVisitor(rightVisitor)) {
			return;
		}

		smtFormula.append("(" + operator + leftVisitor.getSmtFormula()
				+ rightVisitor.getSmtFormula() + ")");
		// smtFormula = smtFormula + "(" + operator +
		// leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")";

	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		// smtFormula = smtFormula + " Bool ";
		smtFormula.append(" Bool ");
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		// smtFormula = smtFormula + " " + expression.getValue() + " ";
		smtFormula.append(" " + expression.getValue() + " ");
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		if (expression.getTag() == Formula.QUNION) {
			notImplementedOperation = "n-ary union";
			notImplementedOperation = "Translation of N-Ary Union like in "
					+ expression.toString() + " is not implemented yet";
		} else if (expression.getTag() == Formula.QINTER) {
			notImplementedOperation = "Translation of N-Ary Interseccion like in "
					+ expression.toString() + " is not implemented yet";
		} else if (expression.getTag() == Formula.CSET) {
			notImplementedOperation = "Translation of Comprehension Set like in "
					+ expression.toString() + " is not implemented yet";
		}
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		StringBuffer setBuffer = new StringBuffer();
		Pair<String, Long> p = getValidName("enum_", minimalEnumValue);

		String nameOfSet = p.getKey();

		minimalEnumValue = p.getValue();
		setBuffer.append("(" + p.getKey() + "(lambda(?");

		p = getValidName("enum_", minimalElemvalue);
		minimalElemvalue = p.getValue();
		Type setType = expression.getType();
		if (expression.getType().getBaseType() != null) {
			if (expression.getType().getBaseType().getBaseType() != null) {
				notImplementedOperation = "The Power Set of Power Set in"
						+ expression.toString() + " is not implemented yet";
			}
			// TODO Implement Set Extension of sets of pairs.
			else {
				setType = expression.getType().getBaseType();
			}
		}

		String name1 = p.getKey();
		String name2 = null;

		if (setType.toString().contains("\u21a6")) // MAPSTO
		{
			setBuffer.append(p.getKey()
					+ " "
					+ TypeEnvironment.getSMTAtomicExpressionFormat(setType
							.getSource().toString()) + ")(?");

			p = getValidName("enum_", minimalElemvalue);

			minimalElemvalue = p.getValue();

			setBuffer.append(p.getKey()
					+ " "
					+ TypeEnvironment.getSMTAtomicExpressionFormat(setType
							.getTarget().toString()) + ") . (or");

			name2 = p.getKey();

		} else {
			setBuffer.append(p.getKey()
					+ " "
					+ TypeEnvironment.getSMTAtomicExpressionFormat(setType
							.toString()) + ") . (or");
		}
		for (int i = 0; i < expression.getMembers().length; i++) {
			if (expression.getMembers()[i].getTag() == Formula.MAPSTO) {
				
				//TODO: Changes made by Vitor
				String var1 = singleQuotVars.get(expression.getMembers()[i].getSyntacticallyFreeIdentifiers()[0].getName());
                if(var1 == null)
                    var1 = expression.getMembers()[i].getSyntacticallyFreeIdentifiers()[0].getName();
                String var2 = singleQuotVars.get(expression.getMembers()[i].getSyntacticallyFreeIdentifiers()[1].getName());
                if(var2 == null)
                    var2 = expression.getMembers()[i].getSyntacticallyFreeIdentifiers()[1].getName();
                //END-TODO
							
              //TODO:Changes made by Vitor
                //setBuffer.append("(= (pair "
                setBuffer.append("(= (Pair "
                //END-TODO               
						+ "?"
						+ name1						
						+ " "
						+ "?"
						+ name2
						
						//TODO: Changes made by Vitor
						//+ ")(pair "
						+ ")(Pair "
						//END-TODO
						
						//TODO: Changes made by Vitor
//						+ expression.getMembers()[i]
//								.getSyntacticallyFreeIdentifiers()[0].getName()
						+ var1
						//END-TODO
						+ " "
						
						//TODO: Changes made by Vitor
//						+ expression.getMembers()[i]
//								.getSyntacticallyFreeIdentifiers()[1].getName()
						+ var2
						//END-TODO
						+ "))");
				// + expression.getMembers()[i].toString() + ")");
			} else {
				
				//TODO: Changes made by Vitor
//				setBuffer.append("(= ?" + name1 + " "
//						+ expression.getMembers()[i].toString() + ")");
					String var1 = null;
	                if(expression.getMembers()[i] instanceof BoundIdentifier)
	                {
	                    BoundIdentifier bi = (BoundIdentifier)expression.getMembers()[i];
	                    var1 = getBoundVarFromDeBrujinIndex(bi.getBoundIndex());
	                } else
	                {
	                    var1 = singleQuotVars.get(expression.getMembers()[i].toString());
	                    if(var1 == null)
	                        var1 = expression.getMembers()[i].toString();
	                }
	                setBuffer.append((new StringBuilder("(= ?")).append(name1).append(" ").append(var1).append(")").toString());
	           //END-TODO     
			}
		}
		setBuffer.append(")))");
		macros.add(setBuffer.toString());
		// smtFormula = smtFormula + " " + nameOfSet + " ";
		smtFormula.append(" " + nameOfSet + " ");
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		String operator = "";
		if (expression.getTag() == Formula.POW) {
			notImplementedOperation = "The set of all subsets (power set) in "
					+ expression.toString() + " is not implemented yet";
		} else if (expression.getTag() == Formula.KCARD) {
			operator = "card";
		} else if (expression.getTag() == Formula.POW1) {
			notImplementedOperation = "the set of all no-empty subsets";
		} else if (expression.getTag() == Formula.KUNION) {
			notImplementedOperation = "The translation of operator KUNION in "
					+ expression.toString() + " is not implemented yet";
		} else if (expression.getTag() == Formula.KINTER) {
			notImplementedOperation = "The translation of operator KINTER in "
					+ expression.toString() + " is not implemented yet";
		} else if (expression.getTag() == Formula.KDOM) {
			operator = "domain";
			// isNecessaryAllMacros = true;
		} else if (expression.getTag() == Formula.KRAN) {
			operator = "ran";
		} else if (expression.getTag() == Formula.KMIN) {
			operator = "min";
		} else if (expression.getTag() == Formula.KMAX) {
			operator = "max";
		} else if (expression.getTag() == Formula.CONVERSE) {
			operator = "inv";
			isNecessaryAllMacros = true;
		} else if (expression.getTag() == Formula.UNMINUS) {
			operator = "-";
		}

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		expression.getChild().accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		// smtFormula = smtFormula + "(" + operator + smv.getSmtFormula() + ")";
		smtFormula.append("(" + operator + smv.getSmtFormula() + ")");

	}

	private boolean getDataFromVisitor(SimpleSMTVisitor smv) {
		funs.putAll(smv.getFuns());
		sorts.addAll(smv.getSorts());
		preds.putAll(smv.getPreds());
		assumptions.addAll(smv.getAssumptions());
		macros.addAll(smv.getMacros());
		minimalElemvalue = smv.getMinimalElemvalue();
		minimalEnumValue = smv.getMinimalEnumValue();
		minimalFiniteValue = smv.getMinimalFiniteValue();
		indexesOfboundIdentifiers = smv.getIndexesOfboundIdentifiers();
		this.notImplementedOperation = smv.getNotImplementedOperation();
		if (smv.isNecessaryAllMacros() == true) {
			isNecessaryAllMacros = true;
		}
		if (!notImplementedOperation.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public ArrayList<String> getIndexesOfboundIdentifiers() {
		return indexesOfboundIdentifiers;
	}

	public void setIndexesOfboundIdentifiers(
			ArrayList<String> indexesOfboundIdentifiers) {
		this.indexesOfboundIdentifiers = indexesOfboundIdentifiers;
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		// smtFormula = smtFormula + " " + identifierExpression.toString() +
		// " ";
		smtFormula.append(" "
			//TODO Changes made by Vitor	
			//	+ indexesOfboundIdentifiers.get(identifierExpression
				+ getBoundVarFromDeBrujinIndex(identifierExpression.getBoundIndex())
			//END-TODO			
				+ " ");
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		String subVar = this.singleQuotVars.get(identifierExpression.getName()
				.trim());
		if (subVar == null) {
			subVar = identifierExpression.getName();

		}
		if (this.isNecessaryInterrogation.contains(subVar)) {
			subVar = "?" + subVar;
		}
		// smtFormula = smtFormula + " " + subVar + " ";
		smtFormula.append(" " + subVar + " ");
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		String operator = "";
		if (predicate.getTag() == Formula.LAND) {
			operator = "and";
		} else {
			operator = "or";
		}
		StringBuffer sb = new StringBuffer();

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		predicate.getChildren()[0].accept(smv);	
		smtFormula.append("(" + operator + smv.getSmtFormula());
		smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue,
				minimalElemvalue, singleQuotVars, indexesOfboundIdentifiers);
		predicate.getChildren()[1].accept(smv);
		// smtFormula += smv.getSmtFormula() + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		for (int i = 2; i < predicate.getChildren().length; i++) {
			smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue,
					minimalElemvalue, singleQuotVars, indexesOfboundIdentifiers);
			predicate.getChildren()[i].accept(smv);
			if (!getDataFromVisitor(smv)) {
				return;
			}
			StringBuffer temp = new StringBuffer("(" + operator
					+ smtFormula.toString() + smv.getSmtFormula() + ")");
			smtFormula = temp;

		}
		// smtFormula = smtFormula + sb.toString();
		smtFormula.append(sb.toString());
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		String operator = "";
		if (predicate.getTag() == Formula.LEQV) {
			operator = "iff";
		} else {
			operator = "implies";
		}
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(
				minimalFiniteValue, minimalEnumValue, minimalElemvalue,
				singleQuotVars, indexesOfboundIdentifiers);
		Predicate pred = predicate.getLeft();
		pred.accept(leftVisitor);
		if (!getDataFromVisitor(leftVisitor)) {
			return;
		}
		pred = predicate.getRight();
		pred.accept(rightVisitor);
		if (!getDataFromVisitor(rightVisitor)) {
			return;
		}
		smtFormula.append("(" + operator + leftVisitor.getSmtFormula()
				+ rightVisitor.getSmtFormula() + ")");
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		if (predicate.getTag() == Formula.BTRUE) {
			// smtFormula = smtFormula + " True ";
				smtFormula.append(" True ");
		} else {
			// smtFormula = smtFormula + " False ";
			smtFormula.append(" False ");
		}
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		notImplementedOperation = "Translation of multiple predicates like "
				+ predicate.toString() + " was not implemented yet";
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		String operator = "";
		if (predicate.getTag() == Formula.EXISTS) {
			operator = "exists";
		} else {
			operator = "forall";
		}

		BoundIdentDecl[] boundVars = predicate.getBoundIdentDecls();
		StringBuffer sb = new StringBuffer();
		sb.append("(" + operator);
		// String smtF = smv.getSmtFormula();
		for (int i = 0; i < boundVars.length; i++) {
			SimpleSMTVisitor boundDecl = new SimpleSMTVisitor(
					minimalFiniteValue, minimalEnumValue, minimalElemvalue,
					singleQuotVars, indexesOfboundIdentifiers);
			boundVars[i].accept(boundDecl);
			// getDataFromVisitor(boundDecl);
			if (!getDataFromVisitor(boundDecl)) {
				return;
			}
			sb.append(boundDecl.getSmtFormula());
		}
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		predicate.getPredicate().accept(smv);
		// getDataFromVisitor(smv);
		if (!getDataFromVisitor(smv)) {
			return;
		}
		smtFormula.append(sb.toString() + smv.getSmtFormula() + ")");
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		String operator = "";
		boolean needsNotClause = false;
		int operatorTag = predicate.getTag();
		if (operatorTag == Formula.EQUAL) {
			operator = "=";
		} else if (operatorTag == Formula.NOTEQUAL) {
			operator = "=";
			needsNotClause = true;
		} else if (operatorTag == Formula.LT) {
			operator = "<";
		} else if (operatorTag == Formula.LE) {
			operator = "<=";
		} else if (operatorTag == Formula.GT) {
			operator = ">";
		} else if (operatorTag == Formula.GE) {
			operator = ">=";
		} else if (operatorTag == Formula.IN) {
			operator = "in";
		} else if (operatorTag == Formula.NOTIN) {
			operator = "in";
			needsNotClause = true;
		} else if (operatorTag == Formula.SUBSET) {
			operator = "subset";
		} else if (operatorTag == Formula.NOTSUBSET) {
			operator = "subset";
			needsNotClause = true;
		} else if (operatorTag == Formula.SUBSETEQ) {
			operator = "subseteq";

		} else if (operatorTag == Formula.NOTSUBSETEQ) {
			operator = "subseteq";
			needsNotClause = true;
		}

		Expression exp = predicate.getLeft();
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue,
				minimalEnumValue, minimalElemvalue, singleQuotVars,
				indexesOfboundIdentifiers);
		exp.accept(leftVisitor);
		// getDataFromVisitor(leftVisitor);
		if (!getDataFromVisitor(leftVisitor)) {
			return;
		}
		exp = predicate.getRight();
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(
				minimalFiniteValue, minimalEnumValue, minimalElemvalue,
				singleQuotVars, indexesOfboundIdentifiers);
		exp.accept(rightVisitor);
		// getDataFromVisitor(rightVisitor);
		if (!getDataFromVisitor(rightVisitor)) {
			return;
		}	
		if (needsNotClause) {
			smtFormula.append("(not(" + operator + " "
					+ leftVisitor.getSmtFormula()
					+ rightVisitor.getSmtFormula() + "))");
		} else {
			smtFormula.append("(" + operator + " "
					+ leftVisitor.getSmtFormula()
					+ rightVisitor.getSmtFormula() + ")");

		}
	}

	private Pair<String, Long> getValidName(String prefix, long lowestIndex) {
		String name = "";
		while (true) {
			name = prefix + lowestIndex;
			if (sorts.contains(name)) {
				lowestIndex++;
				continue;
			} else if (funs.containsKey(name) == true
					|| funs.containsValue(name) == true) {
				lowestIndex++;
				continue;
			} else if (preds.contains(name) == true
					|| funs.contains(name) == true) {
				lowestIndex++;
				continue;
			}
			break;
		}
		return new Pair<String, Long>(name, ++lowestIndex);

	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		String pVar = null;
		String kVar = null;
		String fVar = null;
		int i = 0;
		while (i < 3) {
			String name = "";
			if (i == 0) {
				name = "finP_" + minimalFiniteValue;
			} else if (i == 1) {
				name = "finK_" + minimalFiniteValue;
			} else {
				name = "finV_" + minimalFiniteValue;
			}

			if (funs.get(name) != null && sorts.contains(name)
					&& preds.get(name) != null) {
				++minimalFiniteValue;
				continue;
			}

			if (i == 0) {
				pVar = name;
				++minimalFiniteValue;
				++i;
				continue;
			} else if (i == 1) {
				kVar = name;
				++minimalFiniteValue;
				++i;
				continue;
			} else {
				fVar = name;
				break;
			}
		}
		try {
			if (kVar == null) {
				throw new Exception("kVar was not initialized");
			}
			if (pVar == null) {
				throw new Exception("pVar was not initialized");
			}
			if (fVar == null) {
				throw new Exception(
						"The expression of finite operation has no type checked");
			}

			Expression finiteExp = predicate.getExpression();
			Type finiteType = finiteExp.getType();
			if (finiteType != null) {
				String originalType = "";
				if (finiteType.getBaseType() != null) {
					if (finiteType.getBaseType().getBaseType() != null) {
						this.notImplementedOperation = "Translation of power set of power set like "
								+ predicate.toString()
								+ "is not implemented yet";
					} else {
						originalType = TypeEnvironment
								.getSMTAtomicExpressionFormat(finiteType
										.getBaseType().toString());
					}
				} else {
					originalType = TypeEnvironment
							.getSMTAtomicExpressionFormat(finiteType.toString());
				}
				SimpleSMTVisitor finiteVisitor = new SimpleSMTVisitor(
						minimalFiniteValue, minimalEnumValue, minimalElemvalue,
						singleQuotVars, indexesOfboundIdentifiers);
				finiteExp.accept(finiteVisitor);
				getDataFromVisitor(finiteVisitor);
				preds.put(pVar, "");
				funs.put(kVar, "Int");
				funs.put(fVar, "(" + originalType + " Int)");
				String tVar = finiteVisitor.getSmtFormula();
				assumptions.add("(finite " + tVar + " " + pVar + " " + fVar
						+ " " + kVar + ")");
				// smtFormula = smtFormula + " " + pVar + " ";
				smtFormula.append(" " + pVar + " ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		Predicate child = predicate.getChild();

		SimpleSMTVisitor childvisitor = new SimpleSMTVisitor(
				minimalFiniteValue, minimalEnumValue, minimalElemvalue,
				singleQuotVars, indexesOfboundIdentifiers);
		child.accept(childvisitor);
		// getDataFromVisitor(childvisitor);
		if (!getDataFromVisitor(childvisitor)) {
			return;
		}
		// smtFormula = smtFormula + "(not" + childvisitor.getSmtFormula() +
		// ")";
		smtFormula.append("(not" + childvisitor.getSmtFormula() + ")");

	}

	public ArrayList<String> getMacros() {
		return macros;
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		// TODO I don't know how to implement this method

	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		// TODO I don't know how to implement this method

	}

}
