/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.tests;

import static org.eventb.smt.ui.internal.preferences.configurations.StringListSerializer.ESCAPE;
import static org.eventb.smt.ui.internal.preferences.configurations.StringListSerializer.SEPARATOR;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.ui.internal.preferences.configurations.StringListSerializer;
import org.junit.Test;

public class StringListSerializerTests {

	/**
	 * Ensures that an empty list can be serialized.
	 */
	@Test
	public void emptyList() {
		assertSerialization();
	}

	/**
	 * Ensures that a one element list can be serialized.
	 */
	@Test
	public void oneElement() {
		assertSerialization("foo");
	}

	/**
	 * Ensures that a list of two elements can be serialized.
	 */
	@Test
	public void twoElements() {
		assertSerialization("foo", "bar");
	}

	/**
	 * Ensures that a list of three elements can be serialized.
	 */
	@Test
	public void threeElements() {
		assertSerialization("foo", "bar", "baz");
	}

	/**
	 * Ensures that empty strings can be serialized (whatever its position).
	 */
	@Test
	public void emptyString() {
		assertSerialization("", "foo", "", "bar", "");
	}

	/**
	 * Ensures that the separator character occurring in a string can be
	 * serialized (whatever its position).
	 */
	@Test
	public void separator() {
		assertSpecialChar(SEPARATOR);
	}

	/**
	 * Ensures that the escape character occurring in a string can be serialized
	 * (whatever its position).
	 */
	@Test
	public void escape() {
		assertSpecialChar(ESCAPE);
	}

	/**
	 * Checks that strings containing a special character are properly
	 * serialized.
	 *
	 * @param special
	 *            some character
	 */
	private static void assertSpecialChar(final char special) {
		final String single = Character.toString(special);
		// Character alone
		assertSerialization(single);
		assertSerialization(single, "foo", single, "bar", single);
		// Middle of a word
		assertSerialization("foo" + special + "bar");
		// Doubled
		assertSerialization("foo" + special + special + "bar");
	}

	/**
	 * Checks that de-serializing a serialized string returns the original list.
	 */
	private static void assertSerialization(String... inputs) {
		final List<String> list = Arrays.asList(inputs);
		final String serialized = StringListSerializer.serialize(list);
		assertEquals(list, StringListSerializer.deserialize(serialized));
	}

}
