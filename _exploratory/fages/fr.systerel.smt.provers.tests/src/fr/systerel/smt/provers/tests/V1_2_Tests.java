package fr.systerel.smt.provers.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import java.util.ArrayList;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.pptrans.Translator;

import fr.systerel.smt.provers.tests.AbstractTranslationTests;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.VisitorV1_2;
import br.ufrn.smt.solver.translation.TypeEnvironment;


public class V1_2_Tests extends AbstractTranslationTests {

	protected static final Type S = ff.makeGivenType("S");
	protected static final Type T = ff.makeGivenType("T");
	protected static final Type U = ff.makeGivenType("U");
	protected static final Type V = ff.makeGivenType("V");
	protected static final Type X = ff.makeGivenType("X");
	protected static final Type Y = ff.makeGivenType("Y");
	protected static final ITypeEnvironment defaultTe;
	static {
		defaultTe = ff.makeTypeEnvironment();
		defaultTe.addGivenSet("S");
		defaultTe.addGivenSet("T");
		defaultTe.addGivenSet("U");
		defaultTe.addGivenSet("V");
	}

	private static void doTest(String input) {
		doTest(input, defaultTe);
	}

	private static void doTest(String input, ITypeEnvironment te) {	
		Predicate pinput = parse(input, te);
		doTest(pinput);
	}
	
	private static void doTest(Predicate input) {
		assertTypeChecked(input);
		
		// reduce predicate
		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);
		
		// Create a Type environment and a fids list to use Visitor1_2
		TypeEnvironment type = new TypeEnvironment(null, null);
		ArrayList<String> fids = new ArrayList<String>();
		
		VisitorV1_2 visHyp = new VisitorV1_2(type, fids);
		actual.accept(visHyp);
		
