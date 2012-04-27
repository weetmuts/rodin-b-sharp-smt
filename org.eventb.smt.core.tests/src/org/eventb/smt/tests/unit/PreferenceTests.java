/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.unit;

import static junit.framework.Assert.assertEquals;
import static org.eventb.smt.core.internal.preferences.AbstractPreferences.IDS_UPPER_BOUND;
import static org.eventb.smt.core.internal.preferences.solvers.SMTSolversPreferences.getSMTSolversPrefs;
import static org.eventb.smt.core.preferences.PreferenceManager.freshSolverID;
import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.internal.preferences.solvers.SMTSolver;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Systerel (yguyot)
 * 
 */
public class PreferenceTests {

	final ISMTSolversPreferences solversPrefs = getSMTSolversPrefs(false);
	final IPath ALT_ERGO_PATH = new Path("/home/guyot/bin/alt-ergo");

	public void addSolver(final String id, final int nameSuffixe) {
		solversPrefs.add(new SMTSolver(id, "test" + nameSuffixe, UNKNOWN,
				ALT_ERGO_PATH));
	}

	@Test
	public void firstID() {
		final String freshID = freshSolverID();
		assertTrue("Wrong ID: " + freshID + ", expected 0.",
				freshID.equals("0"));
	}

	@Test
	@Ignore("too long")
	public void freshID() {
		String freshID = "";
		int i;
		// We use IDS_UPPER_BOUND - 2 because of the two bundled solvers
		for (i = 0; i < IDS_UPPER_BOUND - 2; i++) {
			freshID = freshSolverID();
			assertFalse("null ID", freshID == null);
			final ISMTSolver solver = new SMTSolver(freshID, "test" + i,
					UNKNOWN, ALT_ERGO_PATH);
			solversPrefs.add(solver);
		}

		freshID = freshSolverID();
		assertTrue("ID not null: " + freshID, freshID == null);

		solversPrefs.remove("16765");
		solversPrefs.remove("16766");
		solversPrefs.remove("16767");
		solversPrefs.remove("7");

		freshID = freshSolverID();
		assertEquals("99998", freshID);
		addSolver(freshID, i++);

		freshID = freshSolverID();
		assertEquals("99999", freshID);
		addSolver(freshID, i++);

		freshID = freshSolverID();
		assertEquals("7", freshID);
	}
}
