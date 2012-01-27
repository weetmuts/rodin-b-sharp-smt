package org.eventb.smt.ast.macros;

import static org.eventb.smt.ast.macros.SMTMacroSymbol.BINTER;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.BUNION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.CARD;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.EMPTY;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.FINITE;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.IN;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.ISMAX;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.ISMIN;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_FUNCTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_INJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_SURJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RELATION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_BIJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_FUNCTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_INJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_SURJECTION;
import static org.eventb.smt.ast.symbols.SMTSymbol.INT;

import java.util.Set;

import org.eventb.smt.ast.SMTFactoryVeriT;
import org.eventb.smt.ast.SMTSignatureV1_2Verit;

public class SMTMacroFactoryV1_2 extends SMTMacroFactory {

	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(lambda (?EMPTY_0 't). false)", 0, false, false,
			EMPTY_MACROS);

	public static SMTPredefinedMacro BUNION_MACRO = new SMTPredefinedMacro(
			BUNION,
			"("
					+ BUNION
					+ "(lambda (?UNION_0 ('t Bool)) (?UNION_1 ('t Bool)) . (lambda (?UNION_2 't) . (or (?UNION_0 ?UNION_2) (?UNION_1 ?UNION_2)))))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro INTEGER_MACRO = new SMTPredefinedMacro(
			INT, "(" + INT + "(lambda (?INT_0 Int). true))", 0, false, false,
			EMPTY_MACROS);

	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"("
					+ BINTER
					+ "(lambda (?BINTER_0 ('t Bool))(?BINTER_1 ('t Bool)) . (lambda (?BINTER_2 't) . (and (?BINTER_0 ?BINTER_2) (?BINTER_1 ?BINTER_2)))))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro IN_MACRO = new SMTPredefinedMacro(
			IN, "(" + IN
					+ "(lambda (?IN_0 't) (?IN_1 ('t Bool)) . (?IN_1 ?IN_0)))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro RELATION_MACRO = new SMTPredefinedMacro(
			RELATION,
			"("
					+ RELATION
					+ "(lambda (?REL_0 ('s Bool)) (?REL_1 ('t Bool)) . (lambda (?REL_2  ((Pair 's 't) Bool)) . (forall (?REL_3 (Pair 's 't)) . (implies (?REL_2 ?REL_3) (and (?REL_0 (fst ?REL_3)) (?REL_1 (snd ?REL_3))))))))",
			1, true, true, EMPTY_MACROS);

