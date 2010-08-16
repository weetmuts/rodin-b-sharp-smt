/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vítor Alcântara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = SmtProversCore.getDefault().getPreferenceStore();
		store.setDefault("solverPreferences", "");
		store.setDefault("solverindex", -1);
		store.setDefault("usingprepro", false);
		store.setDefault("prepropath", "");
	}

}
