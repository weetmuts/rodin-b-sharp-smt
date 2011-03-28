package fr.systerel.smt.provers.ast;

import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.ast.SMTTheory.VeritPredefinedTheory;

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

	public static final String BUNION_MACRO = "(lambda (?p1 ('t Bool)) (?q1 ('t Bool)) . (lambda (?x1 't) . (or (?p1 ?x6) (?q1 ?x1))))";
	public static final String BINTER_MACRO = "(lambda (?p2 ('t Bool))(?q2 ('t Bool)) . (lambda (?x2 't) . (and (?p2 ?x2) (?q2 ?x2))))";
	public static final String FCOMP_MACRO = "(lambda (?p3 ((Pair 's 't) bool)(?q3 ((Pair 't 'u) bool)(lambda (?s1 (Pair 's 'u))(exists (?x3 't) (and (?p3 (pair (fst ?s1) ?x3)) (?q3 (pair ?x3 (snd ?s1)))))))))";
	public static final String REL_OVR_MACRO = "(lambda (?p4 ((Pair 's 't) bool)(?q4 ((Pair 's 't) bool)(lambda (?x4 (Pair 's 'u)) (or (?q4 ?x4)(and (?p4 ?x4)(not(exists(?s2 (Pair 's 't))(and (?q4 ?s2)(= (fst ?s2)(fst ?x4)))))))))))";
	public static final String EMPTYSET_MACRO = "(lambda (?x5 't). false)";
	public static final String EMPTYSET_PAIR_MACRO = "(lambda (?p5 't1) (?q5 't2). false)";
	public static final String IN_MACRO = "(lambda (?p6 't) (?q6 ('t Bool)) . (?q6 ?p6))";
	public static final String SUBSET_MACRO = "(lambda (?p7 ('t Bool)) (?q7 ('t Bool)) . (and (subseteq ?p7 ?q7) (not (= ?p7 ?q7))))";
	public static final String SUBSETEQ_MACRO = "(lambda (?p8 ('t Bool)) (?q8 ('t Bool)) . (forall (?x6 't). (implies (?p8 ?x6) (?q8 ?x6))))";
	public static final String RANGE_INTEGER_MACRO = "(lambda (?i1 Int) (?i2 Int) . (lambda (?i Int) . (and (<= ?i1 ?i) (<= ?i ?i2))))";
	public static final String RANGE_SUBSTRACION_MACRO = "(lambda (?x8 ((Pair 's 't) Bool)(?q10 ('t Bool)).(lambda (?p10 (Pair 's 't).(and (?x8 ?p10)(not (?q10 (snd ?p10)))))))";
	public static final String RANGE_RESTRICTION_MACRO = "(lambda (?q11 ((Pair 's 't) Bool)(?x9 ('t Bool))(lambda (?p11 (Pair 's 't) (and (?q11 ?p11)(?x9 (snd ?p11))))))";
	public static final String RELATION_MACRO = "(lambda (?x10 ('s Bool)) (?q12 ('s Bool)) . (lambda (?r ((Pair 's 't) Bool)) . (forall (?p12 (Pair 's 't)) (implies (?r ?p12) (and (?x10 (fst ?p12))(?q12 (snd ?p12)))))))";
	// Using the totp (total property) to define this macro
	public static final String TOTAL_RELATION_MACRO = "(lambda (?x11 ('s Bool)) . (?r1 ((Pair 's 't) Bool))(forall (?p13 (Pair 's 't)) (= (?r1 ?p13) (?x11 (fst ?p13)))))";
	// Using the surp (surjective property) to define this macro
	public static final String SURJECTIVE_RELATION_MACRO = "(lambda (?y ('t Bool)) (?r2 ((Pair 's 't) Bool))(forall (?p14 (Pair 's 't)) (= (?r2 ?p14) (?y (snd ?p14)))))";
	// Using the conjunction of surjective relation and total relation macros
	public static final String TOTAL_SURJECTIVE_RELATION_MACRO = "(lambda (?x12 ('t Bool)) . (?r3 ((Pair 's 't) Bool))(and (forall (?p15 (Pair 's 't)) (= (?r3 ?p15) (?x12 (snd ?p15))))  (forall (?p16 (Pair 's 't)) (= (?r3 ?p16) (?x12 (fst ?p16))))))";
	public static final String PARTIAL_FUNCTION_MACRO = "(lambda (?x13 ('s Bool)) (?y1 ('s Bool)) . (lambda (?r4 ((Pair 's 't) Bool)) .  (and ((rel ?x13 ?y1) ?r4) (funp ?r4))))";
	public static final String TOTAL_FUNCTION_MACRO = "(lambda (?x14 ('s Bool)) (?y2 ('s Bool))(lambda (?r5 ((Pair 's 't) Bool)) (and ((pfun ?x14 ?y2) ?r5) (totp ?x14 ?r5))))";
	public static final String NAT_MACRO = "(lambda (?x15 Int) . (<= 0 ?x15))";
	public static final String NAT1_MACRO = "(lambda (?x16 Int) . (<= 1 ?x16))";
	public static final String INVERSE_MACRO = "(lambda (?r6 ((Pair 's 't) bool).(lambda (?p17 (Pair 's 't).(?r6 (pair (snd ?p17)(fst ?p17)))))))";
	public static final String ID_MACRO = "(lambda (?p18 (Pair 't 't)) . (= (fst ?p18)(snd ?p18)))";
	public static final String DOM = "(lambda (?r7 ('t1 't2 Bool)) . (lambda (?r17 't1) . (exists (?r18 't2) . (?r7 ?r17 ?r18))))";
	public static final String PARTIAL_INJECTION = "(lambda (?x17 ('s Bool)) . (?y3 ('s Bool))(lambda (?r19 ((Pair 's 't) Bool)) . (and ((pfun ?x17 ?y3) ?r19) (injp ?r19))))";
	public static final String TOTAL_INJECTION = "(lambda (?x18 ('s Bool)) . (?y4 ('s Bool))(lambda (?r20 ((Pair 's 't) Bool)) . (and ((pinj ?x18 ?y4) ?r20) (totp ?x18 ?r20))))";
	public static final String PARTIAL_SURJECTION = "(lambda (?x19 ('s Bool))(?y5 ('s Bool)) . (lambda (?r21 ((Pair 's 't) Bool)) .  (and ((pfun ?x19 ?y5) ?r21) (surp ?y5 ?r21))))";
	public static final String TOTAL_SURJECTION = "(lambda (?x20 ('s Bool)) (?y6 ('s Bool))(lambda (?r22 ((Pair 's 't) Bool)) (and ((psur ?x20 ?y6) ?r22) (totp ?x20 ?r22))))";
	public static final String TOTAL_BIJECTION = "(lambda (?X21 ('s Bool)) (?y7 ('s Bool)) . (lambda (?r23 ((Pair 's 't) Bool)) . (and ((tsur ?X21 ?y7) ?r23) ((tinj ?X21 ?y7)?r23))))";
	public static final String CARTESIAN_PRODUCT_MACRO = "(cartesianproduct (lambda (?p12 ('t1 Bool)) (?q12 ('t2 Bool)) . (lambda (?x1 't1) (?x2 't2) . (and (?p12 ?x1) (?q12 ?x2)))))";
	public static final String DOMAIN_RESTRICTION = "(lambda (?r24 ((Pair 's 't) Bool)(?s3 ('s Bool)) . (lambda (?p20 (Pair 's 't) . (and (?r24 ?p20)(?s3 (fst ?p20)))))))";
	public static final String DOMAIN_SUBSTRACTION = "(lambda (?r25 ((Pair 's 't) Bool)(?s4 ('s Bool)) . (lambda (?p21 (Pair 's 't) . (and (?r25 ?p21)(not (?s4 (fst ?p21))))))))";
	public static final String RELATIONAL_IMAGE = "(lambda (?r26 ((Pair 's 't) Bool)(?p22 ('s Bool)(lambda (?y10 't) (exists (?x22 's)(and (?p22 ?x22)(?r26 (pair ?x22 ?y10))))))";
	public static final String SETMINUS = "(lambda (?x23 ('t Bool)) (?q13 ('t Bool)) . (lambda (?x23 't) . (and (?x23 ?x23) (not (?q13 ?x23)))))";
	public static final String ISMIN_MACRO = "(lambda (?m Int) (?t (Int Bool)) . (and(in ?m ?t)(forall (?x24 Int) (implies (in ?x24 ?t)(<= ?m ?x24)))))";
	public static final String ISMAX_MACRO = "(lambda (?m1 Int) (?t1 (Int Bool)) . (and(in ?m1 ?t1)(forall (?x24 Int) (implies (in ?x24 ?t1)(<= ?x24 ?m1)))))";
	public static final String FINITE_MACRO = "(lambda (?p23 Bool) (?t2 ('s Bool)) (?f ('s Int)) (?k Int)(iff ?p23 (and (forall (?x25 s)(implies (in ?x25 ?t2)(in (?f ?x25)(range 1 ?k))))(forall (?x25 s)(?y s)(implies (and (in ?x25 ?t2)(in ?y ?t2)(not (equal ?x25 ?y)))(not (equal (?f ?x25)(?f ?y))))))))";
	public static final String CARD_MACRO = "(lambda (?t3 ('s Bool)) (?f1 ('s Int)) (?k1 Int))(forall (?x26 's)(implies (in ?x26 ?t3)(in (?f1 ?x26)(range 1 ?k1))))(forall (?x26 's)(?y1 's)(implies (and (in ?x26 ?t3) (in ?y1 ?t3))(iff(equal ?x26 ?y1) (equal (?f1 ?x26)(?f1 ?y1))))))";
	public static final String PAIR_MACRO = "(Pair (lambda (?e1 't) (?e2 't) . (lambda (?f1 't) (?f2 't) . (and (= ?f1 ?e1) (= ?f2 ?e2)))))";

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

	public static SMTPairEnumMacro makePairEnumerationMacro(
			final String macroName, final SMTVarSymbol varName1,
			final SMTVarSymbol varName2, final SMTTerm[] terms) {

		SMTMacroTerm[] macroTerms = new SMTMacroTerm[terms.length];
		for (int i = 0; i < macroTerms.length; i++) {
			macroTerms[i] = (SMTMacroTerm) terms[i];
		}

		return new SMTPairEnumMacro(macroName, varName1, varName2, macroTerms);
	}

	public static SMTEnumMacro makeEnumMacro(final String macroName,
			final SMTVarSymbol varName, final SMTTerm[] terms) {

		return new SMTEnumMacro(macroName, varName, terms);
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
	public static SMTSetComprehensionMacro makeSetComprehensionMacro(
			String macroName, SMTTerm[] terms, SMTVarSymbol lambdaVar,
			SMTFormula formula, SMTTerm expression) {
		// TODO Auto-generated method stub
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
				formula, expression);
	}

	public static SMTPredefinedMacro makePredefinedMacro(
			final SMTVeriTOperator operator) {
		switch (operator) {
		case BUNION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.BUNION,
					SMTMacros.BUNION_MACRO);
		}
		case BINTER: {
			return new SMTPredefinedMacro(SMTMacroSymbol.BINTER,
					SMTMacros.BINTER_MACRO);
		}
		case FCOMP: {
			return new SMTPredefinedMacro(SMTMacroSymbol.FCOMP,
					SMTMacros.FCOMP_MACRO);
		}
		case OVR: {
			return new SMTPredefinedMacro(SMTMacroSymbol.OVR,
					SMTMacros.REL_OVR_MACRO);
		}
		case EMPTY: {
			return new SMTPredefinedMacro(SMTMacroSymbol.EMPTY,
					SMTMacros.EMPTYSET_MACRO);
		}
		case EMPTY_PAIR: {
			return new SMTPredefinedMacro(SMTMacroSymbol.EMPTY_PAIR,
					SMTMacros.EMPTYSET_MACRO);
		}
		case IN: {
			return new SMTPredefinedMacro(SMTMacroSymbol.IN, SMTMacros.IN_MACRO);
		}
		case SUBSET: {
			return new SMTPredefinedMacro(SMTMacroSymbol.SUBSET,
					SMTMacros.SUBSET_MACRO);
		}
		case SUBSETEQ: {
			return new SMTPredefinedMacro(SMTMacroSymbol.SUBSETEQ,
					SMTMacros.SUBSETEQ_MACRO);
		}
		case RANGE_INTEGER: {
			return new SMTPredefinedMacro(SMTMacroSymbol.RANGE_INTEGER,
					SMTMacros.RANGE_INTEGER_MACRO);
		}
		case RANGE_SUBSTRACTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.RANGE_SUBSTRACION,
					SMTMacros.RANGE_SUBSTRACION_MACRO);
		}
		case RANGE_RESTRICTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.RANGE_RESTRICTION,
					SMTMacros.RANGE_RESTRICTION_MACRO);
		}
		case RELATION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.RELATION,
					SMTMacros.RELATION_MACRO);
		}
		case TOTAL_RELATION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.TOTAL_RELATION,
					SMTMacros.TOTAL_RELATION_MACRO);
		}
		case SURJECTIVE_RELATION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.SURJECTIVE_RELATION,
					SMTMacros.SURJECTIVE_RELATION_MACRO);
		}
		case TOTAL_SURJECTIVE_RELATION: {
			return new SMTPredefinedMacro(
					SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION,
					SMTMacros.TOTAL_SURJECTIVE_RELATION_MACRO);
		}
		case PARTIAL_FUNCTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.PARTIAL_FUNCTION,
					SMTMacros.PARTIAL_FUNCTION_MACRO);
		}
		case TOTAL_FUNCTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.TOTAL_FUNCTION,
					SMTMacros.TOTAL_FUNCTION_MACRO);
		}
		case ID: {
			return new SMTPredefinedMacro(SMTMacroSymbol.ID, SMTMacros.ID_MACRO);
		}
		case NAT: {
			return new SMTPredefinedMacro(SMTMacroSymbol.NAT,
					SMTMacros.NAT_MACRO);
		}
		case NAT1: {
			return new SMTPredefinedMacro(SMTMacroSymbol.NAT1,
					SMTMacros.NAT1_MACRO);
		}
		case INV: {
			return new SMTPredefinedMacro(SMTMacroSymbol.INV,
					SMTMacros.INVERSE_MACRO);
		}
		case DOM: {
			return new SMTPredefinedMacro(SMTMacroSymbol.DOM, SMTMacros.DOM);
		}
		case PARTIAL_INJECTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.PARTIAL_INJECTION,
					SMTMacros.PARTIAL_INJECTION);
		}
		case TOTAL_INJECTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.TOTAL_INJECTION,
					SMTMacros.TOTAL_INJECTION);
		}
		case PARTIAL_SURJECTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.BUNION,
					SMTMacros.BUNION_MACRO);
		}
		case TOTAL_SURJECTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.TOTAL_SURJECTION,
					SMTMacros.TOTAL_SURJECTION);
		}
		case TOTAL_BIJECTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.TOTAL_BIJECTION,
					SMTMacros.TOTAL_BIJECTION);
		}
		case CARTESIAN_PRODUCT: {
			return new SMTPredefinedMacro(SMTMacroSymbol.CARTESIAN_PRODUCT,
					SMTMacros.CARTESIAN_PRODUCT_MACRO);
		}
		case DOMAIN_RESTRICTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.DOMAIN_RESTRICTION,
					SMTMacros.DOMAIN_RESTRICTION);
		}
		case DOMAIN_SUBSTRACTION: {
			return new SMTPredefinedMacro(SMTMacroSymbol.DOMAIN_SUBSTRACTION,
					SMTMacros.DOMAIN_SUBSTRACTION);
		}
		case RELATIONAL_IMAGE: {
			return new SMTPredefinedMacro(SMTMacroSymbol.RELATIONAL_IMAGE,
					SMTMacros.RELATIONAL_IMAGE);
		}
		case SETMINUS: {
			return new SMTPredefinedMacro(SMTMacroSymbol.SETMINUS,
					SMTMacros.SETMINUS);
		}
		case ISMIN: {
			return new SMTPredefinedMacro(SMTMacroSymbol.ISMIN,
					SMTMacros.ISMIN_MACRO);
		}
		case ISMAX: {
			return new SMTPredefinedMacro(SMTMacroSymbol.ISMAX,
					SMTMacros.ISMAX_MACRO);
		}
		case FINITE: {
			return new SMTPredefinedMacro(SMTMacroSymbol.FINITE,
					SMTMacros.FINITE_MACRO);
		}
		case CARD: {
			return new SMTPredefinedMacro(SMTMacroSymbol.CARD,
					SMTMacros.CARD_MACRO);
		}
		default:
			throw new IllegalArgumentException(
					"There is no defined macro with symbol: "
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
					"There is no defined macro with symbol: "
							+ operator.toString());
		}
	}

	public static SMTMacroSymbol makeMacroSymbol(final String macroName) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT);
	}

}
