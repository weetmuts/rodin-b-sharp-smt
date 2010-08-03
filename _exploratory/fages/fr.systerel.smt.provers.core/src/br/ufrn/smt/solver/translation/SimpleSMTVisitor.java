package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

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
import org.eventb.core.ast.FormulaFactory;
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

public class SimpleSMTVisitor implements ISimpleVisitor {

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
	private String notImplementedOperation;

	public SimpleSMTVisitor(long minimalFiniteValue2,long minimalEnumValue2,long minimalElemvalue2,Hashtable<String, String> singleQuotVars,ArrayList<String> indexesOfboundIdentifiers) {
		this.minimalFiniteValue = minimalFiniteValue2;
		this.minimalEnumValue = minimalEnumValue2;
		this.minimalElemvalue = minimalElemvalue2;
		this.singleQuotVars = singleQuotVars;
		
		this.indexesOfboundIdentifiers = indexesOfboundIdentifiers;	
	}

	public SimpleSMTVisitor(RodinToSMTPredicateParser parser) {
		funs = parser.getFuns();
		sorts = parser.getSorts();
		preds = parser.getPreds();
		assumptions = parser.getAssumptions();
		macros = parser.getMacros();
		singleQuotVars = parser.getSingleQuotVars();
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

	public SimpleSMTVisitor(SimpleSMTVisitor visitor) {

	}

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
		minimalElemvalue = setEl.getSecondElement();
		sb.append("(forall (? " + setEl.getFirstElement() + " " + "(or");
		for (int i = 0; i < se.getMembers().length; i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			se.getMembers()[i].accept(smv);
			getDataFromVisitor(smv); 
			String setElement = smv.getSmtFormula();
			sb.append("(= + ?" + setEl.getFirstElement() + " " + setElement
					+ ")");
		}
		sb.append("))");
		return sb.toString();
	}

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
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			identifiers[i].accept(smv);
			getDataFromVisitor(smv);
			// smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		for (int i = 0; i < exp.size(); i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			exp.get(i).accept(smv);
			getDataFromVisitor(smv);
			// smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		// smtFormula = smtFormula + ")";
		smtFormula.append(")");
	}

	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		assignment.getAssignedIdentifiers();
		assignment.getSet();

		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();

		//smtFormula = smtFormula + "(=";
		smtFormula.append("(=");

		for (int i = 0; i < identifiers.length; i++) {
			SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			identifiers[i].accept(smv);
			getDataFromVisitor(smv);
		//	smtFormula = smtFormula + smv.getSmtFormula();
			smtFormula.append(smv.getSmtFormula());
		}
		Expression expression = assignment.getSet();
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		expression.accept(smv);
		getDataFromVisitor(smv);
		//smtFormula = smtFormula + smv.getSmtFormula();
		//smtFormula = smtFormula + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
	}

	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		StringBuffer sb = new StringBuffer();
		sb.append("(lambda");
		FreeIdentifier[] assignedIdentifiers = assignment
				.getAssignedIdentifiers();
		for (int i = 0; i < assignedIdentifiers.length; i++) {
			sb.append("(?"
					+ assignedIdentifiers[i].getName()
					+ " "
					+ RodinToSMTPredicateParser
							.getSMTAtomicExpressionFormat(assignedIdentifiers[i]
									.getType().toString()) + ")");
		}
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		assignment.getCondition().accept(smv);
		getDataFromVisitor(smv);
		//smtFormula += sb.toString() + smv.getSmtFormula();
		smtFormula.append(sb.toString() + smv.getSmtFormula());

	}

	private String verifyBoundedQuotedVar(String name)
	{
		if(name.lastIndexOf('\'') > 0)
		{
			int countofQuots = name.length() - name.lastIndexOf('\'');
			//String alternativeName = name.substring(0,name.lastIndexOf('\'')) + "_" + countofQuots;
			String alternativeName = name.replaceAll("'", "_" + countofQuots + "_");
			while(true)
			{
				if(funs.containsKey(alternativeName) || preds.containsKey(alternativeName) || sorts.contains(alternativeName))
				{
					alternativeName = name.replaceAll("'", "_" + ++countofQuots + "_");
					continue;
				}
				else
				{
					break;
				}				
			}
			this.singleQuotVars.put(name, alternativeName);
			return alternativeName;
		}
		return name;
	}
	
	
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		String subVar = this.singleQuotVars.get(boundIdentDecl.getName().trim());
		if (subVar == null) {
			subVar = boundIdentDecl.getName();

		}
		subVar = "?" + subVar;
		subVar = verifyBoundedQuotedVar(subVar);
		indexesOfboundIdentifiers.add(subVar.trim());
		smtFormula.append("(" + subVar + " " + RodinToSMTPredicateParser.getSMTAtomicExpressionFormat(boundIdentDecl.getType().toString()) + ")");
	}

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
			notImplementedOperation = "Backward Composition";
		}

		Expression[] expressions = expression.getChildren();

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		expressions[0].accept(smv);
		getDataFromVisitor(smv);
		//smtFormula += "(" + operator + smv.getSmtFormula();
		smtFormula.append("(" + operator + smv.getSmtFormula());
		smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		expressions[1].accept(smv);
		getDataFromVisitor(smv);
		//smtFormula += smv.getSmtFormula() + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
		for (int i = 2; i < expressions.length; i++) {
			smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			expressions[i].accept(smv);
			getDataFromVisitor(smv);
		//	smtFormula = "(" + operator + smtFormula + smv.getSmtFormula() + ")";
			StringBuffer temp = new StringBuffer("(" + operator + smtFormula.toString() + smv.getSmtFormula() + ")");
			smtFormula = temp;
		}

	}

	public void visitAtomicExpression(AtomicExpression expression) {
		int vTag = expression.getTag();
		if (vTag == Formula.INTEGER) {
			//smtFormula = smtFormula + " Int ";
			smtFormula.append(" Int ");
		} else if (vTag == Formula.NATURAL) {
			//smtFormula = smtFormula + " Nat ";
			smtFormula.append(" Nat ");
		} else if (vTag == Formula.NATURAL1) {
			//smtFormula = smtFormula + " Nat1 ";
			smtFormula.append(" Nat1 ");
		} else if (vTag == Formula.BOOL) {
			//smtFormula = smtFormula + " Bool ";
			smtFormula.append(" Bool ");
		} else if (vTag == Formula.TRUE) {
			//smtFormula = smtFormula + " True ";
			smtFormula.append(" True ");
		} else if (vTag == Formula.FALSE) {
			//smtFormula = smtFormula + " False ";
			smtFormula.append(" False ");
		} else if (vTag == Formula.EMPTYSET) {
			//smtFormula = smtFormula + " emptyset ";
			smtFormula.append(" emptyset ");
		} else if (vTag == Formula.KPRED) {
			//smtFormula = smtFormula + " pred ";
			smtFormula.append(" pred ");
			notImplementedOperation = "KPRED";
		} else if (vTag == Formula.KSUCC) {
			//smtFormula = smtFormula + " succ ";
			smtFormula.append(" succ ");
			notImplementedOperation = "KSUCC";
		} else if (vTag == Formula.KPRJ1_GEN) {
			//smtFormula = smtFormula + " prj1 ";
			smtFormula.append(" prj1 ");
			notImplementedOperation = "KPRJ1_GEN";
		} else if (vTag == Formula.KPRJ2_GEN) {
			//smtFormula = smtFormula + " prj2 ";
			smtFormula.append(" prj2 ");
			notImplementedOperation = "KPRJ2_GEN";
		} else if (vTag == Formula.KID_GEN) {
			//smtFormula = smtFormula + " id ";
			smtFormula.append(" id ");
			notImplementedOperation = "KID_GEN";
		}

	}

	public void visitBinaryExpression(BinaryExpression expression) {
		String operator = "";
		int vTag = expression.getTag();
		if (vTag == Formula.MAPSTO) {
			operator = "pair";
		} else if (vTag == Formula.REL) {
			operator = "rel";
			this.isNecessaryAllMacros = true;
		} else if (vTag == Formula.TREL) {
			notImplementedOperation = "total relation";
		} else if (vTag == Formula.SREL) {
			notImplementedOperation = "surjective relation";
		} else if (vTag == Formula.STREL) {
			notImplementedOperation = "total surjective relation";
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
			notImplementedOperation = "direct product";
		} else if (vTag == Formula.PPROD) {
			notImplementedOperation = "parallel product";

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
		} else if (vTag == Formula.FUNIMAGE) {
			notImplementedOperation = "FUNIMAGE: function application";
		} else if (vTag == Formula.RELIMAGE) {
			notImplementedOperation = "RELIMATION: relational image";
		}
		Expression exp = expression.getLeft();
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		exp.accept(leftVisitor);
		getDataFromVisitor(leftVisitor);
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		exp = expression.getRight();
		exp.accept(rightVisitor);
		getDataFromVisitor(rightVisitor);

		smtFormula.append("(" + operator + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")");
		//smtFormula = smtFormula + "(" + operator + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")";
		
	}
	public void visitBoolExpression(BoolExpression expression) {
		//smtFormula = smtFormula + " Bool ";
		smtFormula.append(" Bool ");
	}

	public void visitIntegerLiteral(IntegerLiteral expression) {
		//smtFormula = smtFormula + " " + expression.getValue() + " ";
		smtFormula.append(" " + expression.getValue() + " ");
	}

	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		if (expression.getTag() == Formula.QUNION) {
			notImplementedOperation = "n-ary union";
		} else if (expression.getTag() == Formula.QINTER) {
			notImplementedOperation = "n-ary interseccion";
		} else if (expression.getTag() == Formula.CSET) {
			notImplementedOperation = "comprehension set";
		}
	}

	public void visitSetExtension(SetExtension expression) {
		StringBuffer setBuffer = new StringBuffer();
		Pair<String, Long> p = getValidName("enum_", minimalEnumValue);

		String nameOfSet = p.getFirstElement();

		minimalEnumValue = p.getSecondElement();
		setBuffer.append("(" + p.getFirstElement() + "(lambda(?");

		p = getValidName("enum_", minimalElemvalue);
		minimalElemvalue = p.getSecondElement();
		Type setType = expression.getType();
		if(expression.getType().getBaseType() != null)
		{
			if(expression.getType().getBaseType().getBaseType() != null)
			{
				notImplementedOperation = "Power Set of Power Set";
			}
			//TODO Implement Set Extension of sets of pairs.			
			else
			{
				setType = expression.getType().getBaseType();
			}
		}
		
		String name1 = p.getFirstElement();
		String name2 = null;

		if (setType.toString().contains("\u21a6")) // MAPSTO
		{
			setBuffer.append(p.getFirstElement()
					+ " "
					+ RodinToSMTPredicateParser
							.getSMTAtomicExpressionFormat(setType.getSource()
									.toString()) + ")(?");

			p = getValidName("enum_", minimalElemvalue);

			minimalElemvalue = p.getSecondElement();

			setBuffer.append(p.getFirstElement()
					+ " "
					+ RodinToSMTPredicateParser
							.getSMTAtomicExpressionFormat(setType.getTarget()
									.toString()) + ") . (or");

			name2 = p.getFirstElement();

		} else {
			setBuffer.append(p.getFirstElement()
					+ " "
					+ RodinToSMTPredicateParser
							.getSMTAtomicExpressionFormat(setType.toString())
					+ ") . (or");
		}
		for (int i = 0; i < expression.getMembers().length; i++) {
			if (expression.getMembers()[i].getTag() == Formula.MAPSTO) {
				setBuffer.append("(= (pair "
						+ "?"
						+ name1
						+ " "
						+ "?"
						+ name2
						+ ")(pair "
						+ expression.getMembers()[i]
								.getSyntacticallyFreeIdentifiers()[0].getName()
						+ " "
						+ expression.getMembers()[i]
								.getSyntacticallyFreeIdentifiers()[1].getName()
						+ "))");
				// + expression.getMembers()[i].toString() + ")");
			} else {
				setBuffer.append("(= ?" + name1 + " "
						+ expression.getMembers()[i].toString() + ")");
			}
		}
		setBuffer.append(")))");
		macros.add(setBuffer.toString());
		//smtFormula = smtFormula + " " + nameOfSet + " ";
		smtFormula.append(" " + nameOfSet + " ");
	}

	public void visitUnaryExpression(UnaryExpression expression) {
		String operator = "";
		if (expression.getTag() == Formula.POW) {
			notImplementedOperation = "set of all subsets (power set)";
		} else if (expression.getTag() == Formula.KCARD) {
			operator = "card";
		} else if (expression.getTag() == Formula.POW1) {
			notImplementedOperation = "the set of all no-empty subsets";
		} else if (expression.getTag() == Formula.KUNION) {
			notImplementedOperation = "KUNION";
		} else if (expression.getTag() == Formula.KINTER) {
			notImplementedOperation = "KINTER";
		} else if (expression.getTag() == Formula.KDOM) {
			operator = "domain";
			//isNecessaryAllMacros = true;
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
			notImplementedOperation = "UNMINUS";
		}

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		expression.getChild().accept(smv);
		getDataFromVisitor(smv);
		//smtFormula = smtFormula + "(" + operator + smv.getSmtFormula() + ")";
		smtFormula.append("(" + operator + smv.getSmtFormula() + ")");

	}
	
	private void getDataFromVisitor(SimpleSMTVisitor smv)
	{
		funs.putAll(smv.getFuns());
		sorts.addAll(smv.getSorts());
		preds.putAll(smv.getPreds());
		assumptions.addAll(smv.getAssumptions());
		macros.addAll(smv.getMacros());
		minimalElemvalue = smv.getMinimalElemvalue();
		minimalEnumValue = smv.getMinimalEnumValue();
		minimalFiniteValue = smv.getMinimalFiniteValue();
		indexesOfboundIdentifiers = smv.getIndexesOfboundIdentifiers();
		if(smv.isNecessaryAllMacros() == true)
		{
			isNecessaryAllMacros = true;
		}		
	}
	

	public ArrayList<String> getIndexesOfboundIdentifiers() {
		return indexesOfboundIdentifiers;
	}

	public void setIndexesOfboundIdentifiers(
			ArrayList<String> indexesOfboundIdentifiers) {
		this.indexesOfboundIdentifiers = indexesOfboundIdentifiers;
	}

	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		//smtFormula = smtFormula + " " + identifierExpression.toString() + " ";
		smtFormula.append(" " + indexesOfboundIdentifiers.get(identifierExpression.getBoundIndex()) + " ");
	}

	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		String subVar = this.singleQuotVars.get(identifierExpression.getName().trim());
		if (subVar == null) {
			subVar = identifierExpression.getName();

		}
		if (this.isNecessaryInterrogation.contains(subVar)) {
			subVar = "?" + subVar;
		}
		//smtFormula = smtFormula + " " + subVar + " ";
		smtFormula.append(" " + subVar + " ");
	}

	public void visitAssociativePredicate(AssociativePredicate predicate) {
		String operator = "";
		if (predicate.getTag() == Formula.LAND) {
			operator = "and";
		} else {
			operator = "or";
		}
		StringBuffer sb = new StringBuffer();

		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		predicate.getChildren()[0].accept(smv);
		//smtFormula += "(" + operator + smv.getSmtFormula();
		smtFormula.append("(" + operator + smv.getSmtFormula());
		smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		predicate.getChildren()[1].accept(smv);
		//smtFormula += smv.getSmtFormula() + ")";
		smtFormula.append(smv.getSmtFormula() + ")");
		getDataFromVisitor(smv);
		for (int i = 2; i < predicate.getChildren().length; i++) {
			smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			predicate.getChildren()[i].accept(smv);
			getDataFromVisitor(smv);
			StringBuffer temp = new StringBuffer("(" + operator + smtFormula.toString() + smv.getSmtFormula() + ")");
			//smtFormula = "(" + operator + smtFormula + smv.getSmtFormula() + ")";
			smtFormula = temp;
			
		}
		//smtFormula = smtFormula + sb.toString();
		smtFormula.append(sb.toString());
	}

	public void visitBinaryPredicate(BinaryPredicate predicate) {
		String operator = "";
		if (predicate.getTag() == Formula.LEQV) {
			operator = "iff";
		} else {
			operator = "implies";
		}
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		Predicate pred = predicate.getLeft();
		pred.accept(leftVisitor);
		getDataFromVisitor(leftVisitor);
		pred = predicate.getRight();
		pred.accept(rightVisitor);
		getDataFromVisitor(rightVisitor);
		//smtFormula = smtFormula + "(" + operator + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")";
		smtFormula.append("(" + operator + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")");
	}

	public void visitLiteralPredicate(LiteralPredicate predicate) {
		if (predicate.getTag() == Formula.BTRUE) {
			//smtFormula = smtFormula + " True ";
			smtFormula.append(" True ");
		} else {
			//smtFormula = smtFormula + " False ";
			smtFormula.append(" False ");
		}
	}

	public void visitMultiplePredicate(MultiplePredicate predicate) {
		notImplementedOperation = "KPARTITION";
	}

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
		//String smtF = smv.getSmtFormula();
		for(int i = 0 ; i < boundVars.length; i++)
		{
			SimpleSMTVisitor boundDecl = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
			boundVars[i].accept(boundDecl);
			getDataFromVisitor(boundDecl);
			sb.append(boundDecl.getSmtFormula());
		}
		SimpleSMTVisitor smv = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		predicate.getPredicate().accept(smv);
		getDataFromVisitor(smv);
		

