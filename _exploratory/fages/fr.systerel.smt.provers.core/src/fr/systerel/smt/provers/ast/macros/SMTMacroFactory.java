/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.ast.macros;

import static fr.systerel.smt.provers.ast.SMTSymbol.BOOLS;
import static fr.systerel.smt.provers.ast.SMTSymbol.INT;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import static fr.systerel.smt.provers.ast.VeritPredefinedTheory.POLYMORPHIC;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.BCOMP;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.BINTER;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.BUNION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.CARD;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.CARTESIAN_PRODUCT;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.DOM;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.DOMAIN_RESTRICTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.DOMAIN_SUBSTRACTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.EMPTY;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.FCOMP;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.FINITE;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.FUNP;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.ID;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.IN;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.INJP;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.INV;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.ISMAX;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.ISMIN;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.MAPSTO;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.NAT;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.NAT1;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.OVR;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_FUNCTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_INJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_SURJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PRED;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_INTEGER;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_RESTRICTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_SUBSTRACTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RELATION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RELATIONAL_IMAGE;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SETMINUS;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SUBSET;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SUBSETEQ;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SUCC;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SURJECTIVE_RELATION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_BIJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_FUNCTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_INJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_RELATION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_SURJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION;

import java.util.HashSet;
import java.util.Set;

import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFactoryVeriT;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTPolymorphicSortSymbol;
import fr.systerel.smt.provers.ast.SMTSignatureVerit;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.ast.SMTVarSymbol;
import fr.systerel.smt.provers.ast.VeritPredefinedTheory;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and creates macro
 * enumerations, which are used to translate in extension.
 * 
 * @author vitor almeida
 * 
 */
public class SMTMacroFactory {

	public static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	private static final SMTPolymorphicSortSymbol FST_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(FST_PAIR_SORT_NAME);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(SND_PAIR_SORT_NAME);

