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

import static org.eventb.smt.core.SMTPreferences.PREF_NODE_NAME;
import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.smt.core.prefs.IDescriptor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public abstract class AbstractPreferences<T extends IDescriptor> {

	private static final IEclipsePreferences root = InstanceScope.INSTANCE
			.getNode(PREF_NODE_NAME);

	private final String nodeName;
	protected final DescriptorList<T> bundled;
	protected final DescriptorList<T> userDefined;

	protected AbstractPreferences(String nodeName) {
		this.nodeName = nodeName;
		this.bundled = loadBundledDescriptors();
		this.userDefined = loadUserDescriptors();
	}

	protected abstract DescriptorList<T> loadBundledDescriptors();

	private DescriptorList<T> loadUserDescriptors() {
		final DescriptorList<T> result = newDescriptorList();
		try {
			if (root.nodeExists(nodeName)) {
				loadFromNode(root.node(nodeName), result);
			}
		} catch (BackingStoreException e) {
			logError("loading preferences from " + root + "/" + nodeName, e);
		}
		return result;
	}

	protected abstract DescriptorList<T> newDescriptorList();

	private void loadFromNode(Preferences node,
			DescriptorList<T> result) throws BackingStoreException {
		for (final String childName : sortedChildrenNames(node)) {
			try {
				result.add(loadFromNode(node.node(childName)));
			} catch (IllegalArgumentException e) {
				logError("loading preference " + node.absolutePath(), e);
			}
		}
	}

	protected abstract T loadFromNode(Preferences node);

	private String[] sortedChildrenNames(final Preferences node)
			throws BackingStoreException {
		final String[] childrenNames = node.childrenNames();
		Arrays.sort(childrenNames, new Comparator<String>() {
			// Compare lengths first to implement numerical ordering
			@Override
			public int compare(String o1, String o2) {
				final int lengthDiff = o1.length() - o2.length();
				return lengthDiff == 0 ? o1.compareTo(o2) : lengthDiff;
			}
		});
		return childrenNames;
	}

	public void setUser(T[] newSolvers) {
		doSetUser(newSolvers);
		try {
			doSave();
		} catch (BackingStoreException e) {
			logError("saving preferences to " + root + "/" + nodeName, e);
		}
	}

	protected void doSetUser(T[] newSolvers) {
		userDefined.clear();
		userDefined.addAll(newSolvers);
	}

	public T[] doGetBundled() {
		return bundled.toArray();
	}

	public T[] doGetUser() {
		return userDefined.toArray();
	}

	protected T doGet(String name) {
		final T desc = bundled.get(name);
		if (desc != null) {
			return desc;
		}
		return userDefined != null ? userDefined.get(name) : null;
	}

	public void doSave() throws BackingStoreException {
		root.node(nodeName).removeNode();
		final Preferences node = root.node(nodeName);
		int count = 1;
		for (final T desc : userDefined) {
			final Preferences childNode = node.node(Integer.toString(count++));
			((Descriptor) desc).serialize(childNode);
		}
		node.flush();
	}

	// TODO Listen to preference changes

}