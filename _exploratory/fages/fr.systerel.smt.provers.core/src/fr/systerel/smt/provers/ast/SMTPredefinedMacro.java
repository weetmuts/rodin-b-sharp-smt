package fr.systerel.smt.provers.ast;

public class SMTPredefinedMacro extends SMTMacro {

	SMTPredefinedMacro(String macroName, String bodyText) {
		super(macroName);
		this.body = bodyText;
	}

	private String body;

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

}
