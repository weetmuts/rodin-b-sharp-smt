/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.unit;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.internal.preferences.SolverConfigLoader;
import org.eventb.smt.core.preferences.ExtensionLoadingException;
import org.junit.Test;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverLoaderTests {

	SolverConfigLoader bundledSolverDesc;

	public void makeBundledSolverDesc(final String bundle, final String id,
			final String name, final String kind, final String binary,
			final String args, final String smtlib) {
		bundledSolverDesc = new SolverConfigLoader(new BundledSolverConfigElt(
				bundle, id, name, kind, binary, args, smtlib));
	}

	public void makeBundledSolverDesc(final String id, final String name,
			final String kind, final String binary, final String args,
			final String smtlib) {
		bundledSolverDesc = new SolverConfigLoader(new BundledSolverConfigElt(id,
				name, kind, binary, args, smtlib));
	}

	public static class BundledSolverConfigElt implements IConfigurationElement {
		static final IConfigurationElement[] NO_ELEMENT = new IConfigurationElement[] {};

		String bundleName = null;
		String id = null;
		String name = null;
		String kind = null;
		String binary = null;
		String args = null;
		String smtlib = null;

		public BundledSolverConfigElt(final String bundle, final String id,
				final String name, final String kind, final String binary,
				final String args, final String smtlib) {
			this.bundleName = bundle;
			this.id = id;
			this.name = name;
			this.kind = kind;
			this.binary = binary;
			this.args = args;
			this.smtlib = smtlib;
		}

		public BundledSolverConfigElt(final String id, final String name,
				final String kind, final String binary, final String args,
				final String smtlib) {
			this("org.eventb.smt.core.tests", id, name, kind, binary, args,
					smtlib);
		}

		@Override
		public Object createExecutableExtension(String propertyName) {
			return new UnsupportedOperationException();
		}

		@Override
		public String getAttribute(String attributeName) {
			if (attributeName.equals("id")) {
				return id;
			} else if (attributeName.equals("name")) {
				return name;
			} else if (attributeName.equals("kind")) {
				return kind;
			} else if (attributeName.equals("binary")) {
				return binary;
			} else if (attributeName.equals("args")) {
				return args;
			} else if (attributeName.equals("smtlib")) {
				return smtlib;
			} else {
				return null;
			}
		}

		@Override
		public String getAttribute(String attrName, String locale) {
			return getAttribute(attrName);
		}

		@Deprecated
		@Override
		public String getAttributeAsIs(String attrName) {
			throw new UnsupportedOperationException();
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
		public IConfigurationElement[] getChildren(String attrName) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IContributor getContributor() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExtension getDeclaringExtension() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getName() {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public String getNamespace() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getNamespaceIdentifier() {
			return bundleName;
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
			return getValue();
		}

		@Deprecated
		@Override
		public String getValueAsIs() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isValid() {
			throw new UnsupportedOperationException();
		}
	}

	@Test(expected = ExtensionLoadingException.class)
	public void nullID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverDesc(null, "", "", "", "", "");

		bundledSolverDesc.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void dotInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverDesc("bundled.solver", "", "", "", "", "", "");

		bundledSolverDesc.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void whitespaceInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverDesc("bundled solver", "", "", "", "", "", "");

		bundledSolverDesc.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void testColonInIDException() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverDesc("bundled:solver", "", "", "", "", "", "");

		bundledSolverDesc.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void testNullPathException() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverDesc("bundledsolver", "", "", null, "", "");

		bundledSolverDesc.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void testInvalidBundleNameException()
			throws InvalidRegistryObjectException, ExtensionLoadingException {
		makeBundledSolverDesc("bundledsolver", "", "", "bundledsolver", "", "");

		bundledSolverDesc.load();
	}

	// TODO other tests...
}
