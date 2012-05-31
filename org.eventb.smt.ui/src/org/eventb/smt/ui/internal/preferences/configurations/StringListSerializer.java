/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to serialize a list of Strings into a single String.
 *
 * @author Laurent Voisin
 */
public class StringListSerializer {

	/*
	 * Escape and separator characters. These have been chosen to be infrequent
	 * and different from characters escaped in properties serialization).
	 */
	public static final char ESCAPE = '~';
	public static final char SEPARATOR = ';';

	private static final String SEPARATOR_STR = Character.toString(SEPARATOR);

	/**
	 * Serializes the given list of strings into one string, using some escape
	 * and separator characters. The given array can be empty and contain
	 * arbitrary strings (including strings using the escape and separator
	 * characters). However, members of the array must not be <code>null</code>.
	 *
	 * @param strings
	 *            an array of non-<code>null</code> arbitrary strings
	 * @return a serialization of the given array
	 */
	public static String serialize(List<String> strings) {
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		for (final String string : strings) {
			sb.append(sep);
			sep = SEPARATOR_STR;
			final int length = string.length();
			for (int i = 0; i < length; i++) {
				final char c = string.charAt(i);
				switch (c) {
				case ESCAPE:
					sb.append(ESCAPE);
					sb.append(ESCAPE);
					break;
				case SEPARATOR:
					sb.append(ESCAPE);
					sb.append(SEPARATOR);
					break;
				default:
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * De-serializes a string previously obtained from the
	 * {@link #serialize(List)} method. It is guaranteed that
	 *
	 * <pre>
	 * <code>deserialize(serialize(x)).equals(x)</code>
	 * </pre>
	 *
	 * @param serialized
	 *            a serialized list of string
	 * @return a list of all strings contained in the given string
	 */
	public static List<String> deserialize(String serialized) {
		final List<String> result = new ArrayList<String>();
		final int length = serialized.length();
		if (length == 0) {
			return result;
		}
		final StringBuilder sb = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < length; i++) {
			final char c = serialized.charAt(i);
			if (escaped) {
				sb.append(c);
				escaped = false;
			} else if (c == ESCAPE) {
				escaped = true;
			} else if (c == SEPARATOR) {
				result.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		result.add(sb.toString());
		return result;
	}

}
