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
package fr.systerel.smt.provers.ast;

import org.eventb.core.ast.Type;

import br.ufrn.smt.solver.translation.TranslatorV1_2;

/**
 * This class represents a bound identifier in SMT-LIB grammar.
 */
public final class SMTQuantifiedVariable extends SMTTerm {

	/** The members. */
	private final String identifier;

	private final Type type;

	/**
	 * Creates a new Bound Identifier declaration.
	 */
	SMTQuantifiedVariable(final int tag, final String identifier,
			final Type type) {
		super(tag);
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public void toString(StringBuilder builder) {

		builder.append("(?");
		builder.append(identifier);
		builder.append(" ");
		builder.append(TranslatorV1_2.getSMTAtomicExpressionFormat(type.toString()));
		builder.append(")");

	}
}
