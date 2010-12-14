/**
 * 
 */
package br.ufrn.smt.solver.translation;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

/**
 * @author guyot
 * 
 */
public class SmtThroughPp extends TranslatorV1_2 {

	public static Benchmark translateToSmtLibBenchmark(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {
		return new SmtThroughPp().translate(lemmaName, hypotheses, goal);
	}

	@Override
	public Benchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		final Signature signature = translateSignature(ppTranslatedHypotheses, ppTranslatedGoal);
		final Sequent sequent = translate(signature, ppTranslatedHypotheses, ppTranslatedGoal);
		return new Benchmark(lemmaName, signature, sequent);
	}

	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to predicate calculus
		 */
		ppProof.translate();

		return ppProof;
	}
}
