/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core;

import static org.eventb.smt.core.internal.provers.SMTProversCore.PLUGIN_ID;

/**
 * Common protocol for accessing to the preferences of the SMT core plug-in
 * which are directly exposed. These preferences are:
 * <ul>
 * <li><code>TRANSLATION_PATH_ID</code> is the path to the directory were
 * temporary files are stored,</li>
 * <li><code>VERIT_PATH_ID</code> is the path to the veriT binary used to
 * translate macros.</li>
 * </ul>
 * These preferences are directly accessible through the Eclipse preferences.
 * For instance, to change the translation path, use
 * 
 * <pre>
 * IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
 * 		SMTPreferences.PREF_NODE_NAME);
 * store.setValue(SMTPreferences.TRANSLATION_PATH_ID, &quot;/tmp&quot;);
 * </pre>
 * 
 * @author Laurent Voisin
 */
public class SMTPreferences {

	/**
	 * The name of the preference node that holds the SMT core preferences.
	 */
	public static final String PREF_NODE_NAME = PLUGIN_ID;

	/**
	 * Name of the preference that contains the path to a temporary directory
	 * for storing intermediate files.
	 */
	public static final String TRANSLATION_PATH_ID = "translationPath"; //$NON-NLS-1$

	/**
	 * Name of the preference that contains the path to a veriT binary which
	 * will be used to expand the macros produced by the veriT translation.
	 */
	public static final String VERIT_PATH_ID = "veriTPath"; //$NON-NLS-1$

}
