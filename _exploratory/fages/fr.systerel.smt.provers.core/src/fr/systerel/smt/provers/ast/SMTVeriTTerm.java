package fr.systerel.smt.provers.ast;

import fr.systerel.smt.provers.ast.SMTTheory.Booleans;

public class SMTVeriTTerm extends SMTTerm {

	private SMTPredicateSymbol symbol;

	SMTVeriTTerm(SMTPredicateSymbol symbol) {
		this.symbol = symbol;

		// VeriT uses Bool sort.
		this.sort = Booleans.getInstance().getBooleanSort();
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(symbol.name);
	}

}
