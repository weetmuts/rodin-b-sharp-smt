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
import static org.eventb.smt.core.internal.ast.commands.Command.SMTCommandName.SET_OPTION;

import org.eventb.smt.core.internal.ast.attributes.Option;
import org.eventb.smt.core.internal.ast.attributes.Option.SMTOptionKeyword;

/**
 * @author Yoann Guyot
 * 
 */
public class SetOptionCommand extends Command {
	private final Option option;

	public SetOptionCommand(final SMTOptionKeyword keyword,
			final boolean value) {
		super(SET_OPTION);
		this.option = new Option(keyword, value);
	}

	public static SetOptionCommand setTrue(final SMTOptionKeyword option) {
		return new SetOptionCommand(option, true);
	}

	public static SetOptionCommand setFalse(final SMTOptionKeyword option) {
		return new SetOptionCommand(option, false);
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		builder.append(option);
		builder.append(CPAR);
	}
}
