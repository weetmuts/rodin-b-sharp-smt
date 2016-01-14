/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static java.lang.System.getProperty;
import static org.eventb.smt.core.SMTPreferences.AUTO_TIMEOUT;
import static org.eventb.smt.core.SMTPreferences.PREF_NODE_NAME;
import static org.eventb.smt.core.SMTPreferences.TRANSLATION_PATH_ID;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

/**
 * Container class for the simple preferences of the SMT core plug-in:
 * <ul>
 * <li>temporary translation directory</li>
 * <li>veriT path to use for translation</li>
 * </ul>
 *
 * These preferences are not exposed to the UI plug-in through a specialized
 * API, but rather by using the preference mechanism of Eclipse.
 *
 * @author Laurent Voisin
 */
public class SimplePreferences {

	/**
	 * Default value for the timeout of the auto-tactic.
	 */
	private static int DEFAULT_AUTO_TIMEOUT = 1000;

	public static class DefaultInitializer extends
			AbstractPreferenceInitializer {

		@Override
		@SuppressWarnings("synthetic-access")
		public void initializeDefaultPreferences() {
			final IScopeContext scope = DefaultScope.INSTANCE;
			final Preferences node = scope.getNode(PREF_NODE_NAME);
			node.put(TRANSLATION_PATH_ID, getProperty("java.io.tmpdir"));
			node.putInt(AUTO_TIMEOUT, DEFAULT_AUTO_TIMEOUT);
		}

	}

	private static final IEclipsePreferences NODE = InstanceScope.INSTANCE
			.getNode(PREF_NODE_NAME);

	// Not instantiable class
	private SimplePreferences() {
		// Do nothing
	}

	public static final IPath getTranslationPath() {
		final String value = getPreference(TRANSLATION_PATH_ID);
		return new Path(value);
	}

	public static final long getAutoTimeout() {
		final IPreferencesService svc = Platform.getPreferencesService();
		return svc.getLong(PREF_NODE_NAME, AUTO_TIMEOUT, DEFAULT_AUTO_TIMEOUT,
				null);
	}

	private static String getPreference(final String key) {
		final IPreferencesService svc = Platform.getPreferencesService();
		final String value = svc.getString(PREF_NODE_NAME, key, "", null);
		return value;
	}

	public static void addChangeListener(IPreferenceChangeListener listener) {
		NODE.addPreferenceChangeListener(listener);
	}

}
