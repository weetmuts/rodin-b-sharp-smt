/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.perf.app;

import static org.eventb.smt.core.perf.app.tactics.Tactics.atBPlusSMTTactic;
import static org.eventb.smt.core.perf.app.tactics.Tactics.atelierBTactic;
import static org.eventb.smt.core.perf.app.tactics.Tactics.bareRodinTactic;
import static org.eventb.smt.core.perf.app.tactics.Tactics.smtTactic;

import java.util.ArrayList;
import java.util.List;

import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.SMTCore;
import org.rodinp.core.IRodinProject;

/**
 * Performance test for the Atelier B provers and SMT solvers based on full
 * Event-B projects.
 * 
 * @author Yoann Guyot
 * @author Laurent Voisin
 */
public class TestRunner {

	private final IRodinProject[] projects;

	private final List<TacticTest> tests = new ArrayList<TacticTest>();

	public TestRunner(IRodinProject[] projects) {
		this.projects = projects;
		setTestCases();
	}

	// Sets the list of tactics to run
	private void setTestCases() {
		tests.add(new TacticTest("Rodin", bareRodinTactic()));
		tests.add(new TacticTest("AtelierB", atelierBTactic()));

		// Add all known SMT configurations
		final IConfigDescriptor[] configs = getEnabledConfigurations();
		for (final IConfigDescriptor config : configs) {
			final String name = config.getName();
			tests.add(new TacticTest(name, smtTactic(name)));
		}

		tests.add(new TacticTest("AllSMT", smtTactic(configs)));
		tests.add(new TacticTest("AtB+AllSMT", atBPlusSMTTactic(configs)));
	}

	private IConfigDescriptor[] getEnabledConfigurations() {
		final IConfigDescriptor[] configs = SMTCore.getConfigurations();
		final List<IConfigDescriptor> result = new ArrayList<IConfigDescriptor>(
				configs.length);
		for (final IConfigDescriptor config : configs) {
			if (config.isEnabled()) {
				result.add(config);
			}
		}
		return result.toArray(new IConfigDescriptor[result.size()]);
	}

	/**
	 * Launch the tests on all Rodin projects contained in the projects
	 * directory.
	 */
	public void testProjects() throws Exception {
		for (final IRodinProject project : projects) {
			testProject(project);
		}
	}

	// Launch all tactic tests on the given project.
	private void testProject(IRodinProject project) throws Exception {
		System.out.println(project.getElementName());
		for (final TacticTest test : tests) {
			test.run(project);
		}
	}

}
