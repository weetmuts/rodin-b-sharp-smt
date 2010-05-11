package fr.systerel.smt.provers.internal.core;

import java.io.IOException;
import java.io.PrintStream;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

public class SmtCall extends SmtProversCall {

	public SmtCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm) {
		super(hypotheses, goal, pm, "SMT");
	}

	@Override
	protected String[] proverCommand() {
		return null;
	}

	@Override
	protected String[] parserCommand() {
		return null;
	}

	@Override
	public String displayMessage() {
		return null;
	}

	@Override
	protected String successString() {
		return null;
	}

	@Override
	protected void printInputFile() throws IOException {

	}

}