	private static SMTPredefinedMacro[] REL_AND_FUNP_AND_IN = { RELATION_MACRO,
			FUNP_MACRO, IN_MACRO };

	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"("
					+ PARTIAL_FUNCTION
					+ "(lambda (?PAR_FUN_0 ('s Bool)) (?PAR_FUN_1  ('t Bool)) . (lambda (?PAR_FUN_2 ((Pair 's 't) Bool)) . (and (in ?PAR_FUN_2 (rel ?PAR_FUN_0 ?PAR_FUN_1)) (funp ?PAR_FUN_2)))))",
			3, false, false, REL_AND_FUNP_AND_IN);

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_TOTAL_RELATION_AND_IN = {
			PARTIAL_FUNCTION_MACRO, TOTAL_RELATION_MACRO, IN_MACRO };

	public static final SMTPredefinedMacro TOTAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			TOTAL_FUNCTION,
			"("
					+ TOTAL_FUNCTION
					+ "(lambda (?TOT_FUN_0 ('s Bool)) (?TOT_FUN_1 ('t Bool)) . (lambda (?TOT_FUN_2 ((Pair 's 't) Bool)) . (and (in ?TOT_FUN_2 (pfun ?TOT_FUN_0 ?TOT_FUN_1)) (totp ?TOT_FUN_0 ?TOT_FUN_2)))))",
			3, false, false, PARTIAL_FUNCTION_AND_TOTAL_RELATION_AND_IN);

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_INJP = {
			PARTIAL_FUNCTION_MACRO, INJP_MACRO };

	public static final SMTPredefinedMacro PARTIAL_INJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_INJECTION,
			"("
					+ PARTIAL_INJECTION
					+ "(lambda (?PAR_INJ ('s Bool)) (?PAR_INJ_1 ('s Bool)) . (lambda (?PAR_INJ_2 ((Pair 's 't) Bool)) . (and ((pfun ?PAR_INJ_0 ?PAR_INJ_1) ?PAR_INJ_2) (injp ?PAR_INJ_2)))))",
			3, false, false, PARTIAL_FUNCTION_AND_INJP);

	private static SMTPredefinedMacro[] PARTIAL_INJECTION_AND_TOTAL_RELATION = {
			PARTIAL_INJECTION_MACRO, TOTAL_RELATION_MACRO };

	public static final SMTPredefinedMacro TOTAL_INJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_INJECTION,
			"("
					+ TOTAL_INJECTION
					+ "(lambda (?TOT_INJ_0 ('s Bool))(?TOT_INJ_1 ('s Bool)) . (lambda (?TOT_INJ_2 ((Pair 's 't) Bool)) . (and ((pinj ?TOT_INJ_0 ?TOT_INJ_1) ?TOT_INJ_2) (totp ?TOT_INJ_0 ?TOT_INJ_2)))))",
			4, false, false, PARTIAL_INJECTION_AND_TOTAL_RELATION);

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_SURJECTIVE_RELATION = {
			PARTIAL_FUNCTION_MACRO, SURJECTIVE_RELATION_MACRO };

	public static final SMTPredefinedMacro PARTIAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_SURJECTION,
			"("
					+ PARTIAL_SURJECTION
					+ "(lambda (?PAR_SUR_0 ('s Bool))(?PAR_SUR_1 ('s Bool)) . (lambda (?PAR_SUR_2 ((Pair 's 't) Bool)) .  (and ((pfun ?PAR_SUR_0 ?PAR_SUR_1) ?PAR_SUR_2) (surp ?PAR_SUR_1 ?PAR_SUR_2)))))",
			3, false, false, PARTIAL_FUNCTION_AND_SURJECTIVE_RELATION);

	private static SMTPredefinedMacro[] TOTAL_RELATION_AND_PARTIAL_SURJECTION = {
			TOTAL_RELATION_MACRO, PARTIAL_SURJECTION_MACRO };

	public static final SMTPredefinedMacro TOTAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTION,
			"("
					+ TOTAL_SURJECTION
					+ "(lambda (?TOT_SUR_0 ('s Bool)) (?TOT_SUR_1 ('s Bool)) . (lambda (?TOT_SUR_2 ((Pair 's 't) Bool)) . (and ((psur ?TOT_SUR_0 ?TOT_SUR_1) ?TOT_SUR_2) (totp ?TOT_SUR_0 ?TOT_SUR_2)))))",
			4, false, false, TOTAL_RELATION_AND_PARTIAL_SURJECTION);

	private static final SMTPredefinedMacro[] TOTAL_SURJECTION_AND_TOTAL_INJECTION = {
			TOTAL_SURJECTION_MACRO, TOTAL_INJECTION_MACRO };

	public static final SMTPredefinedMacro TOTAL_BIJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_BIJECTION,
			"("
					+ TOTAL_BIJECTION
					+ "(lambda (?TOTAL_BIJECTION_0 ('s Bool)) (?TOTAL_BIJECTION_1 ('s Bool)) . (lambda (?TOTAL_BIJECTION_2 ((Pair 's 't) Bool)) . (and ((tsur ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1) ?TOTAL_BIJECTION_2) ((tinj ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1)?TOTAL_BIJECTION_2)))))",
			5, false, false, TOTAL_SURJECTION_AND_TOTAL_INJECTION);

	private static SMTPredefinedMacro[] INS = { IN_MACRO };

	public static final SMTPredefinedMacro ISMIN_MACRO = new SMTPredefinedMacro(
			ISMIN,
			"("
					+ ISMIN
					+ "(lambda (?ISMIN_0 Int) (?ISMIN_1 (Int Bool)) . (and(in ?ISMIN_0 ?ISMIN_1)(forall (?ISMIN_2 Int) . (implies (in ?ISMIN_2 ?ISMIN_1)(<= ?ISMIN_0 ?ISMIN_2))))))",
			1, false, false, INS);

	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			"("
					+ ISMAX
					+ "(lambda (?ISMAX_0 Int) (?ISMAX_1 (Int Bool)) . (and(in ?ISMAX_0 ?ISMAX_1)(forall (?ISMAX_2 Int) . (implies (in ?ISMAX_2 ?ISMAX_1)(<= ?ISMAX_2 ?ISMAX_0))))))",
			1, false, false, INS);

	private static SMTPredefinedMacro[] IN_AND_RANGE_INTEGER = { IN_MACRO,
			RANGE_INTEGER_MACRO };

	public static final SMTPredefinedMacro FINITE_MACRO = new SMTPredefinedMacro(
			FINITE,
			"("
					+ FINITE
					+ "(lambda (?FINITE_0 Bool) (?FINITE_1 ('s Bool)) (?FINITE_2 ('s Int)) (?FINITE_3 Int) . (iff ?FINITE_0 (and (forall (?FINITE_4 's) . (implies (in ?FINITE_4 ?FINITE_1)(in (?FINITE_2 ?FINITE_4)(range 1 ?FINITE_3))))(forall (?FINITE_4 's)(?FINITE_5 's) . (implies (and (in ?FINITE_4 ?FINITE_1)(in ?FINITE_5 ?FINITE_1)(not (= ?FINITE_4 ?FINITE_5)))(not (= (?FINITE_2 ?FINITE_4)(?FINITE_2 ?FINITE_5)))))))))",
			1, false, false, IN_AND_RANGE_INTEGER);

	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"("
					+ CARD
					+ "(lambda (?CARD_0 ('s Bool)) (?CARD_1 ('s Int)) (?CARD_2 Int) . (and (forall (?CARD_3 Int) . (implies (in ?CARD_3 (range 1 ?CARD_2))(exists (?CARD_4 's) . (and (in ?CARD_4 ?CARD_0) (= (?CARD_1 ?CARD_4) ?CARD_3)))))(forall (?CARD_4 's) . (implies (in ?CARD_4 ?CARD_0) (in (?CARD_1 ?CARD_4) (range 1 ?CARD_2))))(forall (?CARD_5 's) (?CARD_6 's) . (implies (and (in ?CARD_5 ?CARD_0) (in ?CARD_6 ?CARD_0) (= (?CARD_1 ?CARD_5) (?CARD_1 ?CARD_6))) (= ?CARD_5 ?CARD_6))))))",
			1, false, false, IN_AND_RANGE_INTEGER);

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

	/**
	 * This method checks if the macro is already defined in the signature
	 * 
	 * @param macro
	 *            the macro to be checked
	 * @param signature
	 *            the signature used for the check
	 */
	public static void checkIfMacroIsDefinedInTheSignature(
			final SMTMacroSymbol macro, final SMTSignatureV1_2Verit signature) {
		final Set<SMTMacro> macros = signature.getMacros();
		for (final SMTMacro smtMacro : macros) {
			if (smtMacro.getMacroName().equals(macro.getName())) {
				return;
			}
		}
		throw new IllegalArgumentException(
				"A macro cannot be created without being defined in the signature. The macro which was unduly created was: "
						+ macro.getName());
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
