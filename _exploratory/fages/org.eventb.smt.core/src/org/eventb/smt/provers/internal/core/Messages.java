/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.smt.provers.internal.core"; //$NON-NLS-1$
	public static String SmtProversCall_veriT_path_not_defined;
	public static String SmtProversCall_SMT_file_does_not_exist;
	public static String force_error_invalid_forces;
	public static String SmtProversCall_Check_Smt_Preferences;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
