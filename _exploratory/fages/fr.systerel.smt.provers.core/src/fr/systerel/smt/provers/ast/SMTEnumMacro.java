package fr.systerel.smt.provers.ast;

public class SMTEnumMacro extends SMTMacro {

	SMTEnumMacro(final String macroName, final String varName,final SMTTerm[] terms) {
		super(macroName);
		this.varName = varName;
		this.terms = terms;
	}

	private String varName;
	private SMTTerm[] terms;

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

}
