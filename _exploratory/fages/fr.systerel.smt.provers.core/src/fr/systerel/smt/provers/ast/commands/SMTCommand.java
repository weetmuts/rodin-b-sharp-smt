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

import fr.systerel.smt.provers.astV1_2.SMTNode;

/**
 * A SMT command.
 */
public abstract class SMTCommand extends SMTNode<SMTCommand> {

	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	final static int firstTag = FIRST_COMMAND;

	/** The tags. */
	final static String[] tags = { "set-logic", "set-option", "set-info",
			"declare-sort", "define-sort", "declare-fun", "define-fun", "push",
			"pop", "assert", "check-sat", "get-assertions", "get-proof",
			"get-unsat-core", "get-value", "get-assignment", "get-option",
			"get-info", "exit", };

	/**
	 * <code>SET_LOGIC</code> is the tag for the set-logic script command.
	 * 
	 * @see SMTSetLogicCommand
	 */
	public final static int SET_LOGIC = FIRST_COMMAND + 0;

	/**
	 * <code>SET_OPTION</code> is the tag for the set-option script command.
	 * 
	 * @see SMTSetOptionCommand
	 */
	public final static int SET_OPTION = FIRST_COMMAND + 1;

	/**
	 * <code>SET_INFO</code> is the tag for the set-info script command.
	 * 
	 * @see SMTSetInfoCommand
	 */
	public final static int SET_INFO = FIRST_COMMAND + 2;

	/**
	 * <code>DECLARE_SORT</code> is the tag for the declare-sort script command.
	 * 
	 * @see SMTDeclareSortCommand
	 */
	public final static int DECLARE_SORT = FIRST_COMMAND + 3;

	/**
	 * <code>DEFINE_SORT</code> is the tag for the define-sort script command.
	 * 
	 * @see SMTDefineSortCommand
	 */
	public final static int DEFINE_SORT = FIRST_COMMAND + 4;

	/**
	 * <code>DECLARE_FUN</code> is the tag for the declare-fun script command.
	 * 
	 * @see SMTDeclareFunCommand
	 */
	public final static int DECLARE_FUN = FIRST_COMMAND + 5;

	/**
	 * <code>DEFINE_FUN</code> is the tag for the define-fun script command.
	 * 
	 * @see SMTDefineFunCommand
	 */
	public final static int DEFINE_FUN = FIRST_COMMAND + 6;

	/**
	 * <code>PUSH</code> is the tag for the push script command.
	 * 
	 * @see SMTPushCommand
	 */
	public final static int PUSH = FIRST_COMMAND + 7;

	/**
	 * <code>POP</code> is the tag for the pop script command.
	 * 
	 * @see SMTPopCommand
	 */
	public final static int POP = FIRST_COMMAND + 8;

	/**
	 * <code>ASSERT</code> is the tag for the assert script command.
	 * 
	 * @see SMTAssertCommand
	 */
	public final static int ASSERT = FIRST_COMMAND + 9;

	/**
	 * <code>CHECK-SAT</code> is the tag for the check-sat script command.
	 * 
	 * @see SMTCheckSatCommand
	 */
	public final static int CHECK_SAT = FIRST_COMMAND + 10;

	/**
	 * <code>GET_ASSERTIONS</code> is the tag for the get-assertions script
	 * command.
	 * 
	 * @see SMTGetAssertionsCommand
	 */
	public final static int GET_ASSERTIONS = FIRST_COMMAND + 11;

	/**
	 * <code>GET_PROOF</code> is the tag for the get-proof script command.
	 * 
	 * @see SMTGetProofCommand
	 */
	public final static int GET_PROOF = FIRST_COMMAND + 12;

	/**
	 * <code>GET_UNSAT_CORE</code> is the tag for the get-unsat-core script
	 * command.
	 * 
	 * @see SMTGetUnsatCoreCommand
	 */
	public final static int GET_UNSAT_CORE = FIRST_COMMAND + 13;

	/**
	 * <code>GET_VALUE</code> is the tag for the get-value script command.
	 * 
	 * @see SMTGetValueCommand
	 */
	public final static int GET_VALUE = FIRST_COMMAND + 14;

	/**
	 * <code>GET_ASSIGNMENT</code> is the tag for the get-assignment script
	 * command.
	 * 
	 * @see SMTGetAssignmentCommand
	 */
	public final static int GET_ASSIGNMENT = FIRST_COMMAND + 15;

	/**
	 * <code>GET_OPTION</code> is the tag for the get-option script command.
	 * 
	 * @see SMTGetOptionCommand
	 */
	public final static int GET_OPTION = FIRST_COMMAND + 16;

	/**
	 * <code>GET_INFO</code> is the tag for the get-info script command.
	 * 
	 * @see SMTGetInfoCommand
	 */
	public final static int GET_INFO = FIRST_COMMAND + 17;

	/**
	 * <code>EXIT</code> is the tag for the exit script command.
	 * 
	 * @see SMTExitCommand
	 */
	public final static int EXIT = FIRST_COMMAND + 18;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Creates a new command with the specified tag.
	 * 
	 * @param tag
	 *            node tag of this term
	 */
	SMTCommand(int tag) {
		super(tag);
		assert getTag() >= firstTag && getTag() < firstTag + tags.length;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(')');
	}
}
