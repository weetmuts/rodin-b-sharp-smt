package fr.systerel.smt.provers.ast;

public abstract class SMTMacro implements Comparable<SMTMacro> {

	protected int precedence;

	SMTMacro(String macroName, int precedence) {
		this.macroName = macroName;
		this.precedence = precedence;
	}

	public String getMacroName() {
		return macroName;
	}

	private String macroName;

	public abstract void toString(StringBuffer builder);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SMTMacro) {
			SMTMacro macroObj = (SMTMacro) obj;
			if (macroObj.getMacroName().equals(this.getMacroName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(SMTMacro o) {
		if (o.getPrecedence() == precedence) {
			return 1;
		} else if (precedence > o.getPrecedence()) {
			return 1;
		} else
			return -1;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

}
