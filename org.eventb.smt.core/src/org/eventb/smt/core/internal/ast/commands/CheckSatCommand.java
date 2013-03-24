/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.commands;

import static org.eventb.smt.core.internal.ast.commands.Command.SMTCommandName.CHECK_SAT;

/**
 * @author Yoann Guyot
 * 
 */
public class CheckSatCommand extends Command {
	private final static CheckSatCommand checkSatCommand = new CheckSatCommand();

	private CheckSatCommand() {
		super(CHECK_SAT);
	}

	public static CheckSatCommand getCheckSatCommand() {
		return checkSatCommand;
	}
}
