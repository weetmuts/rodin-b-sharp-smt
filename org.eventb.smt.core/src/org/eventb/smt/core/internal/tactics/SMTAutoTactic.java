/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.tactics;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilSuccess;
import static org.eventb.smt.core.internal.tactics.SMTTacticDescriptors.getTacticDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;
import org.eventb.smt.core.internal.prefs.IPreferencesChangeListener;

/**
 * An automated tactic that runs successively all enabled SMT configurations
 * until one discharges the current sequent.
 * <p>
 * This tactic caches the list of enabled solvers, which is refreshed in a
 * thread-safe manner.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class SMTAutoTactic implements ITactic {

	public static boolean DEBUG = false;

	/*
	 * Sub-class for initializing the list of tactics in a lazy manner. This
	 * class is also responsible for maintaining it up to date.
	 */
	private static class TacticsHolder implements IPreferencesChangeListener {

		static {
			if (DEBUG) {
				trace("Loading holder class");
			}
			final TacticsHolder instance = new TacticsHolder();
			ConfigPreferences.addChangeLister(instance);
			instance.preferencesChange();
		}

		static volatile ITactic[] tactics;

		@Override
		public void preferencesChange() {
			final IConfigDescriptor[] configs = SMTCore.getConfigurations();
			if (DEBUG) {
				trace("Updating tactic list with " + Arrays.toString(configs));
			}
			final List<ITactic> newTactics = new ArrayList<ITactic>(
					configs.length);
			for (final IConfigDescriptor config : configs) {
				if (config.isEnabled()) {
					final ITactic tactic = makeConfigTactic(config);
					newTactics.add(tactic);
				}
			}
			tactics = newTactics.toArray(new ITactic[newTactics.size()]);
		}

		/*
		 * Returns a tactic for running the given SMT solver with default
		 * parameters (restricted and timeout).
		 */
		private ITactic makeConfigTactic(IConfigDescriptor config) {
			final String configName = config.getName();
			final ITacticDescriptor tacticDesc = getTacticDescriptor(configName);
			return tacticDesc.getTacticInstance();
		}

	}

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final ITactic[] tactics = TacticsHolder.tactics;
		if (DEBUG) {
			trace("Launching with " + Arrays.toString(tactics));
		}
		return composeUntilSuccess(tactics).apply(ptNode, pm);
	}

	static void trace(String msg) {
		System.out.println("SMTAutoTactic: " + msg);
	}
}
