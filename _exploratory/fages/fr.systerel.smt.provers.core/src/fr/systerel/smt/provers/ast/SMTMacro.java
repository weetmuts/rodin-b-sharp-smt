/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import java.util.HashSet;
import java.util.Set;

public abstract class SMTMacro implements Comparable<SMTMacro> {

	protected int precedence;

	SMTMacro(String macroName, int precedence) {
		this.macroName = macroName;
		this.precedence = precedence;
	}

	public String getMacroName() {
		return macroName;
	}

	private String macroName;
	private final Set<String> qSymbols = new HashSet<String>();

	public Set<String> getQSymbols() {
		return qSymbols;
	}

	public abstract void toString(StringBuffer builder);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SMTMacro) {
			SMTMacro macroObj = (SMTMacro) obj;
			if (macroObj.getMacroName().equals(this.getMacroName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(SMTMacro o) {
		if (o.getPrecedence() == precedence) {
			return 1;
		} else if (precedence > o.getPrecedence()) {
			return 1;
		} else
			return -1;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	protected abstract void extractQSymbols();

}
