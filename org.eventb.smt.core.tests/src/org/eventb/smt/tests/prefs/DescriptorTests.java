/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.prefs;

import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.eventb.smt.core.SMTCore.newSolverDescriptor;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;
import org.eventb.smt.core.internal.prefs.SolverPreferences;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.prefs.IDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.junit.Test;

/**
 * Unit tests for configuration and solver descriptors, both bundled and
 * user-contributed.
 * 
 * @author Laurent Voisin
 */
public class DescriptorTests {

	/**
	 * Ensures that one can create a user-contributed descriptor for a solver.
	 */
	@Test
	public void userSolverDescriptor() {
		final String name = "foo";
		final SolverKind kind = VERIT;
		final IPath path = new Path("/path/to/file");
		final ISolverDescriptor desc = newSolverDescriptor(name, kind, path);
		assertSame(name, desc.getName());
		assertFalse(desc.isBundled());
		assertSame(kind, desc.getKind());
		assertSame(path, desc.getPath());
	}

	/**
	 * Ensures that one can create a user-contributed descriptor for a
	 * configuration.
	 */
	@Test
	public void userConfigDescriptor() {
		final String name = "foo";
		final String solverName = "bar";
		final String args = "-some -args";
		final TranslationApproach approach = USING_PP;
		final SMTLIBVersion version = V2_0;
		final IConfigDescriptor desc = newConfigDescriptor(name, solverName,
				args, approach, version);
		assertSame(name, desc.getName());
		assertFalse(desc.isBundled());
		assertSame(solverName, desc.getSolverName());
		assertSame(args, desc.getArgs());
		assertSame(approach, desc.getTranslationApproach());
		assertSame(version, desc.getSmtlibVersion());
	}

	/**
	 * Ensures that bundled solver descriptors say they are bundled.
	 */
	@Test
	public void bundledSolverDescriptor() {
		assertAreBundled(SolverPreferences.getBundledSolvers());
	}

	/**
	 * Ensures that bundled configuration descriptors say they are bundled.
	 */
	@Test
	public void bundledConfigDescriptor() {
		assertAreBundled(ConfigPreferences.getBundledConfigs());
	}

	private void assertAreBundled(IDescriptor[] descs) {
		for (IDescriptor desc : descs) {
			assertTrue(desc.isBundled());
		}
	}
}
