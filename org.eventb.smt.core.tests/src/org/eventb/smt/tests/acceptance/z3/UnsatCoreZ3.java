/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.acceptance.z3;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.tests.ConfigProvider.BUNDLED_Z3;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.tests.acceptance.UnsatCoreExtractionWithPP;

public class UnsatCoreZ3 extends UnsatCoreExtractionWithPP {

	public UnsatCoreZ3() {
		super(BUNDLED_Z3, V2_0);
	}

	@Override
	protected List<String> someHypsGoalReqHyps() {
		return Arrays.asList(//
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"m ∈ q", //
				"m = 1");
	}
	
}
