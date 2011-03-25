package fr.systerel.smt.provers.ast;

public class SMTPredefinedMacro extends SMTMacro {

	SMTPredefinedMacro(String macroName, String bodyText) {
		super(macroName);
		this.body = bodyText;
	}

	private String body;

	@Override
	public void toString(StringBuffer builder) {
		// TODO: Nothing
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(macroName);
		sb.append(body);
		sb.append(")");
		return sb.toString();
	}
}
