/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.prefs;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.smt.core.internal.prefs.AttributeSerializer;
import org.eventb.smt.core.internal.prefs.PathSerializer;
import org.eventb.smt.core.internal.prefs.SolverKindSerializer;
import org.eventb.smt.core.internal.prefs.StringSerializer;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.tests.SMTCoreTests;
import org.junit.After;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

/**
 * Unit tests for attribute serialization. The test for missing value is done
 * only once, because the code checking this is in the common abstract class.
 * There is no test for illegal value when all values are legal (e.g., strings
 * and paths).
 *
 * @author Laurent Voisin
 */
public class AttributeSerializationTests {

	// Some arbitrary preference node
	private static final Preferences node = InstanceScope.INSTANCE.getNode(
			SMTCoreTests.PLUGIN_ID).node("attributeTests");

	// Some arbitrary preference key
	private static final String KEY = "key";

	private static final StringSerializer STRING = new StringSerializer(KEY);
	private static final SolverKindSerializer KIND = new SolverKindSerializer(
			KEY);
	private static final PathSerializer PATH = new PathSerializer(KEY);

	@After
	public void tearDown() throws Exception {
		node.clear();
	}

	/**
	 * Ensures that a missing preference throws an exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void missing() {
		STRING.load(node);
	}

	/**
	 * Ensures that strings can be stored and retrieved from a preference.
	 */
	@Test
	public void string() {
		assertSerialized(STRING, "");
		assertSerialized(STRING, "foo");
	}

	/**
	 * Ensures that solver kinds can be stored and retrieved from a preference.
	 */
	@Test
	public void solverKind() {
		for (final SolverKind kind : SolverKind.values()) {
			assertSerialized(KIND, kind);
		}
	}

	/**
	 * Ensures that an illegal solver kind throws an exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void solverKindError() {
		node.put(KEY, "erroneous solver kind");
		KIND.load(node);
	}

	/**
	 * Ensures that paths can be stored and retrieved from a preference.
	 */
	@Test
	public void path() {
		assertSerialized(PATH, new Path(""));
		assertSerialized(PATH, new Path("."));
		assertSerialized(PATH, new Path("/foo/bar"));
	}

	private static <T> void assertSerialized(AttributeSerializer<T> serializer,
			T value) {
		serializer.store(node, value);
		final T actual = serializer.load(node);
		assertEquals(value, actual);
	}

}
