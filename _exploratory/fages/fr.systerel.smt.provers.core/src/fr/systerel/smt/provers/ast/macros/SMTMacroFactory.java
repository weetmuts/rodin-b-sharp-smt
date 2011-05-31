/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.macros;

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
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
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.NOT_EQUAL;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.OVR;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_FUNCTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_INJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.PARTIAL_SURJECTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_INTEGER;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_RESTRICTION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RANGE_SUBSTRACION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RELATION;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.RELATIONAL_IMAGE;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SETMINUS;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SUBSET;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.SUBSETEQ;
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
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunApplication;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTPolymorphicSortSymbol;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTQuantifierSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
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
 * @author vitor
 * 
 */
public class SMTMacroFactory {

	private static final String SND_PAIR_ARG_NAME = "e";
	private static final String FST_PAIR_ARG_NAME = "f";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	public static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	private final Set<String> qSymbols = new HashSet<String>();

	/**
	 * Retrieves the name of the identifiers that have a question mark as a
	 * prefix.
	 * 
	 * @return the identifiers as defined above.
	 */
	public Set<String> getqSymbols() {
		return qSymbols;
	}

	public static SMTPredefinedMacro BUNION_MACRO = new SMTPredefinedMacro(
			BUNION,
			"(lambda (?UNION_0 ('t Bool)) (?UNION_1 ('t Bool)) . (lambda (?UNION_2 't) . (or (?UNION_0 ?UNION_2) (?UNION_1 ?UNION_2))))",
			0);
	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"(lambda (?BINTER_0 ('t Bool))(?BINTER_1 ('t Bool)) . (lambda (?BINTER_2 't) . (and (?BINTER_0 ?BINTER_2) (?BINTER_1 ?BINTER_2))))",
			0);
	public static SMTPredefinedMacro FCOMP_MACRO = new SMTPredefinedMacro(
			FCOMP,
			"(lambda (?FCOMP_0 ((Pair 's 't) Bool)) (?FCOMP_1 ((Pair 't 'u) Bool)) . (lambda (?FCOMP_2 (Pair 's 'u)) . (exists (?FCOMP_3 't) . (and (?FCOMP_0 (pair (fst ?FCOMP_2) ?FCOMP_3)) (?FCOMP_1 (pair ?FCOMP_3 (snd ?FCOMP_2)))))))",
			1);

	public static final SMTPredefinedMacro BCOMP_MACRO = new SMTPredefinedMacro(
			BCOMP,
			"(lambda (?BCOMP_0 ((Pair 's 't) Bool)) (?BCOMP_1 ((Pair 't 'u) Bool)) . (fcomp ?BCOMP_1 ?BCOMP_0))",
			2);

	public static final SMTPredefinedMacro REL_OVR_MACRO = new SMTPredefinedMacro(
			OVR,
			"(lambda (?OVR_0 ((Pair 's 't) Bool)) (?OVR_1 ((Pair 's 't) Bool)) . (lambda (?OVR_2 (Pair 's 'u)) . (or (?OVR_1 ?OVR_2) (and (?OVR_0 ?OVR_2)(not(exists (?OVR_3 (Pair 's 't)) . (and (?OVR_1 ?OVR_3)(= (fst ?OVR_3)(fst ?OVR_2)))))))))",
			1);
	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(lambda (?EMPTY_0 't). false)", 0);
	public static final SMTPredefinedMacro IN_MACRO = new SMTPredefinedMacro(
			IN, "(lambda (?IN_0 't) (?IN_1 ('t Bool)) . (?IN_1 ?IN_0))", 0);

	public static final SMTPredefinedMacro SUBSET_MACRO = new SMTPredefinedMacro(
			SUBSET,
			"(lambda (?SUBSET_0 ('t Bool)) (?SUBSET_1 ('t Bool)) . (and (subseteq ?SUBSET_0 ?SUBSET_1) (not (= ?SUBSET_0 ?SUBSET_1))))",
			1);
	public static final SMTPredefinedMacro SUBSETEQ_MACRO = new SMTPredefinedMacro(
			SUBSETEQ,
			"(lambda (?SUBSETEQ_0 ('t Bool)) (?SUBSETEQ_1 ('t Bool)) . (forall (?SUBSETEQ_2 't). (implies (?SUBSETEQ_0 ?SUBSETEQ_2) (?SUBSETEQ_1 ?SUBSETEQ_2))))",
			0);
	public static final SMTPredefinedMacro RANGE_INTEGER_MACRO = new SMTPredefinedMacro(
			RANGE_INTEGER,
			"(lambda (?RANGE_INTEGER_0 Int) (?RANGE_INTEGER_1 Int) . (lambda (?RANGE_INTEGER_2 Int) . (and (<= ?RANGE_INTEGER_0 ?RANGE_INTEGER_2) (<= ?RANGE_INTEGER_2 ?RANGE_INTEGER_1))))",
			0);
	public static final SMTPredefinedMacro RANGE_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			RANGE_SUBSTRACION,
			"(lambda (?RANGE_SUBSTRACION_0 ((Pair 's 't) Bool))(?RANGE_SUBSTRACION_1 ('t Bool)) . (lambda (?RANGE_SUBSTRACION_2 (Pair 's 't)) . (and (?RANGE_SUBSTRACION_0 ?RANGE_SUBSTRACION_2)(not (?RANGE_SUBSTRACION_1 (snd ?RANGE_SUBSTRACION_2))))))",
			1);
	public static final SMTPredefinedMacro RANGE_RESTRICTION_MACRO = new SMTPredefinedMacro(
			RANGE_RESTRICTION,
			"(lambda (?RANGE_RESTRICTION_0 ((Pair 's 't) Bool))(?RANGE_RESTRICTION_1 ('t Bool)) . (lambda (?RANGE_RESTRICTION_2 (Pair 's 't)) . (and (?RANGE_RESTRICTION_0 ?RANGE_RESTRICTION_2)(?RANGE_RESTRICTION_1 (snd ?RANGE_RESTRICTION_2)))))",
			1);
	public static final SMTPredefinedMacro RELATION_MACRO = new SMTPredefinedMacro(
			RELATION,
			"(lambda (?RELATION_0 ('s Bool)) (?RELATION_1 ('s Bool)) . (lambda (?RELATION_2  ((Pair 's 't) Bool)) .  (forall (?RELATION_3 (Pair 's 't)) . (implies (?RELATION_2 ?RELATION_3) (and (?RELATION_0 (fst ?RELATION_3))(?RELATION_1 (snd ?RELATION_3)))))))",
			1);
	// Using the surp (surjective property) to define this macro
	public static final SMTPredefinedMacro SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			SURJECTIVE_RELATION,
			"(lambda (?SURJECTIVE_RELATION_0 ('t Bool)) (?SURJECTIVE_RELATION_1 ((Pair 's 't) Bool))(forall (?SURJECTIVE_RELATION_2 (Pair 's 't)) (= (?SURJECTIVE_RELATION_1 ?SURJECTIVE_RELATION_2) (?SURJECTIVE_RELATION_0 (snd ?SURJECTIVE_RELATION_2)))))",
			1);
	// Using the conjunction of surjective relation and total relation macros
	public static final SMTPredefinedMacro TOTAL_SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTIVE_RELATION,
			"(lambda (?TOTAL_SURJECTIVE_RELATION_0 ('t Bool)) . (?TOTAL_SURJECTIVE_RELATION_1 ((Pair 's 't) Bool))(and (forall (?TOTAL_SURJECTIVE_RELATION_2 (Pair 's 't)) (= (?TOTAL_SURJECTIVE_RELATION_1 ?TOTAL_SURJECTIVE_RELATION_2) (?TOTAL_SURJECTIVE_RELATION_0 (snd ?TOTAL_SURJECTIVE_RELATION_2))))  (forall (?TOTAL_SURJECTIVE_RELATION_3 (Pair 's 't)) (= (?TOTAL_SURJECTIVE_RELATION_1 ?TOTAL_SURJECTIVE_RELATION_3) (?TOTAL_SURJECTIVE_RELATION_0 (fst ?TOTAL_SURJECTIVE_RELATION_3))))))",
			1);
	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"(lambda (?PARTIAL_FUNCTION_0 ('s Bool)) (?PARTIAL_FUNCTION_1  ('t Bool)) . (lambda (?PARTIAL_FUNCTION_2 ((Pair 's 't) Bool)) .  (and ((rel ?PARTIAL_FUNCTION_0 ?PARTIAL_FUNCTION_1) ?PARTIAL_FUNCTION_2) (funp ?PARTIAL_FUNCTION_2))))",
			2);
	public static final SMTPredefinedMacro TOTAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			TOTAL_FUNCTION,
			"(lambda (?TOTAL_FUNCTION_0 ('s Bool)) (?TOTAL_FUNCTION_1 ('t Bool)) . (lambda (?TOTAL_FUNCTION_2 ((Pair 's 't) Bool)) . (and ((pfun ?TOTAL_FUNCTION_0 ?TOTAL_FUNCTION_1) ?TOTAL_FUNCTION_2) (totp ?TOTAL_FUNCTION_0 ?TOTAL_FUNCTION_2))))",
			3);
	public static final SMTPredefinedMacro NAT_MACRO = new SMTPredefinedMacro(
			NAT, "(lambda (?NAT_0 Int) . (<= 0 ?NAT_0))", 0);
	public static final SMTPredefinedMacro NAT1_MACRO = new SMTPredefinedMacro(
			NAT1, "(lambda (?NAT1_0 Int) . (<= 1 ?NAT1_0))", 0);
	public static final SMTPredefinedMacro INVERSE_MACRO = new SMTPredefinedMacro(
			INV,
			"(lambda (?INV_0 ((Pair 's 't) Bool)) . (lambda (?INV_1 (Pair 's 't)) . (?INV_0 (pair (snd ?INV_1)(fst ?INV_1)))))",
			1);
	public static final SMTPredefinedMacro ID_MACRO = new SMTPredefinedMacro(
			ID, "(lambda (?ID_0 (Pair 't 't)) . (= (fst ?ID_0)(snd ?ID_0)))", 1);
	public static final SMTPredefinedMacro DOM_MACRO = new SMTPredefinedMacro(
			DOM,
			"(lambda (?DOM_0 ((Pair't1 't2) Bool)) . (lambda (?DOM_1 't1) . (exists (?DOM_2 't2) . (?DOM_0 ?DOM_1 ?DOM_2))))",
			0);
	public static final SMTPredefinedMacro PARTIAL_INJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_INJECTION,
			"(lambda (?PARTIAL_INJECTION_0 ('s Bool)) (?PARTIAL_INJECTION_1 ('s Bool)) . (lambda (?PARTIAL_INJECTION_2 ((Pair 's 't) Bool)) . (and ((pfun ?PARTIAL_INJECTION_0 ?PARTIAL_INJECTION_1) ?PARTIAL_INJECTION_2) (injp ?PARTIAL_INJECTION_2))))",
			3);
	public static final SMTPredefinedMacro TOTAL_INJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_INJECTION,
			"(lambda (?TOTAL_INJECTION_0 ('s Bool)) . (?TOTAL_INJECTION_1 ('s Bool))(lambda (?TOTAL_INJECTION_2 ((Pair 's 't) Bool)) . (and ((pinj ?TOTAL_INJECTION_0 ?TOTAL_INJECTION_1) ?TOTAL_INJECTION_2) (totp ?TOTAL_INJECTION_0 ?TOTAL_INJECTION_2))))",
			4);
	public static final SMTPredefinedMacro PARTIAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_SURJECTION,
			"(lambda (?PARTIAL_SURJECTION_0 ('s Bool))(?PARTIAL_SURJECTION_1 ('s Bool)) . (lambda (?PARTIAL_SURJECTION_2 ((Pair 's 't) Bool)) .  (and ((pfun ?PARTIAL_SURJECTION_0 ?PARTIAL_SURJECTION_1) ?PARTIAL_SURJECTION_2) (surp ?PARTIAL_SURJECTION_1 ?PARTIAL_SURJECTION_2))))",
			3);
	public static final SMTPredefinedMacro TOTAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTION,
			"(lambda (?TOTAL_SURJECTION_0 ('s Bool)) (?TOTAL_SURJECTION_1 ('s Bool))(lambda (?TOTAL_SURJECTION_2 ((Pair 's 't) Bool)) (and ((psur ?TOTAL_SURJECTION_0 ?TOTAL_SURJECTION_1) ?TOTAL_SURJECTION_2) (totp ?TOTAL_SURJECTION_0 ?TOTAL_SURJECTION_2))))",
			4);
	public static final SMTPredefinedMacro TOTAL_BIJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_BIJECTION,
			"(lambda (?TOTAL_BIJECTION_0 ('s Bool)) (?TOTAL_BIJECTION_1 ('s Bool)) . (lambda (?TOTAL_BIJECTION_2 ((Pair 's 't) Bool)) . (and ((tsur ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1) ?TOTAL_BIJECTION_2) ((tinj ?TOTAL_BIJECTION_0 ?TOTAL_BIJECTION_1)?TOTAL_BIJECTION_2))))",
			5);
	public static final SMTPredefinedMacro CARTESIAN_PRODUCT_MACRO = new SMTPredefinedMacro(
			CARTESIAN_PRODUCT,
			"(lambda (?CARTESIAN_PRODUCT_0 ('s Bool))(?CARTESIAN_PRODUCT_1 ('t Bool)) . (lambda (?CARTESIAN_PRODUCT_2 (Pair 's 't)) . (and (?CARTESIAN_PRODUCT_0 (fst ?CARTESIAN_PRODUCT_2)) (?CARTESIAN_PRODUCT_1 (snd ?CARTESIAN_PRODUCT_2)))))",
			1);
	public static final SMTPredefinedMacro DOMAIN_RESTRICTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_RESTRICTION,
			"(lambda (?DOMAIN_RESTRICTION_0 ('s Bool))(?DOMAIN_RESTRICTION_1 ((Pair 's 't) Bool)) . (lambda (?DOMAIN_RESTRICTION_2 (Pair 's 't)) . (and (?DOMAIN_RESTRICTION_1 ?DOMAIN_RESTRICTION_2)(?DOMAIN_RESTRICTION_0 (fst ?DOMAIN_RESTRICTION_2)))))",
			1);
	public static final SMTPredefinedMacro DOMAIN_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_SUBSTRACTION,
			"(lambda (?DOMAIN_SUBSTRACTION_0 ('s Bool))(?DOMAIN_SUBSTRACTION_1 ((Pair 's 't) Bool)) . (lambda (?DOMAIN_SUBSTRACTION_2 (Pair 's 't)) . (and (?DOMAIN_SUBSTRACTION_1 ?DOMAIN_SUBSTRACTION_2)(not (?DOMAIN_SUBSTRACTION_0 (fst ?DOMAIN_SUBSTRACTION_2))))))",
			1);
	public static final SMTPredefinedMacro RELATIONAL_IMAGE_MACRO = new SMTPredefinedMacro(
			RELATIONAL_IMAGE,
			"(lambda (?RELATIONAL_IMAGE_0 ((Pair 's 't) Bool)(?RELATIONAL_IMAGE_1 ('s Bool)(lambda (?RELATIONAL_IMAGE_2 't) (exists (?RELATIONAL_IMAGE_3 's)(and (?RELATIONAL_IMAGE_1 ?RELATIONAL_IMAGE_3)(?RELATIONAL_IMAGE_0 (pair ?RELATIONAL_IMAGE_3 ?RELATIONAL_IMAGE_2))))))",
			1);
	public static final SMTPredefinedMacro SETMINUS_MACRO = new SMTPredefinedMacro(
			SETMINUS,
			"(lambda (?SETMINUS_0 ('t Bool)) (?SETMINUS_1 ('t Bool)) . (lambda (?SETMINUS_2 't) . (and (?SETMINUS_0 ?SETMINUS_2) (not (?SETMINUS_1 ?SETMINUS_2)))))",
			0);
	public static final SMTPredefinedMacro ISMIN_MACRO = new SMTPredefinedMacro(
			ISMIN,
			"(lambda (?ISMIN_0 Int) (?ISMIN_1 (Int Bool)) . (and(in ?ISMIN_0 ?ISMIN_1)(forall (?ISMIN_2 Int) . (implies (in ?ISMIN_2 ?ISMIN_1)(<= ?ISMIN_0 ?ISMIN_2)))))",
			1);
	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			"(lambda (?ISMAX_0 Int) (?ISMAX_1 (Int Bool)) . (and(in ?ISMAX_0 ?ISMAX_1)(forall (?ISMAX_2 Int) . (implies (in ?ISMAX_2 ?ISMAX_1)(<= ?ISMAX_2 ?ISMAX_0)))))",
			1);
	public static final SMTPredefinedMacro FINITE_MACRO = new SMTPredefinedMacro(
			FINITE,
			"(lambda (?FINITE_0 Bool) (?FINITE_1 ('s Bool)) (?FINITE_2 ('s Int)) (?FINITE_3 Int) . (iff ?FINITE_0 (and (forall (?FINITE_4 's) . (implies (in ?FINITE_4 ?FINITE_1)(in (?FINITE_2 ?FINITE_4)(range 1 ?FINITE_3))))(forall (?FINITE_4 's)(?FINITE_5 's) . (implies (and (in ?FINITE_4 ?FINITE_1)(in ?FINITE_5 ?FINITE_1)(not (= ?FINITE_4 ?FINITE_5)))(not (= (?FINITE_2 ?FINITE_4)(?FINITE_2 ?FINITE_5))))))))",
			1);
	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"(lambda (?CARD_0 ('s Bool)) (?CARD_1 ('s Int)) (?CARD_2 Int) . (and (forall (?CARD_3 Int) . (implies (in ?CARD_3 (range 1 ?CARD_2))(exists (?CARD_4 's) . (and (in ?CARD_4 ?CARD_0) (= (?CARD_1 ?CARD_4) ?CARD_3)))))(forall (?CARD_4 's) . (implies (in ?CARD_4 ?CARD_0) (in (?CARD_1 ?CARD_4) (range 1 ?CARD_2))))(forall (?CARD_5 's) (?CARD_6 's) . (implies (and (in ?CARD_5 ?CARD_0) (in ?CARD_6 ?CARD_0) (= (?CARD_1 ?CARD_5) (?CARD_1 ?CARD_6))) (= ?CARD_5 ?CARD_6)))))",
			1);
	private static final SMTPredefinedMacro FUNP_MACRO = new SMTPredefinedMacro(
			FUNP,
			"(lambda (?FUNP_0 ((Pair 's 't )Bool)) . (forall (?FUNP_1 (Pair 's 't))(?FUNP_2 (Pair 's 't)) . (implies (and (?FUNP_0 ?FUNP_1) (?FUNP_0 ?FUNP_2)) (implies (= (fst ?FUNP_1) (fst ?FUNP_2))(= (snd ?FUNP_1) (snd ?FUNP_2))))))",
			1);
	private static final SMTPredefinedMacro INJP_MACRO = new SMTPredefinedMacro(
			INJP,
			"(lambda (?INJP_0 ((Pair 's 't )Bool)) . (funp (inv ?INJP_0)))", 2);
	// Using the totp (total property) to define this macro
	public static final SMTPredefinedMacro TOTAL_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_RELATION,
			"(lambda (?TOTAL_RELATION_0 ('s Bool)) (?TOTAL_RELATION_1 ((Pair 's 't) Bool)) . (forall (?TOTAL_RELATION_2 (Pair 's 't)) . (iff (?TOTAL_RELATION_1 ?TOTAL_RELATION_2) (?TOTAL_RELATION_0 (fst ?TOTAL_RELATION_2)))))",
			1);
	public static final SMTPredefinedMacro RANGE_MACRO = new SMTPredefinedMacro(
			RANGE,
			"(lambda (?RANGE_0 ((Pair 's 't) Bool)) . (lambda (?RANGE_1 't) . (exists (?RANGE_2 's) . (?RANGE_0 (pair ?RANGE_2 ?RANGE_1)))))",
			1);
	public static final SMTPredefinedMacro NOTEQUAL_MACRO = new SMTPredefinedMacro(
			NOT_EQUAL,
			"(lambda (?NOT_EQUAL_0 Bool) (?NOT_EQUAL_1 Bool) . (not (= ?NOT_EQUAL_0 ?NOT_EQUAL_1)))",
			0);

	public static final SMTPredefinedMacro INTEGER_MACRO = new SMTPredefinedMacro(
			INT, "(lambda (?INT_0 Int). true)", 0);

	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };
	public static SMTPolymorphicSortSymbol[] POLYMORPHICS = { POLYMORPHIC };
	private static SMTSortSymbol[] ISMIN_MAX_SORTS = { Ints.getInt(),
			POLYMORPHIC };
	private static SMTSortSymbol[] FINITE_SORTS = {
			fr.systerel.smt.provers.ast.VeritPredefinedTheory.getInstance()
					.getBooleanSort(), POLYMORPHIC, Ints.getInt(),
			Ints.getInt() };

	private static SMTSortSymbol[] CARD_SORTS = { POLYMORPHIC, Ints.getInt(),
			Ints.getInt() };

	private static final SMTMacroSymbol RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE, POLYMORPHIC, !PREDEFINED, POLYMORPHICS);

	private static final SMTMacroSymbol INTEGER_SYMBOL = new SMTMacroSymbol(
			INT, POLYMORPHIC, !PREDEFINED, EMPTY_SORT);
	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(CARD,
			POLYMORPHIC, !PREDEFINED, CARD_SORTS);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(FINITE,
			POLYMORPHIC, !PREDEFINED, FINITE_SORTS);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(ISMAX,
			POLYMORPHIC, !PREDEFINED, ISMIN_MAX_SORTS);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(ISMIN,
			POLYMORPHIC, !PREDEFINED, ISMIN_MAX_SORTS);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(SETMINUS,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			RELATIONAL_IMAGE, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_SUBSTRACTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_RESTRICTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			CARTESIAN_PRODUCT, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_BIJECTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_SURJECTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_INJECTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_INJECTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(DOM,
			POLYMORPHIC, !PREDEFINED, POLYMORPHICS);
	private static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(INV,
			POLYMORPHIC, !PREDEFINED, POLYMORPHICS);
	private static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(NAT1,
			POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(NAT,
			POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(ID,
			POLYMORPHIC, !PREDEFINED);
	private static SMTMacroSymbol MAPSTO_SYMBOL = new SMTMacroSymbol(MAPSTO,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_FUNCTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_FUNCTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTIVE_RELATION, POLYMORPHIC, !PREDEFINED,
			POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SURJECTIVE_RELATION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_RELATION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			RELATION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			RANGE_RESTRICTION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			RANGE_SUBSTRACION, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE_INTEGER, POLYMORPHIC, !PREDEFINED,
			VeritPredefinedTheory.getIntIntTab());
	private static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SUBSETEQ, POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(SUBSET,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(IN,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(BUNION,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(BINTER,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(FCOMP,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol BCOMP_SYMBOL = new SMTMacroSymbol(BCOMP,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(OVR,
			POLYMORPHIC, !PREDEFINED, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(EMPTY,
			POLYMORPHIC, !PREDEFINED);

	public static SMTSortSymbol PAIR_SORT = SMTFactory.makeSortSymbol(
			"(Pair 's 't)", !PREDEFINED);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	private static final SMTSortSymbol FST_RETURN_SORT = SMTFactory
			.makeSortSymbol(FST_PAIR_SORT_NAME, !SMTSymbol.PREDEFINED);

	private static final SMTSortSymbol SND_RETURN_SORT = SMTFactory
			.makeSortSymbol(SND_PAIR_SORT_NAME, !SMTSymbol.PREDEFINED);

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_ARG_SORTS);

	private static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", FST_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_SORTS);

	private static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", SND_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_SORTS);

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
			TOTAL_RELATION_MACRO, RANGE_MACRO, NOTEQUAL_MACRO, BCOMP_MACRO,
			INTEGER_MACRO };

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
			qSymbols.add(FST_PAIR_ARG_NAME);
			qSymbols.add(SND_PAIR_ARG_NAME);
		}
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
	 * 
	 * (macroName (lambda (?y s) . (exists (?x1 s1) ... (?xn sn) (and (= y
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

	/**
	 * This method adds the <code>fst<code> and <code>snd</code> to the
	 * signature, as well as the assumptions that defines them.
	 * 
	 * @param signature
	 *            The signature that will receive the functions and assumptions
	 */
	private static void addFstAndSndFunctionsInSignature(
			final SMTSignatureVerit signature) {
		signature.addConstant(FST_SYMBOL);
		signature.addConstant(SND_SYMBOL);
		signature.addFstOrSndAuxiliarAssumption(createFstAssumption(signature));
		signature.addFstOrSndAuxiliarAssumption(createSndAssumption(signature));
		signature.setFstAndSndAssumptionsAdded(true);
	}

	public static void addPairEqualityAxiomsInSignature(
			final SMTSignatureVerit signature) {
		addFstAndSndFunctionsInSignature(signature);
		signature.addPairEqualityAxiom(createPairEqualityAxiom());
	}

	/**
	 * This method creates the auxiliar assumption:
	 * 
	 * <p>
	 * <code>(forall (?e 's) (?f 't) (= (fst (pair ?e ?f)) ?e))</code>
	 * </p>
	 * 
	 * <p>
	 * that defines the <code>fst</code> function
	 * </p>
	 * 
	 * @return The formula that represents the fst auxiliar function assumption
	 */
	private static SMTFormula createFstAssumption(final SMTSignature signature) {
		final SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(
				FST_PAIR_ARG_NAME, FST_RETURN_SORT, PREDEFINED);
		final SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(
				SND_PAIR_ARG_NAME, SND_RETURN_SORT, PREDEFINED);

		final SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		final SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		final SMTFunApplication pairFunAppl = new SMTFunApplication(
				PAIR_SYMBOL, varSymbol1, varSymbol2);

		final SMTFunApplication fstFunAppl = new SMTFunApplication(FST_SYMBOL,
				pairFunAppl);

		return createAuxiliarAssumption(signature, forallVarSymbol1,
				forallVarSymbol2, varSymbol1, fstFunAppl);
	}

	/**
	 * Given the elements (?e 's) , (?f 't), ?x , and (fun (pair ?e ?f)) ?x), it
	 * creates the final auxiliar assumption.
	 * 
	 * @param signature
	 *            the signature
	 * @param forallVarSymbol1
	 *            (?e 's)
	 * @param forallVarSymbol2
	 *            (?f 't)
	 * @param varSymbol1
	 *            ?x
	 * @param fstFunAppl
	 *            (fun (pair ?e ?f)) ?x)
	 * @return The formula that represents the fst or the snd auxiliar function
	 *         assumption
	 * 
	 * @see #createFstAssumption(SMTSignature)
	 * @see #createSndAssumption(SMTSignature)
	 */
	private static SMTFormula createAuxiliarAssumption(
			final SMTSignature signature, final SMTVarSymbol forallVarSymbol1,
			final SMTVarSymbol forallVarSymbol2, final SMTVar varSymbol1,
			final SMTFunApplication fstFunAppl) {
		final SMTFormula equalAtom = SMTFactory.makeAtom(
				new SMTPredicateSymbol.SMTEqual(POLYMORPHIC_PAIRS), signature,
				fstFunAppl, varSymbol1);

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						equalAtom, forallVarSymbol1, forallVarSymbol2);

		return quantifiedFormula;
	}

	/**
	 * This method creates the auxiliar assumption:
	 * 
	 * <p>
	 * (forall (?e 's) (?f 't) (= (snd (pair ?e ?f)) ?f))
	 * </p>
	 * 
	 * <p>
	 * that defines the <code>snd</code> function
	 * </p>
	 * 
	 * @return The formula that represents the snd auxiliar function assumption
	 */
	private static SMTFormula createSndAssumption(final SMTSignature signature) {
		final SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(
				FST_PAIR_ARG_NAME, FST_RETURN_SORT, !PREDEFINED);
		final SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(
				SND_PAIR_ARG_NAME, SND_RETURN_SORT, !PREDEFINED);

		final SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		final SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		final SMTFunApplication pairFunAppl = new SMTFunApplication(
				PAIR_SYMBOL, varSymbol1, varSymbol2);

		final SMTFunApplication sndFunAppl = new SMTFunApplication(SND_SYMBOL,
				pairFunAppl);

		return createAuxiliarAssumption(signature, forallVarSymbol1,
				forallVarSymbol2, varSymbol2, sndFunAppl);
	}

	/**
	 * Creates the pair equality axiom. It is defined as:
	 * 
	 * (forall (?p1 (Pair 's 't)) (?p2 (Pair 's 't)) (implies (= ?p1 ?p2) (and
	 * (= (fst ?p1) (fst ?p2)) (= (snd ?p1) (snd ?p2)))))
	 * 
	 * @return the pair equality axiom
	 */
	public static SMTFormula createPairEqualityAxiom() {

		final SMTVarSymbol pSymbol1 = new SMTVarSymbol("t8", PAIR_SORT,
				!PREDEFINED);
		final SMTVarSymbol pSymbol2 = new SMTVarSymbol("t9", PAIR_SORT,
				!PREDEFINED);

		final SMTVar pairVar1 = new SMTVar(pSymbol1);
		final SMTVar pairVar2 = new SMTVar(pSymbol2);

		final SMTFormula equalsFormula = SMTFactory.makeEqual(pairVar1,
				pairVar2);

		final SMTFunApplication fstFunAppl1 = new SMTFunApplication(FST_SYMBOL,
				pairVar1);

		final SMTFunApplication fstFunAppl2 = new SMTFunApplication(FST_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula1 = SMTFactory.makeEqual(fstFunAppl1,
				fstFunAppl2);

		final SMTFunApplication sndFunAppl1 = new SMTFunApplication(SND_SYMBOL,
				pairVar1);

		final SMTFunApplication sndFunAppl2 = new SMTFunApplication(SND_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula2 = SMTFactory.makeEqual(sndFunAppl1,
				sndFunAppl2);

		final SMTFormula andFormula = SMTFactory.makeAnd(subEqualsFormula1,
				subEqualsFormula2);

		final SMTFormula impliesFormula = SMTFactory.makeImplies(andFormula,
				equalsFormula);

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						impliesFormula, pairVar1, pairVar2);

		return quantifiedFormula;
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
			final SMTVeriTOperator operator, final SMTSignatureVerit signature) {
		switch (operator) {
		case MAPSTO: {
			break;
		}
		case PAIR: {
			signature.addPairSortAndFunction();
			break;
		}
		case BUNION: {
			signature.addMacro(BUNION_MACRO);
			break;
		}
		case BINTER: {
			signature.addMacro(BINTER_MACRO);
			break;
		}
		case FCOMP: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(FCOMP_MACRO);
			break;
		}
		case OVR: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(REL_OVR_MACRO);
			break;
		}
		case EMPTY: {
			signature.addMacro(EMPTYSET_MACRO);
			break;
		}
		case IN: {
			signature.addMacro(IN_MACRO);
			break;
		}
		case SUBSET: {
			addPredefinedMacroInSignature(SMTVeriTOperator.SUBSETEQ, signature);

			signature.addMacro(SUBSET_MACRO);
			break;
		}
		case SUBSETEQ: {
			signature.addMacro(SUBSETEQ_MACRO);
			break;
		}
		case RANGE_INTEGER: {
			signature.addMacro(RANGE_INTEGER_MACRO);
			break;
		}
		case BCOMP:
			addPredefinedMacroInSignature(SMTVeriTOperator.FCOMP, signature);

			signature.addMacro(BCOMP_MACRO);
			break;
		case RANGE_SUBSTRACTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(RANGE_SUBSTRACTION_MACRO);
			break;
		}
		case RANGE_RESTRICTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(RANGE_RESTRICTION_MACRO);
			break;
		}
		case RELATION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(RELATION_MACRO);
			break;
		}
		case TOTAL_RELATION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(TOTAL_RELATION_MACRO);
			break;
		}
		case SURJECTIVE_RELATION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(SURJECTIVE_RELATION_MACRO);
			break;
		}
		case TOTAL_SURJECTIVE_RELATION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(TOTAL_SURJECTIVE_RELATION_MACRO);
			break;
		}
		case PARTIAL_FUNCTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.RELATION, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.FUNP, signature);

			signature.addMacro(PARTIAL_FUNCTION_MACRO);
			break;
		}
		case TOTAL_FUNCTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PARTIAL_FUNCTION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.TOTAL_RELATION,
					signature);

			signature.addMacro(TOTAL_FUNCTION_MACRO);
			break;
		}
		case ID: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(ID_MACRO);
			break;
		}
		case NAT: {
			signature.addMacro(NAT_MACRO);
			break;
		}
		case NAT1: {
			signature.addMacro(NAT1_MACRO);
			break;
		}
		case INV: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(INVERSE_MACRO);
			break;
		}
		case DOM: {
			signature.addMacro(DOM_MACRO);
		}
		case PARTIAL_INJECTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.PARTIAL_FUNCTION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.INJP, signature);

			signature.addMacro(PARTIAL_INJECTION_MACRO);
			break;
		}
		case TOTAL_INJECTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PARTIAL_INJECTION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.TOTAL_RELATION,
					signature);

			signature.addMacro(TOTAL_INJECTION_MACRO);
			break;
		}
		case PARTIAL_SURJECTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.PARTIAL_FUNCTION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.SURJECTIVE_RELATION,
					signature);

			signature.addMacro(BUNION_MACRO);
			break;
		}
		case TOTAL_SURJECTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.TOTAL_RELATION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.PARTIAL_SURJECTION,
					signature);

			signature.addMacro(TOTAL_SURJECTION_MACRO);
			break;
		}
		case TOTAL_BIJECTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.TOTAL_SURJECTION,
					signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.TOTAL_INJECTION,
					signature);

			signature.addMacro(TOTAL_BIJECTION_MACRO);
			break;
		}
		case CARTESIAN_PRODUCT: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(CARTESIAN_PRODUCT_MACRO);
			break;
		}
		case DOMAIN_RESTRICTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(DOMAIN_RESTRICTION_MACRO);
			break;
		}
		case DOMAIN_SUBSTRACTION: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(DOMAIN_SUBSTRACTION_MACRO);
			break;
		}
		case RELATIONAL_IMAGE: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);

			signature.addMacro(RELATIONAL_IMAGE_MACRO);
			break;
		}
		case SETMINUS: {
			signature.addMacro(SETMINUS_MACRO);
			break;
		}
		case ISMIN: {
			addPredefinedMacroInSignature(SMTVeriTOperator.IN, signature);
			signature.addMacro(ISMIN_MACRO);
			break;
		}
		case ISMAX: {
			addPredefinedMacroInSignature(SMTVeriTOperator.IN, signature);
			signature.addMacro(ISMAX_MACRO);
			break;
		}
		case FINITE: {
			addPredefinedMacroInSignature(SMTVeriTOperator.IN, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.RANGE_INTEGER,
					signature);

			signature.addMacro(FINITE_MACRO);
			break;
		}
		case CARD: {
			addPredefinedMacroInSignature(SMTVeriTOperator.IN, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.RANGE_INTEGER,
					signature);

			signature.addMacro(CARD_MACRO);
			break;
		}
		case FUNP: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);
			addFstAndSndFunctionsInSignature(signature);

			signature.addMacro(FUNP_MACRO);
			break;
		}
		case INJP: {
			addPredefinedMacroInSignature(SMTVeriTOperator.FUNP, signature);
			addPredefinedMacroInSignature(SMTVeriTOperator.INV, signature);

			signature.addMacro(INJP_MACRO);
			break;
		}
		case RANGE: {
			addPredefinedMacroInSignature(SMTVeriTOperator.PAIR, signature);

			signature.addMacro(RANGE_MACRO);
			break;
		}
		case INTEGER: {
			signature.addMacro(INTEGER_MACRO);
			break;
		}
		default:
			throw new IllegalArgumentException(
					"There is no predefined macro with symbol: "
							+ operator.toString());
		}
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
		addPredefinedMacroInSignature(operator, signature);
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
		case IN: {
			return IN_SYMBOL;
		}
		case SUBSET: {
			return SUBSET_SYMBOL;
		}
		case SUBSETEQ: {
			return SUBSETEQ_SYMBOL;
		}
		case RANGE_INTEGER: {
			return INTEGER_RANGE_SYMBOL;
		}
		case RANGE_SUBSTRACTION: {
			return RANGE_SUBSTRACTION_SYMBOL;
		}
		case RANGE_RESTRICTION: {
			return RANGE_RESTRICTION_SYMBOL;
		}
		case RELATION: {
			return RELATION_SYMBOL;
		}
		case TOTAL_RELATION: {
			return TOTAL_RELATION_SYMBOL;
		}
		case SURJECTIVE_RELATION: {
			return SURJECTIVE_RELATION_SYMBOL;
		}
		case TOTAL_SURJECTIVE_RELATION: {
			return TOTAL_SURJECTIVE_RELATION_SYMBOL;
		}
		case PARTIAL_FUNCTION: {
			return PARTIAL_FUNCTION_SYMBOL;
		}
		case TOTAL_FUNCTION: {
			return TOTAL_FUNCTION_SYMBOL;
		}
		case MAPSTO: {
			return MAPSTO_SYMBOL;
		}
		case ID: {
			return ID_SYMBOL;
		}
		case NAT: {
			return NAT_SYMBOL;
		}
		case NAT1: {
			return NAT1_SYMBOL;
		}
		case INV: {
			return INVERSE_SYMBOL;
		}
		case DOM: {
			return DOM_SYMBOL;
		}
		case PARTIAL_INJECTION: {
			return PARTIAL_INJECTION_SYMBOL;
		}
		case TOTAL_INJECTION: {
			return TOTAL_INJECTION_SYMBOL;
		}
		case PARTIAL_SURJECTION: {
			return PARTIAL_SURJECTION_SYMBOL;
		}
		case TOTAL_SURJECTION: {
			return TOTAL_SURJECTION_SYMBOL;
		}
		case TOTAL_BIJECTION: {
			return TOTAL_BIJECTION_SYMBOL;
		}
		case CARTESIAN_PRODUCT: {
			return CARTESIAN_PRODUCT_SYMBOL;
		}
		case DOMAIN_RESTRICTION: {
			return DOMAIN_RESTRICTION_SYMBOL;
		}
		case DOMAIN_SUBSTRACTION: {
			return DOMAIN_SUBSTRACTION_SYMBOL;
		}
		case RELATIONAL_IMAGE: {
			return RELATIONAL_IMAGE_SYMBOL;
		}
		case SETMINUS: {
			return SETMINUS_SYMBOL;
		}
		case ISMIN: {
			return ISMIN_SYMBOL;
		}
		case ISMAX: {
			return ISMAX_SYMBOL;
		}
		case FINITE: {
			return FINITE_SYMBOL;
		}
		case CARD: {
			return CARD_SYMBOL;
		}
		case RANGE: {
			return RANGE_SYMBOL;
		}
		case BCOMP:
			return BCOMP_SYMBOL;
		case INTEGER:
			return INTEGER_SYMBOL;
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
		return new SMTMacroSymbol(macroName, sort, false);
	}

}
