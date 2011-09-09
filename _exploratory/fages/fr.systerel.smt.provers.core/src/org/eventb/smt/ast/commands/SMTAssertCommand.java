/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ast.commands;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

import org.eventb.smt.ast.SMTFormula;

/**
 * @author Systerel (yguyot)
 *
 */
public class SMTAssertCommand extends SMTCommand {
	private final static String ASSERT = "assert";
	private final SMTFormula formula;

	public SMTAssertCommand(final SMTFormula formula) {
		super(ASSERT);
		this.formula = formula;
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		formula.toString(builder, 0, false);
		builder.append(CPAR);
	}
}
