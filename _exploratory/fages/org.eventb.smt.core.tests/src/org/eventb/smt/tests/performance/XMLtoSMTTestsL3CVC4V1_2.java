/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.performance;

import static org.eventb.smt.provers.internal.core.SMTSolver.CVC4;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.utils.Theory.TheoryLevel.L3;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.utils.LemmaData;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XMLtoSMTTestsL3CVC4V1_2 extends XMLtoSMTTests {

	public XMLtoSMTTestsL3CVC4V1_2(final LemmaData data) {
		super(data, CVC4, V1_2);
	}

	@Parameters
	public static List<LemmaData[]> getDocumentDatas() {
		return getDocumentDatas(Arrays.asList(L3));
	}
}
