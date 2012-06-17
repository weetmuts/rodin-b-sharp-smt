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

import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;
import static org.junit.Assert.assertEquals;

import org.eventb.smt.core.internal.preferences.AbstractLoader.LoadingException;
import org.eventb.smt.core.internal.preferences.configurations.BundledConfigLoader;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.junit.Test;

/**
 * Unit tests for parsing a configuration element contributing to the "config"
 * extension point.
 *
 * @author Yoann Guyot
 */
public class BundledConfigLoaderTests {

	private final FakeConfigurationElement ce = new FakeConfigurationElement();
	private final BundledConfigLoader loader = new BundledConfigLoader(ce);

	// Name already tested with solvers.

	@Test(expected = LoadingException.class)
	public void missingSolverName() {
		loader.getSolverName();
	}

	@Test
	public void validSolverName() {
		final String value = "some solver name";
		ce.add("solverName", value);
		assertEquals(value, loader.getSolverName());
	}

	@Test(expected = LoadingException.class)
	public void missingArgs() {
		loader.getArgs();
	}

	@Test
	public void validArgs() {
		final String value = "some arguments";
		ce.add("args", value);
		assertEquals(value, loader.getArgs());
	}

	public void missingApproach() {
		assertEquals(USING_PP, loader.getTranslationApproach());
	}

	@Test
	public void validApproach() {
		ce.add("translator", "veriT");
		assertEquals(USING_VERIT, loader.getTranslationApproach());
	}

	public void missingVersion() {
		loader.getVersion();
	}

	@Test
	public void validVersion() {
		ce.add("smt-lib", "V1.2");
		assertEquals(SMTLIBVersion.V1_2, loader.getVersion());
	}

}
