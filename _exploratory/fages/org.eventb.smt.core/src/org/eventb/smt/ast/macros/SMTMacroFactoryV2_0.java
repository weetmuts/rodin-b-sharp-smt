/*******************************************************************************
 * Copyright (c) 2012 UFRN. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	UFRN - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.macros;

import static org.eventb.smt.ast.macros.SMTMacroSymbol.*;
import static org.eventb.smt.ast.symbols.SMTSymbol.INT;

import java.util.Set;

import org.eventb.smt.ast.SMTSignatureV1_2Verit;
import org.eventb.smt.ast.SMTSignatureV2_0Verit;

public class SMTMacroFactoryV2_0 extends SMTMacroFactory {

	public static final SMTPredefinedMacro NAT_MACRO = new SMTPredefinedMacro(
			NAT, NAT + " ((?NAT_0 Int)) (Int Bool) (<= 0 ?NAT_0)", 0, false,
			false, EMPTY_MACROS);

	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(par (t) (" + EMPTY + " ((t Int)) (t Bool) false))", 0,
			false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro INTEGER_MACRO = new SMTPredefinedMacro(
			INT, "(par (t) (" + INT + " ((?INT_0 Int)) (t Bool) true)))", 0,
			false, false, EMPTY_MACROS);

	public static SMTPredefinedMacro BUNION_MACRO = new SMTPredefinedMacro(
			BUNION,
			"(par (t) ("
					+ BUNION
					+ " ((?UNION_0 (t Bool)) (?UNION_1 (t Bool))) (t Bool) (lambda ((?UNION_2 t)) (or (?UNION_0 ?UNION_2) (?UNION_1 ?UNION_2)))))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro IN_MACRO = new SMTPredefinedMacro(
			IN, "(par (t) (" + IN
					+ " ((?IN_0 t) (?IN_1 (t Bool))) (t Bool) (?IN_1 ?IN_0)))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"(par (t) ("
					+ BINTER
					+ "((?BINTER_0 (t Bool)) (?BINTER_1 (t Bool))) (t Bool) (lambda ((?BINTER_2 t)) (and (?BINTER_0 ?BINTER_2) (?BINTER_1 ?BINTER_2)))))",
			0, false, false, EMPTY_MACROS);

	// TODO Test
	public static final SMTPredefinedMacro RELATION_MACRO = new SMTPredefinedMacro(
			RELATION,
			"(par (s t) ("
					+ RELATION
					+ " ((?REL_0 (s Bool)) (?REL_1 (t Bool))) (s Bool) (lambda (?REL_2  ((Pair s t) Bool))  (forall (?REL_3 (Pair s t))  (implies (?REL_2 ?REL_3) (and (?REL_0 (fst ?REL_3)) (?REL_1 (snd ?REL_3))))))))",
			1, true, true, EMPTY_MACROS);

	// TODO Test
	public static final SMTPredefinedMacro RANGE_INTEGER_MACRO = new SMTPredefinedMacro(
			RANGE_INTEGER,
			RANGE_INTEGER
					+ " ((?RI_0 Int) (?RI_1 Int)) (Int Bool) (lambda ((?RI_2 Int)) (and (<= ?RI_0 ?RI_2) (<= ?RI_2 ?RI_1)))",
			0, false, false, EMPTY_MACROS);

	// TODO
	public static SMTPredefinedMacro FUNP_MACRO = BUNION_MACRO;

	private static SMTPredefinedMacro[] REL_AND_FUNP_AND_IN = { RELATION_MACRO,
			FUNP_MACRO, IN_MACRO };

	private static SMTPredefinedMacro[] IN_AND_RANGE_INTEGER = { IN_MACRO,
			RANGE_INTEGER_MACRO };

	// TODO: test
	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"(par (s) ("
					+ CARD
					+ " ((?CARD_0 (s Bool)) (?CARD_1 (s Int)) (?CARD_2 Int)) (and (forall (?CARD_3 Int) (implies (in ?CARD_3 (range 1 ?CARD_2))(exists (?CARD_4 s) (and (in ?CARD_4 ?CARD_0) (= (?CARD_1 ?CARD_4) ?CARD_3)))))(forall (?CARD_4 s) (implies (in ?CARD_4 ?CARD_0) (in (?CARD_1 ?CARD_4) (range 1 ?CARD_2))))(forall (?CARD_5 s) (?CARD_6 s) (implies (and (in ?CARD_5 ?CARD_0) (in ?CARD_6 ?CARD_0) (= (?CARD_1 ?CARD_5) (?CARD_1 ?CARD_6))) (= ?CARD_5 ?CARD_6))))))",
			1, false, false, IN_AND_RANGE_INTEGER);

	// FIXME to SMT 2.0
	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"("
					+ PARTIAL_FUNCTION
					+ "(lambda (?PAR_FUN_0 ('s Bool)) (?PAR_FUN_1  ('t Bool)) (lambda (?PAR_FUN_2 ((Pair 's 't) Bool)) (and (in ?PAR_FUN_2 (rel ?PAR_FUN_0 ?PAR_FUN_1)) (funp ?PAR_FUN_2)))))",
			3, false, false, REL_AND_FUNP_AND_IN);

	public static final SMTPredefinedMacro SUBSETEQ_MACRO = new SMTPredefinedMacro(
			SUBSETEQ,
			"(par (t) ("
					+ SUBSETEQ
					+ " ((?SUBSETEQ_0 (t Bool)) (?SUBSETEQ_1 (t Bool))) (t Bool) (forall ((?SUBSETEQ_2 t)) (=> (?SUBSETEQ_0 ?SUBSETEQ_2) (?SUBSETEQ_1 ?SUBSETEQ_2)))))",
			0, false, false, EMPTY_MACROS);

	private static SMTPredefinedMacro[] SUBSETEQS = { SUBSETEQ_MACRO };

	public static final SMTPredefinedMacro NAT1_MACRO = new SMTPredefinedMacro(
			NAT1, NAT1 + " ((?NAT1_0 Int)) (Int Bool) (<= 1 ?NAT1_0)", 0,
			false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro SUBSET_MACRO = new SMTPredefinedMacro(
			SUBSET,
			"(par (t) ("
					+ SUBSET
					+ " ((?SUBSET_0 (t Bool)) (?SUBSET_1 (t Bool))) (t Bool) (and (subseteq ?SUBSET_0 ?SUBSET_1) (not (= ?SUBSET_0 ?SUBSET_1)))))",
			1, false, false, SUBSETEQS);

	public static final SMTPredefinedMacro SETMINUS_MACRO = new SMTPredefinedMacro(
			SETMINUS,
			"(par (t) ("
					+ SETMINUS
					+ " ((?SM_0 (t Bool)) (?SM_1 (t Bool))) (t Bool) (lambda ((?SM_2 t)) (and (?SM_0 ?SM_2) (not (?SM_1 ?SM_2))))))",
			0, false, false, EMPTY_MACROS);

	private static SMTPredefinedMacro[] INS = { IN_MACRO };

	public static final SMTPredefinedMacro BOOL_SET_MACRO = new SMTPredefinedMacro(
			BOOLS, BOOLS + " ((?BOOL_0 BOOL)) (Bool Bool) true", 0, false,
			false, EMPTY_MACROS);

	// TODO: Create test
	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			ISMAX
					+ " ((?ISMAX_0 Int) (?ISMAX_1 (Int Bool))) (Int Bool) (and (in ?ISMAX_0 ?ISMAX_1) (forall ((?ISMAX_2 Int)) (=> (in ?ISMAX_2 ?ISMAX_1) (<= ?ISMAX_2 ?ISMAX_0))))",
			1, false, false, INS);

	// TODO Implement 2.0 version of the macros below:
	public static SMTPredefinedMacro TOTAL_FUNCTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro PARTIAL_INJECTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro TOTAL_INJECTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro PARTIAL_SURJECTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro TOTAL_SURJECTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro TOTAL_BIJECTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro ISMIN_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro REL_OVR_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro ID_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro RANGE_SUBSTRACTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro FCOMP_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro RANGE_RESTRICTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro TOTAL_RELATION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro SURJECTIVE_RELATION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro TOTAL_SURJECTIVE_RELATION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro INJP_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro BCOMP_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro RELATIONAL_IMAGE_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro DOM_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro DOMAIN_RESTRICTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro DOMAIN_SUBSTRACTION_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro INVERSE_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro CARTESIAN_PRODUCT_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro RANGE_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro SUCCESSOR_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro PREDECESSOR_MACRO = BUNION_MACRO;
	public static SMTPredefinedMacro FINITE_MACRO = BUNION_MACRO;

	public static enum SMTVeriTOperatorV2_0 {
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
		SMTVeriTOperatorV2_0(final SMTPredefinedMacro symbol) {
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

	/**
	 * Adds a predefined macro and other macros on which it depends on the
	 * signature
	 * 
	 * @param operator
	 *            The operator that represents the predefined macro
	 * @param signature
	 *            The signature that will receive the macro
	 */
	public static void addPredefinedMacroInSignature(
			final SMTPredefinedMacro operator,
			final SMTSignatureV2_0Verit signature) {
		final SMTPredefinedMacro pmacro = operator;
		if (pmacro.usesPairFunctionAndSort()) {
			signature.addPairSortAndFunction();
		}
		if (pmacro.usesFstAndSndFunctions()) {
			signature.addFstAndSndAuxiliarFunctions();
		}
		for (final SMTPredefinedMacro macro : pmacro.getRequiredMacros()) {
			addPredefinedMacroInSignature(macro, signature);
			signature.addMacro(macro);
		}
		signature.addMacro(pmacro);
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
			final SMTMacroSymbol macro, final SMTSignatureV2_0Verit signature) {
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

	public static final SMTMacroSymbol getMacroSymbol(
			final SMTVeriTOperatorV2_0 operator,
			final SMTSignatureV2_0Verit signature) {
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

	public static SMTMacroSymbol getMacroSymbol(
			final SMTVeriTOperatorV2_0 operator,
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

}
