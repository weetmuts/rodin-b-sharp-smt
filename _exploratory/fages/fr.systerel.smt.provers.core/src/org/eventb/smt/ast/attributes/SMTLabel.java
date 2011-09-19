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

import static org.eventb.smt.ast.SMTFactory.SPACE;

import java.util.ArrayList;

import org.eventb.smt.ast.symbols.SMTSymbol;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTLabel extends SMTAttribute {
	public static final boolean GOAL_LABEL = true;
	public static final String LABEL_KEYWORD = "named";
	public static final String DEFAULT_HYPOTHESIS_LABEL = "hyp";
	public static final String DEFAULT_GOAL_LABEL = "goal";

	public SMTLabel(final SMTSymbol name) {
		super(LABEL_KEYWORD, new ArrayList<SMTSymbol>(1));
		this.values.add(name);
	}

	@Override
	public void printValues(StringBuilder builder) {
		builder.append(values.get(0).getName());
	}

	@Override
	public void toString(StringBuilder builder) {
		printKeyword(builder);
		builder.append(SPACE);
		printValues(builder);
	}
}
