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

import static junit.framework.Assert.assertTrue;
import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.internal.preferences.BundledSolverLoader;
import org.eventb.smt.core.internal.preferences.ExtensionLoadingException;
import org.eventb.smt.core.internal.preferences.SMTSolver;
import org.junit.Test;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverLoaderTests {

	BundledSolverLoader bundledSolverLoader;

	public void makeBundledSolverLoader(final String bundle, final String id,
			final String name, final String kind, final String localPath) {
		bundledSolverLoader = new BundledSolverLoader(new BundledSolverElt(
				bundle, id, name, kind, localPath));
	}

	public void makeBundledSolverLoader(final String id, final String name,
			final String kind, final String localPath) {
		bundledSolverLoader = new BundledSolverLoader(new BundledSolverElt(id,
				name, kind, localPath));
	}

	public static class BundledSolverElt implements IConfigurationElement {
		static final IConfigurationElement[] NO_ELEMENT = new IConfigurationElement[] {};

		String bundleName = null;
		String id = null;
		String name = null;
		String kind = null;
		String localPath = null;

		public BundledSolverElt(final String bundle, final String id,
				final String name, final String kind, final String localPath) {
			this.bundleName = bundle;
			this.id = id;
			this.name = name;
			this.kind = kind;
			this.localPath = localPath;
		}

		public BundledSolverElt(final String id, final String name,
				final String kind, final String localPath) {
			this("org.eventb.smt.core.tests", id, name, kind, localPath);
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
			} else if (attributeName.equals("localpath")) {
				return localPath;
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
			return new IContributor() {
				
				@Override
				public String getName() {
					return bundleName;
				}
			};
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
		makeBundledSolverLoader(null, "", "", "");

		bundledSolverLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void dotInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("bundled.solver", "", "", "");

		bundledSolverLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void whitespaceInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("bundled solver", "", "", "");

		bundledSolverLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void colonInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("bundled:solver", "", "", "");

		bundledSolverLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void nullName() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("nullNameTest", null, "", "");

		bundledSolverLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void emptyName() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("emptyNameTest", "", "", "bundledsolver");

		bundledSolverLoader.load();
	}

	@Test
	public void nullKind() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("nullKind", "nullKind", null, "");

		final SMTSolver solver = bundledSolverLoader.load();
		assertTrue(solver.getKind() == UNKNOWN);
	}

	@Test(expected = ExtensionLoadingException.class)
	public void nullPath() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeBundledSolverLoader("nullPathTest", "nullPathTest", "", null);

		bundledSolverLoader.load();
	}
}
