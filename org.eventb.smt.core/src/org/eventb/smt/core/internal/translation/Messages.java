/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.translation;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.smt.core.internal.translation.messages"; //$NON-NLS-1$
	public static String Incompatible_Formula_With_PPTrans_Production;
	public static String Misformed_EventB_Types;
	public static String Translation_error;
	public static String TRUE_On_Both_Sides_Of_Boolean_Equality_error;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
