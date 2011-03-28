package fr.systerel.smt.provers.ast;

public abstract class SMTMacro {

	SMTMacro(String macroName) {
		this.macroName = macroName;
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

}
