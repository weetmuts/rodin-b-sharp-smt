package fr.systerel.smt.provers.ast;

import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and cretes macro
 * enumerations, which are used to translate sets in extension.
 * 
 * @author vitor
 * 
 */
public class SMTMacros {

	private static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static String ENUM_PREFIX = "enum";

	public static String BUNION_MACRO = "(lambda (?p1 ('t boolean)) (?q1 ('t boolean)) . (lambda (?x1 't) . (or (?p1 ?x6) (?q1 ?x1))))";
	public static String BINTER_MACRO = "(lambda (?p2 ('t boolean))(?q2 ('t boolean)) . (lambda (?x2 't) . (and (?p2 ?x2) (?q2 ?x2))))";
	public static String FCOMP_MACRO = "(lambda (?p3 ((Pair 's 't) bool)(?q3 ((Pair 't 'u) bool)(lambda (?s1 (Pair 's 'u))(exists (?x3 't) (and (?p3 (pair (fst ?s1) ?x3)) (?q3 (pair ?x3 (snd ?s1)))))))))";
	public static String REL_OVR_MACRO = "(lambda (?p4 ((Pair 's 't) bool)(?q4 ((Pair 's 't) bool)(lambda (?x4 (Pair 's 'u)) (or (?q4 ?x4)(and (?p4 ?x4)(not(exists(?s2 (Pair 's 't))(and (?q4 ?s2)(= (fst ?s2)(fst ?x4)))))))))))";
	public static String EMPTYSET_MACRO = "(lambda (?x5 't). false)";
	public static String EMPTYSET_PAIR_MACRO = "(lambda (?p5 't1) (?q5 't2). false)";

	private static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol(
			"");

	private static SMTPairSortSymbol[] POLYMORPHIC_PAIRS = { new SMTPairSortSymbol(
			"", POLYMORPHIC, POLYMORPHIC) };

	private static SMTPolymorphicSortSymbol[] POLYMORPHICS = { POLYMORPHIC };

	public static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.BUNION, POLYMORPHIC_PAIRS, true);
	public static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.BINTER, POLYMORPHIC_PAIRS, true);
	public static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.COMP, POLYMORPHIC_PAIRS, true);
	public static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.OVR, POLYMORPHIC_PAIRS, true);
	public static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.EMPTY, EMPTY_SORT, true);
	public static SMTMacroSymbol EMPTYSET_PAIR_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.EMPTY_PAIR, EMPTY_SORT, true);

	/**
	 * This method creates the macro of an enumeration, as described in the rule
	 * 19 of the Paper
	 * "Integration of SMT-Solvers in B and Event-B Development Environments"
	 * from the author DEHARBE, David.
	 * 
	 * For example: the enumeration macro of the translation of the expression R
	 * = {1,2,3}, where Type(R) = Int, is:
	 * 
	 * (lambda ?x Int)(or (= ?x 1) (= ?x 2) (= ?x 3))
	 * 
	 * @param terms
	 *            the terms to be inserted in the macro enumeration
	 * @return the string of the enumeration macro.
	 */
	private static String createEnumerationMacro(SMTTerm[] terms) {
		// TODO: Verify if all the arguments are of the same sort
		SMTSortSymbol sort = terms[0].getSort();

		StringBuilder sb = new StringBuilder();
		if (terms.length == 1) {
			sb.append("(lambda (?s ");
			sb.append(sort.toString());
			sb.append(") . (or \n");
			for (SMTTerm term : terms) {
				sb.append("\t\t\t(= ?s ");
				sb.append(term.toString());
				sb.append(")\n");
			}
			sb.append("\t\t\t))");
		}
		return sb.toString();
	}

	/**
	 * This method adds a new enumeration macro in the signature, and returns a
	 * enumeration MacroSymbol. For each new defined enumeration macro, the
	 * method associates with it a new name.
	 * 
	 * @param signature
	 *            The signature to check the already used smt symbol names.
	 * @param terms
	 *            The terms to be added in the enumeration macro and in the
	 *            Macro Symbol.
	 * @return
	 */
	public static final SMTMacroSymbol addEnumerationMacroInSignature(
			final SMTSignatureVerit signature, SMTTerm[] terms) {
		String macroName = signature.freshCstName(SMTMacroSymbol.ENUM);
		String macroBody = createEnumerationMacro(terms);
		signature.addMacro(macroName, macroBody);
		SMTMacroSymbol macroSymbol = new SMTMacroSymbol(macroName, EMPTY_SORT);
		return macroSymbol;
	}

	/**
	 * Given a operator, this method returns the MacroSymbol associated with it.
	 * 
	 * @see SMTVeriTOperator
	 * 
	 * @param operator
	 *            the operator used to create the Macro Symbol
	 * @return the macro symbol associated with the operator
	 */
	public static final SMTMacroSymbol getMacroSymbol(
			final SMTVeriTOperator operator) {
		switch (operator) {
		case BUNION: {
			return BUNION_SYMBOL;
		}
		case BINTER: {
			return BINTER_SYMBOL;
		}
		case FCOMP: {
			return FCOMP_SYMBOL;
		}
		case OVR: {
			return REL_OVR_SYMBOL;
		}
		case EMPTY: {
			return EMPTYSET_SYMBOL;
		}
		case EMPTY_PAIR: {
			return EMPTYSET_PAIR_SYMBOL;
		}
		default:

		}
		return null;
	}

}
