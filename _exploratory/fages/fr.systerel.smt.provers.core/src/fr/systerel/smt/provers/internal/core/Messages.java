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

package fr.systerel.smt.provers.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.internal.core.messages"; //$NON-NLS-1$
	public static String force_error_invalid_forces;
	public static String SmtProversCall_no_defined_solver_path;
	public static String SmtProversCall_preprocessor_path_not_defined;
	public static String SmtProversCall_translated_file_not_exists;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
