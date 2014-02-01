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

import fr.systerel.decert.smt.Sort;

public class SortTests {

	@Test
	public void testName() {
		assertEquals("Int", Sort.INT.getName());
		assertEquals(Sort.INT, Sort.fromName("Int"));
	}

}
