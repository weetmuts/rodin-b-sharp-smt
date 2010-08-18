/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.commands;

/**
 * The exit script command.
 */
public class SMTExitCommand extends SMTCommand {
	
	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a exit command with the specified tag.
	 */
	SMTExitCommand() {
		super(EXIT);
	}

}
