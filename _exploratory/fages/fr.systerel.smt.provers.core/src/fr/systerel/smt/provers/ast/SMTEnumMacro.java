package fr.systerel.smt.provers.ast;

public class SMTEnumMacro extends SMTMacro {

	SMTEnumMacro(final String macroName, final SMTVarSymbol varName,
			final SMTTerm[] terms) {
		super(macroName);
		this.var = varName;
		this.terms = terms;
	}

	private SMTVarSymbol var;
	private SMTTerm[] terms;

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(macroName);
		sb.append(" (lambda ");
		sb.append(var);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append("(= ");
			sb.append(var.name);
			sb.append(" ");
			sb.append(terms[0]);
			sb.append(")))");
		} else {
			sb.append("(or");
			for (SMTTerm term : terms) {
				sb.append(" (= ");
				sb.append(var.name);
				sb.append(" ");
				sb.append(term);
				sb.append(")");
			}
			sb.append(")))");
		}
		return sb.toString();
	}
}
