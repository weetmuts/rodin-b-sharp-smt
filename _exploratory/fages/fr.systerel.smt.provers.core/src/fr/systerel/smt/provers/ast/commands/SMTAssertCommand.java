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

import fr.systerel.smt.provers.astV1_2.SMTFormula;
import fr.systerel.smt.provers.astV1_2.SMTNode;
import fr.systerel.smt.provers.astV1_2.SMTTerm;

/**
 * An assert script command.
 */
public class SMTAssertCommand extends SMTCommand {
	// =========================================================================
	// Variables
	// =========================================================================
	/** A well sorted closed term of sort Bool. */
	private final SMTNode<?> node;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a command with the specified tag.
	 * 
	 * @param term
	 *            a well sorted closed term of sort Bool
	 */
	public SMTAssertCommand(SMTNode<?> node) {
		super(ASSERT);
		this.node = node;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(" ");
		node.toString(builder);
		builder.append(')');
	}
}
