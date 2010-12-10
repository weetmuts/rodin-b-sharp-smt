/**
 * 
 */
package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eventb.core.ast.Predicate;

import fr.systerel.smt.provers.ast.SMTFormula;

/**
 * @author Yoann Guyot
 * 
 */
public class Sequent {
	private final Signature signature;

	private final List<SMTFormula> assumptions;

	private SMTFormula goal;

	public Sequent(final Signature signature,
			final List<SMTFormula> assumptions, final SMTFormula goal) {
		this.signature = signature;
		this.assumptions = assumptions;
		this.goal = goal;
	}

	/**
	 * This method parses hypothesis and goal predicates and translates them
	 * into SMT nodes
	 */
	public static Sequent translate(final Signature signature,
			final List<Predicate> hypotheses, final Predicate goal) {
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		HashSet<String> boundIdentifiers = new HashSet<String>();
		HashSet<String> freeIdentifiers = new HashSet<String>();

		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();

		for (Predicate assumption : hypotheses) {
			IdentifiersAndSMTStorage iSMT = TranslatorV1_2.translate1(
					assumption, a, b);

			boundIdentifiers.addAll(iSMT.getBoundIdentifiers());
			freeIdentifiers.addAll(iSMT.getFreeIdentifiers());
			translatedAssumptions.add(iSMT.getSmtFormula());

			a = new ArrayList<String>(boundIdentifiers);
			b = new ArrayList<String>(freeIdentifiers);
		}

		SMTFormula smtFormula = TranslatorV1_2.translate(goal, a, b);
		return new Sequent(signature, translatedAssumptions, smtFormula);
	}

	private static void assumptionsSection(final StringBuilder sb,
			List<SMTFormula> assumptions) {
		for (final SMTFormula assumption : assumptions) {
			sb.append(" :assumption ");
			assumption.toString(sb);
			sb.append("\n");
		}
	}

	private static void formulaSection(StringBuilder sb, SMTFormula goal) {
		sb.append(" :formula (not ");
		goal.toString(sb);
		sb.append(")\n");
	}

	public void toString(StringBuilder sb) {
		assumptionsSection(sb, this.assumptions);
		formulaSection(sb, this.goal);
	}
}
