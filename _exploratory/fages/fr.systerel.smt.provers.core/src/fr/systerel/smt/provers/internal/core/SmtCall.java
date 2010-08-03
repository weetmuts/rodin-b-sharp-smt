package fr.systerel.smt.provers.internal.core;

import java.io.IOException;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

import fr.systerel.smt.provers.core.SmtProversCore;

public class SmtCall extends SmtProversCall {

	public SmtCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm) {
		super(hypotheses, goal, pm, "SMT");
	}

	@Override
	protected String[] proverCommand() {
		String solverArgs = SmtProversCore.getDefault().getPreferenceStore().getString("solverarguments");
		return solverArgs.split(" ");
	}

	@Override
	protected String[] parserCommand() {
		return null;
	}

	@Override
	public String displayMessage() {
		return super.resultOfSolver;
	}

	@Override
	protected String successString() {
		return "is valid";	
	}

	@Override
	protected void printInputFile() throws IOException {

	}

}
