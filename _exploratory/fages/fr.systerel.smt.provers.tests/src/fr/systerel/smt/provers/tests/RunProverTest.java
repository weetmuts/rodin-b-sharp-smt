package fr.systerel.smt.provers.tests;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.junit.Test;

import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.core.SmtProversCore;
import fr.systerel.smt.provers.internal.core.SmtProversCall;
import br.ufrn.smt.solver.preferences.SolverDetail;
import static br.ufrn.smt.solver.preferences.SMTPreferencesStore.*;

public class RunProverTest {

	private static class NullProofMonitor implements IProofMonitor {

		public boolean isCanceled() {
			return false;
		}

		public void setCanceled(boolean value) {
			// nothing to do
		}

		public void setTask(String name) {
			// nothing to do
		}

	}

	private class SequentTest {

		List<Predicate> hypotheses;

		Predicate goal;

		IProofMonitor pm;

		public SequentTest(List<Predicate> hypotheses, Predicate goal,
				IProofMonitor pm) {
			this.hypotheses = hypotheses;
			this.goal = goal;
			this.pm = pm;
		}
	}

	private static <T> T[] list(T... objs) {
		return objs;
	}

	public static final String VERIT_PATH = "C:\\Utilisateurs\\fages\\Projets\\C444_Decert\\solver\\exe\\veriT_200907.exe";

	public static final String CVC3_PATH = "C:\\Utilisateurs\\fages\\Projets\\C444_Decert\\solver\\exe\\cvc3-2.2-win32-opt.exe";

	public static final String Z3_PATH = "C:\\Program Files\\Microsoft Research\\Z3-2.10\\bin\\z3.exe";

	private SequentTest CreateSimpleSequent() {

		// Set up hypotheses and goal
		FormulaFactory ff = FormulaFactory.getDefault();
		IParseResult result = ff.parseType("Int", LanguageVersion.V2);
		final FreeIdentifier n = ff.makeFreeIdentifier("n", null,
				result.getParsedType());
		final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);

		List<Predicate> hypotheses = new ArrayList<Predicate>();
		AssociativeExpression exp = ff.makeAssociativeExpression(Formula.PLUS,
				list(n, one), null);

		// Predicate n in NATURAL1
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, n,
				ff.makeAtomicExpression(Formula.NATURAL1, null), null);
		// Predicate n+1 in NATURAL1
		Predicate goal = ff.makeRelationalPredicate(Predicate.IN, exp,
				ff.makeAtomicExpression(Formula.NATURAL1, null), null);

		hypotheses.add(pred1);

		IProofMonitor pm = new NullProofMonitor();

		SequentTest seq = new SequentTest(hypotheses, goal, pm);

		return seq;
	}

	private void setPreferencesForVeriTTest() {
		SmtProversCore core = SmtProversCore.getDefault();
		IPreferenceStore store = core.getPreferenceStore();
		List<SolverDetail> solvers = new ArrayList<SolverDetail>();
		solvers.add(new SolverDetail("veriT", VERIT_PATH, "", true, false));
		String preferences = CreatePreferences(solvers);
		store.setValue("solverpreferences",preferences);
		store.setValue("solverindex", 0);
		store.setValue("usingprepro", true);
		store.setValue("prepropath", VERIT_PATH);
	}

	private void setPreferencesForCvc3Test() {
		SmtProversCore core = SmtProversCore.getDefault();
		IPreferenceStore store = core.getPreferenceStore();
		List<SolverDetail> solvers = new ArrayList<SolverDetail>();
		solvers.add(new SolverDetail("cvc3", CVC3_PATH, "-lang smt", true, false));
		String preferences = CreatePreferences(solvers);
		store.setValue("solverpreferences",preferences);
		store.setValue("solverindex", 0);
		store.setValue("usingprepro", true);
		store.setValue("prepropath", VERIT_PATH);
	}
	
	private void setPreferencesForZ3Test() {
		SmtProversCore core = SmtProversCore.getDefault();
		IPreferenceStore store = core.getPreferenceStore();
		List<SolverDetail> solvers = new ArrayList<SolverDetail>();
		solvers.add(new SolverDetail("z3", Z3_PATH, "", true, false));
		String preferences = CreatePreferences(solvers);
		store.setValue("solverpreferences",preferences);
		store.setValue("solverindex", 0);
		store.setValue("usingprepro", true);
		store.setValue("prepropath", VERIT_PATH);
		
	}

	@Test
	public void testSolverCallWithVeriT() {
		
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();
		
		// Create a Simple sequent
		SequentTest seq = CreateSimpleSequent();
		
		// Create an instance of SmtProversCall
		SmtProversCall smtProversCall = new SmtProversCall(seq.hypotheses, seq.goal,
				seq.pm, "SMT") {

			@Override
			public String displayMessage() {
				return "SMT";
			}

			@Override
			protected void printInputFile() throws IOException {
				// TODO Auto-generated method stub
			}

			@Override
			protected String[] proverCommand() {
				return null;
			}

			@Override
			protected String[] parserCommand() {
				return null;
			}

			@Override
			protected String successString() {
				return "is valid";
			}
		};

		try {
			smtProversCall.smtTranslationSolverCall();
		} catch (TranslationException t) {
			System.out.println(t.getMessage());
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	@Test
	public void testSolverCallWithCvc3() {
		
		// Set preferences to test with CVC3
		setPreferencesForCvc3Test();
		
		// Create a Simple sequent
		SequentTest seq = CreateSimpleSequent();
		
		// Create an instance of SmtProversCall
		SmtProversCall smtProversCall = new SmtProversCall(seq.hypotheses, seq.goal,
				seq.pm, "SMT") {

			@Override
			public String displayMessage() {
				return "SMT";
			}

			@Override
			protected void printInputFile() throws IOException {
				// TODO Auto-generated method stub
			}

			@Override
			protected String[] proverCommand() {
				return null;
			}

			@Override
			protected String[] parserCommand() {
				return null;
			}

			@Override
			protected String successString() {
				return "is valid";
			}
		};

		try {
			smtProversCall.smtTranslationSolverCall();
		} catch (TranslationException t) {
			System.out.println(t.getMessage());
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	@Test
	public void testSolverCallWithZ3() {
		
		// Set preferences to test with Z3
		setPreferencesForZ3Test();
		
		// Create a Simple sequent
		SequentTest seq = CreateSimpleSequent();
		
		// Create an instance of SmtProversCall
		SmtProversCall smtProversCall = new SmtProversCall(seq.hypotheses, seq.goal,
				seq.pm, "SMT") {

			@Override
			public String displayMessage() {
				return "SMT";
			}

			@Override
			protected void printInputFile() throws IOException {
				// TODO Auto-generated method stub
			}

			@Override
			protected String[] proverCommand() {
				return null;
			}

			@Override
			protected String[] parserCommand() {
				return null;
			}

			@Override
			protected String successString() {
				return "is valid";
			}
		};

		try {
			smtProversCall.smtTranslationSolverCall();
		} catch (TranslationException t) {
			System.out.println(t.getMessage());
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
}
