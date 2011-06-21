/**
 * 
 */
package fr.systerel.smt.provers.internal.core;

import java.io.IOException;
import java.util.List;

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

	protected SMTPPCall(final Iterable<Predicate> hypotheses,
			final Predicate goal, final IProofMonitor pm,
			final SMTPreferences preferences, final String lemmaName) {
		super(hypotheses, goal, pm, preferences, lemmaName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Execute translation of Event-B predicates using the PP approach.
	 * 
	 * @return the list of arguments
	 * @throws IOException
	 */
	@Override
	public List<String> smtTranslation() throws IOException {
		return smtTranslation(translateToBenchmarkThroughPP());
	}

	/**
	 * Execute translation of Event-B predicates using the PP approach.
	 * 
	 * @return the list of arguments
	 * @throws IOException
	 */
	public SMTBenchmark translateToBenchmarkThroughPP() throws IOException {
		final SMTBenchmark benchmark = SMTThroughPP
				.translateToSmtLibBenchmark(lemmaName, hypotheses, goal,
						smtPreferences.getSolver().getId());
		return benchmark;
	}

}
