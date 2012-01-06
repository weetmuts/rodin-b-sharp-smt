/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance;

import static org.eventb.smt.translation.SMTTranslationApproach.USING_PP;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.smt.translation.SMTTranslationApproach;

/**
 * @author Systerel (yguyot)
 * 
 */
public class RealProjectTests extends BuilderTest {
	private final static String VERIT_CONFIG_ID = "veriT-dev-r2863";
	private final static String EPROVER_CONFIG_ID = "veriT+e-prover";
	private final static String CVC3_CONFIG_ID = "cvc3-2011-11-21";
	private final static String ALTERGO_CONFIG_ID = "alt-ergo-r217";
	private final static String Z3_CONFIG_ID = "z3-3.2";

	private final void initBuilder(final String project) throws Exception {
		rodinProject = createRodinProject(project);
		importProject(project);
		enableAutoProver();
	}

	private final void setTactic(final SMTTranslationApproach approach,
			final String solverConfigId) {
		final IParameterizerDescriptor tacticDescriptor;
		if (approach.equals(SMTTranslationApproach.USING_PP)) {
			tacticDescriptor = smtPpParamTacticDescriptor;
		} else {
			tacticDescriptor = smtVeritParamTacticDescriptor;
		}
		EventBPlugin
				.getAutoPostTacticManager()
				.getAutoTacticPreference()
				.setSelectedDescriptor(
						makeSMTTactic(tacticDescriptor, solverConfigId));
	}

	private final void doTest() throws CoreException {
		System.out.println("Project: " + rodinProject.getProject().getName());
		runBuilder();

		for (final IPSRoot rootElement : rodinProject
				.getRootElementsOfType(IPSRoot.ELEMENT_TYPE)) {
			System.out.println("Component: " + rootElement.getComponentName());
			for (final IPSStatus status : rootElement.getStatuses()) {
				System.out.print(status.getElementName() + ": ");
				if (!status.isBroken() && status.getConfidence() == 1000) {
					System.out.println("discharged");
				} else {
					System.out.println("undischarged");
				}
			}
		}

		archiveFiles();
	}

	private final void doTestProject(final String project) throws Exception {
		initBuilder(project);
		setTactic(USING_PP, VERIT_CONFIG_ID);
		doTest();
		setTactic(USING_PP, EPROVER_CONFIG_ID);
		doTest();
		setTactic(USING_PP, CVC3_CONFIG_ID);
		doTest();
		setTactic(USING_PP, ALTERGO_CONFIG_ID);
		doTest();
		setTactic(USING_PP, Z3_CONFIG_ID);
	}

	public final void testQuick() throws Exception {
		doTestProject("Quick");
	}
}
