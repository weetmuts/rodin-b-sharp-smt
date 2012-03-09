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
import org.eventb.smt.internal.preferences.BundledSolverDesc;
import org.eventb.smt.internal.preferences.BundledSolverRegistry.BundledSolverLoadingException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverDescTests {

	public static class BundledSolverConfigElt implements IConfigurationElement {
		static final IConfigurationElement[] NO_ELEMENT = new IConfigurationElement[] {};

		final private String bundleName;
		final private String id;
		final private String name;
		final private String kind;
		final private String binary;
		final private String args;
		final private String smtlib;

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
			this("test", id, name, kind, binary, args, smtlib);
		}

		@Override
		public Object createExecutableExtension(String propertyName) {
			return null;
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
			return attrName;
		}

		@Override
		public String[] getAttributeNames() {
			return new String[0];
		}

		@Override
		public IConfigurationElement[] getChildren() {
			return NO_ELEMENT;
		}

		@Override
		public IConfigurationElement[] getChildren(String attrName) {
			return NO_ELEMENT;
		}

		@Override
		public IContributor getContributor() {
			return null;
		}

		@Override
		public IExtension getDeclaringExtension() {
			return null;
		}

		@Override
		public String getName() {
			return "name";
		}

		@Deprecated
		@Override
		public String getNamespace() {
			return bundleName;
		}

		@Override
		public String getNamespaceIdentifier() {
			return bundleName;
		}

		@Override
		public Object getParent() {
			return null;
		}

		@Override
		public String getValue() {
			return "value";
		}

		@Override
		public String getValue(String locale) {
			return getValue();
		}

		@Deprecated
		@Override
		public String getValueAsIs() {
			return "value";
		}

		@Override
		public boolean isValid() {
			return true;
		}
	}

	@Test
	public void testNullIDException() {
		final IConfigurationElement configEltWithNullID = new BundledSolverConfigElt(
				null, "", "", "", "", "");
		final BundledSolverDesc desc = new BundledSolverDesc(
				configEltWithNullID);
		try {
			desc.load();
			Assert.fail("BundledSolverLoadingException expected because of a null ID.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}
	}

	@Test
	public void testDotInIDException() {
		final IConfigurationElement configEltWithDotID = new BundledSolverConfigElt(
				"bundled.solver", "", "", "", "", "");
		final BundledSolverDesc desc = new BundledSolverDesc(configEltWithDotID);
		try {
			desc.load();
			Assert.fail("BundledSolverLoadingException expected because of a dot in the ID.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}
	}

	@Test
	public void testWhitespaceOrColonInIDException() {
		final IConfigurationElement configEltWithWhiteSpaceID = new BundledSolverConfigElt(
				"bundled solver", "", "", "", "", "");
		final BundledSolverDesc desc1 = new BundledSolverDesc(
				configEltWithWhiteSpaceID);
		try {
			desc1.load();
			Assert.fail("BundledSolverLoadingException expected because of a space in the ID.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}

		final IConfigurationElement configEltWithColonID = new BundledSolverConfigElt(
				"bundled:solver", "", "", "", "", "");
		final BundledSolverDesc desc2 = new BundledSolverDesc(
				configEltWithColonID);
		try {
			desc2.load();
			Assert.fail("BundledSolverLoadingException expected because of a colon in the ID.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}
	}

	@Test
	public void testNullBinaryException() {
		final IConfigurationElement configEltWithDotID = new BundledSolverConfigElt(
				"bundledsolver", "", "", null, "", "");
		final BundledSolverDesc desc = new BundledSolverDesc(configEltWithDotID);
		try {
			desc.load();
			Assert.fail("BundledSolverLoadingException expected because of a null binary.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}
	}

	@Test
	public void testInvalidBundleNameException() {
		final IConfigurationElement configEltWithDotID = new BundledSolverConfigElt(
				"bundledsolver", "", "", "bundledsolver", "", "");
		final BundledSolverDesc desc = new BundledSolverDesc(configEltWithDotID);
		try {
			desc.load();
			Assert.fail("BundledSolverLoadingException expected because of an invalid bundleName name.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			// expected exception
		}
	}

	@Test
	public void testUnknownBinaryException() {
		final IConfigurationElement configEltWithDotID = new BundledSolverConfigElt(
				"bundledsolver", "", "", "bundledsolver", "", "");
		final BundledSolverDesc desc = new BundledSolverDesc(configEltWithDotID);
		try {
			desc.load();
			Assert.fail("BundledSolverLoadingException expected because of a null binary.");
		} catch (InvalidRegistryObjectException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		} catch (BundledSolverLoadingException e) {
			Assert.fail("Unexpected exception raised: " + e.getMessage());
		}
	}
}
