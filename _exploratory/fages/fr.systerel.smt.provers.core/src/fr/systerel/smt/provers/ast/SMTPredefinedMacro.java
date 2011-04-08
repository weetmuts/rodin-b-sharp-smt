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

public class SMTPredefinedMacro extends SMTMacro {

	SMTPredefinedMacro(final String macroName, final String bodyText,
			final int precedence) {
		super(macroName, precedence);
		this.body = bodyText;
		extractQSymbols();
	}

	private final String body;

	@Override
	public void toString(StringBuffer builder) {
		// TODO: Nothing
	}

	@Override
	protected void extractQSymbols() {
		for (int i = 0; i < body.length(); i++) {
			if (body.charAt(i) == '?') {
				for (int j = i + 1; j < body.length(); j++) {
					if (body.charAt(j) == ' ') {
						++i;
						++j;
						super.getQSymbols().add(body.substring(i, j));
						i = j;
						break;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append(body);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SMTMacro) {
			SMTMacro objMacro = (SMTMacro) object;
			if (super.getMacroName().equals(objMacro.getMacroName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
