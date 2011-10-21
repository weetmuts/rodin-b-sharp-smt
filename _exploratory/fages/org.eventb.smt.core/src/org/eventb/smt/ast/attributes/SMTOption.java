/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ast.attributes;

import java.util.Arrays;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTOption extends SMTAttribute<String> {
	public enum SMTOptionKeyword {
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

		SMTOptionKeyword(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public SMTOption(final SMTOptionKeyword keyword, final boolean value) {
		super(keyword.toString(), Arrays.asList(Boolean.toString(value)));
	}

	@Override
	public void printValues(StringBuilder builder) {
		builder.append(values.get(0));
	}
}
