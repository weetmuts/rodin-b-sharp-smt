/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ast.attributes;

import java.util.List;

import org.eventb.smt.ast.symbols.SMTSymbol;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class SMTAttribute {
	private static final String KEYWORD_SPECIAL_CHARACTER = ":";

	private final String keyword;
	final List<SMTSymbol> values;

	public SMTAttribute(final String keyword, final List<SMTSymbol> values) {
		this.keyword = keyword;
		this.values = values;
	}

	public void printKeyword(final StringBuilder builder) {
		builder.append(KEYWORD_SPECIAL_CHARACTER).append(keyword);
	}

	public abstract void printValues(final StringBuilder builder);

	public abstract void toString(final StringBuilder builder);

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}
}
