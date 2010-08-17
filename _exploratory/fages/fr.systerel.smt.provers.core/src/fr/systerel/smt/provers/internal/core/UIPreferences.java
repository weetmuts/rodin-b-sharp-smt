/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import java.util.List;

import br.ufrn.smt.solver.preferences.SolverDetail;
import static br.ufrn.smt.solver.preferences.SmtPreferencesStore.*;

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