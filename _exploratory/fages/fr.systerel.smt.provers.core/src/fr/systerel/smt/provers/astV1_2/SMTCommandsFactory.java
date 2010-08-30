/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.astV1_2;

import java.math.BigInteger;

import fr.systerel.smt.provers.ast.commands.SMTAssertCommand;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public final class SMTCommandsFactory {

	private final static SMTCommandsFactory DEFAULT_INSTANCE = new SMTCommandsFactory();

	/**
	 * Returns the default instance of the factory.
	 * 
	 * @return the single instance of this class
	 */
	public static SMTCommandsFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * Creates a new assert command.
	 * 
	 * @param tag
	 *            the tag of the Assert Command
	 * @param children
	 *            the children of the assert command
	 * @return the newly created formula
	 */
	public SMTAssertCommand makeAssertCommand(SMTNode<?> children) {
		return new SMTAssertCommand(children);
	}
	
}
