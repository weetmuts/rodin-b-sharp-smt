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

import static org.eventb.smt.core.internal.log.SMTStatus.smtError;

import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.PreferenceManager;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractPreferences implements IPreferences {
	protected static final boolean USE_DEFAULT_SCOPE = true;

	protected static final IEclipsePreferences SMT_PREFS_NODE = InstanceScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);
	protected static final IEclipsePreferences DEFAULT_SMT_PREFS_NODE = DefaultScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);

	public static final boolean FORCE_RELOAD = true;
	public static final boolean FORCE_REPLACE = true;
	public static final int IDS_UPPER_BOUND = 100000;

	public static final String SEPARATOR = ";"; //$NON-NLS-1$

	protected final IEclipsePreferences prefsNode;
	protected boolean loaded;
	protected int idCounter;

	protected AbstractPreferences(boolean useDefaultScope) {
		loaded = false;
		idCounter = 0;
		if (useDefaultScope) {
			prefsNode = DEFAULT_SMT_PREFS_NODE;
		} else {
			prefsNode = SMT_PREFS_NODE;
		}
	}

	protected static final String getValidPath(final String currentPath,
			final ISMTSolver solver, final String defaultPath) {
		final String newPath = solver.getPath().toOSString();
		if (isValidPath(newPath)) {
			return newPath;
		} else if (!isValidPath(currentPath)) {
			return defaultPath;
		} else {
			return currentPath;
		}
	}

	protected static boolean isValidPath(final String path) {
		return PreferenceManager.isValidPath(path, new StringBuilder(0));
	}

	protected static <T> int getHighestID(final Map<String, T> map) {
		int highestID = 0;
		for (final String idStr : map.keySet()) {
			try {
				int id = Integer.parseInt(idStr);
				if (id > highestID) {
					highestID = id;
				}
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
		return highestID;
	}

	protected <T> String freshID(final Map<String, T> map) {
		if (map.size() == IDS_UPPER_BOUND) {
			smtError("Too many items.", null);
			return null;
		}

		for (int i = idCounter; i < IDS_UPPER_BOUND; i++) {
			final String newID = Integer.toString(i);
			if (!map.containsKey(newID)) {
				return newID;
			}
		}
		for (int i = 0; i < idCounter; i++) {
			final String newID = Integer.toString(i);
			if (!map.containsKey(newID)) {
				return newID;
			}
		}
		return null;
	} 

	abstract protected void load(final boolean reload);

	@Override
	abstract public void loadDefault();

	@Override
	abstract public void save();
}
