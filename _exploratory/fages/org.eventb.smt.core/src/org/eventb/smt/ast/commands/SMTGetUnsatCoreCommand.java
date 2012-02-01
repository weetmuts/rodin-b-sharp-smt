/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.commands;

import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.GET_UNSAT_CORE;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTGetUnsatCoreCommand extends SMTCommand {
	private final static SMTGetUnsatCoreCommand getUnsatCoreCommand = new SMTGetUnsatCoreCommand();

	private SMTGetUnsatCoreCommand() {
		super(GET_UNSAT_CORE);
	}

	public static SMTGetUnsatCoreCommand getGetUnsatCoreCommand() {
		return getUnsatCoreCommand;
	}
}
