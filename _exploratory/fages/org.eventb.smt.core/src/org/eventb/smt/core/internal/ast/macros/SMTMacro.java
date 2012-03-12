/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.macros;

/**
 * <p>
 * This class is the base class for implementing macros. All macros has at least
 * a name to define it.
 * </p>
 * 
 * <p>
 * The macro has also a priority number. This number is used to define which
 * macros must be written first, because some macros are dependent on other
 * macros
 * </p>
 * 
 * @author vitor
 * 
 */
public abstract class SMTMacro implements Comparable<SMTMacro> {

	/**
	 * The precedence of the macro. See {@link SMTMacro} for more details.
	 */
	protected final int precedence;

	/**
	 * The name of the macro
	 */
	private final String macroName;

	/**
	 * Initializes a macro with a name and precedence.
	 * 
	 * @param macroName
	 *            the name of the macro
	 * @param precedence
	 *            the precedence of the macro
	 */
	SMTMacro(final String macroName, final int precedence) {
		this.macroName = macroName;
		this.precedence = precedence;
	}

	/**
	 * Retrieves the macro name
	 * 
	 * @return the macro name
	 */
	public String getMacroName() {
		return macroName;
	}

	/**
	 * Adds a string representation to the builder
	 * 
	 * @param builder
	 */
	public abstract void toString(final StringBuilder builder, final int offset);

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof SMTMacro) {
			final SMTMacro macroObj = (SMTMacro) obj;
			if (macroObj.getMacroName().equals(getMacroName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final int compareTo(final SMTMacro o) {
		if (o.getPrecedence() == precedence) {
			return getMacroName().compareTo(o.getMacroName());
		} else if (precedence > o.getPrecedence()) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Get the precedence. See {@link SMTMacro} for more details.
	 * 
	 * @return the precedence.
	 */
	public int getPrecedence() {
		return precedence;
	}
}
