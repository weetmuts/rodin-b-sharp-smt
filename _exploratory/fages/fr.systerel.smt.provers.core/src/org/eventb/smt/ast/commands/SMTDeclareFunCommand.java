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

import org.eventb.smt.ast.symbols.SMTFunctionSymbol;

/**
 * @author Systerel (yguyot)
 *
 */
public class SMTDeclareFunCommand extends SMTCommand {
	private final static String DECLARE_FUN = "declare-fun";
	private final SMTFunctionSymbol functionSymbol;

	public SMTDeclareFunCommand(final SMTFunctionSymbol functionSymbol) {
		super(DECLARE_FUN);
		this.functionSymbol = functionSymbol;
	}

	@Override
	public void toString(final StringBuilder builder) {
		openCommand(builder);
		builder.append(SPACE);
		functionSymbol.toString(builder);
		builder.append(CPAR);
	}
}
