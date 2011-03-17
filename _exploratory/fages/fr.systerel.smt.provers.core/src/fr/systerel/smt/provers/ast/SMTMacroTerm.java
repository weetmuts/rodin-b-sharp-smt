package fr.systerel.smt.provers.ast;

public class SMTMacroTerm extends SMTTerm {

	private String name;
	SMTSortSymbol[] argSorts;

	@Override
	public void toString(StringBuilder builder) {
		// TODO Auto-generated method stub

	}

	public SMTMacroTerm(String name, SMTSortSymbol[] argSorts) {
		this.name = name;
		this.argSorts = argSorts;
	}

}