//		for (int i = 0; i < boundVars.length; i++) {
//			sb.append("(?" + boundVars[i].getName() + " "
//					+ boundVars[i].getType().toString() + ")");
//			smtF.replaceAll(boundVars[i].getName(),
//					"?" + boundVars[i].getName());
//		}
		

		//smtFormula = smtFormula + sb.toString() + smtF + ")";
		//smtFormula.append(sb.toString() + smtF + ")");
		smtFormula.append(sb.toString() + smv.getSmtFormula() +  ")");
	}

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
		SimpleSMTVisitor leftVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		exp.accept(leftVisitor);
		getDataFromVisitor(leftVisitor);
		exp = predicate.getRight();
		SimpleSMTVisitor rightVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		exp.accept(rightVisitor);
		getDataFromVisitor(rightVisitor);
//		if (operator.equals("in")
//				&& predicate.getLeft().getType().getBaseType() != null
//				&& predicate.getRight().getType().getBaseType() != null) {
//			operator = "subseteq";
//		}
		if (needsNotClause) {
			//smtFormula = smtFormula + "(not(" + operator + " " + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + "))";
			smtFormula.append("(not(" + operator + " " + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + "))");
		} else {
			//smtFormula = smtFormula + "(" + operator + " " + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")";
			smtFormula.append("(" + operator + " " + leftVisitor.getSmtFormula() + rightVisitor.getSmtFormula() + ")");
			
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

			String any = funs.get(name);
			if (any == null) {
			} else {
				if (sorts.contains(name)) {
					any = preds.get(name);
					if (any == null) {
					} else {
						++minimalFiniteValue;
						continue;
					}
				} else {
				}
			}
			if (true) {
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
		}
		try {
			if (kVar == null) {
				throw new Exception("Variavel kVar não foi inicializada");
			}
			if (pVar == null) {
				throw new Exception("Variavel pVar não foi inicializada");
			}
			if (fVar == null) {
				throw new Exception(
						"The expression of finite operation has no type checked");
			}

			Expression finiteExp = predicate.getExpression();
			Type finiteType = finiteExp.getType();
			if (finiteType != null) {
				String originalType = "";
				if(finiteType.getBaseType() != null)
				{
					if(finiteType.getBaseType().getBaseType() != null)
					{
						this.notImplementedOperation = "Power Set of Power Set";
					}
					else
					{
						originalType = RodinToSMTPredicateParser.getSMTAtomicExpressionFormat(finiteType.getBaseType().toString());
					}
				}
				else
				{
					originalType = RodinToSMTPredicateParser.getSMTAtomicExpressionFormat(finiteType.toString());
				}
				SimpleSMTVisitor finiteVisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
				finiteExp.accept(finiteVisitor);
				getDataFromVisitor(finiteVisitor);
				preds.put(pVar, "");
				funs.put(kVar, "Int");
				funs.put(fVar, "(" + originalType + " Int)");
				String tVar = finiteVisitor.getSmtFormula();
				assumptions.add("(finite " + tVar + " " + pVar + " " + fVar
						+ " " + kVar + ")");
				//smtFormula = smtFormula + " " + pVar + " ";
				smtFormula.append(" " + pVar + " ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void visitUnaryPredicate(UnaryPredicate predicate) {
		Predicate child = predicate.getChild();

		SimpleSMTVisitor childvisitor = new SimpleSMTVisitor(minimalFiniteValue, minimalEnumValue, minimalElemvalue,singleQuotVars,indexesOfboundIdentifiers);
		child.accept(childvisitor);
		getDataFromVisitor(childvisitor);
		//smtFormula = smtFormula + "(not" + childvisitor.getSmtFormula() + ")";
		smtFormula.append("(not" + childvisitor.getSmtFormula() + ")");

	}

	public ArrayList<String> getMacros() {
		return macros;
	}

	public void visitExtendedExpression(ExtendedExpression expression) {
		// TODO I don't know how to implement this method
		
	}

	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		// TODO I don't know how to implement this method
		
	}

}
