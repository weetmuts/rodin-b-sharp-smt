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

import java.util.Arrays;

import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;

/**
 * @author Yoann Guyot
 * 
 */
public class Label extends Attribute<SMTSymbol> {
	public static final boolean GOAL_LABEL = true;
	public static final String LABEL_KEYWORD = "named";
	public static final String DEFAULT_HYPOTHESIS_LABEL = "hyp";
	public static final String DEFAULT_GOAL_LABEL = "goal";

	public Label(final SMTSymbol name) {
		super(LABEL_KEYWORD, Arrays.asList(name));
	}

	public String getName() {
		return values.get(0).getName();
	}

	@Override
	public void printValues(StringBuilder builder) {
		builder.append(values.get(0).getName());
	}
}
