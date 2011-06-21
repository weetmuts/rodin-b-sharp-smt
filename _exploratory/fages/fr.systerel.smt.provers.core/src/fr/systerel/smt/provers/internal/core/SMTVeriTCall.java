/**
 * 
 */
package fr.systerel.smt.provers.internal.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.SMTThroughVeriT;
import fr.systerel.smt.provers.ast.SMTBenchmark;

/**
 * @author guyot
 * 
 */
public class SMTVeriTCall extends SMTProverCall {
	private static final String VERIT_TEMP_FILE = "_prep";
	private static final String VERIT_SIMPLIFY_ARGUMENT_STRING = "--print-simp-and-exit";
	private static final String VERIT_DISABLE_BANNER = "--disable-banner";
	private static final String POST_PROCESSED_FILE_POSTFIX = "_pop.";

	protected SMTVeriTCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm, preferences, lemmaName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Execute translation of Event-B predicates using the VeriT pre-processing
	 * approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public List<String> smtTranslation() throws IOException {
		final SMTBenchmark benchmark = SMTThroughVeriT
				.translateToSmtLibBenchmark(lemmaName, hypotheses, goal,
						smtPreferences.getSolver().getId());
		/**
		 * The name of the SMT file with macros.
		 */
		if (translationFolder == null) {
			translationFolder = mkTranslationDir(!CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF);
		}
		final String veriTPreProcessingFileName = smtVeriTPreProcessFilePath(benchmark
				.getName());

		/**
		 * First, write the SMT file with macros
		 */
		final File preprocessedFile = writeVeritPreprocessedSMTFile(benchmark,
				veriTPreProcessingFileName);

		if (!preprocessedFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Then, call veriT, which produces a version of the SMT file without
		 * macros
		 */
		callVeriT(preprocessedFile);

		final List<String> args = setSolverArgs(iFile.getPath());

		return args;
	}

	/**
	 * This method should: call the veriT, produce a simplified version of the
	 * SMT file without macros, and verify if there is any input error
	 * 
	 * @param preprocessedFile
	 * @throws IOException
	 */
	private void callVeriT(final File preprocessedFile) throws IOException {
		final List<String> args = new ArrayList<String>();

		if (smtPreferences.getPreproPath().isEmpty()
				|| smtPreferences.getPreproPath() == null) {
			throw new IllegalArgumentException(
					Messages.SmtProversCall_preprocessor_path_not_defined);
		}

		args.add(smtPreferences.getPreproPath());
		args.add(VERIT_SIMPLIFY_ARGUMENT_STRING);
		args.add(VERIT_DISABLE_BANNER);
		args.add(preprocessedFile.getPath());

		resultOfSolver = execProcess(args);

		/**
		 * Set up temporary result file
		 */
		checkPreProcessingResult(preprocessedFile.getParent());
	}

	private void createPostProcessedFile(final String parentFolder,
			final String extension) throws IOException {
		iFile = new File(parentFolder + File.separatorChar + lemmaName
				+ POST_PROCESSED_FILE_POSTFIX + extension);
		if (!iFile.exists()) {
			iFile.createNewFile();
		}
		final FileWriter fileWriter = new FileWriter(iFile);
		fileWriter.write(resultOfSolver);
		fileWriter.close();
	}

	private void checkPreProcessingResult(final String parentFolder)
			throws IOException {
		if (resultOfSolver.contains("(benchmark")) {
			resultOfSolver = resultOfSolver.substring(resultOfSolver
					.indexOf("(benchmark"));
			createPostProcessedFile(parentFolder, "smt");
			return;
		} else {
			createPostProcessedFile(parentFolder, RES);
			if (resultOfSolver.contains("syntax error")
					|| resultOfSolver.contains("parse error")
					|| resultOfSolver.contains("Lexical_error")) {
				throw new IllegalArgumentException(proverName
						+ " could not pre-process " + lemmaName
						+ ".smt with VeriT. See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + " for more details.");
			} else {
				throw new IllegalArgumentException("Unexpected response of "
						+ proverName + ". See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + RES
						+ " for more details.");
			}
		}
	}

	private String smtVeriTPreProcessFilePath(final String fileName) {
		return translationFolder + File.separatorChar + fileName
				+ VERIT_TEMP_FILE + SMT_LIB_FILE_EXTENSION;
	}

	private File writeVeritPreprocessedSMTFile(final SMTBenchmark benchmark,
			final String veriTPreProcessingFileName) {
		final File preProcessedSMTFile = new File(veriTPreProcessingFileName);
		try {
			preProcessedSMTFile.createNewFile();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		}
		final PrintWriter smtFileWriter = openSMTFileWriter(preProcessedSMTFile);
		benchmark.print(smtFileWriter);
		smtFileWriter.close();
		return preProcessedSMTFile;

	}
}
