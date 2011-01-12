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
	final protected String name;

	SMTSymbol(final String symbolName) {
		this.name = symbolName;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public void toString(final StringBuilder buffer) {
		buffer.append(toString());
	}
}
