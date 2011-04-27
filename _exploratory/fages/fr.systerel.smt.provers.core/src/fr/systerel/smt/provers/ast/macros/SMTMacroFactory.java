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
import fr.systerel.smt.provers.ast.SMTMacroTerm;
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
			"(lambda (?p1 ('t Bool)) (?q1 ('t Bool)) . (lambda (?x1 't) . (or (?p1 ?x1) (?q1 ?x1))))",
			0);
	public static final SMTPredefinedMacro BINTER_MACRO = new SMTPredefinedMacro(
			BINTER,
			"(lambda (?p2 ('t Bool))(?q2 ('t Bool)) . (lambda (?x2 't) . (and (?p2 ?x2) (?q2 ?x2))))",
			0);
	public static SMTPredefinedMacro FCOMP_MACRO = new SMTPredefinedMacro(
			FCOMP,
			"(lambda (?p3 ((Pair 's 't) Bool)) (?q3 ((Pair 't 'u) Bool)) . (lambda (?s1 (Pair 's 'u)) . (exists (?x3 't) . (and (?p3 (pair (fst ?s1) ?x3)) (?q3 (pair ?x3 (snd ?s1)))))))",
			1);

	public static final SMTPredefinedMacro BCOMP_MACRO = new SMTPredefinedMacro(
			BCOMP,
			"(lambda (?p3b ((Pair 's 't) Bool)) (?q3b ((Pair 't 'u) Bool)) . (fcomp ?q3b ?p3b))",
			2);

	public static final SMTPredefinedMacro REL_OVR_MACRO = new SMTPredefinedMacro(
			OVR,
			"(lambda (?p4 ((Pair 's 't) Bool)) (?q4 ((Pair 's 't) Bool)) . (lambda (?x4 (Pair 's 'u)) . (or (?q4 ?x4) (and (?p4 ?x4)(not(exists (?s2 (Pair 's 't)) . (and (?q4 ?s2)(= (fst ?s2)(fst ?x4)))))))))",
			1);
	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(lambda (?x5 't). false)", 0);
	public static final SMTPredefinedMacro IN_MACRO = new SMTPredefinedMacro(
			IN, "(lambda (?p6 't) (?q6 ('t Bool)) . (?q6 ?p6))", 0);

	public static final SMTPredefinedMacro SUBSET_MACRO = new SMTPredefinedMacro(
			SUBSET,
			"(lambda (?p7 ('t Bool)) (?q7 ('t Bool)) . (and (subseteq ?p7 ?q7) (not (= ?p7 ?q7))))",
			1);
	public static final SMTPredefinedMacro SUBSETEQ_MACRO = new SMTPredefinedMacro(
			SUBSETEQ,
			"(lambda (?p8 ('t Bool)) (?q8 ('t Bool)) . (forall (?x6 't). (implies (?p8 ?x6) (?q8 ?x6))))",
			0);
	public static final SMTPredefinedMacro RANGE_INTEGER_MACRO = new SMTPredefinedMacro(
			RANGE_INTEGER,
			"(lambda (?i1 Int) (?i2 Int) . (lambda (?i Int) . (and (<= ?i1 ?i) (<= ?i ?i2))))",
			0);
	public static final SMTPredefinedMacro RANGE_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			RANGE_SUBSTRACION,
			"(lambda (?x8 ((Pair 's 't) Bool))(?q10 ('t Bool)) . (lambda (?p10 (Pair 's 't)) . (and (?x8 ?p10)(not (?q10 (snd ?p10))))))",
			1);
	public static final SMTPredefinedMacro RANGE_RESTRICTION_MACRO = new SMTPredefinedMacro(
			RANGE_RESTRICTION,
			"(lambda (?q11 ((Pair 's 't) Bool))(?x9 ('t Bool)) . (lambda (?p11 (Pair 's 't)) . (and (?q11 ?p11)(?x9 (snd ?p11)))))",
			1);
	public static final SMTPredefinedMacro RELATION_MACRO = new SMTPredefinedMacro(
			RELATION,
			"(lambda (?x10 ('s Bool)) (?q12 ('s Bool)) . (lambda (?r  ((Pair 's 't) Bool)) .  (forall (?p12 (Pair 's 't)) . (implies (?r ?p12) (and (?x10 (fst ?p12))(?q12 (snd ?p12)))))))",
			1);
	// Using the surp (surjective property) to define this macro
	public static final SMTPredefinedMacro SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			SURJECTIVE_RELATION,
			"(lambda (?y8 ('t Bool)) (?r2 ((Pair 's 't) Bool))(forall (?p14 (Pair 's 't)) (= (?r2 ?p14) (?y8 (snd ?p14)))))",
			1);
	// Using the conjunction of surjective relation and total relation macros
	public static final SMTPredefinedMacro TOTAL_SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTIVE_RELATION,
			"(lambda (?x12 ('t Bool)) . (?r3 ((Pair 's 't) Bool))(and (forall (?p15 (Pair 's 't)) (= (?r3 ?p15) (?x12 (snd ?p15))))  (forall (?p16 (Pair 's 't)) (= (?r3 ?p16) (?x12 (fst ?p16))))))",
			1);
	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"(lambda (?x13 ('s Bool)) (?y0  ('s Bool)) . (lambda (?r4 ((Pair 's 't) Bool)) .  (and ((rel ?x13 ?y0) ?r4) (funp ?r4))))",
			2);
	public static final SMTPredefinedMacro TOTAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			TOTAL_FUNCTION,
			"(lambda (?x14 ('s Bool)) (?y2 ('s Bool)) . (lambda (?r5 ((Pair 's 't) Bool)) . (and ((pfun ?x14 ?y2) ?r5) (totp ?x14 ?r5))))",
			3);
	public static final SMTPredefinedMacro NAT_MACRO = new SMTPredefinedMacro(
			NAT, "(lambda (?x15 Int) . (<= 0 ?x15))", 0);
	public static final SMTPredefinedMacro NAT1_MACRO = new SMTPredefinedMacro(
			NAT1, "(lambda (?x16 Int) . (<= 1 ?x16))", 0);
	public static final SMTPredefinedMacro INVERSE_MACRO = new SMTPredefinedMacro(
			INV,
			"(lambda (?r6 ((Pair 's 't) Bool)) . (lambda (?p17 (Pair 's 't)) . (?r6 (pair (snd ?p17)(fst ?p17)))))",
			1);
	public static final SMTPredefinedMacro ID_MACRO = new SMTPredefinedMacro(
			ID, "(lambda (?p18 (Pair 't 't)) . (= (fst ?p18)(snd ?p18)))", 1);
	public static final SMTPredefinedMacro DOM_MACRO = new SMTPredefinedMacro(
			DOM,
			"(lambda (?r7 ((Pair't1 't2) Bool)) . (lambda (?r17 't1) . (exists (?r18 't2) . (?r7 ?r17 ?r18))))",
			0);
	public static final SMTPredefinedMacro PARTIAL_INJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_INJECTION,
			"(lambda (?x17 ('s Bool)) (?y3 ('s Bool)) . (lambda (?r19 ((Pair 's 't) Bool)) . (and ((pfun ?x17 ?y3) ?r19) (injp ?r19))))",
			3);
	public static final SMTPredefinedMacro TOTAL_INJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_INJECTION,
			"(lambda (?x18 ('s Bool)) . (?y4 ('s Bool))(lambda (?r20 ((Pair 's 't) Bool)) . (and ((pinj ?x18 ?y4) ?r20) (totp ?x18 ?r20))))",
			4);
	public static final SMTPredefinedMacro PARTIAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_SURJECTION,
			"(lambda (?x19 ('s Bool))(?y5 ('s Bool)) . (lambda (?r21 ((Pair 's 't) Bool)) .  (and ((pfun ?x19 ?y5) ?r21) (surp ?y5 ?r21))))",
			3);
	public static final SMTPredefinedMacro TOTAL_SURJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTION,
			"(lambda (?x20 ('s Bool)) (?y6 ('s Bool))(lambda (?r22 ((Pair 's 't) Bool)) (and ((psur ?x20 ?y6) ?r22) (totp ?x20 ?r22))))",
			4);
	public static final SMTPredefinedMacro TOTAL_BIJECTION_MACRO = new SMTPredefinedMacro(
			TOTAL_BIJECTION,
			"(lambda (?X21 ('s Bool)) (?y7 ('s Bool)) . (lambda (?r23 ((Pair 's 't) Bool)) . (and ((tsur ?X21 ?y7) ?r23) ((tinj ?X21 ?y7)?r23))))",
			5);
	public static final SMTPredefinedMacro CARTESIAN_PRODUCT_MACRO = new SMTPredefinedMacro(
			CARTESIAN_PRODUCT,
			"(lambda (?p ('s Bool))(?q ('t Bool)) . (lambda (?p9 (Pair 's 't)) . (and (?p (fst ?p9)) (?q (snd ?p9)))))",
			1);
	public static final SMTPredefinedMacro DOMAIN_RESTRICTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_RESTRICTION,
			"(lambda (?r24 ((Pair 's 't) Bool))(?s3 ('s Bool)) . (lambda (?p20 (Pair 's 't)) . (and (?r24 ?p20)(?s3 (fst ?p20)))))",
			1);
	public static final SMTPredefinedMacro DOMAIN_SUBSTRACTION_MACRO = new SMTPredefinedMacro(
			DOMAIN_SUBSTRACTION,
			"(lambda (?r25 ((Pair 's 't) Bool))(?s4 ('s Bool)) . (lambda (?p21 (Pair 's 't)) . (and (?r25 ?p21)(not (?s4 (fst ?p21))))))",
			1);
	public static final SMTPredefinedMacro RELATIONAL_IMAGE_MACRO = new SMTPredefinedMacro(
			RELATIONAL_IMAGE,
			"(lambda (?r26 ((Pair 's 't) Bool)(?p22 ('s Bool)(lambda (?y10 't) (exists (?x22 's)(and (?p22 ?x22)(?r26 (pair ?x22 ?y10))))))",
			1);
	public static final SMTPredefinedMacro SETMINUS_MACRO = new SMTPredefinedMacro(
			SETMINUS,
			"(lambda (?x23 ('t Bool)) (?q13 ('t Bool)) . (lambda (?x23e 't) . (and (?x23 ?x23e) (not (?q13 ?x23e)))))",
			0);
	public static final SMTPredefinedMacro ISMIN_MACRO = new SMTPredefinedMacro(
			ISMIN,
			"(lambda (?m Int) (?t (Int Bool)) . (and(in ?m ?t)(forall (?x24 Int) . (implies (in ?x24 ?t)(<= ?m ?x24)))))",
			1);
	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			"(lambda (?m1 Int) (?t1 (Int Bool)) . (and(in ?m1 ?t1)(forall (?x24i Int) . (implies (in ?x24i ?t1)(<= ?x24i ?m1)))))",
			1);
	public static final SMTPredefinedMacro FINITE_MACRO = new SMTPredefinedMacro(
			FINITE,
			"(lambda (?p25 Bool) (?t4 ('s Bool)) (?f1 ('s Int)) (?k2 Int) . (iff ?p25 (and (forall (?x28 's) . (implies (in ?x28 ?t4)(in (?f1 ?x28)(range 1 ?k2))))(forall (?x28 's)(?y 's) . (implies (and (in ?x28 ?t4)(in ?y ?t4)(not (= ?x28 ?y)))(not (= (?f1 ?x28)(?f1 ?y))))))))",
			1);
	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"(lambda (?t3 ('s Bool)) (?f4 ('s Int)) (?k1 Int) . (and (forall (?x26 's) . (implies (in ?x26 ?t3)(in (?f4 ?x26)(range 1 ?k1))))(forall (?x26 's)(?y1 's) . (implies (and (in ?x26 ?t3) (in ?y1 ?t3))(iff(= ?x26 ?y1) (= (?f4 ?x26)(?f4 ?y1)))))))",
			1);
	private static final SMTPredefinedMacro FUNP_MACRO = new SMTPredefinedMacro(
			FUNP,
			"(lambda (?r27 ((Pair 's 't )Bool)) . (forall (?pt (Pair 's 't))(?p0 (Pair 's 't)) . (implies (and (?r27 ?pt) (?r27 ?p0)) (implies (= (fst ?pt) (fst ?p0))(= (snd ?pt) (snd ?p0))))))",
			1);
	private static final SMTPredefinedMacro INJP_MACRO = new SMTPredefinedMacro(
			INJP, "(lambda (?r28 ((Pair 's 't )Bool)) . (funp (inv ?r28)))", 2);
	// Using the totp (total property) to define this macro
	public static final SMTPredefinedMacro TOTAL_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_RELATION,
			"(lambda (?x27 ('s Bool)) (?r29 ((Pair 's 't) Bool)) . (forall (?p24 (Pair 's 't)) . (= (?r29 ?p24) (?x27 (fst ?p24)))))",
			1);
	public static final SMTPredefinedMacro RANGE_MACRO = new SMTPredefinedMacro(
			RANGE,
			"(lambda (?r30 ((Pair 's 't) Bool)) . (lambda (?f3 't) . (exists (?e3 's) . (?r30 (pair ?e3 ?f3)))))",
			1);
	public static final SMTPredefinedMacro NOTEQUAL_MACRO = new SMTPredefinedMacro(
			NOT_EQUAL, "(lambda (?t6 Bool) (?t5 Bool) . (not (= ?t6 ?t5)))", 0);

	public static final SMTPredefinedMacro INTEGER_MACRO = new SMTPredefinedMacro(
			INT, "(lambda (?t7 Int). true)", 0);

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
			RANGE, POLYMORPHICS);

	private static final SMTMacroSymbol INTEGER_SYMBOL = new SMTMacroSymbol(
			INT, EMPTY_SORT);
	private static final SMTMacroSymbol NOT_EQUAL_SYMBOL = new SMTMacroSymbol(
			NOT_EQUAL, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(CARD,
			CARD_SORTS);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(FINITE,
			FINITE_SORTS);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(ISMAX,
			ISMIN_MAX_SORTS);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(ISMIN,
			ISMIN_MAX_SORTS);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(SETMINUS,
			POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			RELATIONAL_IMAGE, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_SUBSTRACTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_RESTRICTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			CARTESIAN_PRODUCT, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_BIJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_SURJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_INJECTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_INJECTION, POLYMORPHIC_PAIRS);

	private static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(DOM,
			POLYMORPHICS);
	private static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(INV,
			POLYMORPHICS);
	private static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(NAT1,
			EMPTY_SORT);
	private static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(NAT,
			EMPTY_SORT);
	private static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(ID, EMPTY_SORT);
	private static SMTMacroSymbol MAPSTO_SYMBOL = new SMTMacroSymbol(MAPSTO,
			POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_FUNCTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_FUNCTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTIVE_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SURJECTIVE_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			RANGE_RESTRICTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			RANGE_SUBSTRACION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE_INTEGER, Ints.getIntIntTab());
	private static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SUBSETEQ, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(SUBSET,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(IN,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(BUNION,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(BINTER,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(FCOMP,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol BCOMP_SYMBOL = new SMTMacroSymbol(BCOMP,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(OVR,
			POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(EMPTY,
			EMPTY_SORT, true);

	public static SMTSortSymbol PAIR_SORT = SMTFactory.makeSortSymbol(
			"(Pair 's 't)", !PREDEFINED);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	private static final SMTSortSymbol FST_RETURN_SORT = SMTFactory
			.makeSortSymbol(FST_PAIR_SORT_NAME, !SMTSymbol.PREDEFINED);

	private static final SMTSortSymbol SND_RETURN_SORT = SMTFactory
			.makeSortSymbol(SND_PAIR_SORT_NAME, !SMTSymbol.PREDEFINED);

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	private static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			"pair", PAIR_ARG_SORTS, PAIR_SORT, !ASSOCIATIVE, !PREDEFINED);

	private static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED);

	private static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED);

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
		for (SMTPredefinedMacro pMacro : PREDEFINED_MACROS) {
			for (String qSymbol : pMacro.getQSymbols()) {
				if (this.qSymbols.contains(qSymbol)) {
					throw new IllegalArgumentException(
							"Two macros cannot have same lambda variable names. One of the macros which have this problem is: "
									+ pMacro.toString()
									+ ". The qSymbol is: "
									+ qSymbol + ".");
				}
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
			final SMTTerm[] terms, SMTSignatureVerit signature) {
		addPairMacroSortAndFunInSignature(signature);

		SMTMacroTerm[] macroTerms = new SMTMacroTerm[terms.length];
		for (int i = 0; i < macroTerms.length; i++) {
			macroTerms[i] = (SMTMacroTerm) terms[i];
		}

		return new SMTPairEnumMacro(macroName, varName1, macroTerms, 1);
	}

	public static SMTEnumMacro makeEnumMacro(final String macroName,
			final SMTVarSymbol varName, final SMTTerm[] terms) {

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
			String macroName, SMTTerm[] terms, SMTVarSymbol lambdaVar,
			SMTFormula formula, SMTTerm expression, SMTSignatureVerit signature) {
		addPairMacroSortAndFunInSignature(signature);

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
	private static SMTFormula createFstAssumption(SMTSignature signature) {
		// TODO Refactor
		SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(FST_PAIR_ARG_NAME,
				FST_RETURN_SORT, PREDEFINED);
		SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(SND_PAIR_ARG_NAME,
				SND_RETURN_SORT, PREDEFINED);

		SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		SMTTerm[] pairTerms = { varSymbol1, varSymbol2 };

		SMTFunApplication pairFunAppl = new SMTFunApplication(PAIR_SYMBOL,
				pairTerms);

		SMTTerm[] fstTerms = { pairFunAppl };

		SMTFunApplication fstFunAppl = new SMTFunApplication(FST_SYMBOL,
				fstTerms);

		SMTTerm[] equalTerms = { fstFunAppl, varSymbol1 };

		SMTSortSymbol[] fstReturnSorts = { FST_RETURN_SORT, FST_RETURN_SORT };

		SMTFormula equalAtom = SMTFactory.makeAtom(
				new SMTPredicateSymbol.SMTEqual(fstReturnSorts), equalTerms,
				signature);

		SMTVarSymbol[] forallVarSymbols = { forallVarSymbol1, forallVarSymbol2 };

		SMTFormula quantifiedFormula = SMTFactory.makeSMTQuantifiedFormula(
				SMTQuantifierSymbol.FORALL, forallVarSymbols, equalAtom);

		return quantifiedFormula;
	}

	/**
	 * This method creates the auxiliar assumption:
	 * 
	 * <p>
	 * (forall (?e 's) (?f 't) (= (snd (pair ?e ?f)) ?e))
	 * </p>
	 * 
	 * <p>
	 * that defines the <code>snd</code> function
	 * </p>
	 * 
	 * @return The formula that represents the snd auxiliar function assumption
	 */
	private static SMTFormula createSndAssumption(SMTSignature signature) {
		SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(FST_PAIR_ARG_NAME,
				FST_RETURN_SORT, PREDEFINED);
		SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(SND_PAIR_ARG_NAME,
				SND_RETURN_SORT, PREDEFINED);

		SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		SMTTerm[] pairTerms = { varSymbol1, varSymbol2 };

		SMTFunApplication pairFunAppl = new SMTFunApplication(PAIR_SYMBOL,
				pairTerms);

		SMTTerm[] fstTerms = { pairFunAppl };

		SMTFunApplication sndFunAppl = new SMTFunApplication(SND_SYMBOL,
				fstTerms);

		SMTTerm[] equalTerms = { sndFunAppl, varSymbol2 };

		SMTFormula equalAtom = SMTFactory.makeAtom(
				new SMTPredicateSymbol.SMTEqual(POLYMORPHIC_PAIRS), equalTerms,
				signature);

		SMTVarSymbol[] forallVarSymbols = { forallVarSymbol1, forallVarSymbol2 };

		SMTFormula quantifiedFormula = SMTFactory.makeSMTQuantifiedFormula(
				SMTQuantifierSymbol.FORALL, forallVarSymbols, equalAtom);

		return quantifiedFormula;
	}

	/**
	 * 
	 * @param signature
	 */
	private static void addPairMacroSortAndFunInSignature(
			SMTSignatureVerit signature) {
		signature.addSort(PAIR_SORT);
		signature.addConstant(PAIR_SYMBOL);
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
			addPairMacroSortAndFunInSignature(signature);
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
		case NOT_EQUAL: {
			signature.addMacro(NOTEQUAL_MACRO);
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
		case NOT_EQUAL:
			return NOT_EQUAL_SYMBOL;
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
	public static SMTMacroSymbol makeMacroSymbol(final String macroName) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT);
	}

}
