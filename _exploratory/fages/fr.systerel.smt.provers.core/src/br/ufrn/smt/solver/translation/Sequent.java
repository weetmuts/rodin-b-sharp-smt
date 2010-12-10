/**
 * 
 */
package br.ufrn.smt.solver.translation;

import java.util.List;

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
