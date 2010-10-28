/**
 * 
 */
package br.ufrn.smt.solver.translation;

import java.util.List;

import fr.systerel.smt.provers.ast.SMTNode;

/**
 * @author Yoann Guyot
 * 
 */
public class Sequent {

	private final List<SMTNode<?>> assumptions;

	private SMTNode<?> goal;

	public Sequent(final List<SMTNode<?>> assumptions, final SMTNode<?> goal) {
		this.assumptions = assumptions;
		this.goal = goal;
	}

	public List<SMTNode<?>> getAssumptions() {
		return this.assumptions;
	}

	public SMTNode<?> getGoal() {
		return this.goal;
	}

	public void addAssumption(final SMTNode<?> assumption) {
		this.assumptions.add(assumption);
	}

	public void setGoal(final SMTNode<?> goal) {
		this.goal = goal;
	}
}
