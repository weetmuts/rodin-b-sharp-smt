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
package org.eventb.smt.core.perf.app.tactics;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.internal.tactics.DefaultAutoWithSMT;

/**
 * Builds tactics that can be used to run the Event-B sequent prover in
 * automated mode. All built tactics are based on the
 * <code>Default Auto Tactic</code> where some external provers have been
 * inserted. They allow to compare the performances of these external provers in
 * automated mode.
 * 
 * @author Laurent Voisin
 */
public abstract class Tactics {

	/**
	 * Returns a tactic running just provers embedded within the core Rodin
	 * platform.
	 */
	public static ITacticDescriptor bareRodinTactic() {
		return RODIN_AUTO;
	}

	/**
	 * Returns a tactic running the two Atelier B provers.
	 * 
	 * @return an auto-tactic with both Atelier B provers.
	 */
	public static ITacticDescriptor atelierBTactic() {
		return new TacticInserter(RODIN_AUTO, atbBuilder(),
				"org.eventb.core.seqprover.dtDestrWDTac").insert();
	}

	/**
	 * Returns a tactic running the given SMT solver configuration.
	 * 
	 * @param configName
	 *            name of the SMT configuration to run
	 * @return an auto-tactic with the given SMT configuration
	 */
	public static ITacticDescriptor smtTactic(String configName) {
		final TacticBuilder builder = new SMTTacticBuilder(configName);
		return new TacticInserter(RODIN_AUTO, builder,
				"org.eventb.core.seqprover.partitionRewriteTac").insert();
	}

	/**
	 * Returns a tactic running all the given SMT solver configurations.
	 * 
	 * @param configs
	 *            the SMT configurations to run
	 * @return an auto-tactic with all the given SMT configurations
	 */
	public static ITacticDescriptor smtTactic(IConfigDescriptor[] configs) {
		return new TacticInserter(RODIN_AUTO, smtBuilder(configs),
				"org.eventb.core.seqprover.partitionRewriteTac").insert();
	}

	/**
	 * Returns a tactic running all Atelier B provers and the given SMT solver
	 * configurations.
	 * 
	 * @param configs
	 *            the SMT configurations to run
	 * @return an auto-tactic with all Atelier B and the given SMT
	 *         configurations
	 */
	public static ITacticDescriptor atBPlusSMTTactic(IConfigDescriptor[] configs) {
		final ITacticDescriptor withAtB = atelierBTactic();
		return new TacticInserter((ICombinedTacticDescriptor) withAtB,
				smtBuilder(configs),
				"org.eventb.core.seqprover.partitionRewriteTac").insert();
	}

	private static TacticBuilder atbBuilder() {
		return new MultiTacticBuilder("AtelierB", //
				new MLTacticBuilder(), //
				new PPTacticBuilder());
	}

	private static TacticBuilder smtBuilder(IConfigDescriptor[] configs) {
		final int length = configs.length;
		final TacticBuilder[] builders = new TacticBuilder[length];
		for (int i = 0; i < length; i++) {
			builders[i] = new SMTTacticBuilder(configs[i].getName());
		}
		return new MultiTacticBuilder("AllSMT", builders);
	}

	// Auto-tactic registry
	protected static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	// The "Default Auto Tactic"
	private static final ITacticDescriptor DEFAULT_AUTO = DefaultAutoWithSMT
			.getDefaultAuto();

	// The "Default Auto Tactic" with all Atelier B removed
	private static final ICombinedTacticDescriptor RODIN_AUTO = removeAtelierB(
			DEFAULT_AUTO, "Rodin");

	// Prefix of tactic descriptor ids of Atelier B provers
	private static final String ATELIERB_PREFIX = "com.clearsy.atelierb";

	protected static ICombinatorDescriptor getCombinator(
			ICombinedTacticDescriptor desc) {
		final String combinatorId = desc.getCombinatorId();
		return REGISTRY.getCombinatorDescriptor(combinatorId);
	}

	private static ICombinedTacticDescriptor removeAtelierB(
			ITacticDescriptor baseDesc, String id) {
		final ICombinedTacticDescriptor desc = (ICombinedTacticDescriptor) baseDesc;
		final ICombinatorDescriptor combinator = getCombinator(desc);
		final List<ITacticDescriptor> children = desc.getCombinedTactics();
		final List<ITacticDescriptor> tactics = removeAtelierB(children);
		return combinator.combine(tactics, id);
	}

	private static List<ITacticDescriptor> removeAtelierB(
			List<ITacticDescriptor> descs) {
		final List<ITacticDescriptor> tactics = new ArrayList<ITacticDescriptor>();
		for (final ITacticDescriptor desc : descs) {
			if (!desc.getTacticID().startsWith(ATELIERB_PREFIX)) {
				tactics.add(desc);
			}
		}
		return tactics;
	}

}
