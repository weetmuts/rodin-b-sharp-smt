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

import fr.systerel.smt.provers.ast.SMTOption;

/**
 * The set-logic script command.
 */
public class SMTSetOptionCommand extends SMTCommand {
	// =========================================================================
	// Variables
	// =========================================================================
	/** An option. */
	private final SMTOption option;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a command with the specified tag.
	 * 
	 * @param option
	 *            an option 
	 */
	public SMTSetOptionCommand(SMTOption option) {
		super(SET_OPTION);
		this.option = option;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(" :");
		builder.append(option.getName());
		builder.append(')');
	}
}
