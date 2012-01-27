/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.macros;

import static org.eventb.smt.ast.macros.SMTMacroSymbol.BCOMP;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.BINTER;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.BUNION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.CARD;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.CARTESIAN_PRODUCT;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.DOM;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.DOMAIN_RESTRICTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.DOMAIN_SUBSTRACTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.EMPTY;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.FCOMP;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.FINITE;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.FUNP;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.ID;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.IN;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.INJP;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.INV;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.ISMAX;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.ISMIN;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.MAPSTO;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.NAT;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.NAT1;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.OVR;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_FUNCTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_INJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PARTIAL_SURJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.PRED;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RANGE;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RANGE_INTEGER;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RANGE_RESTRICTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RANGE_SUBSTRACTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RELATION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.RELATIONAL_IMAGE;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.SETMINUS;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.SUBSET;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.SUBSETEQ;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.SUCC;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.SURJECTIVE_RELATION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_BIJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_FUNCTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_INJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_RELATION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_SURJECTION;
import static org.eventb.smt.ast.macros.SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION;
import static org.eventb.smt.ast.symbols.SMTSymbol.BOOLS;
import static org.eventb.smt.ast.symbols.SMTSymbol.INT;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.ast.theories.VeritPredefinedTheoryV1_2.POLYMORPHIC;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;

import java.util.HashSet;
import java.util.Set;

import org.eventb.smt.ast.SMTFactoryVeriT;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTSignatureV1_2Verit;
import org.eventb.smt.ast.SMTTerm;
import org.eventb.smt.ast.SMTVar;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.symbols.SMTVarSymbol;
import org.eventb.smt.ast.theories.SMTTheoryV1_2;
import org.eventb.smt.ast.theories.VeriTBooleansV1_2;
import org.eventb.smt.ast.theories.VeritPredefinedTheoryV1_2;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and creates macro
 * enumerations, which are used to translate in extension.
 * 
 * @author vitor almeida
 * 
 */
public abstract class SMTMacroFactory {

	public final static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	private static final SMTPolymorphicSortSymbol FST_RETURN_SORT = SMTFactoryVeriT
			.makePolymorphicSortSymbol(FST_PAIR_SORT_NAME);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = SMTFactoryVeriT
			.makePolymorphicSortSymbol(SND_PAIR_SORT_NAME);

