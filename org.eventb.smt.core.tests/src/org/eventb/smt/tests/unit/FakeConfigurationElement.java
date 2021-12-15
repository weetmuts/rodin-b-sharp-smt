/*******************************************************************************
 * Copyright (c) 2012, 2021 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.unit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eventb.smt.tests.SMTCoreTests;
import org.osgi.framework.Bundle;

public class FakeConfigurationElement implements IConfigurationElement {

	final Map<String, String> attributes = new HashMap<String, String>();

	public void add(String name, String value) {
		attributes.put(name, value);
	}

	@Override
	public Object createExecutableExtension(String propertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public String getAttribute(String attrName, String locale) {
		return getAttribute(attrName);
	}

	@Override
	@Deprecated
	public String getAttributeAsIs(String name) {
		return getAttribute(name);
	}

	@Override
	public String[] getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IConfigurationElement[] getChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IConfigurationElement[] getChildren(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExtension getDeclaringExtension() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getHandleId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getValue(String locale) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getValueAsIs() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getNamespace() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNamespaceIdentifier() {
		return SMTCoreTests.PLUGIN_ID;
	}

	@Override
	public IContributor getContributor() {
		final Bundle bundle = Platform.getBundle(SMTCoreTests.PLUGIN_ID);
		return ContributorFactoryOSGi.createContributor(bundle);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
