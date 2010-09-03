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

	private static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, defaultTe);
	}

	private static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {	
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		
		if(transformExpected) {
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);
		
		// reduce predicate
		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);
		
		// Create a Type environment and a fids list to use Visitor1_2
		TypeEnvironment type = new TypeEnvironment(null, null);
		ArrayList<String> fids = new ArrayList<String>();
		
		VisitorV1_2 visHyp = new VisitorV1_2(type, fids);
		actual.accept(visHyp);
		String translatedHyp = visHyp.getSMTNode();

		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	

	static ITypeEnvironment br_te = mTypeEnvironment(
			mList("s", "t", "v", "w", "p", "q", "e1", "e2", "e3"), 
			mList(POW(S), POW(S), POW(S), POW(S), REL(S, T), REL(S, T), S, S, S));
	
	public void testBR1_recursion() {

		doTest( "s∪v ⊆ t∪w",
				"s∪v ∈ ℙ(t∪w)", true, br_te);
	}

}
