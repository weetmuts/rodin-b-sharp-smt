package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;

public class VeriTBooleans extends SMTTheory implements ISMTBooleanSort {

	private static final String BOOLS_THEORY_NAME = "Bools";

	private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
			SMTSymbol.BOOL, !PREDEFINED);
	private static final SMTSortSymbol[] SORTS = { BOOL_SORT };

	private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol("TRUE",
			SMTMacroFactory.EMPTY_SORT, BOOL_SORT, false, false);

	private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
			"FALSE", SMTMacroFactory.EMPTY_SORT, BOOL_SORT, false, false);

	private final static SMTPredicateSymbol[] PREDICATES = {};

	private static SMTFunctionSymbol[] FUNCTIONS = { TRUE, FALSE };

	private static final VeriTBooleans INSTANCE = new VeriTBooleans();

	private VeriTBooleans() {
		super(BOOLS_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	public static VeriTBooleans getInstance() {
		return INSTANCE;
	}

	public SMTFunctionSymbol getTrueConstant() {
		return TRUE;
	}

	public SMTFunctionSymbol getFalseConstant() {
		return FALSE;
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL_SORT;
	}

	@Override
	public SMTSortSymbol getPowerSetBooleanSort() {
		// TODO Auto-generated method stub
		return null;
	}
}