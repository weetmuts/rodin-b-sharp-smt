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

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeDotInIDException;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeEmptyNameException;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeInvalidJavaIDException;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeNoSuchBundleException;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeNullIDException;
import static org.eventb.smt.core.internal.preferences.ExtensionLoadingException.makeNullNameException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.eventb.smt.core.internal.log.SMTStatus;
import org.osgi.framework.Bundle;

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

	/**
	 * @param bundleName
	 * @param bundle
	 * @throws ExtensionLoadingException
	 */
	public static void checkBundle(final String bundleName, final Bundle bundle)
			throws ExtensionLoadingException {
		if (bundle == null)
			throw makeNoSuchBundleException(bundleName);
	}

	public static void checkId(String id) throws ExtensionLoadingException {
		if (id == null) {
			throw makeNullIDException();
		}
		if (id.indexOf('.') != -1) {
			throw makeDotInIDException(id);
		}
		if (!isJavaIdentifier(id)) {
			throw makeInvalidJavaIDException(id);
		}
	}

	public static void checkName(String name) throws ExtensionLoadingException {
		if (name == null) {
			throw makeNullNameException();
		} else if (name.isEmpty()) {
			throw makeEmptyNameException();
		}
	}

	/**
	 * Checks if a string is a legal Java identifier
	 * 
	 * @param s
	 *            the string to check
	 * @return true if the string is a legal Java identifier
	 */
	private static boolean isJavaIdentifier(String s) {
		if (s.length() == 0 || !isJavaIdentifierStart(s.charAt(0))) {
			return false;
		}
		for (int i = 1; i < s.length(); i++) {
			if (!isJavaIdentifierPart(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
