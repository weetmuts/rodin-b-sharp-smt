/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.commands;

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yoann Guyot
 * 
 */
public abstract class Command {
	private final SMTCommandName name;

	public enum SMTCommandName {
		ASSERT("assert"), //
		CHECK_SAT("check-sat"), //
		DECLARE_SORT("declare-sort"), //
		DECLARE_FUN("declare-fun"), //
		DEFINE_SORT("define-sort"), //
		DEFINED_FUN("define-fun"), //
		EXIT("exit"), //
		GET_ASSERTIONS("get-assertions"), //
		GET_ASSIGMENT("get-assignment"), //
		GET_INFO("get-info"), //
		GET_OPTION("get-option"), //
		GET_PROOF("get-proof"), //
		GET_UNSAT_CORE("get-unsat-core"), //
		GET_VALUE("get-value"), //
		POP("pop"), //
		PUSH("push"), //
		SET_LOGIC("set-logic"), //
		SET_INFO("set-info"), //
		SET_OPTION("set-option");

		private String name;

		SMTCommandName(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Retrieves all the SMT command names.
		 * 
		 * @return the list with all the command names.
		 */
		public static final List<String> getCommandNames() {
			final SMTCommandName[] smtCmdNames = SMTCommandName.values();
			final List<String> names = new ArrayList<String>(smtCmdNames.length);
			for (final SMTCommandName name : smtCmdNames) {
				names.add(name.toString());
			}
			return names;
		}
	}

	public Command(final SMTCommandName name) {
		this.name = name;
	}

	public void openCommand(final StringBuilder builder) {
		builder.append(OPAR);
		builder.append(name);
	}

	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(CPAR);
	}
}
