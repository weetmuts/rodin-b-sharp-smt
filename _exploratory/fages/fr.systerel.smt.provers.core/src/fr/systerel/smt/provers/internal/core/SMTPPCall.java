/**
 * 
 */
package fr.systerel.smt.provers.internal.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.SMTThroughPP;
import fr.systerel.smt.provers.ast.SMTBenchmark;

/**
 * @author guyot
 * 
 */
public class SMTPPCall extends SMTProverCall {
	private static final String PP_TRANSLATION_PATH = TRANSLATION_PATH
			+ File.separatorChar + "pp";

	protected SMTPPCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm, preferences, lemmaName);
	}

	/**
	 * Makes an SMT-LIB benchmark file containing the Event-B sequent translated
	 * in SMT-LIB V1.2 language, using the PP approach.
	 * 
	 * @throws IOException
	 */
	@Override
	public void makeSMTBenchmarkFileV1_2() throws IOException {
		proofMonitor.setTask("Translating Event-B proof obligation");
		/**
		 * Creation of an SMT-LIB benchmark using the PP approach of Event-B to
		 * SMT-LIB translation
		 */
		final SMTBenchmark benchmark = SMTThroughPP
				.translateToSmtLibBenchmark(lemmaName, hypotheses, goal,
						smtPreferences.getSolver().getId());
		/**
		 * Update of the lemma name
		 */
		lemmaName = benchmark.getName();
		final String benchmarkTargetedPath = PP_TRANSLATION_PATH
				+ File.separatorChar + lemmaName;

		/**
		 * Creation of the translation folder (cleans it if needed)
		 */
		if (translationFolder == null) {
			translationFolder = mkTranslationFolder(benchmarkTargetedPath,
					!CLEAN_SMT_FOLDER_BEFORE_EACH_PROOF);
		}
		/**
		 * Prints the benchmark in a new file
		 */
		smtBenchmarkFile = new File(smtFilePath());
		smtBenchmarkFile.createNewFile();
		final PrintWriter smtFileWriter = openSMTFileWriter(smtBenchmarkFile);
		benchmark.print(smtFileWriter);
		smtFileWriter.close();
		if (!smtBenchmarkFile.exists()) {
			System.out.println(Messages.SmtProversCall_SMT_file_does_not_exist);
		}
	}
}
