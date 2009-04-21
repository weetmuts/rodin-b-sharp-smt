/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.IResult;

/**
 * The class used to indicate that an error was encountered during parsing.
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = -1766599495248240033L;

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable exception) {
		super(exception);
	}

	/**
	 * Throws an exception.
	 * 
	 * @param result
	 *            the parsing result.
	 * @param message
	 *            an additional useful message.
	 * @throws ParseException
	 */
	public final static void throwIt(final IResult result, final String message)
			throws ParseException {
		String msg = message + "\n";
		for (ASTProblem problem : result.getProblems())
			msg = msg + problem.toString() + "\n";
		throw new ParseException(msg);
	}

	/**
	 * Throws an exception.
	 * 
	 * @param result
	 *            the parsing result.
	 * @throws ParseException
	 */
	public final static void throwIt(IResult result) throws ParseException {
		throwIt(result, "");
	}

}