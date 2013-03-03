/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.tactics;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilSuccess;
import static org.eventb.smt.core.internal.tactics.SMTTacticDescriptors.getTacticDescriptor;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;

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

	private static volatile ITactic[] tactics;

	/*
	 * Force loading of preferences at class creation. The initialization of the
	 * preferences class will call back updateTactics.
	 */
	static {
		ConfigPreferences.init();
	}

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		return composeUntilSuccess(tactics).apply(ptNode, pm);
	}

	/*
	 * Must be called whenever the list of enabled SMT configurations changes.
	 */
	public static void updateTactics(List<IConfigDescriptor> configs) {
		final List<ITactic> newTactics = new ArrayList<ITactic>(configs.size());
		for (final IConfigDescriptor config : configs) {
			if (config.isEnabled()) {
				final ITactic tactic = makeConfigTactic(config);
				newTactics.add(tactic);
			}
		}
		tactics = newTactics.toArray(new ITactic[newTactics.size()]);
	}

	/*
	 * Returns a tactic for running the given SMT solver with default parameters
	 * (restricted and timeout).
	 */
	private static ITactic makeConfigTactic(IConfigDescriptor config) {
		final String configName = config.getName();
		final ITacticDescriptor tacticDesc = getTacticDescriptor(configName);
		return tacticDesc.getTacticInstance();
	}

}