	public static SMTSortSymbol PAIR_SORT = SMTFactory
			.makePolymorphicSortSymbol("(Pair 's 't)");

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_ARG_SORTS, PAIR_SORT, false, !PREDEFINED);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	public static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, false, !PREDEFINED);

	public static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, false, !PREDEFINED);

	public static final SMTSymbol[] PAIR_AND_FST_AND_SND_SYMBOLS = { PAIR_SORT,
			PAIR_SYMBOL, FST_SYMBOL, SND_SYMBOL };

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	private final Set<String> qSymbols = new HashSet<String>();

	private static SMTPredefinedMacro[] EMPTY_MACROS = {};

	public static SMTPredefinedMacro BUNION_MACRO = new SMTPredefinedMacro(
			BUNION,
			"(lambda (?UNION_0 ('t Bool)) (?UNION_1 ('t Bool)) . (lambda (?UNION_2 't) . (or (?UNION_0 ?UNION_2) (?UNION_1 ?UNION_2))))",
			0, false, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"(lambda (?BINTER_0 ('t Bool))(?BINTER_1 ('t Bool)) . (lambda (?BINTER_2 't) . (and (?BINTER_0 ?BINTER_2) (?BINTER_1 ?BINTER_2))))",
			0, false, false, EMPTY_MACROS);

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

	public static final SMTPredefinedMacro IN_MACRO = new SMTPredefinedMacro(
			IN, "(lambda (?IN_0 't) (?IN_1 ('t Bool)) . (?IN_1 ?IN_0))", 0,
			false, false, EMPTY_MACROS);

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
	public static final SMTPredefinedMacro RELATION_MACRO = new SMTPredefinedMacro(
			RELATION,
			"(lambda (?REL_0 ('s Bool)) (?REL_1 ('t Bool)) . (lambda (?REL_2  ((Pair 's 't) Bool)) . (forall (?REL_3 (Pair 's 't)) . (implies (?REL_2 ?REL_3) (and (?REL_0 (fst ?REL_3)) (?REL_1 (snd ?REL_3)))))))",
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

	private static SMTPredefinedMacro[] REL_AND_FUNP_AND_IN = { RELATION_MACRO,
			FUNP_MACRO, IN_MACRO };

	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"(lambda (?PAR_FUN_0 ('s Bool)) (?PAR_FUN_1  ('t Bool)) . (lambda (?PAR_FUN_2 ((Pair 's 't) Bool)) . (and (in ?PAR_FUN_2 (rel ?PAR_FUN_0 ?PAR_FUN_1)) (funp ?PAR_FUN_2))))",
			3, false, false, REL_AND_FUNP_AND_IN);

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_TOTAL_RELATION_AND_IN = {
			PARTIAL_FUNCTION_MACRO, TOTAL_RELATION_MACRO, IN_MACRO };

	public static final SMTPredefinedMacro TOTAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			TOTAL_FUNCTION,
			"(lambda (?TOT_FUN ('s Bool)) (?TOT_FUN_1 ('t Bool)) . (lambda (?TOT_FUN_2 ((Pair 's 't) Bool)) . (and (in ?TOT_FUN_2 (pfun ?TOT_FUN_0 ?TOT_FUN_1)) (totp ?TOT_FUN_0 ?TOT_FUN_2))))",
			3, false, false, PARTIAL_FUNCTION_AND_TOTAL_RELATION_AND_IN);

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

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_INJP = {
			PARTIAL_FUNCTION_MACRO, INJP_MACRO };

	public static final SMTPredefinedMacro PARTIAL_INJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_INJECTION,
			"(lambda (?PAR_INJ ('s Bool)) (?PAR_INJ_1 ('s Bool)) . (lambda (?PAR_INJ_2 ((Pair 's 't) Bool)) . (and ((pfun ?PAR_INJ_0 ?PAR_INJ_1) ?PAR_INJ_2) (injp ?PAR_INJ_2))))",
			3, false, false, PARTIAL_FUNCTION_AND_INJP);

	private static SMTPredefinedMacro[] PARTIAL_INJECTION_AND_TOTAL_RELATION = {
			PARTIAL_INJECTION_MACRO, TOTAL_RELATION_MACRO };

	public static final SMTPredefinedMacro TOTAL_INJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_INJECTION,
			"(lambda (?TOT_INJ_0 ('s Bool))(?TOT_INJ_1 ('s Bool)) . (lambda (?TOT_INJ_2 ((Pair 's 't) Bool)) . (and ((pinj ?TOT_INJ_0 ?TOT_INJ_1) ?TOT_INJ_2) (totp ?TOT_INJ_0 ?TOT_INJ_2))))",
			4, false, false, PARTIAL_INJECTION_AND_TOTAL_RELATION);

	private static SMTPredefinedMacro[] PARTIAL_FUNCTION_AND_SURJECTIVE_RELATION = {
			PARTIAL_FUNCTION_MACRO, SURJECTIVE_RELATION_MACRO };

	public static final SMTPredefinedMacro PARTIAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_SURJECTION,
			"(lambda (?PAR_SUR_0 ('s Bool))(?PAR_SUR_1 ('s Bool)) . (lambda (?PAR_SUR_2 ((Pair 's 't) Bool)) .  (and ((pfun ?PAR_SUR_0 ?PAR_SUR_1) ?PAR_SUR_2) (surp ?PAR_SUR_1 ?PAR_SUR_2))))",
			3, false, false, PARTIAL_FUNCTION_AND_SURJECTIVE_RELATION);

	private static SMTPredefinedMacro[] TOTAL_RELATION_AND_PARTIAL_SURJECTION = {
			TOTAL_RELATION_MACRO, PARTIAL_SURJECTION_MACRO };

	public static final SMTPredefinedMacro TOTAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTION,
			"(lambda (?TOT_SUR_0 ('s Bool)) (?TOT_SUR_1 ('s Bool)) . (lambda (?TOT_SUR_2 ((Pair 's 't) Bool)) . (and ((psur ?TOT_SUR_0 ?TOT_SUR_1) ?TOT_SUR_2) (totp ?TOT_SUR_0 ?TOT_SUR_2))))",
			4, false, false, TOTAL_RELATION_AND_PARTIAL_SURJECTION);

	private static final SMTPredefinedMacro[] TOTAL_SURJECTION_AND_TOTAL_INJECTION = {
			TOTAL_SURJECTION_MACRO, TOTAL_INJECTION_MACRO };

	public static final SMTPredefinedMacro TOTAL_BIJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_BIJECTION,
			"(lambda (?TOTAL_BIJECTION_0 ('s Bool)) (?TOTAL_BIJECTION_1 ('s Bool)) . (lambda (?TOTAL_BIJECTION_2 ((Pair 's 't) Bool)) . (and ((tsur ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1) ?TOTAL_BIJECTION_2) ((tinj ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1)?TOTAL_BIJECTION_2))))",
			5, false, false, TOTAL_SURJECTION_AND_TOTAL_INJECTION);

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

	private static SMTPredefinedMacro[] INS = { IN_MACRO };

	public static final SMTPredefinedMacro ISMIN_MACRO = new SMTPredefinedMacro(
			ISMIN,
			"(lambda (?ISMIN_0 Int) (?ISMIN_1 (Int Bool)) . (and(in ?ISMIN_0 ?ISMIN_1)(forall (?ISMIN_2 Int) . (implies (in ?ISMIN_2 ?ISMIN_1)(<= ?ISMIN_0 ?ISMIN_2)))))",
			1, false, false, INS);

	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			"(lambda (?ISMAX_0 Int) (?ISMAX_1 (Int Bool)) . (and(in ?ISMAX_0 ?ISMAX_1)(forall (?ISMAX_2 Int) . (implies (in ?ISMAX_2 ?ISMAX_1)(<= ?ISMAX_2 ?ISMAX_0)))))",
			1, false, false, INS);

	private static SMTPredefinedMacro[] IN_AND_RANGE_INTEGER = { IN_MACRO,
			RANGE_INTEGER_MACRO };

	public static final SMTPredefinedMacro FINITE_MACRO = new SMTPredefinedMacro(
			FINITE,
			"(lambda (?FINITE_0 Bool) (?FINITE_1 ('s Bool)) (?FINITE_2 ('s Int)) (?FINITE_3 Int) . (iff ?FINITE_0 (and (forall (?FINITE_4 's) . (implies (in ?FINITE_4 ?FINITE_1)(in (?FINITE_2 ?FINITE_4)(range 1 ?FINITE_3))))(forall (?FINITE_4 's)(?FINITE_5 's) . (implies (and (in ?FINITE_4 ?FINITE_1)(in ?FINITE_5 ?FINITE_1)(not (= ?FINITE_4 ?FINITE_5)))(not (= (?FINITE_2 ?FINITE_4)(?FINITE_2 ?FINITE_5))))))))",
			1, false, false, IN_AND_RANGE_INTEGER);

	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"(lambda (?CARD_0 ('s Bool)) (?CARD_1 ('s Int)) (?CARD_2 Int) . (and (forall (?CARD_3 Int) . (implies (in ?CARD_3 (range 1 ?CARD_2))(exists (?CARD_4 's) . (and (in ?CARD_4 ?CARD_0) (= (?CARD_1 ?CARD_4) ?CARD_3)))))(forall (?CARD_4 's) . (implies (in ?CARD_4 ?CARD_0) (in (?CARD_1 ?CARD_4) (range 1 ?CARD_2))))(forall (?CARD_5 's) (?CARD_6 's) . (implies (and (in ?CARD_5 ?CARD_0) (in ?CARD_6 ?CARD_0) (= (?CARD_1 ?CARD_5) (?CARD_1 ?CARD_6))) (= ?CARD_5 ?CARD_6)))))",
			1, false, false, IN_AND_RANGE_INTEGER);

	public static final SMTPredefinedMacro RANGE_MACRO = new SMTPredefinedMacro(
			RANGE,
			"(lambda (?RANGE_0 ((Pair 's 't) Bool)) . (lambda (?RANGE_1 't) . (exists (?RANGE_2 's) . (?RANGE_0 (pair ?RANGE_2 ?RANGE_1)))))",
			1, true, false, EMPTY_MACROS);

	public static final SMTPredefinedMacro INTEGER_MACRO = new SMTPredefinedMacro(
			INT, "(lambda (?INT_0 Int). true)", 0, false, false, EMPTY_MACROS);

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
	private static SMTSortSymbol[] ISMIN_MAX_SORTS = { Ints.getInt(),
			POLYMORPHIC };
	private static SMTSortSymbol[] FINITE_SORTS = {
			fr.systerel.smt.provers.ast.VeriTBooleans.getInstance()
					.getBooleanSort(), POLYMORPHIC, Ints.getInt(),
			Ints.getInt() };

	private static SMTSortSymbol[] CARD_SORTS = { POLYMORPHIC, Ints.getInt(),
			Ints.getInt() };

	private static final SMTMacroSymbol RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE, POLYMORPHICS, POLYMORPHIC, !PREDEFINED);

	private static final SMTMacroSymbol BOOL_SET_SYMBOL = new SMTMacroSymbol(
			BOOLS, EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static final SMTMacroSymbol INTEGER_SYMBOL = new SMTMacroSymbol(
			INT, EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(CARD,
			CARD_SORTS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(FINITE,
			FINITE_SORTS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(ISMAX,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(ISMIN,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(SETMINUS,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			RELATIONAL_IMAGE, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			CARTESIAN_PRODUCT, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_BIJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(DOM,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(INV,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(NAT1,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(NAT,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(ID,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC,
			!PREDEFINED);
	private static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			RANGE_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			RANGE_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE_INTEGER, VeritPredefinedTheory.getIntIntTab(), POLYMORPHIC,
			!PREDEFINED);
	private static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SUBSETEQ, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(SUBSET,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(IN,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(BUNION,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(BINTER,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(FCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol BCOMP_SYMBOL = new SMTMacroSymbol(BCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(OVR,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(EMPTY,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol SUCC_SYMBOL = new SMTMacroSymbol(SUCC,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol PRED_SYMBOL = new SMTMacroSymbol(PRED,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED);

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
	 * Retrieves the name of the identifiers that have a question mark as a
	 * prefix.
	 * 
	 * @return the identifiers as defined above.
	 */
	public Set<String> getqSymbols() {
		return qSymbols;
	}

	/**
	 * Thi constructor adds all qSymbols which will are necessary to be checked
	 * when creating fresh name.
	 */
	public SMTMacroFactory() {
		for (final SMTPredefinedMacro pMacro : PREDEFINED_MACROS) {
			for (final String qSymbol : pMacro.getQSymbols()) {
				assert !qSymbols.contains(qSymbol);
			}
			qSymbols.addAll(pMacro.getQSymbols());
			qSymbols.add(SMTFactoryVeriT.FST_PAIR_ARG_NAME);
			qSymbols.add(SMTFactoryVeriT.SND_PAIR_ARG_NAME);
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
			final SMTMacroSymbol macro, final SMTSignatureVerit signature) {
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
			final SMTTerm[] terms, final SMTSignatureVerit signature) {
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
			final SMTTerm expression, final SMTSignatureVerit signature) {
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

	public static enum SMTVeriTOperator {
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
		SMTVeriTOperator(final SMTPredefinedMacro symbol) {
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
			final SMTPredefinedMacro operator, final SMTSignatureVerit signature) {
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
	 * Given a operator, this method returns the MacroSymbol associated with it.
	 * 
	 * @see SMTVeriTOperator
	 * 
	 * @param operator
	 *            the operator used to create the Macro Symbol
	 * @return the macro symbol associated with the operator
	 */
	public static final SMTMacroSymbol getMacroSymbol(
			final SMTVeriTOperator operator, final SMTSignatureVerit signature) {
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
	 * Creates and returns a macroSymbol.
	 * 
	 * @param macroName
	 *            The string representation of the macro
	 * @return a new macroSymbol
	 */
	public static SMTMacroSymbol makeMacroSymbol(final String macroName,
			final SMTSortSymbol sort) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT, sort, false);
	}
}
