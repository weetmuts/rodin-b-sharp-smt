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

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;

public class AbstractTests {

	public static final FormulaFactory ff = FormulaFactory.getDefault();

	protected static <T> void assertElements(List<T> list, T...expected) {
		assertEquals(Arrays.asList(expected), list);
	}

}