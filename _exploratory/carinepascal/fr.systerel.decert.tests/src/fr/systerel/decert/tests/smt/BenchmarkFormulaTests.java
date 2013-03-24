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

import fr.systerel.decert.LemmaPredicate;
import fr.systerel.decert.smt.BenchmarkFormula;

public class BenchmarkFormulaTests extends AbstractSMTTests {

	private final BenchmarkFormula f1;
	private final BenchmarkFormula f2;

	public BenchmarkFormulaTests() throws Exception {
		f1 = new BenchmarkFormula(v, new LemmaPredicate(ff, "x = y",
				true));
		f2 = new BenchmarkFormula("(= x y)");
	}

	@Test
	public void testContent() throws Exception {
		assertEquals("(= x y)", f1.getContent());
		assertEquals("(= x y)", f2.getContent());
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("(= x y)", f1.toString());
		assertEquals("(= x y)", f2.toString());
	}
	
	@Test
	public void testEquals() {
		assertEquals(f1, f2);
	}

}