		// Get smt translation via channel 2
		String translatedHyp = visHyp.getSMTNode();

		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		//assertEquals("Unexpected result of translation", expected, actual);
	}
	

	static ITypeEnvironment br_te = mTypeEnvironment(
			mList("s", "t", "v", "w", "p", "q", "e1", "e2", "e3"), 
			mList(POW(S), POW(S), POW(S), POW(S), REL(S, T), REL(S, T), S, S, S));
	
	/**
	 *  Tests for BR1
	 */
	public void testBR1_simple() {
		
		doTest( "s⊆t", br_te);
	}
	
	public void testBR1_recursion() {

		doTest( "s∪v ⊆ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR2
	 */
	public void testBR2_simple() {

		doTest( "s ⊈ t", br_te);
	}
	
	public void testBR2_recursion() {
		
		doTest( "s∪v ⊈ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR3
	 */
	public void testBR3_simple() {

		doTest( "s⊂t", br_te);
	}

	public void testBR3_recursion() {

		doTest( "s∪v ⊂ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR4
	 */
	public void testBR4_simple() {

		doTest( "s ⊄ t", br_te);
	}

	public void testBR4_recursion() {

		doTest( "s∪v ⊄ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR5
	 */
	public void testBR5_simple() {

		doTest( "s ≠ t", br_te);
	}

	public void testBR5_recursion() {

		doTest( "s∪v ≠ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR6
	 */
	public void testBR6_simple() {

		doTest( "x ∉ s", br_te);
	}

	public void testBR6_recursion() {

		doTest( "s∪v ∉ ℙ(t∪w)", br_te);
	}
	
	/**
	 * Tests for BR7
	 */
	public void testBR7_simple() {

		doTest( "finite(s)", br_te);
	}
	
	public void testBR7_recursive() {

		doTest( "finite(ℙ(s∪t))", br_te);
	}
	
	public void testBR7_complex() {
		
		doTest( "∀x·∃y·y=t∨finite({s∪x∪y})",br_te);
	}
	
	public void testBR8_simple() throws Exception {
		doTest( "partition(s, t, v)", br_te);
		doTest( "partition(s, {e1}, {e2}, {e3})", br_te);
	}

	public void testBR8_recursive() throws Exception {
		doTest( "partition(ℙ(s∪t))",br_te);
	}

	static ITypeEnvironment er_te = mTypeEnvironment(
			mList("f", "s", "t", "v", "w", "x", "y", "a", "b", "is", "it"), 
			mList(REL(S, T), POW(S), POW(S), POW(S), POW(S), S, T, S, T, INT_SET, INT_SET));

	/**
	 * Tests for ER1
	 */
	public void testER1_simple() {
		
		doTest( "f(a) = f(a)",er_te);
	}
	
	/**
	 * Tests for ER2
	 */
	public void testER2_simple() {
		
		doTest( "x↦y = a↦b",er_te);
	}
	
	public void testER2_recursive() {
		doTest( "s∪v↦v∪t = t∪s↦v∪w", er_te);
	}
	
	/**
	 * Tests for ER3
	 */
	public void testER3_simple() {
		
		doTest( "bool(n>0) = bool(n>2)", er_te);
	}
	
	public void testER3_recursive() {

		doTest( "bool(1∈{1}) = bool(1∈{1,2})", er_te);
	}
	
	/**
	 * Tests for ER4
	 */
	public void testER4_simple() {
		
		doTest( "bool(n>0) = TRUE",er_te);
	}
	
	public void testER4_recursive() {

		doTest( "bool(1∈{1}) = TRUE", er_te);
	}

	/**
	 * Tests for ER5
	 */
	public void testER5_simple() {
		
		doTest( "bool(n>0) = FALSE", er_te);
	}
	
	public void testER5_recursive() {

		doTest( "bool(1∈{1}) = FALSE", er_te);
	}
	
	/**
	 * Tests for ER6
	 */
	public void testER6_simple() {
		
		doTest( "x = FALSE");
	}
	
	public void testER6_recursive() {

		doTest( "x = bool(1∈{1})");
	}
	
	/**
	 * Tests for ER7
	 */
	public void testER7_simple() {
		
		doTest( "x = bool(n>0)");
	}
	
	public void testER7_recursive() {

		doTest( "x = bool(1∈{1})");
	}

	/**
	 * Tests for ER8
	 */
	public void testER8_simple() {
		
		doTest( "y = f(x)", 
				er_te);
	}
	
	public void testER8_recursive() {
		ITypeEnvironment te = mTypeEnvironment(
				mList( "f", "s", "t", "v", "w"),
				mList(REL(POW(S), POW(T)), POW(S), POW(T), POW(S), POW(T)));

		doTest( "t∪w = f(s∪v)", 
				 te);
	}

	/**
	 * Tests for ER9
	 */
	public void testER9_simple_inGoal() {
		
		doTest( "s = t", 
				er_te);
	}
	
	public void testER9_simple() {
		
		doTest( "is = ℕ", 
				er_te);
	}
	
	public void testER9_recursive() {
		doTest( "s∪v = t∪w", 
				er_te);
	}
	
	/**
	 * Tests for ER10
	 */
	public void testER10_simple() {
		
		doTest( "n = card(s)", 
				 er_te);
	}
	
	public void testER10_recursive() {

		doTest( "n = card(s∪t)", 
				 er_te);
	}
	
	public void testER10_complex() {
		doTest( "∀m,d·m = card(s∪d)", 
				 er_te);
	}

	/**
	 * Tests for ER11
	 */
	public void testER11_simple() {
		
		doTest( "n = min(is)", 
				er_te);
	}
	
	public void testER11_recursive() {

		doTest( "n = min(is∪it)", 
				 er_te);
	}

	/**
	 * Tests for ER12
	 */
	public void testER12_simple() {
		
		doTest( "n = max(is)", 
				 er_te);
	}
	
	public void testER12_recursive() {

		doTest( "n = max(is∪it)", 
				 er_te);
	}
	
	private static ITypeEnvironment cr_te = mTypeEnvironment(
			mList( "s", "t"), mList(INT_SET, INT_SET));

	private static ITypeEnvironment cr_ste = mTypeEnvironment(
			mList( "s", "t"), mList(POW(BOOL), INT_SET));

	/**
	 * Tests for CR1
	 */
	public void testCR1_simple() {
		
		doTest( "a < min(s)", 
				 cr_te);
	}
	
	public void testCR1_recursive() {

		doTest( "min(t) < min(s∪t)", 
				 cr_te);
	}	
	
	public void testCR1_complex() {
		
		doTest( "∀s·∃t·min(t) < min(s∪t)",
				 cr_te);
	}
	
	/**
	 * Tests for CR2
	 */
	public void testCR2_simple() {
		
		doTest( "max(s) < a", 
				 cr_te);
	}
	
	public void testCR2_recursive() {

		doTest( "max(s∪t) < max(t)", 
				 cr_te);
	}	
	
	public void testCR2_complex() {
		
		doTest( "∀s·∃t·max(s∪t) < max(t)",
				 cr_te);
	}

	/**
	 * Tests for CR3
	 */
	public void testCR3_simple() {
		
		doTest( "min(s) < a", 
				 cr_te);
	}
	
	public void testCR3_recursive() {

		doTest( "min(s∪t) < min(t)", 
				 cr_te);
	}	
	
	public void testCR3_complex() {
		
		doTest( "∀s·∃t·min(s∪t) < min(t)",
				 cr_te);
	}

	/**
	 * Tests for CR4
	 */
	public void testCR4_simple() {
		
		doTest( "a < max(s)", 
				 cr_te);
	}
	
	public void testCR4_recursive() {

		doTest( "max(t) < max(s∪t)", 
				 cr_te);
	}	
	
	public void testCR4_complex() {
		
		doTest( "∀s·∃t·max(t) < max(s∪t)",
				 cr_te);
	}
	
	/**
	 * Tests for CR5
	 */
	public void testCR5_simple() {
		
		doTest( "a > b", 
				 cr_te);
	}
	
	public void testCR5_recursive() {

		doTest( "min(t) > max(s)", 
				 cr_te);
	}	
	
	/**
	 * Tests for IR1
	 */
	public void testIR1_simple1() {
		ITypeEnvironment te = mTypeEnvironment(
				mList("e"), mList(S));

		doTest( "e ∈ S", te);
	}
	
	/**
	 * Tests for IR2
	 */
	public void testIR2_simple() {
		
		doTest( "e∈ℙ(t)", cr_te);
	}
	
	public void testIR2_complex() {

		doTest( "∀f,t·e∪f∈ℙ(s∪t)", cr_te);
	}	
	
	/**
	 * Tests for IR2'
	 */
	public void testIR2prime_simple() {
		
		doTest( "e∈s↔t", cr_ste);
	}
	
	public void testIR2prime_complex() {

		doTest( "∀f⦂ℤ↔ℤ,t·e;f ∈ s↔t", cr_ste);
	}	
	
	/**
	 * Tests for IR3
	 */
	public void testIR3_simple() {
		
		doTest( "1 ∈ s");
	}
	
	public void testIR3_recursive() {

		doTest( "s∪t ∈ v",
				mTypeEnvironment(mList("s"), mList(POW(S))));
		
	}

	public void testIR3_complex() {

		doTest( "∀t,w·s∪t ∈ w",
				mTypeEnvironment(mList("s"), mList(POW(S))));
		
	}

	public void testIR3_additional_1() {
		
		doTest( "a↦1 ∈ S",
				mTypeEnvironment(mList("S"), mList(REL(BOOL,INT))));
	}
	
	public void testIR3_additional_2() {
		
		doTest( "a↦1↦2 ∈ S",
				mTypeEnvironment(mList("S"), mList(REL(CPROD(BOOL, INT),INT))));
	}
	
	public void testIR3_additional_3() {
		
		doTest( "a↦b↦f(10)∈S",
				mTypeEnvironment(mList("S"), mList(POW(CPROD(CPROD(BOOL, INT),INT)))));
	}

	public void testIR3_additional_4() {
		
		doTest( "f(a)  ∈ S",
				mTypeEnvironment(mList("f"), mList(REL(S, T))));
	}
	
	public void testIR3_additional_5() {
		
		doTest( "f(a)  ∈ S",
				 mTypeEnvironment(mList("f"), mList(REL(S, CPROD(T, U)))));
	}
	
	/**
	 * Tests for IR4
	 */
	public void testIR4_simple() {
		
		doTest( "e∈ℕ", mTypeEnvironment(mList("e"), mList(INT)));
	}
	
	/**
	 * Tests for IR5
	 */
	public void testIR5_simple() {
		
		doTest( "e∈ℕ1", mTypeEnvironment(mList("e"), mList(INT)));
	}
	
	/**
	 * Tests for IR6
	 */
	public void testIR6_simple() {

		doTest( "e ∈ {x·a<x∣f}",
				mTypeEnvironment(mList("e"), mList(INT)));
	}
	
	public void testIR6_recursive() {

		doTest( "e∈{x·x∈{1}∣f∪g}",
				mTypeEnvironment(mList("e"), mList(POW(S))));
	}

	public void testIR6_complex() {

		doTest( "∀f,b·e∈{x·x∈{1, b}∣f∪g}",
				mTypeEnvironment(mList("e"), mList(POW(S))));
	}

	/**
	 * Tests for IR7
	 */
	public void testIR7_simple() {

		doTest( "e ∈ (⋂x·a<x∣f)",
				mTypeEnvironment(mList("e"), mList(INT)));
	}
	
	public void testIR7_recursive() {

		doTest( "e ∈ (⋂x·x∈{1}∣f∪g)",
				mTypeEnvironment(mList("e"), mList(S)));
	}

	public void testIR7_complex() {

		doTest( "∀f,b·e ∈ (⋂x·x∈{1, b}∣f∪g)", 
				mTypeEnvironment(mList("e"), mList(S)));
	}
	
	private static ITypeEnvironment fct_ste = mTypeEnvironment(
			mList("f", "g"), mList(REL(INT, INT), REL(INT, INT)));
	
	/**
	 * Tests for functions
	 */
	public void testFunc_1() {
		
		doTest( "g∘f = f∘g",
				fct_ste);
	}
	
	public void testFunc_2() {
		
		doTest( "(g∘f)(1) = 2",
				fct_ste);
	}
	
	public void testFunc_3() {
			
			doTest( "{1} ◁ f≠f",
					fct_ste);
	}
	
	public void testFunc_4() {
		
		doTest( "f ▷ {1} ≠f",
				fct_ste);
	}
	
	public void testFunc_5() {
		
		doTest( "{1} ⩤ f ≠ f",
				fct_ste);
	}
	
	public void testFunc_6() {
		
		doTest( "f ⩥ {1} ≠f",
				fct_ste);
	}
	
	public void testFunc_7() {
		
		doTest( "f ⩥ {1} ≠f",
				fct_ste);
	}
	
	public void testFunc_8() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	public void testFunc_9() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	public void testFunc_10() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	public void testFunc_11() {
		doTest( "f∈s↔t", 
				cr_ste);
	}
	
	public void testFunc_12() {
		doTest( "f∈s⤖t", 
				cr_ste);
	}
	
	public void testFunc_13() {
		doTest( "f∈s⇸t", 
				cr_ste);
	}
	
	public void testFunc_14() {
		doTest( "f∈s⤔t", 
				cr_ste);
	}
	
	public void testFunc_15() {
		doTest( "f∈s↣t", 
				cr_ste);
	}
	
	public void testFunc_16() {
		doTest( "f∈s⤀t", 
				cr_ste);
	}
	
	public void testFunc_17() {
		doTest( "f∈s↠t", 
				cr_ste);
	}
	

	/**
	 * Additional Tests
	 */
	public void testAdd_1() {
		
		doTest("{1,4,3,8} ⊂ A",
				mTypeEnvironment(mList("A"), mList(INT_SET)));
	}
	


}