	public static SMTSortSymbol PAIR_SORT = SMTFactoryVeriT
			.makePolymorphicSortSymbol("(Pair 's 't)");

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_ARG_SORTS, PAIR_SORT, false, !PREDEFINED, V1_2);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	public static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, false, !PREDEFINED, V1_2);

	public static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, false, !PREDEFINED, V1_2);

	public static final SMTSymbol[] PAIR_AND_FST_AND_SND_SYMBOLS = { PAIR_SORT,
			PAIR_SYMBOL, FST_SYMBOL, SND_SYMBOL };

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	protected final Set<String> qSymbols = new HashSet<String>();

	protected static SMTPredefinedMacro[] EMPTY_MACROS = {};

	public static SMTPredefinedMacro FCOMP_MACRO = new SMTPredefinedMacro(
			FCOMP,
			"(lambda (?FCOMP_0 ((Pair 's 't) Bool)) (?FCOMP_1 ((Pair 't 'u) Bool)) . (lambda (?FCOMP_2 (Pair 's 'u)) . (exists (?FCOMP_3 't) . (and (?FCOMP_0 (pair (fst ?FCOMP_2) ?FCOMP_3)) (?FCOMP_1 (pair ?FCOMP_3 (snd ?FCOMP_2)))))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro REL_OVR_MACRO = new SMTPredefinedMacro(
			OVR,
			"(lambda (?OVR_0 ((Pair 's 't) Bool)) (?OVR_1 ((Pair 's 't) Bool)) . (lambda (?OVR_2 (Pair 's 'u)) . (or (?OVR_1 ?OVR_2) (and (?OVR_0 ?OVR_2)(not(exists (?OVR_3 (Pair 's 't)) . (and (?OVR_1 ?OVR_3)(= (fst ?OVR_3)(fst ?OVR_2)))))))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(lambda (?EMPTY_0 't). false)", 0, false, false,
			EMPTY_MACROS);

	public static final SMTPredefinedMacro SUBSETEQ_MACRO = new SMTPredefinedMacro(
			SUBSETEQ,
			"(lambda (?SUBSETEQ_0 ('t Bool)) (?SUBSETEQ_1 ('t Bool)) . (forall (?SUBSETEQ_2 't). (implies (?SUBSETEQ_0 ?SUBSETEQ_2) (?SUBSETEQ_1 ?SUBSETEQ_2))))",
			0, false, false, EMPTY_MACROS);

	private static SMTPredefinedMacro[] SUBSETEQS = { SUBSETEQ_MACRO };

	public static final SMTPredefinedMacro SUBSET_MACRO = new SMTPredefinedMacro(
			SUBSET,
			"(lambda (?SUBSET_0 ('t Bool)) (?SUBSET_1 ('t Bool)) . (and (subseteq ?SUBSET_0 ?SUBSET_1) (not (= ?SUBSET_0 ?SUBSET_1))))",
			1, false, false, SUBSETEQS);

	public static final SMTPredefinedMacro RANGE_INTEGER_MACRO = new SMTPredefinedMacro(
			RANGE_INTEGER,
			"(lambda (?RANGE_INTEGER_0 Int) (?RANGE_INTEGER_1 Int) . (lambda (?RANGE_INTEGER_2 Int) . (and (<= ?RANGE_INTEGER_0 ?RANGE_INTEGER_2) (<= ?RANGE_INTEGER_2 ?RANGE_INTEGER_1))))",
			0, false, false, EMPTY_MACROS);

	private static SMTPredefinedMacro[] FCOMPS = { FCOMP_MACRO };

	public static final SMTPredefinedMacro BCOMP_MACRO = new SMTPredefinedMacro(
			BCOMP,
			"(lambda (?BCOMP_0 ((Pair 's 't) Bool)) (?BCOMP_1 ((Pair 't 'u) Bool)) . (fcomp ?BCOMP_1 ?BCOMP_0))",
			2, false, false, FCOMPS);

	public static final SMTPredefinedMacro RANGE_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			RANGE_SUBSTRACTION,
			"(lambda (?RANGE_SUBS_0 ((Pair 's 't) Bool))(?RANGE_SUBS_1 ('t Bool)) . (lambda (?RANGE_SUBS_2 (Pair 's 't)) . (and (?RANGE_SUBS_0 ?RANGE_SUBS_2)(not (?RANGE_SUBS_1 (snd ?RANGE_SUBS_2))))))",
			1, true, true, EMPTY_MACROS);
	public static final SMTPredefinedMacro RANGE_RESTRICTION_MACRO = new SMTPredefinedMacro(
			RANGE_RESTRICTION,
			"(lambda (?RANGE_RES_0 ((Pair 's 't) Bool))(?RANGE_RES_1 ('t Bool)) . (lambda (?RANGE_RES_2 (Pair 's 't)) . (and (?RANGE_RES_0 ?RANGE_RES_2)(?RANGE_RES_1 (snd ?RANGE_RES_2)))))",
			1, true, true, EMPTY_MACROS);

	// Using the totp (total property) to define this macro
	public static final SMTPredefinedMacro TOTAL_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_RELATION,
			"(lambda (?TOTAL_RELATION_0 ('s Bool)) (?TOTAL_RELATION_1 ((Pair 's 't) Bool)) . (forall (?TOTAL_RELATION_2 (Pair 's 't)) . (iff (?TOTAL_RELATION_1 ?TOTAL_RELATION_2) (?TOTAL_RELATION_0 (fst ?TOTAL_RELATION_2)))))",
			1, true, true, EMPTY_MACROS);

	// Using the surp (surjective property) to define this macro
	public static final SMTPredefinedMacro SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			SURJECTIVE_RELATION,
			"(lambda (?SUR_REL_0 ('t Bool)) (?SUR_REL_1 ((Pair 's 't) Bool)) . (forall (?SUR_REL_2 (Pair 's 't)) . (= (?SUR_REL_1 ?SUR_REL_2) (?SUR_REL_0 (snd ?SUR_REL_2)))))",
			1, true, true, EMPTY_MACROS);
	// Using the conjunction of surjective relation and total relation macros
	public static final SMTPredefinedMacro TOTAL_SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTIVE_RELATION,
			"(lambda (?T_SUR_REL_0 ('t Bool)) (?T_SUR_REL_1 ((Pair 's 't) Bool)) . (and (forall (?T_SUR_REL_2 (Pair 's 't)) . (= (?T_SUR_REL_1 ?T_SUR_REL_2) (?T_SUR_REL_0 (snd ?T_SUR_REL_2))))  (forall (?T_SUR_REL_3 (Pair 's 't)) . (= (?T_SUR_REL_1 ?T_SUR_REL_3) (?T_SUR_REL_0 (fst ?T_SUR_REL_3))))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro FUNP_MACRO = new SMTPredefinedMacro(
			FUNP,
			"(lambda (?FUNP_0 ((Pair 's 't )Bool)) . (forall (?FUNP_1 (Pair 's 't))(?FUNP_2 (Pair 's 't)) . (implies (and (?FUNP_0 ?FUNP_1) (?FUNP_0 ?FUNP_2)) (implies (= (fst ?FUNP_1) (fst ?FUNP_2))(= (snd ?FUNP_1) (snd ?FUNP_2))))))",
			2, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro ID_MACRO = new SMTPredefinedMacro(
			ID, "(lambda (?ID_0 (Pair 't 't)) . (= (fst ?ID_0)(snd ?ID_0)))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro NAT_MACRO = new SMTPredefinedMacro(
			NAT, "(lambda (?NAT_0 Int) . (<= 0 ?NAT_0))", 0, false, false,
			EMPTY_MACROS);

	public static final SMTPredefinedMacro NAT1_MACRO = new SMTPredefinedMacro(
			NAT1, "(lambda (?NAT1_0 Int) . (<= 1 ?NAT1_0))", 0, false, false,
			EMPTY_MACROS);

	public static final SMTPredefinedMacro INVERSE_MACRO = new SMTPredefinedMacro(
			INV,
			"(lambda (?INV_0 ((Pair 's 't) Bool)) . (lambda (?INV_1 (Pair 's 't)) . (?INV_0 (pair (snd ?INV_1)(fst ?INV_1)))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro DOM_MACRO = new SMTPredefinedMacro(
			DOM,
			"(lambda (?DOM_0 ((Pair 't1 't2) Bool)) . (lambda (?DOM_1 't1) . (exists (?DOM_2 't2) . (?DOM_0 (pair ?DOM_1 ?DOM_2)))))",
			0, false, false, EMPTY_MACROS);

	private static SMTPredefinedMacro[] FUNP_AND_INV = { FUNP_MACRO,
			INVERSE_MACRO };

	public static final SMTPredefinedMacro INJP_MACRO = new SMTPredefinedMacro(
			INJP,
			"(lambda (?INJP_0 ((Pair 's 't )Bool)) . (funp (inv ?INJP_0)))", 2,
			false, false, FUNP_AND_INV);

	public static final SMTPredefinedMacro CARTESIAN_PRODUCT_MACRO = new SMTPredefinedMacro(
			CARTESIAN_PRODUCT,
			"(lambda (?CARTESIAN_PRODUCT_0 ('s Bool))(?CARTESIAN_PRODUCT_1 ('t Bool)) . (lambda (?CARTESIAN_PRODUCT_2 (Pair 's 't)) . (and (?CARTESIAN_PRODUCT_0 (fst ?CARTESIAN_PRODUCT_2)) (?CARTESIAN_PRODUCT_1 (snd ?CARTESIAN_PRODUCT_2)))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro DOMAIN_RESTRICTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_RESTRICTION,
			"(lambda (?DOMAIN_RESTRICTION_0 ('s Bool))(?DOMAIN_RESTRICTION_1 ((Pair 's 't) Bool)) . (lambda (?DOMAIN_RESTRICTION_2 (Pair 's 't)) . (and (?DOMAIN_RESTRICTION_1 ?DOMAIN_RESTRICTION_2)(?DOMAIN_RESTRICTION_0 (fst ?DOMAIN_RESTRICTION_2)))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro DOMAIN_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_SUBSTRACTION,
			"(lambda (?DOMAIN_SUBSTRACTION_0 ('s Bool))(?DOMAIN_SUBSTRACTION_1 ((Pair 's 't) Bool)) . (lambda (?DOMAIN_SUBSTRACTION_2 (Pair 's 't)) . (and (?DOMAIN_SUBSTRACTION_1 ?DOMAIN_SUBSTRACTION_2)(not (?DOMAIN_SUBSTRACTION_0 (fst ?DOMAIN_SUBSTRACTION_2))))))",
			1, true, true, EMPTY_MACROS);

	public static final SMTPredefinedMacro RELATIONAL_IMAGE_MACRO = new SMTPredefinedMacro(
			RELATIONAL_IMAGE,
			"(lambda (?RELATIONAL_IMAGE_0 ((Pair 's 't) Bool)(?RELATIONAL_IMAGE_1 ('s Bool)(lambda (?RELATIONAL_IMAGE_2 't) (exists (?RELATIONAL_IMAGE_3 's)(and (?RELATIONAL_IMAGE_1 ?RELATIONAL_IMAGE_3)(?RELATIONAL_IMAGE_0 (pair ?RELATIONAL_IMAGE_3 ?RELATIONAL_IMAGE_2))))))))",
			1, true, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro SETMINUS_MACRO = new SMTPredefinedMacro(
			SETMINUS,
			"(lambda (?SETMINUS_0 ('t Bool)) (?SETMINUS_1 ('t Bool)) . (lambda (?SETMINUS_2 't) . (and (?SETMINUS_0 ?SETMINUS_2) (not (?SETMINUS_1 ?SETMINUS_2)))))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro RANGE_MACRO = new SMTPredefinedMacro(
			RANGE,
			"(lambda (?RANGE_0 ((Pair 's 't) Bool)) . (lambda (?RANGE_1 't) . (exists (?RANGE_2 's) . (?RANGE_0 (pair ?RANGE_2 ?RANGE_1)))))",
			1, true, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro BOOL_SET_MACRO = new SMTPredefinedMacro(
			BOOLS, "(lambda (?BOOL_0 BOOL). true)", 0, false, false,
			EMPTY_MACROS);

	public static final SMTPredefinedMacro SUCCESSOR_MACRO = new SMTPredefinedMacro(
			SUCC,
			"(lambda(?SUCC_1 (Pair Int Int)) . (exists (?SUCC_0 Int) . (= ?SUCC_1 (pair ?SUCC_0 (+ ?SUCC_0 1)))))",
			1, true, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro PREDECESSOR_MACRO = new SMTPredefinedMacro(
			PRED,
			"(lambda(?PRED_0 (Pair Int Int)) . (exists (?PRED_1 Int) . (= ?PRED_0 (pair (+ ?PRED_1 1) ?PRED_1))))",
			1, true, false, EMPTY_MACROS);

	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };
	public static SMTPolymorphicSortSymbol[] POLYMORPHICS = { POLYMORPHIC };
	private static SMTSortSymbol[] ISMIN_MAX_SORTS = {
			SMTTheoryV1_2.Ints.getInt(), POLYMORPHIC };
	private static SMTSortSymbol[] FINITE_SORTS = {
			VeriTBooleansV1_2.getInstance().getBooleanSort(), POLYMORPHIC,
			SMTTheoryV1_2.Ints.getInt(), SMTTheoryV1_2.Ints.getInt() };

	private static SMTSortSymbol[] CARD_SORTS = { POLYMORPHIC,
			SMTTheoryV1_2.Ints.getInt(), SMTTheoryV1_2.Ints.getInt() };

	protected static final SMTMacroSymbol RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE, POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);

	protected static final SMTMacroSymbol BOOL_SET_SYMBOL = new SMTMacroSymbol(
			BOOLS, EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static final SMTMacroSymbol INTEGER_SYMBOL = new SMTMacroSymbol(
			INT, EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(CARD,
			CARD_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(FINITE,
			FINITE_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(ISMAX,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(ISMIN,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(SETMINUS,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			RELATIONAL_IMAGE, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			CARTESIAN_PRODUCT, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_BIJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(DOM,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(INV,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(NAT1,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(NAT,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(ID,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC,
			!PREDEFINED, V1_2);
	protected static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			RANGE_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			RANGE_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE_INTEGER, VeritPredefinedTheoryV1_2.getIntIntTab(),
			POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SUBSETEQ, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(SUBSET,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(IN,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(BUNION,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(BINTER,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(FCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BCOMP_SYMBOL = new SMTMacroSymbol(BCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(OVR,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(EMPTY,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUCC_SYMBOL = new SMTMacroSymbol(SUCC,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PRED_SYMBOL = new SMTMacroSymbol(PRED,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);

	/**
	 * Retrieves the name of the identifiers that have a question mark as a
	 * prefix.
	 * 
	 * @return the identifiers as defined above.
	 */
	public Set<String> getqSymbols() {
		return qSymbols;
	}

	/**
	 * Creates a macro of sets defined in extension which the elements are a
	 * mapping. An enumeration macro is of the form:
	 * 
	 * (macroName (lambda (x1 s1)(x2 s2) . (or (pair (= x1 t1a)(= x2 t1b) · · ·
	 * (= x1 tna)(= 2 tnb)))));
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param varName1
	 *            The first variable (in the example above: (x1 s1))
	 * @param terms
	 *            The terms of the set defined by extension (in the example
	 *            above: t1a, t1b ... tna,tnb)
	 * @param signature
	 *            The signature used to add predefined macros if necessary
	 * @return The macro of the set defined in extension
	 */
	public static SMTPairEnumMacro makePairEnumerationMacro(
			final String macroName, final SMTVarSymbol varName1,
			final SMTTerm[] terms, final SMTSignatureV1_2Verit signature) {
		signature.addPairSortAndFunction();
		return new SMTPairEnumMacro(macroName, varName1, terms, 1);
	}

	public static SMTEnumMacro makeEnumMacro(final String macroName,
			final SMTVarSymbol varName, final SMTTerm... terms) {

		return new SMTEnumMacro(macroName, varName, terms, 0);
	}

	/**
	 * Creates a macro from lambda expressions and sets defined in extension.
	 * 
	 * This macro created has the following form:
	 * <p>
	 * 
	 * (macroName (lambda (?y s) . (exists (?x1 s1) ... (?xn sn) (and (= ?y
	 * (E(?x1 ... ?xn))) P(?x1..?xn)))))
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param terms
	 *            The terms that contains the bound identifier declarations (in
	 *            the example above: (?x1 s1) ... (?xn sn))
	 * @param lambdaVar
	 *            The lambda variable with the same type as the expression (in
	 *            the example above: ?y)
	 * @param formula
	 * @param expression
	 * @param signature
	 * @return a new set comprehension macro
	 */
	public static SMTSetComprehensionMacro makeSetComprehensionMacro(
			final String macroName, final SMTTerm[] terms,
			final SMTVarSymbol lambdaVar, final SMTFormula formula,
			final SMTTerm expression, final SMTSignatureV1_2Verit signature) {
		signature.addPairSortAndFunction();

		final SMTVarSymbol[] qVars = new SMTVarSymbol[terms.length];
		for (int i = 0; i < terms.length; i++) {
			final SMTTerm term = terms[i];
			if (term instanceof SMTVar) {
				final SMTVar var = (SMTVar) term;
				qVars[i] = var.getSymbol();
			} else {
				throw new IllegalArgumentException(
						"The term should be an SMTVar");
			}
		}
		return new SMTSetComprehensionMacro(macroName, qVars, lambdaVar,
				formula, expression, 1);
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
			final SMTSignatureV1_2Verit signature) {
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
	 * Creates and returns a macroSymbol.
	 * 
	 * @param macroName
	 *            The string representation of the macro
	 * @return a new macroSymbol
	 */
	public static SMTMacroSymbol makeMacroSymbol(final String macroName,
			final SMTSortSymbol sort) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT, sort, false, V1_2);
	}
}
