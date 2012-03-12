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
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.internal.ast.attributes.SMTAttribute;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTSetInfoCommand extends SMTCommand {
	private final SMTAttribute<String> attribute;

	private final static String STATUS = "status";
	private final static String UNSAT = "unsat";
	private final static SMTSetInfoCommand STATUS_UNSAT = new SMTSetInfoCommand(
			STATUS, Arrays.asList(UNSAT));

	public SMTSetInfoCommand(final String keyword, final List<String> values) {
		super(SMTCommand.SMTCommandName.SET_INFO);
		attribute = new SMTAttribute<String>(keyword, values);
	}

	public static SMTSetInfoCommand setStatusUnsat() {
		return STATUS_UNSAT;
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		builder.append(attribute);
		builder.append(CPAR);
	}
}
