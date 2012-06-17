/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.eventb.smt.core.internal.log.SMTStatus;

/**
 * @author Systerel (yguyot)
 * 
 */
public class Utils {
	private static final String UTF8 = "UTF-8"; //$NON-NLS-1$

	public static String encode(final String s) {
		try {
			return URLEncoder.encode(s, UTF8);
		} catch (UnsupportedEncodingException e) {
			SMTStatus.smtError("Error while encoding.", e);
			return "";
		}
	}

	public static String decode(final String s) {
		try {
			return URLDecoder.decode(s, UTF8);
		} catch (UnsupportedEncodingException e) {
			SMTStatus.smtError("Error while decoding.", e);
			return "";
		}
	}

}
