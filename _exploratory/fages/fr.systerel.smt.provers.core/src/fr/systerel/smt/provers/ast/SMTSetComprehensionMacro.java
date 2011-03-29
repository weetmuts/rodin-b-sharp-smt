package fr.systerel.smt.provers.ast;

public class SMTSetComprehensionMacro extends SMTMacro {

	SMTSetComprehensionMacro(String macroName,
			SMTVarSymbol[] quantifiedVariables, SMTVarSymbol lambdaVar,
			SMTFormula formula, SMTTerm expression, int precedence ) {
		super(macroName, precedence);
		this.qVars = quantifiedVariables;
		this.lambdaVar = lambdaVar;
		this.formula = formula;
		this.expression = expression;
	}

	public SMTVarSymbol getLambdaVar() {
		return lambdaVar;
	}

	public void setLambdaVar(SMTVarSymbol lambdaVar) {
		this.lambdaVar = lambdaVar;
	}

	public SMTVarSymbol[] getqVars() {
		return qVars;
	}

	public void setqVars(SMTVarSymbol[] qVars) {
		this.qVars = qVars;
	}

	SMTVarSymbol lambdaVar;
	SMTVarSymbol[] qVars;
	SMTFormula formula;
	SMTTerm expression;

	@Override
	public void toString(StringBuffer builder) {
		// TODO
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(lambda");
		sb.append(lambdaVar);
		sb.append("(exists ");
		for (SMTVarSymbol qVar : qVars) {
			sb.append(qVar);
		}
		sb.append("(and (= ");
		sb.append(lambdaVar.name);
		sb.append(" ");
		sb.append(expression);
		sb.append(") ");
		sb.append(formula);
		sb.append(")))");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO
		return true;
	}

}
