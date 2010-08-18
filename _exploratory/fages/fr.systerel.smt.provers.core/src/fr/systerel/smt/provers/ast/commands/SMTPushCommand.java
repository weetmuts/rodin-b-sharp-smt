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

import fr.systerel.smt.provers.ast.SMTNumeral;

/**
 * A push script command.
 */
public class SMTPushCommand extends SMTStackCommand {
	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a push command with the specified tag.
	 * 
	 * @param n
	 *            the number of assertion sets
	 */
	SMTPushCommand(SMTNumeral n) {
		super(n,PUSH);
	}
}
