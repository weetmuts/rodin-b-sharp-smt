/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import static java.lang.System.currentTimeMillis;

import java.util.Random;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.PreferenceManager;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractPreferences implements IPreferences {
	protected static final boolean USE_DEFAULT_SCOPE = true;

	protected static final IEclipsePreferences SMT_PREFS_NODE = ConfigurationScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);
	protected static final IEclipsePreferences DEFAULT_SMT_PREFS_NODE = DefaultScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);

	public static final boolean FORCE_RELOAD = true;
	public static final boolean FORCE_REPLACE = true;
	public static final Random RANDOM = new Random(currentTimeMillis());
	public static final int IDS_UPPER_BOUND = 100000;

	public static final String SEPARATOR = ";"; //$NON-NLS-1$

	protected final IEclipsePreferences prefsNode;
	protected boolean loaded;

	protected AbstractPreferences(boolean useDefaultScope) {
		loaded = false;
		if (useDefaultScope) {
			prefsNode = DEFAULT_SMT_PREFS_NODE;
		} else {
			prefsNode = SMT_PREFS_NODE;
		}
	}

	protected static final String getValidPath(final String currentPath,
			final String newPath, final String defaultPath) {
		if (isPathValid(newPath)) {
			return newPath;
		} else if (!isPathValid(currentPath)) {
			return defaultPath;
		} else {
			return currentPath;
		}
	}

	protected static boolean isPathValid(final String path) {
		return PreferenceManager.isPathValid(path, new StringBuilder(0));
	}

	abstract protected void load(final boolean reload);

	@Override
	abstract public void loadDefault();

	@Override
	abstract public void save();
}
