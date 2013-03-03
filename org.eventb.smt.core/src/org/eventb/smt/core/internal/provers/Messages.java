/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eventb.smt.core.internal.provers.messages"; //$NON-NLS-1$

	public static String nullSMTConfigurationError;
	public static String SmtProversCall_veriT_path_not_defined;
	public static String SMTVeriTCall_SMTLIBV2_0_deactivated;

	// Pattern for messages with argument (see corresponding method below)
	public static String unknownSMTConfigurationError_pattern;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// do not instantiate
	}

	public static String unknownSMTConfigurationError(String configName) {
		return bind(unknownSMTConfigurationError_pattern, configName);
	}

}
