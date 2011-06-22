/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida: Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERITPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERITPATH;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = SmtProversUIPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES);
		store.setDefault(SOLVERINDEX, DEFAULT_SOLVERINDEX);
		store.setDefault(VERITPATH, DEFAULT_VERITPATH);
	}
}
