/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.ast.commands;

import static org.eventb.smt.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.internal.ast.SMTFactory.SPACE;
import static org.eventb.smt.internal.ast.commands.SMTCommand.SMTCommandName.DECLARE_SORT;

import org.eventb.smt.internal.ast.symbols.SMTSortSymbol;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTDeclareSortCommand extends SMTCommand {
	private final SMTSortSymbol sortSymbol;

	public SMTDeclareSortCommand(final SMTSortSymbol sortSymbol) {
		super(DECLARE_SORT);
		this.sortSymbol = sortSymbol;
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		sortSymbol.toString(builder);
		builder.append(CPAR);
	}
}
