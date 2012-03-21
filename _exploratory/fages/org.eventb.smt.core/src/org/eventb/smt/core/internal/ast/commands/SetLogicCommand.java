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
import static org.eventb.smt.core.internal.ast.commands.Command.SMTCommandName.SET_LOGIC;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SetLogicCommand extends Command {
	private final String logicName;

	public SetLogicCommand(final String logicName) {
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
