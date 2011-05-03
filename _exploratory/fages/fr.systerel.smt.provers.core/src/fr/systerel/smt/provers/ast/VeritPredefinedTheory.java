package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

public class VeritPredefinedTheory extends SMTTheory implements
		ISMTArithmeticFunsExtended, ISMTArithmeticPreds, ISMTIntegerSort,
		ISMTBooleanSort {

	private static final String NAME = "verit_theory";

	private final static SMTSortSymbol BOOL = new SMTSortSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, PREDEFINED);

	private final static SMTSortSymbol INT = new SMTSortSymbol(SMTSymbol.INT,
			PREDEFINED);

	private final static SMTSortSymbol[] INT_TAB = { INT };
	private final static SMTSortSymbol[] INT_INT_TAB = { INT, INT };

	public static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol();
	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };

	private static final SMTPredicateSymbol EQUAL = new SMTPredicateSymbol(
			SMTSymbol.EQUAL, PREDEFINED, POLYMORPHIC_PAIRS);

	private static final SMTPredicateSymbol LT = new SMTPredicateSymbol(
			SMTSymbol.LT, PREDEFINED, INT_INT_TAB);
	private static final SMTPredicateSymbol LE = new SMTPredicateSymbol(
			SMTSymbol.LE, PREDEFINED, INT_INT_TAB);
	private static final SMTPredicateSymbol GT = new SMTPredicateSymbol(
			SMTSymbol.GT, PREDEFINED, INT_INT_TAB);
	private static final SMTPredicateSymbol GE = new SMTPredicateSymbol(
			SMTSymbol.GE, PREDEFINED, INT_INT_TAB);

	private static final SMTFunctionSymbol UMINUS = new SMTFunctionSymbol(
			SMTSymbol.UMINUS, INT, !ASSOCIATIVE, PREDEFINED, INT_TAB);
	private static final SMTFunctionSymbol MINUS = new SMTFunctionSymbol(
			SMTSymbol.MINUS, INT, !ASSOCIATIVE, PREDEFINED, INT_INT_TAB);
	private static final SMTFunctionSymbol DIV = new SMTFunctionSymbol(
			SMTSymbol.DIV, INT, !ASSOCIATIVE, !PREDEFINED, INT_INT_TAB);
	private static final SMTFunctionSymbol PLUS = new SMTFunctionSymbol(
			SMTSymbol.PLUS, INT, ASSOCIATIVE, PREDEFINED, INT_TAB);
	private static final SMTFunctionSymbol MUL = new SMTFunctionSymbol(
			SMTSymbol.MUL, INT, ASSOCIATIVE, PREDEFINED, INT_TAB);
	private static final SMTFunctionSymbol EXPN = new SMTFunctionSymbol(
			SMTSymbol.EXPN, INT, !ASSOCIATIVE, !PREDEFINED, INT_INT_TAB);

	private static final SMTFunctionSymbol BOOL_FUNCTION = new SMTFunctionSymbol(
			SMTMacroSymbol.BOOL_SORT_VERIT, BOOL, false, true,
			SMTFactory.EMPTY_SORT);

	public static SMTFunctionSymbol getBoolFunction() {
		return BOOL_FUNCTION;
	}

	private static final SMTSortSymbol[] SORTS = { BOOL, INT };

	private static final SMTPredicateSymbol[] PREDICATES = { EQUAL, LT, LE, GT,
			GE };

	private static final SMTFunctionSymbol[] FUNCTIONS = { DIV, UMINUS, MINUS,
			PLUS, MUL };

	protected VeritPredefinedTheory() {
		super(NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	private static final VeritPredefinedTheory INSTANCE = new VeritPredefinedTheory();

	public static VeritPredefinedTheory getInstance() {
		return INSTANCE;
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL;
	}

	public static SMTSortSymbol getInt() {
		return INT;
	}

	public static SMTSortSymbol[] getIntTab() {
		return INT_TAB;
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

	public static SMTSortSymbol[] getIntIntTab() {
		return INT_INT_TAB;
	}

	@Override
	public SMTSymbol getExpn() {
		return EXPN;
	}

}