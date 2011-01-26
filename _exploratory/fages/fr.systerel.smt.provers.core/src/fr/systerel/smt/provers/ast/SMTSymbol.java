/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * @author guyot
 *
 */
public abstract class SMTSymbol {
	protected final String name;
	protected final boolean predefined;

	public static final boolean PREDEFINED = true;

	SMTSymbol(final String symbolName, final boolean predefined) {
		this.name = symbolName;
		this.predefined = predefined;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void toString(final StringBuilder buffer) {
		buffer.append(toString());
	}
}
