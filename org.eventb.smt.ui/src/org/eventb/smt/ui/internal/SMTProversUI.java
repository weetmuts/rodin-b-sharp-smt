/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal;

import static org.eventb.smt.ui.internal.UIUtils.logError;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.smt.ui.internal.provers.TacticProfileContribution;
import org.osgi.framework.BundleContext;

/**
 * This activator class controls the UI plug-in life cycle.
 */
public class SMTProversUI extends AbstractUIPlugin {

	/**
	 * the shared instance
	 */
	private static SMTProversUI plugin;

	/**
	 * This plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.ui";

	public SMTProversUI() {
		// Do nothing
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		try {
			new TacticProfileContribution().contribute();
		} catch (Exception e) {
			logError("Error when installing the default auto + SMT profile", e);
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static SMTProversUI getDefault() {
		return plugin;
	}

}
