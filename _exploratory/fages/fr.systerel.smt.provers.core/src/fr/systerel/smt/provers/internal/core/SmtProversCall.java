package fr.systerel.smt.provers.internal.core;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
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

	// Variables created by Vitor
	String resultOfSolver;
	File smtFile;
	File firstTranslationFile;

	protected SmtProversCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm, String proverName) {
		super(hypotheses, goal, pm);
		this.proverName = proverName;
	}

	protected void callProver(ArrayList<String> args, String successMsg)
			throws IOException {
		String seeFileOrProofCommand = SmtProversCore.getDefault()
				.getPreferenceStore()
				.getString("executeTrans"); //$NON-NLS-1$
		if (seeFileOrProofCommand
				.equals("proofandshowfile") //$NON-NLS-1$
				|| seeFileOrProofCommand
						.equals("proofonly")) { //$NON-NLS-1$
			for (int i = 0; i < args.size(); i++) {
				System.out.println(args.get(i));
			}

			if (SmtProversCore.getDefault().getPreferenceStore()
					.getString("whichsolver") //$NON-NLS-1$
					.equals("cvc3")) { //$NON-NLS-1$
				args.add("-lang"); //$NON-NLS-1$
				args.add("smt"); //$NON-NLS-1$
			}

			String[] terms = new String[args.size()];
			for (int i = 0; i < terms.length; i++) {
				terms[i] = args.get(i);
			}

			resultOfSolver = Exec.execProgram(terms);
			System.out.println("\n********** Solver output:" + resultOfSolver //$NON-NLS-1$
					+ "\n********** End of Solver output\n"); //$NON-NLS-1$

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

		if (seeFileOrProofCommand
				.equals("proofandshowfile") //$NON-NLS-1$
				|| seeFileOrProofCommand
						.equals("showfileonly")) { //$NON-NLS-1$
			boolean preprocess = SmtProversCore.getDefault()
					.getPreferenceStore()
					.getBoolean("usingprepro"); //$NON-NLS-1$
			String solver = SmtProversCore.getDefault().getPreferenceStore()
					.getString("whichsolver"); //$NON-NLS-1$
			String preprocessorOptions = SmtProversCore.getDefault()
					.getPreferenceStore()
					.getString("preprocessingoptions"); //$NON-NLS-1$
			if ((preprocess || !solver.equals("veriT")) //$NON-NLS-1$
					&& (preprocessorOptions
							.equals("aftersmt") || preprocessorOptions //$NON-NLS-1$
							.equals("beforeandafter"))) { //$NON-NLS-1$
				showFileInEditor(this.firstTranslationFile.getPath());
			}

			if (preprocessorOptions.equals("presmt") //$NON-NLS-1$
					|| preprocessorOptions
							.equals("beforeandafter")) { //$NON-NLS-1$
				showFileInEditor(smtFile.getPath());
			}
		}
	}

	private void showFileInEditor(String filePath) {
		String editor = SmtProversCore.getDefault().getPreferenceStore()
				.getString("smteditor"); //$NON-NLS-1$
		String[] args = { editor, filePath };
		try {
			Exec.execProgram(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkResult(String expected) throws IOException {
		String typeOfSolver = SmtProversCore.getDefault().getPreferenceStore()
				.getString("whichsolver"); //$NON-NLS-1$
		if (expected.trim().endsWith("unsat")) { //$NON-NLS-1$
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

	protected static String preprocessSMTinVeriT(String smtFilePath)
			throws PreProcessingException, IOException {
		String pathOfSolver = SmtProversCore.getDefault().getPreferenceStore()
				.getString("prepropath"); //$NON-NLS-1$
		if (pathOfSolver.isEmpty()) {
			throw new PreProcessingException(
					Messages.SmtProversCall_preprocessor_path_not_defined);
		}
		String[] args = new String[3];
		args[0] = pathOfSolver;
		args[1] = "--print-simp-and-exit"; //$NON-NLS-1$
		args[2] = smtFilePath;
		String resultOfPreProcessing = Exec.execProgram(args);
		System.out.println("\n*************** Preprocessing in VeriT output:" //$NON-NLS-1$
				+ resultOfPreProcessing
				+ "\n*************** End of preprocessing in VeriT ouput\n"); //$NON-NLS-1$
		int benchmarkIndex = resultOfPreProcessing.indexOf("(benchmark") + 10; //$NON-NLS-1$
		int i = 1;
		StringBuffer sb = new StringBuffer();
		sb.append("(benchmark"); //$NON-NLS-1$
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

		return sb.toString();
	}

	@Override
	public void run() {
		// Code by Vitor Alcantara de Almeida

		String solverPath = SmtProversCore.getDefault().getPreferenceStore()
				.getString("solver_path"); //$NON-NLS-1$
		if (solverPath.isEmpty()) {
			// Message popup displayed when there is no defined solver path
			UIUtils.showError(Messages.SmtProversCall_no_defined_solver_path);
			return;
		}

		try {
			// Doing the translation:
			smtTranslation();
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
	 * Performs Rodin PO to Smt translation
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 * @throws TranslationException
	 */
	private void smtTranslation() throws PreProcessingException, IOException,
			TranslationException {
		
		// Parse Rodin PO to create Smt file
		RodinToSMTPredicateParser rp = new RodinToSMTPredicateParser(
				hypotheses, goal);

		// Get back Rodin Smt solvers settings
		String pathOfSolver = SmtProversCore.getDefault().getPreferenceStore()
				.getString("solver_path"); //$NON-NLS-1$
		String solverArgs = SmtProversCore.getDefault().getPreferenceStore()
				.getString("solverarguments"); //$NON-NLS-1$
		
		// Get back translated smt file
		smtFile = rp.getTranslatedFile();

		if (!smtFile.exists()) {
			System.out
					.println(Messages.SmtProversCall_translated_file_not_exists);
		}
		ArrayList<String> args = new ArrayList<String>();
		args.add(pathOfSolver);

		args.add(smtFile.getPath());
		if (!solverArgs.isEmpty()) {
			args.add(solverArgs);
		}

		boolean preprocess = SmtProversCore.getDefault().getPreferenceStore()
				.getBoolean("usingprepro"); //$NON-NLS-1$
		String solver = SmtProversCore.getDefault().getPreferenceStore()
				.getString("whichsolver"); //$NON-NLS-1$
		if (preprocess || !solver.equals("veriT")) { //$NON-NLS-1$
			smtTranslationPreprocessing(args);
		}

		iFile = smtFile;
		callProver(args, "Success"); //$NON-NLS-1$
	}

	/**
	 * Performs Rodin PO to Smt translation preprocessing
	 * 
	 * @throws PreProcessingException
	 * @throws IOException
	 */
	private void smtTranslationPreprocessing(ArrayList<String> args)
			throws PreProcessingException, IOException {
		String preprocessedSMT = preprocessSMTinVeriT(smtFile.getPath());// result.getThirdElement().getPath());
		File preprocessedFile = new File(smtFile.getParent()
				+ "/tempPreProcessed.smt"); //$NON-NLS-1$

		if (!preprocessedFile.exists()) {
			preprocessedFile.createNewFile();
		}

		FileWriter fw = new FileWriter(preprocessedFile);
		fw.write(preprocessedSMT);
		fw.close();
		args.set(1, preprocessedFile.getPath());

		this.firstTranslationFile = smtFile;
		this.iFile = preprocessedFile;
		this.smtFile = preprocessedFile;
	}
}
