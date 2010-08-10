/*******************************************************************************
 * Copyright (c) 2010 Systerel .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.TranslationException;

import fr.systerel.smt.provers.core.SmtProversCore;

public class UIPreferences {

	public String solverPath;

	public String solverArguments;

	public boolean usingPrepro;

	public String preproPath;

	public String whichSolver;

	public String proofAndShowFile;

	public String preprocessingOptions;

	public String executeTrans;

	public String smtEditor;

	/**
	 * Creates a new instance of this class to Get back SMT UI preferences.
	 * 
	 * @param solverpath
	 *          Path to solver executable
	 * @param solverarguments
	 *			Solver Arguments 
	 * @param usingprepro 
	 * 			Use of the preprocessing options
	 * @param prepropath
	 * 			Preprocessing solver path
	 * @param whichsolver		
	 * 			Chosen Solver 
	 * @param preprocessingoptions
	 * 			Preprocessing options			 
	 * @param executeTrans 
	 * @param smtEditor
	 * 			Path to a text editor
	 */
	public UIPreferences(String solverpath, String solverarguments,
			boolean usingprepro, String prepropath, String whichsolver,
			String preprocessingoptions,
			String executeTrans, String smtEditor) {
		this.solverPath = solverpath;
		this.solverArguments = solverarguments;
		this.usingPrepro = usingprepro;
		this.preproPath = prepropath;
		this.whichSolver = whichsolver;
		this.preprocessingOptions = preprocessingoptions;
		this.executeTrans = executeTrans;
		this.smtEditor = smtEditor;
	}

}