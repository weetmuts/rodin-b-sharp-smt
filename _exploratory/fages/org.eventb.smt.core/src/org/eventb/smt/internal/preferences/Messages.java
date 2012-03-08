/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.smt.internal.preferences.messages"; //$NON-NLS-1$
	public static String BundledSolverLoadingException_DefaultMessage;
	public static String SMTPreferences_IllegalSMTSolverSettings;
	public static String SMTPreferences_NoSMTSolverSelected;
	public static String SMTPreferences_NoSMTSolverSet;
	public static String SMTPreferences_TranslationPathNotSet;
	public static String SMTPreferences_VeriTPathNotSet;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// do not instantiate
	}
}
