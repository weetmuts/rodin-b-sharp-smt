/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.performance.xml;

import static org.eventb.smt.internal.provers.core.SMTSolver.MATHSAT5;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.utils.Theory.TheoryLevel.L3;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.utils.LemmaData;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XMLtoSMTTestsL3MathSat5V1_2 extends XMLtoSMTTests {

	public XMLtoSMTTestsL3MathSat5V1_2(final LemmaData data) {
		super(data, MATHSAT5, V1_2);
	}

	@Parameters
	public static List<LemmaData[]> getDocumentDatas() {
		return getDocumentDatas(Arrays.asList(L3));
	}
}
