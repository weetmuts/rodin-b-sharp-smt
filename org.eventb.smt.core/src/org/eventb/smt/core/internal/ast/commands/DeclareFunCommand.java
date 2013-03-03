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

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;
import static org.eventb.smt.core.internal.ast.commands.Command.SMTCommandName.DECLARE_FUN;

import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;

/**
 * @author Yoann Guyot
 * 
 */
public class DeclareFunCommand extends Command {
	private final SMTSymbol symbol;

	public DeclareFunCommand(final SMTSymbol symbol) {
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
