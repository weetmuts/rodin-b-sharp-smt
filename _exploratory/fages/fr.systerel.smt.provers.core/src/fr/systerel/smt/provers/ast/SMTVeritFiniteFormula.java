package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

public class SMTVeritFiniteFormula extends SMTFormula {

	private SMTMacroSymbol predicate;
	private SMTPredicateSymbol pArgument;
	private SMTFunctionSymbol kArgument;
	private SMTFunctionSymbol fArgument;
	private SMTTerm[] terms;

	public SMTVeritFiniteFormula(SMTMacroSymbol symbol,
			SMTPredicateSymbol pArgument, SMTFunctionSymbol kArgument,
			SMTFunctionSymbol fArgument, SMTTerm[] terms) {
		this.predicate = symbol;
		this.terms = terms;
		this.pArgument = pArgument;
		this.kArgument = kArgument;
		this.fArgument = fArgument;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(OPAR);
		builder.append(predicate.name);
		builder.append(SPACE);
		builder.append(pArgument.name);
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			builder.append(term);
		}
		builder.append(SPACE);
		builder.append(fArgument.name);
		builder.append(SPACE);
		builder.append(kArgument.name);
		builder.append(CPAR);
	}

}
