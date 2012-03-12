/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.commands;

import static org.eventb.smt.core.internal.ast.SMTBenchmark.PRINT_ANNOTATIONS;
import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;
import static org.eventb.smt.core.internal.ast.commands.SMTCommand.SMTCommandName.ASSERT;

import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTNode;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTAssertCommand extends SMTCommand {
	public static final int ASSERT_COMMAND_OFFSET = 8;
	private final SMTFormula formula;

	public SMTAssertCommand(final SMTFormula formula) {
		super(ASSERT);
		this.formula = formula;
	}

	@Override
	public void toString(final StringBuilder builder) {
		toString(builder, !PRINT_ANNOTATIONS);
	}

	public void toString(final StringBuilder builder,
			final boolean printAnnotations) {
		openCommand(builder);
		builder.append(SPACE);
		if (printAnnotations && formula.isAnnotated()) {
			SMTNode.printAnnotationOperator(builder);
			formula.toString(builder, ASSERT_COMMAND_OFFSET + 3, false);
			formula.printAnnotations(builder);
		} else {
			formula.toString(builder, ASSERT_COMMAND_OFFSET, false);
		}
		builder.append(CPAR);
	}
}
