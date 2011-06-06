/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests.unit;

import static br.ufrn.smt.solver.translation.SMTSolver.VERIT;
import static org.eventb.core.ast.Formula.FORALL;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.junit.Ignore;
import org.junit.Test;

import br.ufrn.smt.solver.translation.SMTThroughVeriT;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.core.tests.AbstractTests;

/**
 * Ensure that translation to veriT extended version of SMT-LIB is correct
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class TranslationTestsWithVeriT extends AbstractTests {
	protected static final ITypeEnvironment defaultTe, simpleTe, cdisTe;
	protected static final SMTLogic defaultLogic;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";
	private SMTSignature signature;

	static {
		simpleTe = mTypeEnvironment("e", "ℙ(S)", "f", "ℙ(S)", "g", "S", "AB",
				"ℤ ↔ ℤ");

		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ", "b",
				"ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");

		cdisTe = mTypeEnvironment("S", "ℙ(S)", "R", "ℙ(R)", "f", "S ↔  R", "x",
				"S", "y", "R");

		defaultLogic = SMTLogic.VeriTSMTLIBUnderlyingLogic.getInstance();
	}

	private static void testTranslationV1_2Default(final String predStr,
			final String expectedSMTNode) {
		testTranslationV1_2(defaultTe, predStr, expectedSMTNode,
				defaultFailMessage, VERIT.toString());
	}

	private static void testTranslationV1_2VerDefaultSolver(
			final ITypeEnvironment typeEnvironment, final String ppPredStr,
			final String expectedSMTNode) {
		testTranslationV1_2(typeEnvironment, ppPredStr, expectedSMTNode,
				defaultFailMessage, VERIT.toString());
	}

	/**
	 * Parses a Predicate Calculus formula, (builds hypotheses and goal) and
	 * tests its SMT-LIB translation
	 * 
	 * @param iTypeEnv
	 *            Input type environment
	 * @param predStr
	 *            String representation of the input predicate
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2(final ITypeEnvironment iTypeEnv,
			final String predStr, final String expectedSMTNode,
			final String failMessage, final String solver)
			throws AssertionError {
		final Predicate pred = parse(predStr, iTypeEnv);

		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(pred);

		testTranslationV1_2Verit(pred, expectedSMTNode, failMessage, solver);
	}

	/**
	 * Tests the SMT-LIB translation with the given Predicate Calculus formula
	 * 
	 * @param ppred
	 *            Input Predicate Calculus formula
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2Verit(final Predicate ppred,
			final String expectedSMTNode, final String failMessage,
			final String solver) {

		final StringBuilder actualSMTNode = new StringBuilder();

		SMTThroughVeriT.translate(defaultLogic, ppred, solver).toString(
				actualSMTNode, false);

		System.out.println(translationMessage(ppred, actualSMTNode.toString()));
		assertEquals(failMessage, expectedSMTNode, actualSMTNode.toString());
	}

	public void setSignatureForTestsVerit(final ITypeEnvironment typeEnvironment) {
		signature = SMTThroughVeriT.translateSMTSignature(typeEnvironment,
				VERIT.toString());
	}

	private static final String translationMessage(final Predicate ppPred,
			final String smtNode) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\'");
		sb.append(ppPred.toString());
		sb.append("\' was translated in \'");
		sb.append(smtNode);
		sb.append("\'");
		return sb.toString();
	}

	@Test
	public void testTypeEnvironmentFunctionSimpleTe() {
		setSignatureForTestsVerit(simpleTe);
		final Set<String> expectedFunctions = new HashSet<String>();

		expectedFunctions.add("(g S)");
		expectedFunctions.add("(pair 's 't (Pair 's 't))");
		expectedFunctions.add("(expn Int Int Int)");
		expectedFunctions.add("(mod Int Int Int)");
		expectedFunctions.add("(divi Int Int Int)");

		testTypeEnvironmentFuns(expectedFunctions, signature);
	}

	/**
	 * Testing rule 7
	 */
	@Test
	public void testTypeEnvironmenSortSimpleTe() {
		setSignatureForTestsVerit(simpleTe);
		final Set<String> expectedSorts = new HashSet<String>();

		expectedSorts.add("S");
		expectedSorts.add("(Pair 's 't)");
		expectedSorts.add("Int");

		testTypeEnvironmentSorts(expectedSorts, signature);
	}

	/**
	 * Testing rules 4, 5 and 6
	 */
	@Test
	public void testTypeEnvironmentPredicateSimpleTe() {
		setSignatureForTestsVerit(simpleTe);
		final Set<String> expectedPredicates = new HashSet<String>();

		expectedPredicates.add("(e S)");
		expectedPredicates.add("(f S)");
		expectedPredicates.add("(AB (Pair Int Int))");

		testTypeEnvironmentPreds(expectedPredicates, signature);
	}

	@Test
	public void testTypeEnvironmentPredicateDefaultTe() {
		setSignatureForTestsVerit(defaultTe);
		final Set<String> expectedPredicates = new HashSet<String>();

		expectedPredicates.add("(r R)");
		expectedPredicates.add("(s R)");
		expectedPredicates.add("(A Int)");
		expectedPredicates.add("(AB (Pair Int Int))");
		expectedPredicates.add("(S_0 S)");

		testTypeEnvironmentPreds(expectedPredicates, signature);
	}

	/**
	 * Testing rule 8
	 */
	@Test
	public void testTypeEnvironmentFunctionDefaultTe() {
		setSignatureForTestsVerit(defaultTe);
		final Set<String> expectedFunctions = new HashSet<String>();

		expectedFunctions.add("(p S)");
		expectedFunctions.add("(q S)");
		expectedFunctions.add("(a Int)");
		expectedFunctions.add("(b Int)");
		expectedFunctions.add("(c Int)");
		expectedFunctions.add("(u Bool)");
		expectedFunctions.add("(v Bool)");
		expectedFunctions.add("(pair 's 't (Pair 's 't))");
		expectedFunctions.add("(expn Int Int Int)");
		expectedFunctions.add("(mod Int Int Int)");
		expectedFunctions.add("(divi Int Int Int)");

		testTypeEnvironmentFuns(expectedFunctions, signature);
	}

	/**
	 * "pred-ass"
	 */
	@Test
	public void testPredAssop() {

		testTranslationV1_2Default("(u = v)", "(iff u v)");

		/**
		 * land
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v)",
				"(and (= a b) (iff u v))");
		/**
		 * land (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v) ∧ (r = s)",
				"(and (= a b) (iff u v) (= r s))");
		/**
		 * lor
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v)",
				"(or (= a b) (iff u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (iff u v) (= r s))");
	}

	/**
	 * "pred-boolequ with constants only"
	 */
	@Test
	public void testPredBoolEquCnst() {
		testTranslationV1_2Default("u = v", "(iff u v)");
	}

	/**
	 * "pred-boolequ"
	 */
	@Test
	@Ignore("Not yet implemented")
	public void testPredBoolEqu() {
		testTranslationV1_2Default("u = TRUE", "(= u TRUE)");
		testTranslationV1_2Default("TRUE = u", "(= TRUE u)");
	}

	/**
	 * "pred-bin" in ppTrans abstract syntax
	 */
	@Test
	public void testPredBinop() {
		/**
		 * limp
		 */
		testTranslationV1_2Default("(a < b ∧ b < c) ⇒ a < c",
				"(implies (and (< a b) (< b c)) (< a c))");
		/**
		 * leqv
		 */
		testTranslationV1_2Default("(a ≤ b ∧ b ≤ a) ⇔ a = b",
				"(iff (and (<= a b) (<= b a)) (= a b))");
	}

	/**
	 * "pred-una" Testing rule 14:
	 */
	@Test
	public void testPredUna() {
		testTranslationV1_2Default("¬ ((a ≤ b ∧ b ≤ c) ⇒ a < c)",
				"(not (implies (and (<= a b) (<= b c)) (< a c)))");
	}

	/**
	 * "pred-lit"
	 */
	@Test
	public void testPredLit() {
		/**
		 * btrue
		 */
		testTranslationV1_2Default("⊤", "true");
		/**
		 * bfalse
		 */
		testTranslationV1_2Default("⊥", "false");
	}

	/**
	 * "pred-rel"
	 */
	@Test
	public void testPredRelop() {
		/**
		 * equal (identifiers of type ℤ)
		 */
		testTranslationV1_2Default("a = b", "(= a b)");
		/**
		 * equal (integer numbers)
		 */
		testTranslationV1_2Default("42 = 42", "(= 42 42)");
		/**
		 * notequal
		 */
		testTranslationV1_2Default("a ≠ b", "(not (= a b))");
		/**
		 * lt
		 */
		testTranslationV1_2Default("a < b", "(< a b)");
		/**
		 * le
		 */
		testTranslationV1_2Default("a ≤ b", "(<= a b)");
		/**
		 * gt
		 */
		testTranslationV1_2Default("a > b", "(> a b)");
		/**
		 * ge
		 */
		testTranslationV1_2Default("a ≥ b", "(>= a b)");
	}

	/**
	 * Arithmetic expressions binary operations: cf. "a-expr-bin"
	 */
	@Test
	public void testArithExprBinop() {
		/**
		 * minus
		 */
		testTranslationV1_2Default("a − b = c", "(= (- a b) c)");
		/**
		 * equal (a-expr-bin)
		 */
		testTranslationV1_2Default("a − b = a − c", "(= (- a b) (- a c))");
	}

	@Test
	// @Ignore("Not yet implemented")
	public void testArithExprBinopExponentialUnsupported() {
		/**
		 * expn
		 */
		testTranslationV1_2Default("a ^ b = c", "(= (expn a b) c)");
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (divi a b) c)");

		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (mod a b) c)");
	}

	@Test
	public void testArithExprBinopUnsupported() {
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (divi a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (mod a b) c)");
	}

	/**
	 * Arithmetic expressions associative operations: cf. "a-expr-ass"
	 */
	@Test
	public void testArithExprAssnop() {
		/**
		 * plus
		 */
		testTranslationV1_2Default("a + c + b = a + b + c",
				"(= (+ a c b) (+ a b c))");
		/**
		 * mul
		 */
		testTranslationV1_2Default("a ∗ b ∗ c = a ∗ c ∗ b",
				"(= (* a b c) (* a c b))");
	}

	@Test
	public void testAssociativeExpression() {
		testTranslationV1_2Default("s = {}", "(= s emptyset)");
	}

	/**
	 * Arithmetic expressions unary operations: cf. "a-expr-una"
	 */
	@Test
	public void testArithExprUnop() {
		/**
		 * uminus (right child)
		 */
		testTranslationV1_2Default("a = −b", "(= a (~ b))");
		/**
		 * uminus (left child)
		 */
		testTranslationV1_2Default("−a = b", "(= (~ a) b)");
	}

	/**
	 * "pred-in" This test should not happen with ppTrans; The
	 */

	@Test
	public void testPredIn() {
		testTranslationV1_2Default("a ∈ A", "(in a A)");
		testTranslationV1_2Default("a↦b ∈ AB", "(in (pair a b) AB)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV1_2Default("r = s", "(= r s)");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		testTranslationV1_2Default("p = q", "(= p q)");
	}

	@Test
	public void testDynamicStableLSR_081014_20_hyp1() {

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "P",
				"ℙ(S × S)", "Q", "ℙ(S × S)", "k", "S × S", "m", "S", "n", "S");

		testTranslationV1_2VerDefaultSolver(te, "¬ k = m ↦ n",
				"(not (= k (pair m n)))");
	}

	@Test
	public void testRule14() {

		/**
		 * inverse(SMT)/converse(EventB)
		 */
		testTranslationV1_2Default("AB = (AB)∼", "(= AB (inv AB))");
		/**
		 * not
		 */
		testTranslationV1_2Default("\u00ac(p = q)", "(not (= p q))");

		/**
		 * uminus
		 */
		testTranslationV1_2Default("a = (−b)", "(= a (~ b))");

		/**
		 * id
		 */
		testTranslationV1_2Default("AB = id", "(= AB id)");

		/**
		 * dom
		 */
		testTranslationV1_2Default("a ∈ dom(AB)", "(in a (dom AB))");

		/**
		 * ran
		 */
		testTranslationV1_2Default("b ∈ ran(AB)", "(in b (ran AB))");

	}

	@Test
	public void testExistsRule17() {
		/**
		 * exists
		 */
		testTranslationV1_2Default("∃x·x∈s", "(exists (?x R) (in ?x s))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV1_2Default("∃x,y·x∈s∧y∈s",
				"(exists (?x R) (?y R) (and (in ?x s) (in ?y s)))");
	}

	@Test
	public void testForallRule17() {
		/**
		 * forall
		 */
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R) (in ?x s))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2Default("∀x,y·x∈s∧y∈s",
				"(forall (?x R) (?y R) (and (in ?x s) (in ?y s)))");

	}

	@Test
	public void testForallRule17Part2() {
		/**
		 * forall (multiple identifiers)
		 */
		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", defaultTe);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(FORALL, bids,
				base.getPredicate(), null);
		// System.out.println("Predicate " + p);
		testTranslationV1_2Verit(p,
				"(forall (?x R) (?x_0 R) (and (in ?x s) (in ?x_0 s)))",
				"twice same decl", VERIT.toString());
	}

	@Test
	public void testRule16() {
		testTranslationV1_2Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∧ (a + b + c = b) ∧  (a ∗ b ∗ c = 0)",
				"(and (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");

		testTranslationV1_2Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∨ (a + b + c = b) ∨  (a ∗ b ∗ c = 0)",
				"(or (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testAssociativeExpressionsUnionAndInter() {
		final ITypeEnvironment tpe = mTypeEnvironment("A", "ℙ(ℤ)", "B", "ℙ(ℤ)",
				"C", "ℙ(ℤ)", "D", "ℙ(ℤ)", "E", "ℙ(ℤ)");
		testTranslationV1_2VerDefaultSolver(tpe, "A ∪ B ∪ C ∪ D = E",
				"(= (union (union (union A B) C) D) E)");

		testTranslationV1_2VerDefaultSolver(tpe, "A ∩ B ∩ C ∩ D = E",
				"(= (inter (inter (inter A B) C) D) E)");
	}

	@Test
	public void testAssociativeFcompAndOvr() {
		final ITypeEnvironment tpe = mTypeEnvironment("A", "ℤ ↔ ℤ", "B",
				"ℤ ↔ ℤ", "C", "ℤ ↔ ℤ", "D", "ℤ ↔ ℤ", "E", "ℤ ↔ ℤ");

		testTranslationV1_2VerDefaultSolver(tpe,
				"A \u003b B \u003b C \u003b D = E",
				"(= (fcomp (fcomp (fcomp A B) C) D) E)");

		testTranslationV1_2VerDefaultSolver(tpe,
				"A \ue103 B \ue103 C \ue103 D = E",
				"(= (ovr (ovr (ovr A B) C) D) E)");
	}

	@Test
	public void testRule15SetMinusUnionInter() {

		testTranslationV1_2Default("(A ∖ A) ⊂ (A ∪ A)",
				"(subset (setminus A A) (union A A))");

		testTranslationV1_2Default("(A ∩ A) ⊂ (A ∪ A)",
				"(subset (inter A A) (union A A))");

	}

	@Test
	public void testRule15() {

		/**
		 * ∈ , ⊆ , ⊂
		 */
		testTranslationV1_2Default("(a ∈ A) ∧ (A ⊆ A)",
				"(and (in a A) (subseteq A A))");

		/**
		 * < , > , ⇒ , =
		 */
		testTranslationV1_2Default("(a < b ∧ b > c) ⇒ a = c",
				"(implies (and (< a b) (> b c)) (= a c))");

		/**
		 * ≤, ∧ , ≥ , ⇔
		 */
		testTranslationV1_2Default("(a ≤ b ∧ b ≥ c) ⇔ (a ÷ b) < (c mod b)",
				"(iff (and (<= a b) (>= b c)) (< (divi a b) (mod c b)))");

	}

	@Test
	public void testRule15Functions() {

		testTranslationV1_2Default("AB ∈ (A↔A)", "(in AB (rel A A))");

		testTranslationV1_2Default("AB ∈ (A→A)", "(in AB (tfun A A))");

		testTranslationV1_2Default("AB ∈ (A⇸A)", "(in AB (pfun A A))");

		testTranslationV1_2Default("AB ∈ (A↣A)", "(in AB (tinj A A))");

		testTranslationV1_2Default("AB ∈ (A⤔A)", "(in AB (pinj A A))");

		testTranslationV1_2Default("AB ∈ (A↠A)", "(in AB (tsur A A))");

		testTranslationV1_2Default("AB ∈ (A⤀A)", "(in AB (psur A A))");

		testTranslationV1_2Default("AB ∈ (A⤖A)", "(in AB (bij A A))");
	}

	@Test
	public void testRule15RelationOverridingCompANdCP() {
		/**
		 * relation overriding
		 */
		testTranslationV1_2Default("(AB \ue103 AB) = (AB \ue103 AB)",
				"(= (ovr AB AB) (ovr AB AB))");

		testTranslationV1_2Default("(AB \u003b AB) = (AB \u003b AB)",
				"(= (fcomp AB AB) (fcomp AB AB))");

		testTranslationV1_2Default("(AB \u2218 AB) = (AB \u2218 AB)",
				"(= (bcomp AB AB) (bcomp AB AB))");
	}

	@Test
	public void testRule15CartesianProductAndIntegerRange() {

		testTranslationV1_2Default("(A × A) = (A × A)",
				"(= (prod A A) (prod A A))");

		testTranslationV1_2Default("(a ‥ a) = (a ‥ a)",
				"(= (range a a) (range a a))");
	}

	@Test
	public void testRule15RestrictionsAndSubstractions() {

		testTranslationV1_2Default("(A ◁ AB) = (A ◁ AB)",
				"(= (domr A AB) (domr A AB))");

		testTranslationV1_2Default("(A ⩤ AB) = (A ⩤ AB)",
				"(= (doms A AB) (doms A AB))");

		testTranslationV1_2Default("(AB ▷ A) = (AB ▷ A)",
				"(= (ranr AB A) (ranr AB A))");

		testTranslationV1_2Default("(AB ⩥ A) = (AB ⩥ A)",
				"(= (rans AB A) (rans AB A))");
	}

	@Test
	public void testRule18() {

		testTranslationV1_2Default("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}",
				"(= cset cset_0)");

		testTranslationV1_2Default("{a∣a ≥ 0} = A", "(= cset A)");
	}

	@Test
	public void testRule19() {
		testTranslationV1_2Default("{0 ↦ 1,1 ↦ 2} = {0 ↦ 1,2 ↦ 3}",
				"(= enum_0 enum_1)");

		testTranslationV1_2Default("{0,1,2,3,4} = A", "(= enum_0 A)");
	}

	@Test
	public void testRule20() {

		testTranslationV1_2Default("(λx·x>0 ∣ x+x) = ∅", "(= cset emptyset)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRule21() {
		// FIXME The translation of bool(⊤) is not defined yet
		testTranslationV1_2Default("bool(⊤) ∈ BOOL",
				"translation not defined yet");
	}

	@Test
	public void testRule22and23() {

		testTranslationV1_2Default("min({2,3}) = min({2,3})",
				"(= ismin_var ismin_var_0)");

		testTranslationV1_2Default("max({2,3}) = max({2,3})",
				"(= ismax_var ismax_var_0)");
	}

	@Test
	public void testRule24() {
		testTranslationV1_2Default("finite({1,2,3})", "finite_p");
	}

	@Test
	public void testRule25() {
		testTranslationV1_2Default("card({1,2,3}) = card({1,2,3})",
				"(= card_k card_k_0)");
	}

	@Test
	public void testKSuccAndKPred() {
		testTranslationV1_2Default("x = pred", "(= x pred)");

		testTranslationV1_2Default("x = succ", "(= x succ)");
	}

	@Test
	public void testDistinct() {
		testTranslationV1_2Default("partition(A,{1},{2},{3})",
				"(= A (union (union set set_0) set_1))");
	}

	@Test
	public void testIntSet() {
		testTranslationV1_2Default("A = ℤ", "(= A Int)");
	}

	@Test
	public void testNatSet() {
		testTranslationV1_2Default("ℕ ⊂ ℤ", "(subset Nat Int)");
	}

	@Test
	public void testNat1Set() {
		testTranslationV1_2Default("ℕ1 ⊂ ℤ", "(subset Nat1 Int)");
	}

	@Test
	public void testTREL() {
		// Total Relation
		testTranslationV1_2Default("r  s = r  s", "(= (totp r s) (totp r s))");
	}

	@Test
	public void testSREL() {
		// Surjective Relation
		testTranslationV1_2Default("r  s = r  s", "(= (surp r s) (surp r s))");
	}

	@Test
	public void testTSREL() {
		// Total Surjective Relation
		testTranslationV1_2Default("r  s = r  s",
				"(= (totsurp r s) (totsurp r s))");
	}

	@Test
	public void testRelimage() {
		// Relimage
		testTranslationV1_2Default("AB[A] = AB[A]", "(= (img AB A) (img AB A))");

		testTranslationV1_2Default("AB[{1}] =  AB[{1}]",
				"(= (img AB enum_0) (img AB enum_1))");
	}

	@Test
	public void testNotSubset() {
		testTranslationV1_2Default("ℕ ⊄ ℤ", "(not (subset Nat Int))");
	}

	@Test
	public void testNotSubseteq() {
		testTranslationV1_2Default("ℕ ⊈ ℤ", "(not (subseteq Nat Int))");
	}

	@Test
	public void testPartition() {
		testTranslationV1_2Default("partition(A,{1,2},{3,4})",
				"(and (= A enum_0) (= (inter enum_1 enum_2) emptyset))");
	}

	@Test
	public void testPartition1Set() {
		testTranslationV1_2Default("partition(A,{1})", "(= A set)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionSetOfSet() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ", "ℤ ↔ ℙ(ℤ)");

		testTranslationV1_2VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionParsePairTypes() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ",
				"ℙ((ℤ ↔ ℤ) ↔ ℤ)");

		testTranslationV1_2VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionParsePairTypes2() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ", "ℤ ↔ (ℤ ↔ ℤ)");

		testTranslationV1_2VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionBoolSort() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(errorTe, "BOOL = BOOL",
				"(= BOOL BOOL)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionPRJ1() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(errorTe, "1 ↦ 2 ↦ 1 ∈ prj1",
				"(= prj1 prj1)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionPRJ2() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(errorTe, "1 ↦ 2 ↦ 1 ∈ prj2",
				"(= prj2s prj2)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionTRUE() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(errorTe, "TRUE ∈ BOOL",
				"(in TRUE BOOL)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionFALSE() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(errorTe, "FALSE ∈ BOOL",
				"(in FALSe BOOL)");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionDPROD() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "A ↔ A");

		testTranslationV1_2VerDefaultSolver(te, "B ⊗ B = B ⊗ B",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionPPROD() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "A ↔ A");

		testTranslationV1_2VerDefaultSolver(te, "B ∥ B = B ∥ B",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionFUNIMAGE() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "ℤ ↔ ℤ",
				"f", "ℤ");

		testTranslationV1_2VerDefaultSolver(te, "B(f) = B(f)",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionKUNION() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te, "union({{1}}) = union({{1}})",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionPOW() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te, "ℙ(ℤ) = ℙ(ℤ)",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionPOW1() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te, "ℙ1(ℤ) = ℙ1(ℤ)",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionQUNION() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te,
				"( ⋃ x ⦂ ℤ · x ∈ {0,1,2} ∧ x > 0 ∣ {x} ) = {1, 2}",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionQINTER() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te,
				"( ⋂ x ⦂ ℤ · x ∈ {0,1,2} ∧ x > 0 ∣ {x} ) = {}",
				"(= (dprod A A) (dprod A A))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionExtendedPredicate() {

		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te, "bar(5)", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionExtendedExpression() {

		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();

		testTranslationV1_2VerDefaultSolver(te, "(foo) = (foo)", "");
	}
}
