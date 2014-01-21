/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static org.eventb.smt.core.SMTCore.getConfigurations;
import static org.eventb.smt.core.SMTCore.getTacticDescriptor;
import static org.eventb.smt.ui.internal.Messages.TacticUI_AllSMT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.ui.prover.IUIDynTactic;
import org.eventb.ui.prover.IUIDynTacticProvider;

/**
 * Dynamic tactic provider for SMT configurations in the Proof Control View.
 * 
 * @author beauger
 */
public class SMTUIDynTacticProvider implements IUIDynTacticProvider {

	private static final String ALL_SMT_AUTO_TACTIC = "org.eventb.smt.core.autoTactic"; //$NON-NLS-1$

	private static class SMTUIDynTactic implements IUIDynTactic {

		private final String name;
		private final ITacticDescriptor desc;

		public SMTUIDynTactic(String name, ITacticDescriptor desc) {
			this.name = name;
			this.desc = desc;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public ITacticDescriptor getTacticDescriptor() {
			return desc;
		}

	}

	@Override
	public Collection<IUIDynTactic> getDynTactics(IProofTreeNode ptNode,
			IPOSequent poSequent) {
		final List<IUIDynTactic> dynTactics = new ArrayList<IUIDynTactic>();
		boolean addAutoTactic = false;
		for (IConfigDescriptor config : getConfigurations()) {
			final String configName = config.getName();
			final ITacticDescriptor desc = getTacticDescriptor(configName);
			dynTactics.add(new SMTUIDynTactic(configName, desc));
			if (config.isEnabled()) {
				addAutoTactic = true;
			}
		}

		if (addAutoTactic) {
			// there is at least one enabled configuration
			final ITacticDescriptor allEnabled = SequentProver
					.getAutoTacticRegistry().getTacticDescriptor(
							ALL_SMT_AUTO_TACTIC);
			dynTactics.add(0, new SMTUIDynTactic(TacticUI_AllSMT, allEnabled));
		}
		return dynTactics;
	}

}
