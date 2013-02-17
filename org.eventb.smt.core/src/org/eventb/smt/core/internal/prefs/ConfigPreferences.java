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

import java.util.List;

import org.eventb.smt.core.internal.tactics.SMTAutoTactic;
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
			if (get(name) != null) {
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

	@Override
	protected IConfigDescriptor getRealDescriptor(IConfigDescriptor desc) {
		final String name = desc.getName();
		final IConfigDescriptor bundledDesc = doGetBundled(name);
		if (bundledDesc != null) {
			// Copy enabled attribute
			((ConfigDescriptor) bundledDesc).setEnabled(desc.isEnabled());
			return bundledDesc;
		}
		return desc;
	}

	/*
	 * Update the cache of the SMT auto-tactic.
	 */
	@Override
	protected void doSetKnown(List<IConfigDescriptor> newDescs) {
		super.doSetKnown(newDescs);
		SMTAutoTactic.updateTactics(newDescs);
	}

	public static IConfigDescriptor[] getBundledConfigs() {
		return INSTANCE.doGetBundled();
	}

	public static IConfigDescriptor[] getConfigs() {
		return INSTANCE.doGetKnown();
	}

	public static void setConfigs(IConfigDescriptor[] newConfigs) {
		INSTANCE.setKnown(newConfigs);
	}

	public static IConfigDescriptor get(String name) {
		return INSTANCE.doGet(name);
	}

	/**
	 * Initializes configuration preferences.
	 */
	public static void init() {
		/*
		 * Actually, this method does nothing, but calling it forces loading of
		 * this class by the JVM which does the appropriate initialization.
		 */
	}

}
