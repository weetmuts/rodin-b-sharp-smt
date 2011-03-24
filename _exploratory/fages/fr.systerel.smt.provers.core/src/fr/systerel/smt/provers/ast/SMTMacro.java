package fr.systerel.smt.provers.ast;

public abstract class SMTMacro {

	SMTMacro(String macroName) {
		this.macroName = macroName;
	}

	public String getMacroName() {
		return macroName;
	}

	protected String macroName;

	public abstract void toString(StringBuffer builder);

}
