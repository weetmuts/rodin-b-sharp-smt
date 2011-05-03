/**
 * 
 */
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * @author vitor
 * 
 */
final public class SMTFactoryVeriT extends SMTFactory {
	private final static SMTFactoryVeriT DEFAULT_INSTANCE = new SMTFactoryVeriT();

	public static SMTFactoryVeriT getInstance() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * This method is used by the Extended SMT-LIB.
	 * 
	 * @param atomicExpression
	 * @param signature
	 */
	public static String getSMTAtomicExpressionFormat(
			final String atomicExpression, final SMTSignatureVerit signature) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return "Int";
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			signature.addMacro(SMTMacroFactory.NAT_MACRO);
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			signature.addMacro(SMTMacroFactory.NAT1_MACRO);
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else {
			return signature.freshCstName(atomicExpression);
		}
	}

	public static SMTFormula makeDistinct(final SMTTerm[] args) {
		return new SMTAtom(DISTINCT, args);
	}

	public static SMTSortSymbol makePairSortSymbol(
			final SMTSortSymbol sourceSymbol, final SMTSortSymbol targetSymbol) {

		final StringBuffer sb = new StringBuffer();
		sb.append("(Pair ");
		sb.append(sourceSymbol.toString());
		sb.append(" ");
		sb.append(targetSymbol.toString());
		sb.append(")");
		return new SMTSortSymbol(sb.toString(), false);
	}

	public static SMTFormula makeMacroAtom(final SMTMacroSymbol macroSymbol,
			final SMTTerm[] args) {
		return new SMTVeriTAtom(macroSymbol, args);
	}

	/**
	 * FIXME: Remake this commentary this method makes a new predicate extended
	 * SMT-LIB macro atom.
	 * 
	 */
	public SMTFormula makeVeriTMacroAtom(final SMTMacroSymbol macroSymbol,
			final SMTTerm... args) {
		return new SMTVeriTAtom(macroSymbol, args);
	}

	/**
	 * this method makes a VeriT term. The difference between normal terms and
	 * VeriT terms is that VeriT terms can accept predicate symbols instead of
	 * function symbols. This happens when predicates are used as arguments of
	 * macros, which in this case, the predicates is used with no arguments and
	 * macros are in the terms level.
	 * 
	 * @param smtSymbol
	 *            the symbol of the term
	 * @param signature
	 *            used to check the rank of the term
	 * @return a new SMT term with the symbol
	 */
	public SMTTerm makeVeriTConstantTerm(final SMTSymbol smtSymbol,
			final SMTSignature signature) {
		if (smtSymbol instanceof SMTPredicateSymbol) {
			return new SMTVeriTTerm((SMTPredicateSymbol) smtSymbol);

		} else if (smtSymbol instanceof SMTFunctionSymbol) {
			return makeFunApplication((SMTFunctionSymbol) smtSymbol, signature,
					EMPTY_TERM);
		} else {
			throw new IllegalArgumentException(
					"In the translation for veriT extended SMT-LIB, the Symbol should be a function or a verit pred symbol");
		}
	}

	/**
	 * This method converts verit SMT-TERM into formulas. These terms must be of
	 * sort Bool, predefined in VeriT.
	 * 
	 * @param terms
	 *            the terms
	 * @return the formulas from the terms
	 */
	public SMTFormula[] convertVeritTermsIntoFormulas(final SMTTerm[] terms) {
		final SMTFormula[] formulas = new SMTFormula[terms.length];
		int i = 0;
		for (final SMTTerm term : terms) {
			if ((!term
					.getSort()
					.toString()
					.equals(VeritPredefinedTheory.getInstance()
							.getBooleanSort().toString()))) {
				throw new IllegalArgumentException(
						"VeriT translation does not accept equal operator under terms with different operators");
			} else {
				if (term instanceof SMTFunApplication) {
					final SMTFunApplication function = (SMTFunApplication) term;
					final SMTSortSymbol[] sortSymbols = new SMTSortSymbol[function.args.length];
					for (int j = 0; j < function.args.length; j++) {
						sortSymbols[j] = function.args[j].getSort();
					}
					final SMTPredicateSymbol predicateSymbol = new SMTPredicateSymbol(
							function.symbol.name, !PREDEFINED, sortSymbols);
					final SMTAtom atom = new SMTAtom(predicateSymbol,
							EMPTY_TERM);
					formulas[i] = atom;
				} else {
					throw new IllegalArgumentException(
							"Conversion from terms to formula in VeriT shall happen only if all arguments of the terms are functions and their return types are boolean");
				}
			}
			++i;
		}
		return formulas;
	}

	/**
	 * This method creates a function application.
	 * 
	 * @param operatorSymbol
	 *            the symbol of the function application
	 * @param args
	 *            the arguments of the application
	 * @param signature
	 *            the signature for checking the rank
	 * @return a new SMT term with the symbol and the arguments
	 */
	public SMTTerm makeVeriTTermOperatorApplication(
			final SMTFunctionSymbol operatorSymbol, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(operatorSymbol);
		return new SMTFunApplication(operatorSymbol, args);
	}

	public static SMTSortSymbol makeVeriTSortSymbol(
			final String sortSymbolName, final SMTSignatureVerit signature) {
		final String symbolName = getSMTAtomicExpressionFormat(sortSymbolName,
				signature);
		return new SMTSortSymbol(symbolName, false);
	}

	public SMTPredicateSymbol makeVeriTPredSymbol(final String predName,
			final SMTSortSymbol symbol) {
		final SMTSortSymbol[] symbols = { symbol };
		return new SMTPredicateSymbol(predName, !SMTSymbol.PREDEFINED, symbols);
	}

}
