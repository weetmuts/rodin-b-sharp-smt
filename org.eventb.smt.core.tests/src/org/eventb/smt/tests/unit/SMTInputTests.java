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
package org.eventb.smt.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.junit.Test;

/**
 * Unit tests for the {@link SMTInput} class.
 * 
 * @author Laurent Voisin
 */
public class SMTInputTests {

	/**
	 * Ensures that a known configuration is accepted by the SMT input. We
	 * suppose that at least one configuration is known (e.g., a bundled
	 * configuration).
	 */
	@Test
	public void knownConfiguration() {
		final String configName = getKnownConfigurationName();
		final SMTInput input = new SMTInput(configName, true, 1000);
		assertSame(configName, input.getConfigName());
		assertFalse(input.hasError());
		assertNull(input.getError());
		assertNotNull(input.getConfiguration());
	}

	/**
	 * Ensures that an unknown configuration is reported as an error by the SMT
	 * input.
	 */
	@Test
	public void unknownConfiguration() {
		final String configName = "inexistent configuration";
		final SMTInput input = new SMTInput(configName, true, 1000);
		assertSame(configName, input.getConfigName());
		assertTrue(input.hasError());
		assertEquals("No such SMT configuration: " + configName,
				input.getError());
		assertNull(input.getConfiguration());
	}

	/**
	 * Ensures that a <code>null</code> configuration name is reported as an
	 * error by the SMT input.
	 */
	@Test
	public void nullConfiguration() {
		final SMTInput input = new SMTInput(null, true, 1000);
		assertNull(input.getConfigName());
		assertTrue(input.hasError());
		assertEquals("Null SMT configuration name", input.getError());
		assertNull(input.getConfiguration());
	}

	private static String getKnownConfigurationName() {
		final IConfigDescriptor[] configs = SMTCore.getConfigurations();
		assertTrue("No bundled SMT configuration !", configs.length != 0);
		final IConfigDescriptor config = configs[0];
		return config.getName();
	}

}
