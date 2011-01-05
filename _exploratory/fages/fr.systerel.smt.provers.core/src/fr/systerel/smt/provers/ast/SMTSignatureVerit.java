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

import java.util.ArrayList;
import java.util.List;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
public class SMTSignatureVerit extends SMTSignature {

	private final List<String> macros = new ArrayList<String>();

	/**
	 * @param logicName
	 */
	public SMTSignatureVerit(String logicName) {
		super(logicName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gives a fresh identifier to a variable of which identifier contains the
	 * character '\''.
	 */
	@Override
	public String giveFreshVar(final String name) {
		String freshVar = name;
		if (name.contains("\'")) {
			int discrNumber = name.length() - name.indexOf('\'');
			freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			while (this.symbols.contains(freshVar)) {
				discrNumber = discrNumber + 1;
				freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			}
		}
		return freshVar;
	}

	private void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			extraSection(sb, this.macros, "extramacros");
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		this.extramacrosSection(sb);
	}
}
