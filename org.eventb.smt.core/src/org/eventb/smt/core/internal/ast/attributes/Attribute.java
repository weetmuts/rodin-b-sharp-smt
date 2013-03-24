/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.attributes;

import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import java.util.List;

/**
 * @author Yoann Guyot
 * 
 */
public class Attribute<T> {
	private static final String KEYWORD_SPECIAL_CHARACTER = ":";

	private final String keyword;
	final List<T> values;

	public Attribute(final String keyword, final List<T> values) {
		this.keyword = keyword;
		this.values = values;
	}

	public void printKeyword(final StringBuilder builder) {
		builder.append(KEYWORD_SPECIAL_CHARACTER).append(keyword);
	}

	public void printValues(final StringBuilder builder) {
		String separator = "";
		for (final T value : values) {
			builder.append(separator);
			builder.append(value);
			separator = SPACE;
		}
	}

	public void toString(StringBuilder builder) {
		printKeyword(builder);
		builder.append(SPACE);
		printValues(builder);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}
}
