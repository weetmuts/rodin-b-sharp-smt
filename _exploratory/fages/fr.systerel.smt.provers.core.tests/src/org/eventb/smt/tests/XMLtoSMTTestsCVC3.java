/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.utils.LemmaData;


public class XMLtoSMTTestsCVC3 extends XMLtoSMTTests {

	public XMLtoSMTTestsCVC3(final LemmaData data) {
		super(data, SMTSolver.CVC3);
	}

}
