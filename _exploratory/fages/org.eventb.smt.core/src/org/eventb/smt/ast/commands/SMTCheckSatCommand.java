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

import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.CHECK_SAT;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCheckSatCommand extends SMTCommand {
	private final static SMTCheckSatCommand checkSatCommand = new SMTCheckSatCommand();

	private SMTCheckSatCommand() {
		super(CHECK_SAT);
	}

	public static SMTCheckSatCommand getCheckSatCommand() {
		return checkSatCommand;
	}
}
