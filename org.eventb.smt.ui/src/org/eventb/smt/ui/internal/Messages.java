/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eventb.smt.ui.internal.messages"; //$NON-NLS-0$

	// Main preference page
	public static String MainPrefPage_description;
	public static String MainPrefPage_missingValue;
	public static String MainPrefPage_notAbsolute;
	public static String MainPrefPage_notADirectory;
	public static String MainPrefPage_notAFile;
	public static String MainPrefPage_notExecutableFile;
	public static String MainPrefPage_notWritableDir;
	public static String MainPrefPage_tmpDirLabel;
	public static String MainPrefPage_tmpDirTooltip;
	public static String MainPrefPage_veriTPathLabel;
	public static String MainPrefPage_veriTPathTooltip;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
