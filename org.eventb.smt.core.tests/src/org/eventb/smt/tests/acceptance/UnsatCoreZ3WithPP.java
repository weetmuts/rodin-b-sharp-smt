/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.core.provers.SolverKind.Z3;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;

public class UnsatCoreZ3WithPP extends UnsatCoreExtractionWithPP {

	public UnsatCoreZ3WithPP() {
		super(Z3, V2_0);
	}

}
