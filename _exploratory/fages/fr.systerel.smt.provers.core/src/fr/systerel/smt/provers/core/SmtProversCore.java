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
import org.osgi.framework.BundleContext;

import br.ufrn.smt.solver.translation.Translator;

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

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging()) {
			configureDebugOptions();
		}
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		Translator.DEBUG = parseOption(DEBUG_TRANSLATOR);
	}

	private static boolean parseOption(final String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
