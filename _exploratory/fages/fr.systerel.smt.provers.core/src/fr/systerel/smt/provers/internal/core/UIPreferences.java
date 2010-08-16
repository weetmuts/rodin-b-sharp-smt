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
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.preferences.SolverDetail;
import static br.ufrn.smt.solver.preferences.SmtPreferencesStore.*;
import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.TranslationException;

import fr.systerel.smt.provers.core.SmtProversCore;

public class UIPreferences {
	
	private SolverDetail solver;
	
	private boolean usingPrepro;

	private String preproPath;
	
	public SolverDetail getSolver(){
		return solver;
	}
	
	public Boolean getUsingPrepro(){
		return usingPrepro;
	}
	
	public String getPreproPath(){
		return preproPath;
	}

	public UIPreferences(String solverSettingsPreferences, int selectedSolverIndex,
			boolean usingprepro, String prepropath) {
		List<SolverDetail> solvers = CreateModel(solverSettingsPreferences);
		if (selectedSolverIndex == -1){
			solver = null;
		}
		else if (selectedSolverIndex < solvers.size()){
			solver = solvers.get(selectedSolverIndex);
		}
		else{
			solver = null;
		}
			
		this.usingPrepro = usingprepro;
		this.preproPath = prepropath;
	}

}