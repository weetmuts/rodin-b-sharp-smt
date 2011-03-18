package fr.systerel.smt.provers.ast;

public class SMTMacroSymbol extends SMTSymbol {

	// VeriT Extended SMT-LIB Symbols
	public static final String BUNION = "union";
	public static final String BINTER = "inter";
	public static final String EMPTY = "emptyset";
	public static final String INTER = "inter";
	public static final String SETMINUS = "setminus";
	public static final String IN = "in";
	public static final String SUBSETEQ = "subseteq";
	public static final String SUBSET = "subset";
	public static final String RANGE = "range";
	public static final String PROD = "prod";
	public static final String DOM = "dom";
	public static final String RAN = "ran";
	public static final String IMG = "img";
	public static final String DOMR = "domr";
	public static final String DOMS = "doms";
	public static final String RANR = "ranr";
	public static final String RANS = "rans";
	public static final String INV = "inv";
	public static final String COMP = "comp";
	public static final String OVR = "ovr";
	public static final String ID = "id";
	public static final String FCOMP = "comp";
	public static final String EMPTY_PAIR = "emptyset2";

	public static final String ENUM = "enum";

	SMTSortSymbol[] argSorts;

	SMTMacroSymbol(String symbolName, SMTSortSymbol[] args, boolean predefined) {
		super(symbolName, predefined);
		this.argSorts = args;
	}

	SMTMacroSymbol(String symbolName, SMTSortSymbol[] args) {
		super(symbolName, false);
		this.argSorts = args;
	}

	public boolean isPropositional() {
		// TODO Auto-generated method stub
		return argSorts.length == 0;
	}

}
