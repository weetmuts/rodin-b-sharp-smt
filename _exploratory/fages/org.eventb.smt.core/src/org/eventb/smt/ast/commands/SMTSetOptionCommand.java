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

import org.eventb.smt.ast.attributes.SMTOption;
import org.eventb.smt.ast.attributes.SMTOption.SMTOptionKeyword;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTSetOptionCommand extends SMTCommand {
	private final SMTOption option;

	public SMTSetOptionCommand(final SMTOptionKeyword keyword,
			final boolean value) {
		super(SET_OPTION);
		this.option = new SMTOption(keyword, value);
	}

	public static SMTSetOptionCommand setTrue(final SMTOptionKeyword option) {
		return new SMTSetOptionCommand(option, true);
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		builder.append(option);
		builder.append(CPAR);
	}
}