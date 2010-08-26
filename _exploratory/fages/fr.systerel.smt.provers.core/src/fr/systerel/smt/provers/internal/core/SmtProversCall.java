/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *     Systerel (YFT) - Code refactoring
 *     Systerel (YFT) - Separate UI and Core as much as possible
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.TranslationException;

import fr.systerel.smt.provers.core.SmtProversCore;

public abstract class SmtProversCall extends XProverCall {

	protected final String proverName;

	private volatile boolean valid;

	// Access to these files must be synchronized
	protected File iFile;
	protected File oFile;

	private String resultOfSolver;
	private File smtFile;
	
	// SMT UI preferences
	private UIPreferences smtUiPreferences;

	public SmtProversCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm, String proverName) {
		super(hypotheses, goal, pm);
		this.proverName = proverName;

		// Get back preferences from UI
		smtUiPreferences = new UIPreferences(SmtProversCore.getDefault()
				.getPreferenceStore().getString("solverpreferences"),//$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getInt("solverindex"), //$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getBoolean("usingprepro"), //$NON-NLS-1$
				SmtProversCore.getDefault().getPreferenceStore()
						.getString("prepropath"));//$NON-NLS-1$
	}

	/**
	 * Method to call a SMT solver.
	 * 
	 * @param args
	 *            Arguments to pass for the call
	 * @throws IOException
	 */
	protected void callProver(ArrayList<String> args) throws IOException {

		for (int i = 0; i < args.size(); i++) {
			System.out.println(args.get(i));
		}

		// Set up arguments for solver call
		String[] terms = new String[args.size()];
		for (int i = 0; i < terms.length; i++) {
			terms[i] = args.get(i);
		}

		// Launch solver and get back solver result
		resultOfSolver = Exec.execProgram(terms);

		// Check Solver Result
		checkResult(resultOfSolver);

		File resultFile = new File(iFile.getParent() + "/smTSolverString"); //$NON-NLS-1$
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(resultFile);
		fileWriter.write(resultOfSolver);
		fileWriter.close();
		oFile = resultFile;
	}

	/**
	 * Check the result provided by the solver (unsat is checked)
	 * 
	 * @param expected
	 *            The string result from the SMT solvers.
	 * @throws IOException
	 */
	private boolean checkResult(String expected) throws IOException {
		if (expected.trim().contains("unsat")) { //$NON-NLS-1$
			valid = true;
		} else {
			valid = false;
		}

		return valid;
	}

	protected boolean callPK(String[] cmdArray) throws IOException {
		return valid;
	}

	@Override
	public synchronized void cleanup() {
		if (iFile != null) {
			iFile.delete();
			iFile = null;
		}
		if (oFile != null) {
			oFile.delete();
			oFile = null;
		}
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	protected synchronized void makeTempFileNames() throws IOException {

	}

	protected abstract void printInputFile() throws IOException;

	protected abstract String[] proverCommand();

	protected abstract String[] parserCommand();

	protected abstract String successString();

	protected static String preprocessSMTinVeriT(String smtFilePath,
			String pathOfSolver) throws PreProcessingException, IOException {

		if (pathOfSolver.isEmpty()) {
			throw new PreProcessingException(
					Messages.SmtProversCall_preprocessor_path_not_defined);
		}
		String[] args = new String[3];
		args[0] = pathOfSolver;
		args[1] = "--print-simp-and-exit"; //$NON-NLS-1$
		args[2] = smtFilePath;
		String resultOfPreProcessing = Exec.execProgram(args);
		int benchmarkIndex = resultOfPreProcessing.indexOf("(benchmark") + 10; //$NON-NLS-1$
		int i = 1;
		StringBuffer sb = new StringBuffer();
		sb.append("(benchmark"); //$NON-NLS-1$
		
		if (benchmarkIndex != -1){
			while (i > 0 || benchmarkIndex >= resultOfPreProcessing.length()) {
				char c = resultOfPreProcessing.charAt(benchmarkIndex);
				if (c == '(') {
					++i;
				} else if (c == ')') {
					--i;
				}
				sb.append(c);
				++benchmarkIndex;
			}
			if (benchmarkIndex >= resultOfPreProcessing.length() && i != 0) {
				throw new PreProcessingException();
			}
		}
		return sb.toString();
	}

	@Override
	public void run() {
		// test the SMT solver path
		if (smtUiPreferences.getSolver() == null) {
			// Message popup displayed when there is no defined solver path
			UIUtils.showError(Messages.SmtProversCall_Check_Smt_Preferences);
			return;
		}

		try {			
			// Translate and apply smt solver
			if (smtUiPreferences.getSolver().getsmtV1_2()){
				// SMT lib v1.2
				smtTranslationSolverCall();
			}
			else if(smtUiPreferences.getSolver().getsmtV2_0()) {
				// SMT lib v2.0
				smtV2Call();
			}				
		} catch (TranslationException t) {
			UIUtils.showError(t.getMessage());
			return;
		} catch (IOException e) {
			UIUtils.showError(e.getMessage());
			return;
		}
	}

	public boolean runWithPk() {
		return valid;
	}

	/**
	 * Performs Rodin PO to SMT translation
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 * @throws TranslationException
	 */
	public void smtTranslationSolverCall() throws PreProcessingException,
			IOException, TranslationException {

		// Parse Rodin PO to create Smt file
		RodinToSMTPredicateParser rp = new RodinToSMTPredicateParser(
				hypotheses, goal);

		// Get back translated smt file
		smtFile = rp.getTranslatedFile();

		if (!smtFile.exists()) {
			System.out
					.println(Messages.SmtProversCall_translated_file_not_exists);
		}
		
		// Set up arguments
		ArrayList<String> args = setSolverArgs();

		if (smtUiPreferences.getUsingPrepro()) {
			// Launch preprocessing
			smtTranslationPreprocessing(args);
		}

		iFile = smtFile;

		// prover with arguments
		callProver(args);
	}
	
	private ArrayList<String> setSolverArgs(){
		ArrayList<String> args = new ArrayList<String>();
		args.add(smtUiPreferences.getSolver().getPath());
	
		args.add(smtFile.getPath());
	
		smtUiPreferences.getSolver().getArgs();
		
		if (!smtUiPreferences.getSolver().getArgs().isEmpty()) {
			// Split arguments and add them in the list
			String[] argumentsString = smtUiPreferences.getSolver().getArgs()
					.split(" "); //$NON-NLS-1$
			for (String argString : argumentsString) {
				args.add(argString);
			}
		}
		
		return args;
	}
	
	/**
	 * Performs SMT-lib 2.0 translation + SMT solver solver call.
	 * 
	 */
	private void smtV2Call(){
		// Parse Rodin PO to create SMT v2.0 file
		
		// Get back translated SMT file
		
		// Set up arguments
		ArrayList<String> args = setSolverArgs();
		
		// Call the prover
		
	}
	
	/**
	 * Performs Rodin PO to SMT translation preprocessing
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 */
	private void smtTranslationPreprocessing(ArrayList<String> args)
			throws PreProcessingException, IOException {
		String preprocessedSMT = preprocessSMTinVeriT(smtFile.getPath(),
				smtUiPreferences.getPreproPath());
		File preprocessedFile = new File(smtFile.getParent()
				+ "/tempPreProcessed.smt"); //$NON-NLS-1$

		if (!preprocessedFile.exists()) {
			preprocessedFile.createNewFile();
		}

		FileWriter fw = new FileWriter(preprocessedFile);
		fw.write(preprocessedSMT);
		fw.close();
		args.set(1, preprocessedFile.getPath());

		this.iFile = preprocessedFile;
		this.smtFile = preprocessedFile;
	}
}
