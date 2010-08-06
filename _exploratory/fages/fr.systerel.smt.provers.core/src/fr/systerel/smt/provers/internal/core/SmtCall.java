package fr.systerel.smt.provers.internal.core;

/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *     Vítor Alcântara de Almeida - First integration Smt solvers 
 *******************************************************************************/

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
