/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.prefs;

import static java.util.Arrays.asList;
import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.eventb.smt.core.SMTCore.newSolverDescriptor;
import static org.eventb.smt.core.SolverKind.VERIT;
import static org.eventb.smt.core.SolverKind.Z3;
import static org.eventb.smt.core.internal.prefs.ConfigPreferences.getBundledConfigs;
import static org.eventb.smt.core.internal.prefs.SolverPreferences.getBundledSolvers;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.IDescriptor;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.SolverKind;
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
		final IConfigDescriptor desc = newConfigDescriptor(name, solverName, args, true);
		assertSame(name, desc.getName());
		assertFalse(desc.isBundled());
		assertSame(solverName, desc.getSolverName());
		assertSame(args, desc.getArgs());
		assertTrue(desc.isEnabled());
	}

	/**
	 * Ensures that bundled solver descriptors say they are bundled and are
	 * parts of the known solvers.
	 */
	@Test
	public void bundledSolverDescriptor() {
		final ISolverDescriptor[] bundled = getBundledSolvers();
		assertAreBundled(bundled);
		assertContainsAll(SMTCore.getSolvers(), bundled);
	}

	/**
	 * Ensures that bundled configuration descriptors say they are bundled.
	 */
	@Test
	public void bundledConfigDescriptor() {
		final IConfigDescriptor[] bundled = getBundledConfigs();
		assertAreBundled(bundled);
		assertContainsAll(SMTCore.getConfigurations(), bundled);
	}

	private void assertAreBundled(IDescriptor[] descs) {
		for (IDescriptor desc : descs) {
			assertTrue(desc.isBundled());
		}
	}

	private void assertContainsAll(IDescriptor[] list, IDescriptor... subList) {
		assertTrue(asList(list).containsAll(asList(subList)));

	}

	/**
	 * Ensures that bundled solvers cannot be removed from the list of known
	 * solvers.
	 */
	@Test
	public void bundledSolverCannotBeRemoved() {
		SMTCore.setSolvers(new ISolverDescriptor[0]);
		assertContainsAll(SMTCore.getSolvers(), getBundledSolvers());
	}

	/**
	 * Ensures that bundled configurations cannot be removed from the list of
	 * known configurations.
	 */
	@Test
	public void bundledConfigCannotBeRemoved() {
		SMTCore.setConfigurations(new IConfigDescriptor[0]);
		assertContainsAll(SMTCore.getConfigurations(), getBundledConfigs());
	}

	/**
	 * Ensures that a bundled solver cannot be changed. To check this, we create
	 * a fake solver with the same name as a bundled solver, and we verify that
	 * its descriptor does not change.
	 */
	@Test
	public void bundledSolverCannotChange() {
		final ISolverDescriptor[] solvers = SMTCore.getSolvers();
		int index = findBundledVeriTSolver(solvers);
		final ISolverDescriptor bundled = solvers[index];
		final ISolverDescriptor fake = newSolverDescriptor(bundled.getName(),
				Z3, bundled.getPath());
		solvers[index] = fake;
		SMTCore.setSolvers(solvers);
		assertContainsAll(SMTCore.getSolvers(), bundled);
	}

	private int findBundledVeriTSolver(ISolverDescriptor[] solvers) {
		for (int i = 0; i < solvers.length; i++) {
			final ISolverDescriptor desc = solvers[i];
			if (desc.isBundled() && desc.getKind() == VERIT) {
				return i;
			}
		}
		fail("Can't find bundled veriT solver");
		return -1;
	}

}
