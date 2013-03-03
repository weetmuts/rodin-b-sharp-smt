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

import static org.eventb.smt.core.internal.ast.commands.Command.SMTCommandName.GET_UNSAT_CORE;

/**
 * @author Yoann Guyot
 * 
 */
public class GetUnsatCoreCommand extends Command {
	private final static GetUnsatCoreCommand getUnsatCoreCommand = new GetUnsatCoreCommand();

	private GetUnsatCoreCommand() {
		super(GET_UNSAT_CORE);
	}

	public static GetUnsatCoreCommand getGetUnsatCoreCommand() {
		return getUnsatCoreCommand;
	}
}
