/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.smt.core.internal.preferences.ui.messages";

	public static String SMTPreferencePage2_MandatoryFieldsInSolverDetails;
	public static String SmtProversCall_no_selected_solver;
	public static String SmtProversCall_no_defined_solver_path;
	public static String SmtProversCall_preprocessor_path_not_defined;
	public static String SmtProversCall_SMT_file_does_not_exist;
	public static String Verit_Preprocessing_SMT_file_does_not_exist;
	public static String SmtProversCall_file_could_not_be_deleted;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
