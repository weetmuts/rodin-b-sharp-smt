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
		// TODO Auto-generated method stub

	}

}
