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

import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;

import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.smt.core.IConfigDescriptor;
import org.osgi.service.prefs.Preferences;

public class ConfigPreferences extends AbstractPreferences<IConfigDescriptor> {

	private static final ConfigPreferences INSTANCE = new ConfigPreferences();

	private static class ConfigList extends DescriptorList<IConfigDescriptor> {

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

	// For testing purposes only
	private ConfigPreferences(IEclipsePreferences root) {
		super(root, "config");
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

	public static void addChangeLister(IPreferencesChangeListener listener) {
		INSTANCE.doAddChangeListener(listener);
	}

	/**
	 * Returns a new instance for tests. This instance is completely decoupled
	 * from the real instance.
	 * 
	 * @param root
	 *            a preferences tree to use for initializing the test instance
	 * @return a new instance for tests
	 */
	public static ConfigPreferences newTestInstance(IEclipsePreferences root) {
		return new ConfigPreferences(root);
	}

}
