/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *******************************************************************************/
package fr.systerel.smt.provers.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.osgi.framework.BundleContext;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.Translator;
import fr.systerel.smt.provers.internal.core.ExternalSMTThroughPP;
import fr.systerel.smt.provers.internal.core.ExternalSMTThroughVeriT;

/**
 * The main plugin class for the Smt provers.
 */
public class SmtProversCore extends Plugin {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "fr.systerel.smt.provers.core";

	private static final String DEBUG = PLUGIN_ID + "/debug/";
	private static final String DEBUG_TRANSLATOR = DEBUG + "translator";

	/**
	 * Default delay for time-out of the Smt provers (value 30 seconds).
	 */
	public static long DEFAULT_DELAY = 3 * 1000;
	public static long NO_DELAY = 0;

	// The shared instance.
	private static SmtProversCore plugin;

	/**
	 * Returns the single instance of the Smt Provers for Rodin core plug-in.
	 * 
	 * @return the single instance of the Smt Provers for Rodin core plug-in
	 */
	public static SmtProversCore getDefault() {
		return plugin;
	}

	private static boolean parseOption(final String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		Translator.DEBUG = parseOption(DEBUG_TRANSLATOR);
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	/**
	 * Returns a tactic for applying the Smt prover to a proof tree node.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * externalSMT(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param smtPreferences
	 * 
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughPP(
			final SMTPreferences smtPreferences, final boolean restricted) {
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughPP(smtPreferences), //
				new XProverInput(restricted, DEFAULT_DELAY));
	}

	public static ITactic externalSMTThroughVeriT(
			final SMTPreferences smtPreferences, final boolean restricted) {
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughVeriT(smtPreferences), //
				new XProverInput(restricted, DEFAULT_DELAY));
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging()) {
			configureDebugOptions();
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
