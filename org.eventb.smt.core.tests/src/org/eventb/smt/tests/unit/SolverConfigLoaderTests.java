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
import static org.eventb.smt.core.translation.SMTLIBVersion.LATEST;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.internal.preferences.ExtensionLoadingException;
import org.eventb.smt.core.internal.preferences.SolverConfigLoader;
import org.eventb.smt.core.internal.preferences.SolverConfiguration;
import org.junit.Test;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigLoaderTests {

	SolverConfigLoader solverConfigLoader;

	public void makeSolverConfigLoader(final String id, final String name,
			final String solverId, final String args, final String smtlib) {
		solverConfigLoader = new SolverConfigLoader(new SolverConfigElt(id,
				name, solverId, args, smtlib));
	}

	public static class SolverConfigElt implements IConfigurationElement {
		static final IConfigurationElement[] NO_ELEMENT = new IConfigurationElement[] {};

		String bundleName = null;
		String id = null;
		String name = null;
		String solverId = null;
		String args = null;
		String smtlib = null;

		public SolverConfigElt(final String bundle, final String id,
				final String name, final String solverId, final String args,
				final String smtlib) {
			this.bundleName = bundle;
			this.id = id;
			this.name = name;
			this.solverId = solverId;
			this.args = args;
			this.smtlib = smtlib;
		}

		public SolverConfigElt(final String id, final String name,
				final String solverId, final String args, final String smtlib) {
			this("org.eventb.smt.core.tests", id, name, solverId, args, smtlib);
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
			} else if (attributeName.equals("solverid")) {
				return solverId;
			} else if (attributeName.equals("args")) {
				return args;
			} else if (attributeName.equals("smt-lib")) {
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
		makeSolverConfigLoader(null, "", "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void dotInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("bundled.solver", "", "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void whitespaceInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("bundled solver", "", "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void colonInID() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("bundled:solver", "", "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void nullName() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("nullNameTest", null, "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void emptyName() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("emptyNameTest", "", "", "", "");

		solverConfigLoader.load();
	}

	@Test(expected = ExtensionLoadingException.class)
	public void nullArgs() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("nullArgsTest", "nullArgsTest", "solver", null,
				"");

		solverConfigLoader.load();
	}

	@Test
	public void nullSmtLibVersion() throws InvalidRegistryObjectException,
			ExtensionLoadingException {
		makeSolverConfigLoader("nullSMTLIBVersion", "nullSMTLIBVersion",
				"solver", "", null);

		final SolverConfiguration config = solverConfigLoader.load();
		assertTrue(config.getSmtlibVersion().equals(LATEST));
	}
}
