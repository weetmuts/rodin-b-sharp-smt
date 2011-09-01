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

package br.ufrn.smt.solver.preferences.ui;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVER_PREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERIT_PATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERIT_PATH_ID;

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
		store.setDefault(SOLVER_PREFERENCES_ID, DEFAULT_SOLVER_PREFERENCES);
		store.setDefault(SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX);
		store.setDefault(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
		store.setDefault(TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH);
	}
}
