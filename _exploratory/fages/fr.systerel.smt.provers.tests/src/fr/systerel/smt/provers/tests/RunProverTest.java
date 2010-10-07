package fr.systerel.smt.provers.tests;

import static br.ufrn.smt.solver.preferences.SMTPreferencesStore.CreatePreferences;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofMonitor;
import org.junit.Test;

import br.ufrn.smt.solver.preferences.SolverDetail;
import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.core.SmtProversCore;
import fr.systerel.smt.provers.internal.core.SmtProversCall;


public class RunProverTest extends AbstractTranslationTests{
	
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
	
	static ITypeEnvironment arith_te = mTypeEnvironment(
			mList("x", "y", "z", "n" ), 
			mList(INT, INT, INT, INT));
	
	/*private static void doTest(String input) {
		doTest(input, defaultTe);
	}*/

	private static void doTest(List<String> inputHyps, String inputGoal,  ITypeEnvironment te) {	
		List<Predicate> hypotheses = new ArrayList<Predicate>();
		
		for (String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}
		
		Predicate goal = parse(inputGoal, te);
		
		doTest(hypotheses,goal);
	}
	
	private static void doTest(List<Predicate> hyp, Predicate goal) {
		// Type check goal and hypotheses
		assertTypeChecked(goal);
		for (Predicate predicate : hyp) {
			assertTypeChecked(predicate);
		}
		
		// Create an instance of SmtProversCall
		SmtProversCall smtProversCall = new SmtProversCall(hyp, goal,
				new NullProofMonitor(), "SMT") {

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


	public static final String VERIT_PATH = "C:\\Utilisateurs\\fages\\Projets\\C444_Decert\\solver\\exe\\veriT_200907.exe";

	public static final String CVC3_PATH = "C:\\Utilisateurs\\fages\\Projets\\C444_Decert\\solver\\exe\\cvc3-2.2-win32-opt.exe";

	public static final String Z3_PATH = "C:\\Program Files\\Microsoft Research\\Z3-2.10\\bin\\z3.exe";

	
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
		
		List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");
		
		// perform test
		doTest(hyps, "x < z",arith_te);
		
	}
	
	@Test
	public void testSolverCallWithCvc3() {
		
		// Set preferences to test with VeriT
		setPreferencesForCvc3Test();
		
		List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");
		
		// perform test
		doTest(hyps, "x < z",arith_te);
		
	}
	
	@Test
	public void testSolverCallWithZ3() {
		
		// Set preferences to test with VeriT
		setPreferencesForZ3Test();
		
		List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");
		
		// perform test
		doTest(hyps, "x < z",arith_te);
		
	}
	
}
