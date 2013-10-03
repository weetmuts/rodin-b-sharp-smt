/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.perf.app;

import java.io.InputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;
import org.eventb.smt.core.internal.prefs.SolverPreferences;

/**
 * Imports preferences into workspace. The preferences are used to set SMT
 * solvers and configurations.
 * 
 * @author Laurent Voisin
 */
public class PrefImporter {

	private int timeout;

	/**
	 * Imports preferences to the SMT core plug-in
	 */
	public void importPreferences(IFileStore store) throws Exception {
		final IPreferencesService svc = Platform.getPreferencesService();
		final InputStream stream = store.openInputStream(EFS.NONE, null);
		final IExportedPreferences root = svc.readPreferences(stream);
		importSolvers(root);
		importConfigurations(root);
		timeout = root.getInt("autoTimeout", 1000);
		if (Application.DEBUG) {
			System.out.println("Timeout is " + timeout + " ms");
		}
	}

	public int getTimeout() {
		return timeout;
	}

	private void importSolvers(IEclipsePreferences root) {
		final SolverPreferences prefs = SolverPreferences.newTestInstance(root);
		final ISolverDescriptor[] solvers = prefs.doGetKnown();
		if (Application.DEBUG) {
			for (final ISolverDescriptor solver : solvers) {
				System.out.println("Importing solver " + solver);
			}
		}
		SMTCore.setSolvers(solvers);
	}

	private void importConfigurations(IEclipsePreferences root) {
		final ConfigPreferences prefs = ConfigPreferences.newTestInstance(root);
		final IConfigDescriptor[] configs = prefs.doGetKnown();
		if (Application.DEBUG) {
			for (final IConfigDescriptor config : configs) {
				System.out.println("Importing config " + config);
			}
		}
		SMTCore.setConfigurations(configs);
	}

}
