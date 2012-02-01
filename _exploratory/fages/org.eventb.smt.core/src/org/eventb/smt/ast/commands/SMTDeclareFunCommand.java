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
import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.DECLARE_FUN;

import org.eventb.smt.ast.symbols.SMTSymbol;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTDeclareFunCommand extends SMTCommand {
	private final SMTSymbol symbol;

	public SMTDeclareFunCommand(final SMTSymbol symbol) {
		// FIXME symbol must be an SMTFunctionSymbol or an SMTPredicateSymbol
		super(DECLARE_FUN);
		this.symbol = symbol;
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		symbol.toString(builder);
		builder.append(CPAR);
	}
}
