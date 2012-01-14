package org.eventb.smt.ast.theories;

import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import org.eventb.smt.ast.macros.SMTMacroSymbol;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;

public class VeritPredefinedTheoryV2_0 extends SMTTheory implements
		ISMTArithmeticFunsExtended, ISMTArithmeticPreds, ISMTIntegerSort,
		ISMTBooleanSort {

	private static final String NAME = "verit_theoryV2_0";

	private final static SMTSortSymbol BOOL = new SMTSortSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, PREDEFINED, V2_0);

	private final static SMTSortSymbol INT = new SMTSortSymbol(SMTSymbol.INT,
			PREDEFINED, V2_0);

	private final static SMTSortSymbol[] INT_TAB = { INT };
	private final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

	public static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol();
	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };

	private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
			SMTSymbol.EQUAL, POLYMORPHIC_PAIRS, PREDEFINED, V2_0);

	/**
	 * Predicate symbols
	 */
	private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
			SMTSymbol.LT, INT_INT_TAB, PREDEFINED, V2_0);
	private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
			SMTSymbol.LE, INT_INT_TAB, PREDEFINED, V2_0);
	private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
			SMTSymbol.GT, INT_INT_TAB, PREDEFINED, V2_0);
	private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
			SMTSymbol.GE, INT_INT_TAB, PREDEFINED, V2_0);

	/**
	 * Function symbols
	 */
	private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
			SMTSymbol.MINUS, INT_TAB, INT, !ASSOCIATIVE, PREDEFINED, V2_0);
	static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
			SMTSymbol.MINUS, INT_INT_TAB, INT, !ASSOCIATIVE, PREDEFINED, V2_0);
	private static final SMTFunctionSymbol DIV = new SMTFunctionSymbol(
			SMTSymbol.DIV, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED, V2_0);
	private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
			SMTSymbol.PLUS, INT_TAB, INT, ASSOCIATIVE, PREDEFINED, V2_0);
	private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
			SMTSymbol.MUL, INT_TAB, INT, ASSOCIATIVE, PREDEFINED, V2_0);
	private static final SMTFunctionSymbol EXPN = new SMTFunctionSymbol(
			SMTSymbol.EXPN, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED, V2_0);
	private static final SMTFunctionSymbol MOD = new SMTFunctionSymbol(
			SMTSymbol.MOD, INT_INT_TAB, INT, !ASSOCIATIVE, !PREDEFINED, V2_0);

	private static final SMTSortSymbol[] SORTS = { BOOL, INT };

	private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE, GT,
			GE };

	private static final SMTFunctionSymbol[] FUNCTIONS = { UMINUS, MINUS, PLUS,
			MUL, DIV, MOD, EXPN };

	/**
	 * Constructs the veriT predefined theory
	 */
	protected VeritPredefinedTheoryV2_0() {
		super(NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	/**
	 * Instance of the VeriT Predefined Theory
	 */
	private static final VeritPredefinedTheoryV2_0 INSTANCE = new VeritPredefinedTheoryV2_0();

	/**
	 * returns the instance of veriT predefined theory
	 * 
	 * @return the instance of veriT predefined theory
	 */
	public static VeritPredefinedTheoryV2_0 getInstance() {
		return INSTANCE;
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL;
	}

	@Override
	public SMTSortSymbol getIntegerSort() {
		return INT;
	}

	@Override
	public SMTFunctionSymbol getUMinus() {
		return UMINUS;
	}

	@Override
	public SMTFunctionSymbol getPlus() {
		return PLUS;
	}

	@Override
	public SMTFunctionSymbol getMul() {
		return MUL;
	}

	@Override
	public SMTFunctionSymbol getMinus() {
		return MINUS;
	}

	@Override
	public SMTSymbol getDiv() {
		return DIV;
	}

	@Override
	public SMTPredicateSymbol getLessThan() {
		return LT;
	}

	@Override
	public SMTPredicateSymbol getLessEqual() {
		return LE;
	}

	@Override
	public SMTPredicateSymbol getGreaterThan() {
		return GT;
	}

	@Override
	public SMTPredicateSymbol getGreaterEqual() {
		return GE;
	}

	/**
	 * 
	 * @return the pair sorts {Int,Int}
	 */
	public static SMTSortSymbol[] getIntIntTab() {
		return INT_INT_TAB;
	}

	/**
	 * returns the exponential symbol
	 */
	@Override
	public SMTSymbol getExpn() {
		return EXPN;
	}

	@Override
	public SMTSymbol getMod() {
		return MOD;
	}
}
