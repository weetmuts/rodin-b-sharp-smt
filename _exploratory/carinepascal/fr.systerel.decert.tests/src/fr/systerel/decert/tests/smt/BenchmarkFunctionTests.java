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

package fr.systerel.decert.tests.smt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.systerel.decert.smt.BenchmarkFunction;
import fr.systerel.decert.smt.Sort;

public class BenchmarkFunctionTests extends AbstractSMTTests {

	private final BenchmarkFunction func1;
	private final BenchmarkFunction func2;

	public BenchmarkFunctionTests() throws Exception {
		func1 = new BenchmarkFunction("n", ff.makeIntegerType());
		func2 = new BenchmarkFunction("b", ff.makeBooleanType());
	}

	@Test
	public void testName() {
		assertEquals("n", func1.getName());
		assertEquals("b", func2.getName());
	}

	@Test
	public void testSignature() throws Exception {
		assertElements(func1.getSignature(),Sort.fromName("Int"));
		assertElements(func2.getSignature(),Sort.fromName("Bool"));
	}
}
