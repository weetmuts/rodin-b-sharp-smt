package fr.systerel.smt.provers.ast;

public class SMTBaseSortSymbol extends SMTSortSymbol {

	SMTSortSymbol base;

	SMTBaseSortSymbol(String symbolName, SMTSortSymbol base, boolean predefined) {
		super(symbolName, predefined);
		this.base = base;
	}

	@Override
	public String toString() {
		return base.toString() + " Bool";
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.toString());
	}

}
