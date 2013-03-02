/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.realprojects;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.smt.core.translation.TranslationApproach;
import org.rodinp.core.RodinDBException;

/**
 * @author Systerel (yguyot)
 * 
 */
public class RealProjectTests extends BuilderTest {
	private final static boolean DEBUG = false;
	private final static boolean LAUNCH_ATELIERB_PROVERS = false;
	private final static boolean LAUNCH_SMT_SOLVERS = true;
	private final static boolean LAUNCH_PP = false;
	private final static boolean LAUNCH_VT = true;

	private final static String VERIT_CONFIG_ID = "veriT-dev-r2863";
	private final static String EPROVER_CONFIG_ID = "veriT+e-prover";
	private final static String CVC3_CONFIG_ID = "cvc3-2011-11-21";
	private final static String ALTERGO_CONFIG_ID = "alt-ergo-r217";
	private final static String Z3_CONFIG_ID = "z3-3.2";
	private final static String VERIT_SMT1_CONFIG_ID = "veriT-dev-r2863-SMT1";
	private final static String EPROVER_SMT1_CONFIG_ID = "veriT+e-prover-SMT1";
	private final static String CVC3_SMT1_CONFIG_ID = "cvc3-2011-11-21-SMT1";
	private final static String ALTERGO_SMT1_CONFIG_ID = "alt-ergo-r217-SMT1";
	private final static String Z3_SMT1_CONFIG_ID = "z3-3.2-SMT1";

	private final void initBuilder(final String project) throws Exception {
		rodinProject = createRodinProject(project);
		importProject(project);
		enableAutoProver();
	}

	private final void setSMTTactic(final TranslationApproach approach,
			final String solverConfigId) {
		final IParameterizerDescriptor tacticDescriptor;
		if (approach == USING_PP) {
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

	private final void setAtelierBTactic() {
		final ITacticDescriptor tacticDescriptor = EventBPlugin
				.getAutoPostTacticManager().getAutoTacticPreference()
				.getDefaultDescriptor();
		EventBPlugin.getAutoPostTacticManager().getAutoTacticPreference()
				.setSelectedDescriptor(tacticDescriptor);
	}

	private void count(final String runKey, final String dischargedKey)
			throws RodinDBException {
		results.put(runKey, 0);
		results.put(dischargedKey, 0);
		for (final IPSRoot rootElement : rodinProject
				.getRootElementsOfType(IPSRoot.ELEMENT_TYPE)) {
			if (DEBUG) {
				System.out.println("Component: "
						+ rootElement.getComponentName());
			}
			for (final IPSStatus status : rootElement.getStatuses()) {
				if (DEBUG) {
					System.out.print(status.getElementName() + ": ");
				}
				results.put(runKey, results.get(runKey) + 1);
				if (!status.isBroken() && status.getConfidence() == 1000) {
					if (DEBUG) {
						System.out.println("discharged");
					}
					results.put(dischargedKey, results.get(dischargedKey) + 1);
				} else {
					if (DEBUG) {
						System.out.println("undischarged");
					}
				}
			}
		}
	}

	private final void doSMTTest(final TranslationApproach approach,
			final String solverConfigId) throws Exception {
		setSMTTactic(approach, solverConfigId);
		System.out
				.println("SMT (approach " + approach.toString() + ") "
						+ solverConfigId + " on "
						+ rodinProject.getProject().getName());
		runBuilder();

		final String runKey = rodinProject.getElementName() + "-"
				+ approach.toString() + "-" + solverConfigId + "-run";
		final String dischargedKey = rodinProject.getElementName() + "-"
				+ approach.toString() + "-" + solverConfigId + "-discharged";
		count(runKey, dischargedKey);

		archiveFiles(approach.toString(), solverConfigId);
		rodinProject.getProject().refreshLocal(DEPTH_INFINITE, null);
	}

	private final void doAtelierBTest() throws Exception {
		setAtelierBTactic();
		System.out.println("Atelier-B on "
				+ rodinProject.getProject().getName());
		runBuilder();

		final String runKey = rodinProject.getElementName() + "-AtBProvers"
				+ "-run";
		final String dischargedKey = rodinProject.getElementName()
				+ "-AtBProvers" + "-discharged";
		count(runKey, dischargedKey);

		archiveFiles("AtBProvers");
		rodinProject.getProject().refreshLocal(DEPTH_INFINITE, null);
	}

	private final void doTestProject(final String project) throws Exception {
		initBuilder(project);
		if (LAUNCH_ATELIERB_PROVERS)
			doAtelierBTest();

		if (LAUNCH_SMT_SOLVERS) {
			if (LAUNCH_PP) {
				doSMTTest(USING_PP, VERIT_CONFIG_ID);
				doSMTTest(USING_PP, EPROVER_CONFIG_ID);
				doSMTTest(USING_PP, CVC3_CONFIG_ID);
				doSMTTest(USING_PP, ALTERGO_CONFIG_ID);
				doSMTTest(USING_PP, Z3_CONFIG_ID);
			}
			if (LAUNCH_VT) {
				doSMTTest(USING_VERIT, VERIT_SMT1_CONFIG_ID);
				doSMTTest(USING_VERIT, EPROVER_SMT1_CONFIG_ID);
				doSMTTest(USING_VERIT, CVC3_SMT1_CONFIG_ID);
				doSMTTest(USING_VERIT, ALTERGO_SMT1_CONFIG_ID);
				doSMTTest(USING_VERIT, Z3_SMT1_CONFIG_ID);
			}
		}
	}

	public final void testProjects() throws Exception {
		final URL entry = ResourceUtils.getProjectsURL();
		if (entry == null) {
			// There is no "projects" directory, don't go further
			return;
		}
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory()) {
				final String projectName = project.getName();
				doTestProject(projectName);
			}
		}
	}
}
