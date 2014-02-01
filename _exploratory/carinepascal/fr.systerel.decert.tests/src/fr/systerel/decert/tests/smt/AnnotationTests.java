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

import fr.systerel.decert.smt.Annotation;

public class AnnotationTests extends AbstractSMTTests {

	private final Annotation annotation;

	public AnnotationTests() throws Exception {
		annotation = new Annotation("origin", "routing_new | rm_5 | change_link/inv1/INV");
	}

	@Test
	public void testAttribute() {
		assertEquals("origin", annotation.getAttribute());
	}

	@Test
	public void testValue() throws Exception {
		assertEquals("routing_new | rm_5 | change_link/inv1/INV", annotation.getValue());
	}
}
