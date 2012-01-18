package org.eventb.smt.ast.theories;

import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import org.eventb.smt.ast.macros.SMTMacroSymbol;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;

public class VeriTBooleansV2_0 extends SMTTheory implements ISMTBooleanSort {

	public final static SMTSortSymbol[] EMPTY_SORT = {};

	private final static SMTSortSymbol BOOL = new SMTSortSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, PREDEFINED, V2_0);

	private static final SMTFunctionSymbol TRUE = new SMTFunctionSymbol("true",
			EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED, V2_0);

	private static final SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
			"false", EMPTY_SORT, BOOL, !ASSOCIATIVE, PREDEFINED, V2_0);

	private static final SMTFunctionSymbol[] FUNCTIONS = { TRUE, FALSE };
	private static final SMTSortSymbol[] SORTS = { BOOL };
	private static final SMTPredicateSymbol[] PREDICATES = {};

	protected VeriTBooleansV2_0() {
		super("verit_booleans_v2_0", SORTS, PREDICATES, FUNCTIONS);
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL;
	}

	public SMTFunctionSymbol getTrueConstant() {
		return TRUE;
	}

	public SMTFunctionSymbol getFalseConstant() {
		return FALSE;
	}

	private static final VeriTBooleansV2_0 INSTANCE = new VeriTBooleansV2_0();

	public static VeriTBooleansV2_0 getInstance() {
		return INSTANCE;
	}

}
