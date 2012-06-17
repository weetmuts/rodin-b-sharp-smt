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

import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;
import static org.junit.Assert.assertEquals;

import org.eventb.smt.core.internal.preferences.AbstractLoader.LoadingException;
import org.eventb.smt.core.internal.preferences.solvers.BundledSolverLoader;
import org.eventb.smt.core.provers.SolverKind;
import org.junit.Test;

/**
 * Unit tests for parsing a configuration element contributing to the "solver"
 * extension point.
 * 
 * @author Yoann Guyot
 */
public class BundledSolverLoaderTests {

	private final FakeConfigurationElement ce = new FakeConfigurationElement();
	private final BundledSolverLoader loader = new BundledSolverLoader(ce);

	@Test(expected = LoadingException.class)
	public void missingID() {
		loader.getId();
	}

	@Test(expected = LoadingException.class)
	public void dotInID() {
		ce.add("id", "bundled.solver");
		loader.getId();
	}

	@Test(expected = LoadingException.class)
	public void whitespaceInID() {
		ce.add("id", "bundled solver");
		loader.getId();
	}

	@Test(expected = LoadingException.class)
	public void colonInID() {
		ce.add("id", "bundled:solver");
		loader.getId();
	}

	@Test(expected = LoadingException.class)
	public void missingName() {
		ce.add("id", "foo");
		loader.getName();
	}

	@Test(expected = LoadingException.class)
	public void emptyName() {
		ce.add("id", "foo");
		ce.add("name", "");
		loader.getName();
	}

	@Test
	public void validName() {
		final String name = "bar";
		ce.add("name", name);
		assertEquals(name, loader.getName());
	}

	@Test
	public void missingKind() {
		assertEquals(UNKNOWN, loader.getKind());
	}

	@Test
	public void validKind() {
		ce.add("kind", "verit");
		assertEquals(SolverKind.VERIT, loader.getKind());
	}

	@Test(expected = LoadingException.class)
	public void missingPath() {
		ce.add("id", "foo");
		ce.add("name", "foo");
		loader.getPath();
	}

	@Test(expected = LoadingException.class)
	public void invalidPath() {
		ce.add("id", "foo");
		ce.add("name", "foo");
		ce.add("localpath", "path/to/invalid/file");
		loader.getPath();
	}

	// Valid path is tested with the bundled veriT solver

}
