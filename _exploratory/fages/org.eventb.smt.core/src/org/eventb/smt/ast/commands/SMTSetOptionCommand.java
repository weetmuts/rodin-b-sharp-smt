/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ast.commands;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;
import static org.eventb.smt.ast.commands.SMTCommand.SMTCommandName.SET_OPTION;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTSetOptionCommand extends SMTCommand {
	private final String OPTION_PREFIX_CHAR = ":";
	private final SMTOptionName option;
	private final boolean value;

	public enum SMTOptionName {
		PRINT_SUCCESS("print-success"), //
		EXPAND_DEFINITIONS("expand-definitions"), //
		INTERACTIVE_MODE("interactive-mode"), //
		PRODUCE_PROOFS("produce-proofs"), //
		PRODUCE_UNSAT_CORE("produce-unsat-cores"), //
		PRODUCE_MODELS("produce-models"), //
		PRODUCE_ASSIGMENTS("produce-assignments"), //
		REGULAR_OUTPUT_CHANNEL("regular-output-channel"), //
		DIAGNOSTIC_OUTPUT_CHANNEL("diagnostic-output-channel"), //
		RANDOM_SEED("random-seed"), //
		VERBOSITY("verbosity");

		private String name;

		SMTOptionName(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public SMTSetOptionCommand(final SMTOptionName option, final boolean value) {
		super(SET_OPTION);
		this.option = option;
		this.value = value;
	}

	public static SMTSetOptionCommand setTrue(final SMTOptionName option) {
		return new SMTSetOptionCommand(option, true);
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		builder.append(OPTION_PREFIX_CHAR);
		builder.append(option);
		builder.append(SPACE);
		builder.append(value);
		builder.append(CPAR);
	}
}
