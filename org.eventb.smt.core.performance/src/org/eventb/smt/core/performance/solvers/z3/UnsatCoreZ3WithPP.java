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
package org.eventb.smt.core.performance.solvers.z3;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.tests.ConfigProvider.LAST_Z3;

import org.eventb.smt.tests.acceptance.UnsatCoreExtractionWithPP;
import org.junit.Ignore;

@Ignore("Cannot run acceptance tests with non-bundled solver")
public class UnsatCoreZ3WithPP extends UnsatCoreExtractionWithPP {

	public UnsatCoreZ3WithPP() {
		super(LAST_Z3, V2_0);
	}

}
