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

import fr.systerel.smt.provers.ast.SMTIdentifier;
import fr.systerel.smt.provers.ast.SMTSort;

/**
 * The declare-fun script command.
 */
public class SMTDeclareFunCommand extends SMTCommand {
	// =========================================================================
	// Variables
	// =========================================================================
	/** The function identifier. */
	private final SMTIdentifier identifier;

	/** The function parameters. */
	private final SMTSort[] parameters;

	/** The function return type. */
	private final SMTSort returnType;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a declare-fun command with the specified tag.
	 * 
	 * @param identifier
	 *            the function identifier
	 * @param parameters
	 *            the function parameters
	 * @param returnType
	 *            the function return type
	 */
	SMTDeclareFunCommand(SMTIdentifier identifier, SMTSort[] parameters,
			SMTSort returnType) {
		super(DECLARE_FUN);
		this.identifier = identifier;
		this.parameters = parameters.clone();
		this.returnType = returnType;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(" ");
		identifier.toString(builder);
		builder.append(" (");
		for (SMTSort child : parameters) {
			builder.append(child.getName());
			builder.append(" ");
		}
		builder.append(") ");
		builder.append(returnType.getName());
		builder.append(')');
	}
}
