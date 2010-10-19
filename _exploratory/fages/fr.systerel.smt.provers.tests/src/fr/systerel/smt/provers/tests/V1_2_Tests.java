package fr.systerel.smt.provers.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;

import br.ufrn.smt.solver.translation.TypeEnvironment;
import br.ufrn.smt.solver.translation.VisitorV1_2;


public class V1_2_Tests extends AbstractTests { 

	protected static final ITypeEnvironment defaultTe = mTypeEnvironment(//
			"S", "ℙ(S)", "T", "ℙ(T)", "U", "ℙ(U)", "V", "ℙ(V)");

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

	static ITypeEnvironment br_te = mTypeEnvironment("s", "ℙ(S)", "t", "ℙ(S)",
			"v", "ℙ(S)", "w", "ℙ(S)", "p", "S ↔ T", "q", "S ↔ T", "e1", "S",
			"e2", "S", "e3", "S");
	
	/**
	 *  Tests for BR1
	 */
	@Test
	public void testBR1_simple() {
		
		doTest( "s⊆t", br_te);
	}
	
	@Test
	public void testBR1_recursion() {

		doTest( "s∪v ⊆ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR2
	 */
	@Test
	public void testBR2_simple() {

		doTest( "s ⊈ t", br_te);
	}
	
	@Test
	public void testBR2_recursion() {
		
		doTest( "s∪v ⊈ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR3
	 */
	@Test
	public void testBR3_simple() {

		doTest( "s⊂t", br_te);
	}

	@Test
	public void testBR3_recursion() {

		doTest( "s∪v ⊂ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR4
	 */
	@Test
	public void testBR4_simple() {

		doTest( "s ⊄ t", br_te);
	}

	@Test
	public void testBR4_recursion() {

		doTest( "s∪v ⊄ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR5
	 */
	@Test
	public void testBR5_simple() {

		doTest( "s ≠ t", br_te);
	}

	@Test
	public void testBR5_recursion() {

		doTest( "s∪v ≠ t∪w", br_te);
	}
	
	/**
	 *  Tests for BR6
	 */
	@Test
	public void testBR6_simple() {

		doTest( "x ∉ s", br_te);
	}

	@Test
	public void testBR6_recursion() {

		doTest( "s∪v ∉ ℙ(t∪w)", br_te);
	}
	
	/**
	 * Tests for BR7
	 */
	@Test
	public void testBR7_simple() {

		doTest( "finite(s)", br_te);
	}
	
	@Test
	public void testBR7_recursive() {

		doTest( "finite(ℙ(s∪t))", br_te);
	}
	
	@Test
	public void testBR7_complex() {
		
		doTest( "∀x·∃y·y=t∨finite({s∪x∪y})",br_te);
	}
	
	@Test
	public void testBR8_simple() throws Exception {
		doTest( "partition(s, t, v)", br_te);
		doTest( "partition(s, {e1}, {e2}, {e3})", br_te);
	}

	@Test
	public void testBR8_recursive() throws Exception {
		doTest( "partition(ℙ(s∪t))",br_te);
	}

	static ITypeEnvironment er_te = mTypeEnvironment("f", "S ↔ T", "s", "ℙ(S)",
			"t", "ℙ(S)", "v", "ℙ(S)", "w", "ℙ(S)", "x", "S", "y", "T", "a",
			"S", "b", "T", "is", "ℙ(ℤ)", "it", "ℙ(ℤ)");

	/**
	 * Tests for ER1
	 */
	@Test
	public void testER1_simple() {
		
		doTest( "f(a) = f(a)",er_te);
	}
	
	/**
	 * Tests for ER2
	 */
	@Test
	public void testER2_simple() {
		
		doTest( "x↦y = a↦b",er_te);
	}
	
	@Test
	public void testER2_recursive() {
		doTest( "s∪v↦v∪t = t∪s↦v∪w", er_te);
	}
	
	/**
	 * Tests for ER3
	 */
	@Test
	public void testER3_simple() {
		
		doTest( "bool(n>0) = bool(n>2)", er_te);
	}
	
	@Test
	public void testER3_recursive() {

		doTest( "bool(1∈{1}) = bool(1∈{1,2})", er_te);
	}
	
	/**
	 * Tests for ER4
	 */
	@Test
	public void testER4_simple() {
		
		doTest( "bool(n>0) = TRUE",er_te);
	}
	
	@Test
	public void testER4_recursive() {

		doTest( "bool(1∈{1}) = TRUE", er_te);
	}

	/**
	 * Tests for ER5
	 */
	@Test
	public void testER5_simple() {
		
		doTest( "bool(n>0) = FALSE", er_te);
	}
	
	@Test
	public void testER5_recursive() {

		doTest( "bool(1∈{1}) = FALSE", er_te);
	}
	
	/**
	 * Tests for ER6
	 */
	@Test
	public void testER6_simple() {
		
		doTest( "x = FALSE");
	}
	
	@Test
	public void testER6_recursive() {

		doTest( "x = bool(1∈{1})");
	}
	
	/**
	 * Tests for ER7
	 */
	@Test
	public void testER7_simple() {
		
		doTest( "x = bool(n>0)");
	}
	
	@Test
	public void testER7_recursive() {

		doTest( "x = bool(1∈{1})");
	}

	/**
	 * Tests for ER8
	 */
	@Test
	public void testER8_simple() {
		
		doTest( "y = f(x)", 
				er_te);
	}
	
	@Test
	public void testER8_recursive() {
		ITypeEnvironment te = mTypeEnvironment("f", "ℙ(S) ↔ ℙ(T)", "s", "ℙ(S)",
				"t", "ℙ(T)", "v", "ℙ(S)", "w", "ℙ(T)");
		doTest( "t∪w = f(s∪v)", 
				 te);
	}

	/**
	 * Tests for ER9
	 */
	@Test
	public void testER9_simple_inGoal() {
		
		doTest( "s = t", 
				er_te);
	}
	
	@Test
	public void testER9_simple() {
		
		doTest( "is = ℕ", 
				er_te);
	}
	
	@Test
	public void testER9_recursive() {
		doTest( "s∪v = t∪w", 
				er_te);
	}
	
	/**
	 * Tests for ER10
	 */
	@Test
	public void testER10_simple() {
		
		doTest( "n = card(s)", 
				 er_te);
	}
	
	@Test
	public void testER10_recursive() {

		doTest( "n = card(s∪t)", 
				 er_te);
	}
	
	@Test
	public void testER10_complex() {
		doTest( "∀m,d·m = card(s∪d)", 
				 er_te);
	}

	/**
	 * Tests for ER11
	 */
	@Test
	public void testER11_simple() {
		
		doTest( "n = min(is)", 
				er_te);
	}
	
	@Test
	public void testER11_recursive() {

		doTest( "n = min(is∪it)", 
				 er_te);
	}

	/**
	 * Tests for ER12
	 */
	@Test
	public void testER12_simple() {
		
		doTest( "n = max(is)", 
				 er_te);
	}
	
	@Test
	public void testER12_recursive() {

		doTest( "n = max(is∪it)", 
				 er_te);
	}
	
	private static ITypeEnvironment cr_te = mTypeEnvironment(//
			"s", "ℙ(ℤ)", "t", "ℙ(ℤ)");

	private static ITypeEnvironment cr_ste = mTypeEnvironment(//
			"s", "ℙ(BOOL)", "t", "ℙ(ℤ)");

	/**
	 * Tests for CR1
	 */
	@Test
	public void testCR1_simple() {
		
		doTest( "a < min(s)", 
				 cr_te);
	}
	
	@Test
	public void testCR1_recursive() {

		doTest( "min(t) < min(s∪t)", 
				 cr_te);
	}	
	
	@Test
	public void testCR1_complex() {
		
		doTest( "∀s·∃t·min(t) < min(s∪t)",
				 cr_te);
	}
	
	/**
	 * Tests for CR2
	 */
	@Test
	public void testCR2_simple() {
		
		doTest( "max(s) < a", 
				 cr_te);
	}
	
	@Test
	public void testCR2_recursive() {

		doTest( "max(s∪t) < max(t)", 
				 cr_te);
	}	
	
	@Test
	public void testCR2_complex() {
		
		doTest( "∀s·∃t·max(s∪t) < max(t)",
				 cr_te);
	}

	/**
	 * Tests for CR3
	 */
	@Test
	public void testCR3_simple() {
		
		doTest( "min(s) < a", 
				 cr_te);
	}
	
	@Test
	public void testCR3_recursive() {

		doTest( "min(s∪t) < min(t)", 
				 cr_te);
	}	
	
	@Test
	public void testCR3_complex() {
		
		doTest( "∀s·∃t·min(s∪t) < min(t)",
				 cr_te);
	}

	/**
	 * Tests for CR4
	 */
	@Test
	public void testCR4_simple() {
		
		doTest( "a < max(s)", 
				 cr_te);
	}
	
	@Test
	public void testCR4_recursive() {

		doTest( "max(t) < max(s∪t)", 
				 cr_te);
	}	
	
	@Test
	public void testCR4_complex() {
		
		doTest( "∀s·∃t·max(t) < max(s∪t)",
				 cr_te);
	}
	
	/**
	 * Tests for CR5
	 */
	@Test
	public void testCR5_simple() {
		
		doTest( "a > b", 
				 cr_te);
	}
	
	@Test
	public void testCR5_recursive() {

		doTest( "min(t) > max(s)", 
				 cr_te);
	}	
	
	/**
	 * Tests for IR1
	 */
	@Test
	public void testIR1_simple1() {
		ITypeEnvironment te = mTypeEnvironment("e", "S");

		doTest( "e ∈ S", te);
	}
	
	/**
	 * Tests for IR2
	 */
	@Test
	public void testIR2_simple() {
		
		doTest( "e∈ℙ(t)", cr_te);
	}
	
	@Test
	public void testIR2_complex() {

		doTest( "∀f,t·e∪f∈ℙ(s∪t)", cr_te);
	}	
	
	/**
	 * Tests for IR2'
	 */
	@Test
	public void testIR2prime_simple() {
		
		doTest( "e∈s↔t", cr_ste);
	}
	
	@Test
	public void testIR2prime_complex() {

		doTest( "∀f⦂ℤ↔ℤ,t·e;f ∈ s↔t", cr_ste);
	}	
	
	/**
	 * Tests for IR3
	 */
	@Test
	public void testIR3_simple() {
		
		doTest( "1 ∈ s");
	}
	
	@Test
	public void testIR3_recursive() {

		doTest( "s∪t ∈ v",
				mTypeEnvironment("s", "ℙ(S)"));
		
	}

	@Test
	public void testIR3_complex() {

		doTest( "∀t,w·s∪t ∈ w",
				mTypeEnvironment("s", "ℙ(S)"));
		
	}

	@Test
	public void testIR3_additional_1() {
		
		doTest( "a↦1 ∈ S",
				mTypeEnvironment("S", "BOOL ↔ ℤ"));
	}
	
	@Test
	public void testIR3_additional_2() {
		
		doTest( "a↦1↦2 ∈ S",
				mTypeEnvironment("S", "BOOL×ℤ ↔ ℤ"));
	}
	
	@Test
	public void testIR3_additional_3() {
		
		doTest( "a↦b↦f(10)∈S",
				mTypeEnvironment("S", "BOOL×ℤ ↔ ℤ"));
	}

	@Test
	public void testIR3_additional_4() {
		
		doTest( "f(a)  ∈ S",
				mTypeEnvironment("f", "S ↔ T"));
	}
	
	@Test
	public void testIR3_additional_5() {
		
		doTest( "f(a)  ∈ S",
				mTypeEnvironment("f", "S ↔ T×U"));
	}
	
	/**
	 * Tests for IR4
	 */
	@Test
	public void testIR4_simple() {
		
		doTest( "e∈ℕ", mTypeEnvironment("e", "ℤ"));
	}
	
	/**
	 * Tests for IR5
	 */
	@Test
	public void testIR5_simple() {
		
		doTest( "e∈ℕ1", mTypeEnvironment("e", "ℤ"));
	}
	
	/**
	 * Tests for IR6
	 */
	@Test
	public void testIR6_simple() {

		doTest( "e ∈ {x·a<x∣f}",
				mTypeEnvironment("e", "ℤ"));
	}
	
	@Test
	public void testIR6_recursive() {

		doTest( "e∈{x·x∈{1}∣f∪g}",
				mTypeEnvironment("e", "ℙ(S)"));
	}

	@Test
	public void testIR6_complex() {

		doTest( "∀f,b·e∈{x·x∈{1, b}∣f∪g}",
				mTypeEnvironment("e", "ℙ(S)"));
	}

	/**
	 * Tests for IR7
	 */
	@Test
	public void testIR7_simple() {

		doTest( "e ∈ (⋂x·a<x∣f)",
				mTypeEnvironment("e", "ℤ"));
	}
	
	@Test
	public void testIR7_recursive() {

		doTest( "e ∈ (⋂x·x∈{1}∣f∪g)",
				mTypeEnvironment("e", "S"));
	}

	@Test
	public void testIR7_complex() {

		doTest( "∀f,b·e ∈ (⋂x·x∈{1, b}∣f∪g)", 
				mTypeEnvironment("e", "S"));
	}
	
	private static ITypeEnvironment fct_ste = mTypeEnvironment(
			"f", "ℤ ↔ ℤ", "g", "ℤ ↔ ℤ");
	
	/**
	 * Tests for functions
	 */
	@Test
	public void testFunc_1() {
		
		doTest( "g∘f = f∘g",
				fct_ste);
	}
	
	@Test
	public void testFunc_2() {
		
		doTest( "(g∘f)(1) = 2",
				fct_ste);
	}
	
	@Test
	public void testFunc_3() {
			
		doTest( "{1} ◁ f≠f",
				fct_ste);
	}
	
	@Test
	public void testFunc_4() {
		
		doTest( "f ▷ {1} ≠f",
				fct_ste);
	}
	
	@Test
	public void testFunc_5() {
		
		doTest( "{1} ⩤ f ≠ f",
				fct_ste);
	}
	
	@Test
	public void testFunc_6() {
		
		doTest( "f ⩥ {1} ≠f",
				fct_ste);
	}
	
	@Test
	public void testFunc_7() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	@Test
	public void testFunc_8() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	@Test
	public void testFunc_9() {
		doTest( "f∈st", 
				cr_ste);
	}
	
	@Test
	public void testFunc_10() {
		doTest( "f∈s↔t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_11() {
		doTest( "f∈s⤖t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_12() {
		doTest( "f∈s⇸t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_13() {
		doTest( "f∈s⤔t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_14() {
		doTest( "f∈s↣t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_15() {
		doTest( "f∈s⤀t", 
				cr_ste);
	}
	
	@Test
	public void testFunc_16() {
		doTest( "f∈s↠t", 
				cr_ste);
	}
	

	/**
	 * Additional Tests
	 */
	@Test
	public void testAdd_1() {
		
		doTest("{1,4,3,8} ⊂ A",
				mTypeEnvironment("A", "ℙ(ℤ)"));
	}
	
	@Test
	public void testAdd_2() {
		
		doTest("{{1},{2}} ⊆ ℙ(ℕ1)");
	}

	
	


}
