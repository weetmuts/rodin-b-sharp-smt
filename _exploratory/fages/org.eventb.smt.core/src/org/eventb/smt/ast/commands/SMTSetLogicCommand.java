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

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;
import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.SET_LOGIC;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTSetLogicCommand extends SMTCommand {
	private final String logicName;

	public SMTSetLogicCommand(final String logicName) {
		super(SET_LOGIC);
		this.logicName = logicName;
	}

	@Override
	public void toString(StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		builder.append(logicName);
		builder.append(CPAR);
	}
}
