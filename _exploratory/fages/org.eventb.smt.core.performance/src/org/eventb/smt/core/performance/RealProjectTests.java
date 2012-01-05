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

import static org.eventb.smt.core.performance.ResourceUtils.loopOnAllPending;
import static org.eventb.smt.preferences.SMTPreferences.getSolverConfiguration;

import java.util.Collections;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.smt.provers.core.SMTProversCore;
import org.eventb.smt.provers.ui.SmtProversUIPlugin;
import org.rodinp.core.IRodinFile;

/**
 * @author Systerel (yguyot)
 * 
 */
public class RealProjectTests extends BuilderTest {
	private final static String VERIT2863_CONFIG_ID = "veriT-dev-r2863";
	private final IParameterizerDescriptor smtPpParamTacticDescriptor = SequentProver
			.getAutoTacticRegistry().getParameterizerDescriptor(
					SMTProversCore.PLUGIN_ID + ".SMTPPParam");

	private ITacticDescriptor makeSMTTactic() {
		SmtProversUIPlugin.getDefault();
		final IParameterSetting settings = smtPpParamTacticDescriptor
				.makeParameterSetting();
		settings.setBoolean("restricted", true);
		settings.setLong("timeout", (long) 4500);
		settings.setString("configId",
				getSolverConfiguration(VERIT2863_CONFIG_ID).getId());
		final ITacticDescriptor smtPpTacticDescriptor = smtPpParamTacticDescriptor
				.instantiate(settings, "");
		return loopOnAllPending(
				Collections.singletonList(smtPpTacticDescriptor), "");
	}

	public final void testProjects() throws Exception {
		importProject("Quick");
		enableAutoProver();
		EventBPlugin.getAutoPostTacticManager().getAutoTacticPreference()
				.setSelectedDescriptor(makeSMTTactic());
		runBuilder();

		System.out.println(rodinProject.getRodinFiles().length);
		for (final IRodinFile rodinFile : rodinProject.getRodinFiles()) {
			final String name = rodinFile.getBareName();
			System.out.println(name);
			for (final IPSStatus status : eventBProject.getPSRoot(name)
					.getStatuses()) {
				System.out.println(status);
			}
		}

		System.out.println(rodinProject.getProject().getLocation());
	}
}
