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

	private final List<SMTFormula> assumptions;

	private SMTFormula goal;

	public Sequent(final List<SMTFormula> assumptions, final SMTFormula goal) {
		this.assumptions = assumptions;
		this.goal = goal;
	}

	public List<SMTFormula> getAssumptions() {
		return this.assumptions;
	}

	public SMTFormula getGoal() {
		return this.goal;
	}

	public void addAssumption(final SMTFormula assumption) {
		this.assumptions.add(assumption);
	}

	public void setGoal(final SMTFormula goal) {
		this.goal = goal;
	}
}
