package fr.systerel.smt.provers.ast;

public interface ISMTArithmeticFuns {
	public abstract SMTFunctionSymbol getUMinus();

	public abstract SMTFunctionSymbol getPlus();

	public abstract SMTFunctionSymbol getMul();

	public abstract SMTFunctionSymbol getMinus();
}