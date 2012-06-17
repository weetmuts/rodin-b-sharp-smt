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

import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;

import org.eventb.smt.core.internal.preferences.configurations.BundledConfigList;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.osgi.service.prefs.Preferences;

public class ConfigPreferences extends AbstractPreferences<IConfigDescriptor> {

	private static final ConfigPreferences INSTANCE = new ConfigPreferences();

	private class ConfigList extends DescriptorList<IConfigDescriptor> {

		public ConfigList() {
			super();
		}

		@Override
		public boolean isValid(IConfigDescriptor desc) {
			final String name = desc.getName();
			if (doGet(name) != null) {
				logError("Duplicate config name " + name + " ignored", null);
				return false;
			}
			return true;
		}

		@Override
		public IConfigDescriptor[] newArray(int length) {
			return new IConfigDescriptor[length];
		}

	}

	// Singleton class.
	private ConfigPreferences() {
		super("config");
	}

	@Override
	protected DescriptorList<IConfigDescriptor> loadBundledDescriptors() {
		return new BundledConfigList();
	}

	@Override
	protected DescriptorList<IConfigDescriptor> newDescriptorList() {
		return new ConfigList();
	}

	@Override
	protected IConfigDescriptor loadFromNode(Preferences node) {
		return new ConfigDescriptor(node);
	}

	public static IConfigDescriptor[] getBundledConfigs() {
		return INSTANCE.doGetBundled();
	}

	public static IConfigDescriptor[] getUserConfigs() {
		return INSTANCE.doGetUser();
	}

	public static void setUserConfigs(IConfigDescriptor[] newConfigs) {
		INSTANCE.setUser(newConfigs);
	}

	public static IConfigDescriptor get(String name) {
		return INSTANCE.doGet(name);
	}

}
