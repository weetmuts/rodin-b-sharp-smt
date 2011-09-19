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

import static org.eventb.smt.ast.SMTBenchmark.PRINT_ANNOTATIONS;
import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;
import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.ASSERT;

import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTNode;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTAssertCommand extends SMTCommand {
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
			formula.toString(builder, 0, false);
			formula.printAnnotations(builder);
		} else {
			formula.toString(builder, 0, false);
		}
		builder.append(CPAR);
	}
}
