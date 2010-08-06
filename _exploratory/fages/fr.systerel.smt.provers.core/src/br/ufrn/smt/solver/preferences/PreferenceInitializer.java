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
		store.setDefault("solver_path", "");
		store.setDefault("usingprepro", false);
		store.setDefault("prepropath", "");
		store.setDefault("showtfile", false);
		store.setDefault("solverarguments", "");
		String value = "";
		if(System.getProperty("os.name").contains("Windows"))
		{
			value = "notepad";
		}
		else if (System.getProperty("os.name").contains("Linux"))
		{
			value = "/usr/bin/gedit";
		}
		store.setDefault("smteditor", value);
		
	}

}
