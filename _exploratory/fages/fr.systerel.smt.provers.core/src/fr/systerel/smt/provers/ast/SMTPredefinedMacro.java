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
		sb.append(super.getMacroName());
		sb.append(body);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SMTMacro) {
			SMTMacro objMacro = (SMTMacro) object;
			if (super.getMacroName().equals(objMacro.getMacroName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
