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

import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.SMTToken;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * A SMT command option.
 */
public class SMTCommandOption extends SMTNode<SMTCommandOption> {

	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	final static int firstTag = FIRST_COMMAND_OPTION;

	/** The tags. */
	final static String[] tags = { "print-success", "expand-definitions",
			"interactive-mode", "produce-proofs", "produce-unsat-cores",
			"produce-models", "produce-assignments", "regular-output-channel",
			"diagnostic-output-channel", "random-seed", "verbosity", };

	/**
	 * <code>PRINT_SUCCESS</code> is the tag for the print-success option.
	 * 
	 * @see SMTPrintSuccessOption
	 */
	public final static int PRINT_SUCCESS = FIRST_COMMAND_OPTION + 0;

	/**
	 * <code>EXPAND_DEFINITIONS</code> is the tag for the expand-definitions
	 * option.
	 * 
	 * @see SMTExpandDefinitionsOption
	 */
	public final static int EXPAND_DEFINITIONS = FIRST_COMMAND_OPTION + 1;

	/**
	 * <code>INTERACTIVE_MODE</code> is the tag for the interactive-mode option.
	 * 
	 * @see SMTInteractiveModeOption
	 */
	public final static int INTERACTIVE_MODE = FIRST_COMMAND_OPTION + 2;

	/**
	 * <code>PRODUCE_PROOFS</code> is the tag for the produce-proofs option.
	 * 
	 * @see SMTProduceProofsOption
	 */
	public final static int PRODUCE_PROOFS = FIRST_COMMAND_OPTION + 3;

	/**
	 * <code>PRODUCE_UNSAT_CORES</code> is the tag for the produce-unsat-cores
	 * option.
	 * 
	 * @see SMTProduceUnsatCoreOption
	 */
	public final static int PRODUCE_UNSAT_CORES = FIRST_COMMAND_OPTION + 4;

	/**
	 * <code>PRODUCE_MODELS</code> is the tag for the produce-models option.
	 * 
	 * @see SMTProduceModelsOption
	 */
	public final static int PRODUCE_MODELS = FIRST_COMMAND_OPTION + 5;

	/**
	 * <code>PRODUCE_ASSIGNMENTS</code> is the tag for the produce-assignments
	 * option.
	 * 
	 * @see SMTProduceAssignmentOption
	 */
	public final static int PRODUCE_ASSIGNMENTS = FIRST_COMMAND_OPTION + 6;

	/**
	 * <code>REGULAR_OUTPUT_CHANNEL</code> is the tag for the
	 * regular-output-channel option.
	 * 
	 * @see SMTRegularOutputChannelOption
	 */
	public final static int REGULAR_OUTPUT_CHANNEL = FIRST_COMMAND_OPTION + 7;

	/**
	 * <code>DIAGNOSTIC_OUTPUT_CHANNEL</code> is the tag for the
	 * diagnostic-output-channel option.
	 * 
	 * @see SMTDiagnosticOutputChannelOption
	 */
	public final static int DIAGNOSTIC_OUTPUT_CHANNEL = FIRST_COMMAND_OPTION + 8;

	/**
	 * <code>RANDOM_SEED</code> is the tag for the random-seed option.
	 * 
	 * @see SMTRandomSeedOption
	 */
	public final static int RANDOM_SEED = FIRST_COMMAND_OPTION + 9;

	/**
	 * <code>VERBOSITY</code> is the tag for the verbosity option.
	 * 
	 * @see SMTVerbosityOption
	 */
	public final static int VERBOSITY = FIRST_COMMAND_OPTION + 10;

	// =========================================================================
	// Variables
	// =========================================================================
	/** The option value. */
	private final SMTToken value;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Creates a new command option with the specified tag.
	 * 
	 * @param value
	 *            command value
	 * @param tag
	 *            node tag of this term
	 */
	SMTCommandOption(SMTToken value, int tag) {
		super(tag);
		if (this.getTag() < firstTag || this.getTag() >= firstTag + tags.length) {
			throw new IllegalTagException(tag);
		}
		this.value = value;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(":");
		builder.append(tags[getTag() - firstTag]);
		builder.append(" ");
		value.toString(builder);
		builder.append(')');
	}
}
