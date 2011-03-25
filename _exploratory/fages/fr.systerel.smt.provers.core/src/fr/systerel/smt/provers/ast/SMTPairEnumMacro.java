package fr.systerel.smt.provers.ast;

public class SMTPairEnumMacro extends SMTMacro {

	SMTPairEnumMacro(String macroName, SMTVarSymbol var1, SMTVarSymbol var2,
			SMTMacroTerm[] terms) {
		super(macroName);
		this.var1 = var1;
		this.var2 = var2;
		this.terms = terms;
	}

	private SMTVarSymbol var1;
	private SMTVarSymbol var2;
	private SMTMacroTerm[] terms;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(macroName);
		sb.append(" (lambda ");
		sb.append(var1);
		sb.append(" ");
		sb.append(var2);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append(elemToString(var1.name, var2.name,
					terms[0].getArgTerms()[0], terms[0].getArgTerms()[1]));
			sb.append("))");
		} else {
			sb.append("(or");
			for (SMTMacroTerm term : terms) {
				sb.append(elemToString(var1.name, var2.name,
						term.getArgTerms()[0], term.getArgTerms()[1]));
			}
			sb.append(")))");
		}
		return sb.toString();
	}

	private String elemToString(String var1, String var2, SMTTerm term1,
			SMTTerm term2) {
		StringBuffer sb = new StringBuffer();
		sb.append("(= (pair ");
		sb.append(var1);
		sb.append(" ");
		sb.append(var2);
		sb.append(")");
		sb.append("(pair ");
		sb.append(term1);
		sb.append(" ");
		sb.append(term2);
		sb.append("))");
		return sb.toString();
	}

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

}
