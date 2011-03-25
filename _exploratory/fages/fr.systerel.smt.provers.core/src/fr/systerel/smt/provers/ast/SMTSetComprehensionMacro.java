package fr.systerel.smt.provers.ast;

public class SMTSetComprehensionMacro extends SMTMacro {

	SMTSetComprehensionMacro(String macroName,
			SMTVarSymbol[] quantifiedVariables, SMTVarSymbol lambdaVar,
			SMTFormula formula, SMTTerm expression) {
		super(macroName);
		this.qVars = quantifiedVariables;
		this.lambdaVar = lambdaVar;
		this.formula = formula;
		this.expression = expression;
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

}
