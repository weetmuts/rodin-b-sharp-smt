package org.eventb.smt.ast.macros;

import static org.eventb.smt.ast.macros.SMTMacroSymbol.BINTER;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.BUNION;

import org.eventb.smt.ast.SMTFactoryVeriT;
import org.eventb.smt.ast.SMTSignatureV1_2Verit;

public class SMTMacroFactoryV1_2 extends SMTMacroFactory {

	public static SMTPredefinedMacro BUNION_MACRO = new SMTPredefinedMacro(
			BUNION,
			"(lambda (?UNION_0 ('t Bool)) (?UNION_1 ('t Bool)) . (lambda (?UNION_2 't) . (or (?UNION_0 ?UNION_2) (?UNION_1 ?UNION_2))))",
			0, false, false, EMPTY_MACROS);
	
	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"(lambda (?BINTER_0 ('t Bool))(?BINTER_1 ('t Bool)) . (lambda (?BINTER_2 't) . (and (?BINTER_0 ?BINTER_2) (?BINTER_1 ?BINTER_2))))",
			0, false, false, EMPTY_MACROS);

	/**
	 * Given a operator, this method returns the MacroSymbol associated with it.
	 * 
	 * @see SMTVeriTOperatorV1_2
	 * 
	 * @param operator
	 *            the operator used to create the Macro Symbol
	 * @return the macro symbol associated with the operator
	 */
	public static SMTMacroSymbol getMacroSymbol(
			final SMTVeriTOperatorV1_2 operator,
			final SMTSignatureV1_2Verit signature) {
		addPredefinedMacroInSignature(operator.getSymbol(), signature);
		switch (operator) {
		case BOOLS_OP:
			return BOOL_SET_SYMBOL;
		case BUNION_OP:
			return BUNION_SYMBOL;
		case BINTER_OP:
			return BINTER_SYMBOL;
		case FCOMP_OP:
			return FCOMP_SYMBOL;
		case OVR_OP:
			return REL_OVR_SYMBOL;
		case EMPTY_OP:
			return EMPTYSET_SYMBOL;
		case IN_OP:
			return IN_SYMBOL;
		case SUBSET_OP:
			return SUBSET_SYMBOL;
		case SUBSETEQ_OP:
			return SUBSETEQ_SYMBOL;
		case RANGE_INTEGER_OP:
			return INTEGER_RANGE_SYMBOL;
		case RANGE_SUBSTRACTION_OP:
			return RANGE_SUBSTRACTION_SYMBOL;
		case RANGE_RESTRICTION_OP:
			return RANGE_RESTRICTION_SYMBOL;
		case RELATION_OP:
			return RELATION_SYMBOL;
		case TOTAL_RELATION_OP:
			return TOTAL_RELATION_SYMBOL;
		case SURJECTIVE_RELATION_OP:
			return SURJECTIVE_RELATION_SYMBOL;
		case TOTAL_SURJECTIVE_RELATION_OP:
			return TOTAL_SURJECTIVE_RELATION_SYMBOL;
		case PARTIAL_FUNCTION_OP:
			return PARTIAL_FUNCTION_SYMBOL;
		case TOTAL_FUNCTION_OP:
			return TOTAL_FUNCTION_SYMBOL;
		case ID_OP:
			return ID_SYMBOL;
		case NAT_OP:
			return NAT_SYMBOL;
		case NAT1_OP:
			return NAT1_SYMBOL;
		case INV_OP:
			return INVERSE_SYMBOL;
		case DOM_OP:
			return DOM_SYMBOL;
		case PARTIAL_INJECTION_OP:
			return PARTIAL_INJECTION_SYMBOL;
		case TOTAL_INJECTION_OP:
			return TOTAL_INJECTION_SYMBOL;
		case PARTIAL_SURJECTION_OP:
			return PARTIAL_SURJECTION_SYMBOL;
		case TOTAL_SURJECTION_OP:
			return TOTAL_SURJECTION_SYMBOL;
		case TOTAL_BIJECTION_OP:
			return TOTAL_BIJECTION_SYMBOL;
		case CARTESIAN_PRODUCT_OP:
			return CARTESIAN_PRODUCT_SYMBOL;
		case DOMAIN_RESTRICTION_OP:
			return DOMAIN_RESTRICTION_SYMBOL;
		case DOMAIN_SUBSTRACTION_OP:
			return DOMAIN_SUBSTRACTION_SYMBOL;
		case RELATIONAL_IMAGE_OP:
			return RELATIONAL_IMAGE_SYMBOL;
		case SETMINUS_OP:
			return SETMINUS_SYMBOL;
		case ISMIN_OP:
			return ISMIN_SYMBOL;
		case ISMAX_OP:
			return ISMAX_SYMBOL;
		case FINITE_OP:
			return FINITE_SYMBOL;
		case CARD_OP:
			return CARD_SYMBOL;
		case RANGE_OP:
			return RANGE_SYMBOL;
		case BCOMP_OP:
			return BCOMP_SYMBOL;
		case INTEGER_OP:
			return INTEGER_SYMBOL;
		case SUCC_OP:
			return SUCC_SYMBOL;
		case PRED_OP:
			return PRED_SYMBOL;
		default:
			throw new IllegalArgumentException(
					"There is no defined macro symbol with symbol: "
							+ operator.toString());
		}

	}

	public static enum SMTVeriTOperatorV1_2 {
		BUNION_OP(BUNION_MACRO), BINTER_OP(BINTER_MACRO), EMPTY_OP(
				EMPTYSET_MACRO), INTER_OP(BINTER_MACRO), SETMINUS_OP(
				SETMINUS_MACRO), IN_OP(IN_MACRO), SUBSETEQ_OP(SUBSETEQ_MACRO), SUBSET_OP(
				SUBSET_MACRO), RANGE_INTEGER_OP(RANGE_INTEGER_MACRO), DOM_OP(
				DOM_MACRO), IMG_OP(RELATIONAL_IMAGE_MACRO), DOMR_OP(
				DOMAIN_RESTRICTION_MACRO), DOMS_OP(DOMAIN_SUBSTRACTION_MACRO), INV_OP(
				INVERSE_MACRO), OVR_OP(REL_OVR_MACRO), ID_OP(ID_MACRO), FCOMP_OP(
				FCOMP_MACRO), RANGE_SUBSTRACTION_OP(RANGE_SUBSTRACTION_MACRO), RANGE_RESTRICTION_OP(
				RANGE_RESTRICTION_MACRO), RELATION_OP(RELATION_MACRO), TOTAL_RELATION_OP(
				TOTAL_RELATION_MACRO), SURJECTIVE_RELATION_OP(
				SURJECTIVE_RELATION_MACRO), TOTAL_SURJECTIVE_RELATION_OP(
				TOTAL_SURJECTIVE_RELATION_MACRO), PARTIAL_FUNCTION_OP(
				PARTIAL_FUNCTION_MACRO), TOTAL_FUNCTION_OP(TOTAL_FUNCTION_MACRO), NAT_OP(
				NAT_MACRO), NAT1_OP(NAT1_MACRO), PARTIAL_INJECTION_OP(
				PARTIAL_INJECTION_MACRO), TOTAL_INJECTION_OP(
				TOTAL_INJECTION_MACRO), PARTIAL_SURJECTION_OP(
				PARTIAL_SURJECTION_MACRO), TOTAL_SURJECTION_OP(
				TOTAL_SURJECTION_MACRO), TOTAL_BIJECTION_OP(
				TOTAL_BIJECTION_MACRO), CARTESIAN_PRODUCT_OP(
				CARTESIAN_PRODUCT_MACRO), DOMAIN_RESTRICTION_OP(
				DOMAIN_RESTRICTION_MACRO), DOMAIN_SUBSTRACTION_OP(
				DOMAIN_SUBSTRACTION_MACRO), RELATIONAL_IMAGE_OP(
				RELATIONAL_IMAGE_MACRO), ISMIN_OP(ISMIN_MACRO), ISMAX_OP(
				ISMAX_MACRO), FINITE_OP(FINITE_MACRO), CARD_OP(CARD_MACRO), FUNP_OP(
				FUNP_MACRO), INJP_OP(INJP_MACRO), RANGE_OP(RANGE_MACRO), BCOMP_OP(
				BCOMP_MACRO), INTEGER_OP(INTEGER_MACRO), SUCC_OP(
				SUCCESSOR_MACRO), PRED_OP(PREDECESSOR_MACRO), BOOLS_OP(
				BOOL_SET_MACRO);

		/**
		 * The symbol string.
		 */
		private SMTPredefinedMacro symbol;

		/**
		 * THe Constructor of the enumeration
		 * 
		 * @param symbol
		 *            the String value of the operator.
		 */
		SMTVeriTOperatorV1_2(final SMTPredefinedMacro symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return symbol.toString();
		}

		public SMTPredefinedMacro getSymbol() {
			return symbol;
		}
	}

	private static SMTPredefinedMacro[] PREDEFINED_MACROS = { BUNION_MACRO,
			BINTER_MACRO, FCOMP_MACRO, REL_OVR_MACRO, EMPTYSET_MACRO, IN_MACRO,
			SUBSET_MACRO, SUBSETEQ_MACRO, RANGE_INTEGER_MACRO,
			RANGE_SUBSTRACTION_MACRO, RANGE_RESTRICTION_MACRO, RELATION_MACRO,
			SURJECTIVE_RELATION_MACRO, TOTAL_SURJECTIVE_RELATION_MACRO,
			PARTIAL_FUNCTION_MACRO, TOTAL_FUNCTION_MACRO, NAT_MACRO,
			NAT1_MACRO, INVERSE_MACRO, ID_MACRO, DOM_MACRO,
			PARTIAL_INJECTION_MACRO, TOTAL_INJECTION_MACRO,
			PARTIAL_SURJECTION_MACRO, TOTAL_SURJECTION_MACRO,
			TOTAL_BIJECTION_MACRO, CARTESIAN_PRODUCT_MACRO,
			DOMAIN_RESTRICTION_MACRO, DOMAIN_SUBSTRACTION_MACRO,
			RELATIONAL_IMAGE_MACRO, SETMINUS_MACRO, ISMIN_MACRO, ISMAX_MACRO,
			FINITE_MACRO, CARD_MACRO, FUNP_MACRO, INJP_MACRO,
			TOTAL_RELATION_MACRO, RANGE_MACRO, BCOMP_MACRO, INTEGER_MACRO,
			SUCCESSOR_MACRO, PREDECESSOR_MACRO, BOOL_SET_MACRO };

	/**
	 * Thi constructor adds all qSymbols which will are necessary to be checked
	 * when creating fresh name.
	 */
	public SMTMacroFactoryV1_2() {
		for (final SMTPredefinedMacro pMacro : PREDEFINED_MACROS) {
			for (final String qSymbol : pMacro.getQSymbols()) {
				assert !qSymbols.contains(qSymbol);
			}
			getqSymbols().addAll(pMacro.getQSymbols());
			qSymbols.add(SMTFactoryVeriT.FST_PAIR_ARG_NAME);
			qSymbols.add(SMTFactoryVeriT.SND_PAIR_ARG_NAME);
		}
	}

}
