/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eventb.smt.ui.internal.preferences.configurations.StringListSerializer.serialize;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.ui.internal.provers.SMTProversUI;
import org.osgi.service.prefs.Preferences;

/**
 * Enables storing the enablement of SMT solver configurations in the SMT UI
 * plug-in preference store.
 * <p>
 * The names of all enabled configurations are just stored in one string in the
 * preference store of the plug-in.
 * </p>
 *
 * @author Laurent Voisin
 * @see StringListSerializer
 */
public class EnablementStore {

	private static final String ENABLED_CONFIGS = "enabledConfigs";

	private static final IPreferenceStore store = SMTProversUI.getDefault()
			.getPreferenceStore();

	public static void setToDefault() {
		store.setToDefault(ENABLED_CONFIGS);
	}

	public static void load(Iterable<ConfigElement> elements) {
		final List<String> names = getEnabledConfigNames();
		for (final ConfigElement element : elements) {
			final String name = element.name;
			element.enabled = names.contains(name);
		}
	}

	public static List<String> getEnabledConfigNames() {
		final String serialized = store.getString(ENABLED_CONFIGS);
		return StringListSerializer.deserialize(serialized);
	}

	public static void store(Iterable<ConfigElement> elements) {
		final List<String> names = new ArrayList<String>();
		for (final ConfigElement element : elements) {
			if (element.enabled) {
				names.add(element.name);
			}
		}
		store.setValue(ENABLED_CONFIGS, StringListSerializer.serialize(names));
	}

	public static void setDefault(Preferences node) {
		node.put(ENABLED_CONFIGS, getDefaultValue());
	}

	/*
	 * By default, all known configurations are enabled.
	 */
	private static String getDefaultValue() {
		final IConfigDescriptor[] configs = SMTCore.getConfigurations();
		final List<String> names = new ArrayList<String>(configs.length);
		for (final IConfigDescriptor config : configs) {
			names.add(config.getName());
		}
		return serialize(names);
	}

}
