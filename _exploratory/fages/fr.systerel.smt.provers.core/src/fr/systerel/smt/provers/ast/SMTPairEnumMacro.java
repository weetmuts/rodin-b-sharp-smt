package fr.systerel.smt.provers.ast;

public class SMTPairEnumMacro extends SMTMacro {

	SMTPairEnumMacro(String macroName, String varName1, String varName2,
			SMTTerm[] terms) {
		super(macroName);
		this.varName1 = varName1;
		this.varName2 = varName2;
		this.terms = terms;
	}

	private String varName1;
	private String varName2;
	private SMTTerm[] terms;

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

}
