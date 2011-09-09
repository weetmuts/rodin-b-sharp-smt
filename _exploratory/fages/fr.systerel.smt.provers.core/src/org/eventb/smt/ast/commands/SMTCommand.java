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
import static org.eventb.smt.ast.SMTFactory.OPAR;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class SMTCommand {
	private final String name;

	public SMTCommand(final String name) {
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
