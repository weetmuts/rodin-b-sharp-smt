/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.tests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.Type;
import org.junit.Test;

import fr.systerel.decert.Variable;

public class VariableTests extends AbstractTests {

	private final Variable variable;
	private final Type type;

	public VariableTests() throws Exception {
		variable = new Variable(ff, "n", "ℤ");
		type = ff.makeIntegerType();
	}

	@Test
	public void testName() {
		assertEquals("n", variable.getName());
	}

	@Test
	public void testType() throws Exception {
		assertEquals(type, variable.getType());
	}
}
