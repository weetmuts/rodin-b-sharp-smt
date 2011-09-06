/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.core.tests;

import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.internal.core.SMTSolver;

public class XMLtoSMTTestsAltErgo extends XMLtoSMTTests {

	public XMLtoSMTTestsAltErgo(final LemmaData data) {
		super(data, SMTSolver.ALT_ERGO);
	}

}
