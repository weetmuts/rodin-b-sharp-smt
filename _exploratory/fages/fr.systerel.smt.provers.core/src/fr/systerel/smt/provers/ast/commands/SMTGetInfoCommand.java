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
 * The get-info script command.
 */
public class SMTGetInfoCommand extends SMTCommand {
	// =========================================================================
	// Variables
	// =========================================================================
	/** An info flag. */
	private final SMTInfoFlag flag;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a command with the specified tag.
	 * 
	 * @param flag
	 *            an info flag
	 */
	SMTGetInfoCommand(SMTInfoFlag flag) {
		super(GET_INFO);
		this.flag = flag;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(" ");
		builder.append(":");
		builder.append(flag.getName());
		builder.append(')');
	}
}
