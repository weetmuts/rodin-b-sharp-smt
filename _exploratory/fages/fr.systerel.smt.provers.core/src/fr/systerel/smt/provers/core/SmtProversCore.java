/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.xprover.XProverInput;

import fr.systerel.smt.provers.internal.core.ExternalSmt;


/**
 * The main plugin class for the Smt provers.
 */
public class SmtProversCore extends AbstractUIPlugin {
	/**
	 * The plug-in identifier 
	 */
	public static final String PLUGIN_ID = "fr.systerel.smt.provers.core"; 

	// The shared instance.
	private static SmtProversCore PLUGIN;
	
	/**
	 * Default delay for time-out of the Smt provers (value 30 seconds).
	 */
	public static long DEFAULT_DELAY = 30 * 1000;

	/**
	 * Returns a tactic for applying the Smt prover to a proof tree node.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * <pre>
	 *    externalSMT(forces, DEFAULT_DELAY)
	 * </pre>
	 * </p>
	 * 
	 * @return a tactic for running SMT with the given forces
	 */
	public static ITactic ExternalSmtTac(boolean restricted) {
		return BasicTactics.reasonerTac(
				new ExternalSmt(),
				new XProverInput(restricted,DEFAULT_DELAY));
	}
	
	/**
	 * Returns the single instance of the Smt Provers for Rodin core plug-in.
	 * 
	 * @return the single instance of the Smt Provers for Rodin core plug-in
	 */
	public static SmtProversCore getDefault() {
		return PLUGIN;
	}

	/**
	 * Creates the Smt Provers for Rodin core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public SmtProversCore() {
		super();
		PLUGIN = this;
	}
	
	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}
	
	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

}
