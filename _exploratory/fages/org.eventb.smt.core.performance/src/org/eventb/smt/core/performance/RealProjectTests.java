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

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;

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

	private final void doTestProject(final String project,
			final String solverConfigId) throws Exception {
		importProject(project);
		enableAutoProver();
		EventBPlugin.getAutoPostTacticManager().getAutoTacticPreference()
				.setSelectedDescriptor(makeSMTPPTactic(solverConfigId));

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
	}

	public final void testQuickWithVerit() throws Exception {
		doTestProject("Quick", VERIT_CONFIG_ID);
	}

	public final void testQuickWithEProver() throws Exception {
		doTestProject("Quick", EPROVER_CONFIG_ID);
	}

	public final void testQuickWithCvc3() throws Exception {
		doTestProject("Quick", CVC3_CONFIG_ID);
	}

	public final void testQuickWithAltErgo() throws Exception {
		doTestProject("Quick", ALTERGO_CONFIG_ID);
	}

	public final void testQuickWithZ3() throws Exception {
		doTestProject("Quick", Z3_CONFIG_ID);
	}
}
