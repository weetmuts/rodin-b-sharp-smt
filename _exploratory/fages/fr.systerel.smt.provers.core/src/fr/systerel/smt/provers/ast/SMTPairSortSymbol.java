package fr.systerel.smt.provers.ast;

public class SMTPairSortSymbol extends SMTSortSymbol {

	private SMTSortSymbol source;
	private SMTSortSymbol target;

	SMTPairSortSymbol(String symbolName, SMTSortSymbol source,
			SMTSortSymbol target, boolean predefined) {
		super(symbolName, predefined);
		this.source = source;
		this.target = target;
	}
	
	SMTPairSortSymbol(String symbolName, SMTSortSymbol source,
			SMTSortSymbol target) {
		super(symbolName, false);
		this.source = source;
		this.target = target;
	}

	@Override
	public String toString() {
		return "(Pair " + source.toString() + " " + target.toString() + ")";
	}
	
	@Override
	public void toString(StringBuilder sb){
		sb.append(this.toString());
	}

}
