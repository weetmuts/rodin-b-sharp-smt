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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Type;

/**
 * Class Variable.
 * 
 */
public final class Variable {

	/** The variable name. */
	private final String name;

	/** The variable type. */
	private final Type type;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a variable.
	 * 
	 * @param ff
	 *            the formula factory to be used to parse the type.
	 * @param name
	 *            the variable name.
	 * @param type
	 *            the variable type.
	 * @throws ParseException
	 *             if a problem occurred when parsing the type.
	 */
	public Variable(FormulaFactory ff, String name, String type)
			throws ParseException {
		this.name = name;

		IParseResult result = ff.parseType(type, LanguageVersion.V2);
		if (!result.hasProblem())
			this.type = result.getParsedType();
		else {
			this.type = null;
			ParseException.throwIt(result,
					"A problem occurred when parsing the following type: "
							+ type);
		}
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the variable name.
	 * 
	 * @return the variable name.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the variable type.
	 * 
	 * @return the variable type, as a <tt>Type</tt> object.
	 */
	public final Type getType() {
		return type;
	}

}
