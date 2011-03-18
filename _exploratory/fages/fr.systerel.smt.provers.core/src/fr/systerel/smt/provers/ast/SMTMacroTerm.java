package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;

public class SMTMacroTerm extends SMTTerm {

	private SMTMacroSymbol macroSymbol;
	SMTTerm[] argTerms;

	@Override
	public void toString(StringBuilder builder) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.name);
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.name);
			for (final SMTTerm term : argTerms) {
				builder.append(SPACE);
				builder.append(term);
			}
			builder.append(CPAR);
		}
	}

	public SMTMacroTerm(SMTMacroSymbol macro, SMTTerm[] argTerms) {
		this.macroSymbol = macro;
		this.argTerms = argTerms;
		this.sort = Booleans.getInstance().getBooleanSort();
	}

}
