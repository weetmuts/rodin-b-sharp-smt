package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

public class SMTVeritCardFormula extends SMTFormula {

	private SMTMacroSymbol predicate;
	private SMTFunctionSymbol kArgument;
	private SMTFunctionSymbol fArgument;
	private SMTTerm[] terms;

	public SMTVeritCardFormula(SMTMacroSymbol cardSymbol,
			SMTFunctionSymbol kVarSymbol, SMTFunctionSymbol fVarSymbol,
			SMTTerm[] terms) {
		this.predicate = cardSymbol;
		this.terms = terms;
		this.kArgument = kVarSymbol;
		this.fArgument = fVarSymbol;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(OPAR);
		builder.append(predicate.getName());
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			builder.append(term);
		}
		builder.append(SPACE);
		builder.append(fArgument.getName());
		builder.append(SPACE);
		builder.append(kArgument.getName());
		builder.append(CPAR);

	}

}
