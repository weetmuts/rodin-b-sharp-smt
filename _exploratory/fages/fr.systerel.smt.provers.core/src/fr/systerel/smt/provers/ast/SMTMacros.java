package fr.systerel.smt.provers.ast;

import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTTheory.VeritPredefinedTheory;
import static fr.systerel.smt.provers.ast.SMTMacroSymbol.*;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and cretes macro
 * enumerations, which are used to translate in extension.
 * 
 * @author vitor
 * 
 */
public class SMTMacros {

	public static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

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
			"(lambda (?p3 ((Pair 's 't) bool)(?q3 ((Pair 't 'u) bool)(lambda (?s1 (Pair 's 'u))(exists (?x3 't) (and (?p3 (pair (fst ?s1) ?x3)) (?q3 (pair ?x3 (snd ?s1)))))))))",
			1);
	public static final SMTPredefinedMacro REL_OVR_MACRO = new SMTPredefinedMacro(
			OVR,
			"(lambda (?p4 ((Pair 's 't) Bool)) (?q4 ((Pair 's 't) Bool)) . (lambda (?x4 (Pair 's 'u)) . (or (?q4 ?x4) (and (?p4 ?x4)(not(exists (?s2 (Pair 's 't)) . (and (?q4 ?s2)(= (fst ?s2)(fst ?x4)))))))))",
			1);
	public static final SMTPredefinedMacro EMPTYSET_MACRO = new SMTPredefinedMacro(
			EMPTY, "(lambda (?x5 't). false)", 0);
	public static final SMTPredefinedMacro EMPTYSET_PAIR_MACRO = new SMTPredefinedMacro(
			EMPTY_PAIR, "(lambda (?p5 't1) (?q5 't2). false)", 0);
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
			"(lambda (?y ('t Bool)) (?r2 ((Pair 's 't) Bool))(forall (?p14 (Pair 's 't)) (= (?r2 ?p14) (?y (snd ?p14)))))",
			1);
	// Using the conjunction of surjective relation and total relation macros
	public static final SMTPredefinedMacro TOTAL_SURJECTIVE_RELATION_MACRO = new SMTPredefinedMacro(
			TOTAL_SURJECTIVE_RELATION,
			"(lambda (?x12 ('t Bool)) . (?r3 ((Pair 's 't) Bool))(and (forall (?p15 (Pair 's 't)) (= (?r3 ?p15) (?x12 (snd ?p15))))  (forall (?p16 (Pair 's 't)) (= (?r3 ?p16) (?x12 (fst ?p16))))))",
			1);
	public static final SMTPredefinedMacro PARTIAL_FUNCTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_FUNCTION,
			"(lambda (?x13 ('s Bool)) (?y1  ('s Bool)) . (lambda (?r4 ((Pair 's 't) Bool)) .  (and ((rel ?x13 ?y1) ?r4) (funp ?r4))))",
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
			"(lambda (?r7 ('t1 't2 Bool)) . (lambda (?r17 't1) . (exists (?r18 't2) . (?r7 ?r17 ?r18))))",
			0);
	public static final SMTPredefinedMacro PARTIAL_INJECTION_MACRO = new SMTPredefinedMacro(
			PARTIAL_INJECTION,
			"(lambda (?x17 ('s Bool)) . (?y3 ('s Bool))(lambda (?r19 ((Pair 's 't) Bool)) . (and ((pfun ?x17 ?y3) ?r19) (injp ?r19))))",
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
			"(lambda (?p ('s Bool))(?q ('t Bool)) . (lambda (?p0 (Pair 's 't)) . (and (?p (fst ?p0)) (?q (snd ?p0)))))",
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
			"(lambda (?x23 ('t Bool)) (?q13 ('t Bool)) . (lambda (?x23 't) . (and (?x23 ?x23) (not (?q13 ?x23)))))",
			0);
	public static final SMTPredefinedMacro ISMIN_MACRO = new SMTPredefinedMacro(
			ISMIN,
			"(lambda (?m Int) (?t (Int Bool)) . (and(in ?m ?t)(forall (?x24 Int) . (implies (in ?x24 ?t)(<= ?m ?x24)))))",
			1);
	public static final SMTPredefinedMacro ISMAX_MACRO = new SMTPredefinedMacro(
			ISMAX,
			"(lambda (?m1 Int) (?t1 (Int Bool)) . (and(in ?m1 ?t1)(forall (?x24 Int) . (implies (in ?x24 ?t1)(<= ?x24 ?m1)))))",
			1);
	public static final SMTPredefinedMacro FINITE_MACRO = new SMTPredefinedMacro(
			FINITE,
			"(lambda (?p23 Bool) (?t2 ('s Bool)) (?f ('s Int)) (?k Int) . (iff ?p23 (and (forall (?x25 's) . (implies (in ?x25 ?t2)(in (?f ?x25)(range 1 ?k))))(forall (?x25 's)(?y 's) . (implies (and (in ?x25 ?t2)(in ?y ?t2)(not (= ?x25 ?y)))(not (= (?f ?x25)(?f ?y))))))))",
			1);
	public static final SMTPredefinedMacro CARD_MACRO = new SMTPredefinedMacro(
			CARD,
			"(lambda (?t3 ('s Bool)) (?f1 ('s Int)) (?k1 Int))(forall (?x26 's)(implies (in ?x26 ?t3)(in (?f1 ?x26)(range 1 ?k1))))(forall (?x26 's)(?y1 's)(implies (and (in ?x26 ?t3) (in ?y1 ?t3))(iff(equal ?x26 ?y1) (equal (?f1 ?x26)(?f1 ?y1))))))",
			1);
	public static final SMTPredefinedMacro PAIR_MACRO = new SMTPredefinedMacro(
			PAIR,
			"(lambda (?e1 't) (?e2 't) . (lambda (?f1 't) (?f2 't) . (and (= ?f1 ?e1) (= ?f2 ?e2))))",
			0);
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

	public static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol(
			"");
	private static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = {
			POLYMORPHIC, POLYMORPHIC };
	public static SMTPolymorphicSortSymbol[] POLYMORPHICS = { POLYMORPHIC };
	private static SMTSortSymbol[] ISMIN_MAX_SORTS = { Ints.getInt(),
			POLYMORPHIC };
	private static SMTSortSymbol[] FINITE_SORTS = {
			VeritPredefinedTheory.getInstance().getBooleanSort(), POLYMORPHIC,
			Ints.getInt(), Ints.getInt() };

	private static SMTSortSymbol[] CARD_SORTS = { POLYMORPHIC, Ints.getInt(),
			Ints.getInt() };

	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.CARD, CARD_SORTS);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.FINITE, FINITE_SORTS);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.ISMAX, ISMIN_MAX_SORTS);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.ISMIN, ISMIN_MAX_SORTS);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.SETMINUS, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.RELATIONAL_IMAGE, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.DOMAIN_SUBSTRACTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.DOMAIN_RESTRICTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.CARTESIAN_PRODUCT, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_BIJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_SURJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.PARTIAL_SURJECTION, POLYMORPHIC_PAIRS);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_INJECTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.PARTIAL_INJECTION, POLYMORPHIC_PAIRS);

	private static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.DOM, POLYMORPHICS);
	private static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.INV, POLYMORPHICS);
	private static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.NAT1, EMPTY_SORT);
	private static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.NAT, EMPTY_SORT);
	private static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.ID, EMPTY_SORT);
	private static SMTMacroSymbol MAPSTO_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.MAPSTO, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_FUNCTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.PARTIAL_FUNCTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.SURJECTIVE_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.TOTAL_RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.RELATION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.RANGE_RESTRICTION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.RANGE_SUBSTRACION, POLYMORPHIC_PAIRS);
	private static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.RANGE_INTEGER, Ints.getIntIntTab());
	private static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.SUBSETEQ, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.SUBSET, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.IN, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.BUNION, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.BINTER, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.COMP, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.OVR, POLYMORPHIC_PAIRS, true);
	private static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.EMPTY, EMPTY_SORT, true);
	private static SMTMacroSymbol EMPTYSET_PAIR_SYMBOL = new SMTMacroSymbol(
			SMTMacroSymbol.EMPTY_PAIR, EMPTY_SORT, true);

	public static SMTSortSymbol PAIR_SORT = new SMTSortSymbol("(Pair 's 't)",
			false);

	public static SMTPairEnumMacro makePairEnumerationMacro(
			final String macroName, final SMTVarSymbol varName1,
			final SMTVarSymbol varName2, final SMTTerm[] terms,
			SMTSignatureVerit signature) {
		addPairMacroSortAndFunInSignature(signature);

		SMTMacroTerm[] macroTerms = new SMTMacroTerm[terms.length];
		for (int i = 0; i < macroTerms.length; i++) {
			macroTerms[i] = (SMTMacroTerm) terms[i];
		}

		return new SMTPairEnumMacro(macroName, varName1, varName2, macroTerms,
				1);
	}

	public static SMTEnumMacro makeEnumMacro(final String macroName,
			final SMTVarSymbol varName, final SMTTerm[] terms) {

		return new SMTEnumMacro(macroName, varName, terms, 0);
	}

	/**
	 * 
	 * @param macroName
	 * @param terms
	 * @param lambdaVar
	 * @param formula
	 * @param expression
	 * @return
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

	private static void addFstAndSndFunctionsInSignature(
			SMTSignatureVerit signature) {
		final String fstSortName = "'s";
		final String sndSortName = "'t";
		SMTSortSymbol fstSort = SMTFactory.makeVeriTSortSymbol(fstSortName,
				signature);
		SMTSortSymbol sndSort = SMTFactory.makeVeriTSortSymbol(sndSortName,
				signature);
		SMTSortSymbol[] argSorts = { PAIR_SORT };
		SMTFunctionSymbol fstFun = new SMTFunctionSymbol("fst", argSorts,
				fstSort, false, false);
		SMTFunctionSymbol sndFun = new SMTFunctionSymbol("snd", argSorts,
				sndSort, false, false);
		signature.addConstant(fstFun);
		signature.addConstant(sndFun);

		// TODO Implement the assumptions that defines fst and snd
	}

	private static void addPairMacroSortAndFunInSignature(
			SMTSignatureVerit signature) {
		signature.addMacro(PAIR_MACRO);
		signature.addSort(PAIR_SORT);
		SMTSortSymbol[] argSorts = {};
		final String symbolName = "pair 's 't";
		SMTFunctionSymbol functionSymbol = new SMTFunctionSymbol(symbolName,
				argSorts, PAIR_SORT, !SMTFunctionSymbol.ASSOCIATIVE,
				!SMTFunctionSymbol.PREDEFINED);

		signature.addConstant(functionSymbol);
	}

	public static void addPredefinedMacroInSignature(
			final SMTVeriTOperator operator, final SMTSignatureVerit signature) {
		switch (operator) {
		case MAPSTO: {
			signature.addMacro(PAIR_MACRO);
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
		case EMPTY: {
			signature.addMacro(EMPTYSET_MACRO);
			break;
		}
		case EMPTY_PAIR: {
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
		case EMPTY_PAIR: {
			return EMPTYSET_PAIR_SYMBOL;
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
		default:
			throw new IllegalArgumentException(
					"There is no defined macro symbol with symbol: "
							+ operator.toString());
		}
	}

	public static SMTMacroSymbol makeMacroSymbol(final String macroName) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT);
	}

}
