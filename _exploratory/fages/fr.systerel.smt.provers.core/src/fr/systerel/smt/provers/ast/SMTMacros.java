package fr.systerel.smt.provers.ast;

public class SMTMacros {

	public static boolean IS_GENERIC_SORT = true;

	public static String BUNION_MACRO = "(union (lambda (?p1 ('t boolean)) (?q1 ('t boolean)) . (lambda (?x1 't) . (or (?p1 ?x6) (?q1 ?x1)))))";
	public static String BINTER_MACRO = "(inter (lambda (?p2 ('t boolean))(?q2 ('t boolean)) . (lambda (?x2 't) . (and (?p2 ?x2) (?q2 ?x2)))))";
	public static String FCOMP = "(comp(lambda (?p3 ((Pair 's 't) bool)(?q3 ((Pair 't 'u) bool)(lambda (?s1 (Pair 's 'u))(exists (?x3 't) (and (?p3 (pair (fst ?s1) ?x3)) (?q3 (pair ?x3 (snd ?s1))))))))";
	public static String REL_OVR = "(ovr (lambda (?p4 ((Pair 's 't) bool)(?q4 ((Pair 's 't) bool)(lambda (?x4 (Pair 's 'u)) (or (?q4 ?x4)(and (?p4 ?x4)(not(exists(?s2 (Pair 's 't))(and (?q4 ?s2)(= (fst ?s2)(fst ?x4))))))))))))";

	private static SMTSortSymbol BOOLEAN = new SMTSortSymbol("boolean",
			!SMTSymbol.PREDEFINED);

	private static SMTGenericSortSymbol GENERIC = new SMTGenericSortSymbol("");

	private static SMTPairSortSymbol[] GENERIC_PAIRS = { new SMTPairSortSymbol(
			"", GENERIC, GENERIC) };

	private static SMTSortSymbol[] BOOL_BOOL = { BOOLEAN, BOOLEAN };

	public static SMTMacroTerm BUNION_TERM = new SMTMacroTerm("union",
			BOOL_BOOL);
	public static SMTMacroTerm BINTER_TERM = new SMTMacroTerm("inter",
			BOOL_BOOL);
	public static SMTMacroTerm FCOMP_TERM = new SMTMacroTerm("comp",
			GENERIC_PAIRS);
	public static SMTMacroTerm REL_OVR_TERM = new SMTMacroTerm("ovr",GENERIC_PAIRS);
	 

}
