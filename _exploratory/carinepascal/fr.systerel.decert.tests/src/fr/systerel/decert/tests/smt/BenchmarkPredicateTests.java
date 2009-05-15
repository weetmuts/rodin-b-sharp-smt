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

import fr.systerel.decert.Variable;
import fr.systerel.decert.smt.BenchmarkPredicate;

public class BenchmarkPredicateTests extends AbstractSMTTests {

	private final BenchmarkPredicate pred;

	public BenchmarkPredicateTests() throws Exception {
		pred = new BenchmarkPredicate(new Variable(ff, "n", "ℤ"));
	}

	@Test
	public void testName() {
		assertEquals("n", pred.getName());
	}

	@Test
	public void testSignature() throws Exception {
		assertElements(pred.getSignature());
	}

}
