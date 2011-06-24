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
	private static final String VERIT_TRANSLATION_PATH = TRANSLATION_PATH
			+ File.separatorChar + "verit";
	private static final String VERIT_TEMP_FILE = "_prep";
	private static final String VERIT_SIMPLIFY_ARGUMENT_STRING = "--print-simp-and-exit";
	private static final String VERIT_PRINT_FLAT = "--print-flat";

	private static final String VERIT_DISABLE_BANNER = "--disable-banner";
	private static final String POST_PROCESSED_FILE_POSTFIX = "_pop.";

	protected SMTVeriTCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm, preferences, lemmaName);
	}

	/**
	 * Execute translation of Event-B predicates using the VeriT pre-processing
	 * approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public void makeSMTBenchmarkFileV1_2() throws IOException {
		proofMonitor.setTask("Translating Event-B proof obligation");
		final SMTBenchmark benchmark = SMTThroughVeriT
				.translateToSmtLibBenchmark(lemmaName, hypotheses, goal,
						smtPreferences.getSolver().getId());
		lemmaName = benchmark.getName();
		final String benchmarkTargetedPath = VERIT_TRANSLATION_PATH
				+ File.separatorChar + lemmaName;

		/**
		 * The name of the SMT file with macros.
		 */
		if (translationFolder == null) {
			translationFolder = mkTranslationFolder(benchmarkTargetedPath,
					!CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF);
		}

		/**
		 * First, write the SMT file with macros
		 */
		final File preProcessedSMTFile = new File(smtVeriTPreProcessFilePath());
		preProcessedSMTFile.createNewFile();
		final PrintWriter smtFileWriter = openSMTFileWriter(preProcessedSMTFile);
		benchmark.print(smtFileWriter);
		smtFileWriter.close();
		if (!preProcessedSMTFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}

		/**
		 * Then, call veriT, which produces a version of the SMT file without
		 * macros
		 */
		callVeriT(preProcessedSMTFile);
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

		if (smtPreferences.getVeriTPath().isEmpty()
				|| smtPreferences.getVeriTPath() == null) {
			throw new IllegalArgumentException(
					Messages.SmtProversCall_veriT_path_not_defined);
		}

		args.add(smtPreferences.getVeriTPath());
		args.add(VERIT_SIMPLIFY_ARGUMENT_STRING);
		args.add(VERIT_PRINT_FLAT);
		args.add(VERIT_DISABLE_BANNER);
		args.add(preprocessedFile.getPath());

		solverResult = execProcess(args);

		/**
		 * Set up temporary result file
		 */
		checkPreProcessingResult(preprocessedFile.getParent());
	}

	private String smtVeriTPreProcessFilePath() {
		return translationFolder + File.separatorChar + lemmaName
				+ VERIT_TEMP_FILE + SMT_LIB_FILE_EXTENSION;
	}

	private void createPostProcessedFile(final String parentFolder,
			final String extension) throws IOException {
		smtBenchmarkFile = new File(parentFolder + File.separatorChar
				+ lemmaName + POST_PROCESSED_FILE_POSTFIX + extension);
		if (!smtBenchmarkFile.exists()) {
			smtBenchmarkFile.createNewFile();
		}
		final FileWriter fileWriter = new FileWriter(smtBenchmarkFile);
		fileWriter.write(solverResult);
		fileWriter.close();
	}

	private void checkPreProcessingResult(final String parentFolder)
			throws IOException {
		if (solverResult.contains("(benchmark")) {
			solverResult = solverResult.substring(solverResult
					.indexOf("(benchmark"));
			createPostProcessedFile(parentFolder, "smt");
			return;
		} else {
			createPostProcessedFile(parentFolder, RES);
			if (solverResult.contains("syntax error")
					|| solverResult.contains("parse error")
					|| solverResult.contains("Lexical_error")) {
				throw new IllegalArgumentException(solverName
						+ " could not pre-process " + lemmaName
						+ ".smt with VeriT. See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + " for more details.");
			} else {
				throw new IllegalArgumentException("Unexpected response of "
						+ solverName + ". See " + lemmaName
						+ POST_PROCESSED_FILE_POSTFIX + RES
						+ " for more details.");
			}
		}
	}
}
