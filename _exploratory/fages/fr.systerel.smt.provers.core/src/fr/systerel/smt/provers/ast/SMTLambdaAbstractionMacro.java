package fr.systerel.smt.provers.ast;

public class SMTLambdaAbstractionMacro extends SMTMacro {

	private SMTVarSymbol pName;
	private SMTVarSymbol xName;
	private SMTVarSymbol yName;
	SMTFormula guardPredicate;
	SMTTerm expression;

	SMTLambdaAbstractionMacro(String macroName) {
		super(macroName);

	}

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

}
