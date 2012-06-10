/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static java.lang.System.getProperty;
import static org.eventb.smt.core.SMTCore.PLUGIN_ID;
import static org.eventb.smt.core.SMTCore.TRANSLATION_PATH_ID;
import static org.eventb.smt.core.SMTCore.VERIT_PATH_ID;
import static org.eventb.smt.core.SMTCore.getBundledSolvers2;
import static org.eventb.smt.core.provers.SolverKind.VERIT;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.osgi.service.prefs.Preferences;

/**
 * Container class for the simple preferences of the SMT core plug-in:
 * <ul>
 * <li>temporary translation directory</li>
 * <li>veriT path to use for translation</li>
 * </ul>
 *
 * This preferences are not exposed to the UI plug-in through a specialized API,
 * but rather by using the preference mechanism of Eclipse.
 *
 * @author Laurent Voisin
 */
public class SimplePreferences {

	public static class DefaultInitializer extends
			AbstractPreferenceInitializer {

		@Override
		public void initializeDefaultPreferences() {
			final IScopeContext scope = DefaultScope.INSTANCE;
			final Preferences node = scope.getNode(SMTCore.PLUGIN_ID);
			node.put(TRANSLATION_PATH_ID, getProperty("java.io.tmpdir"));
			node.put(VERIT_PATH_ID, bundledVeriTPath());
		}

		private String bundledVeriTPath() {
			for (final ISolverDescriptor desc : getBundledSolvers2()) {
				if (VERIT == desc.getKind()) {
					return desc.getPath().toString();
				}
			}
			return ""; // No real default
		}

	}

	private static final IEclipsePreferences NODE = InstanceScope.INSTANCE
			.getNode(PLUGIN_ID);

	// Not instantiable class
	private SimplePreferences() {
		// Do nothing
	}

	public static final IPath getTranslationPath() {
		return new Path(NODE.get(TRANSLATION_PATH_ID, ""));
	}

	public static final IPath getVeriTPath() {
		return new Path(NODE.get(VERIT_PATH_ID, ""));
	}

}
