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

import java.util.ArrayList;

import org.eventb.core.ast.Type;

import br.ufrn.smt.solver.translation.TypeEnvironment;

/**
 * This class represents a bound identifier in SMT-LIB grammar.
 */
public final class SMTBoundIdentifierDecl extends SMTTerm {

	/** The members. */
	private final String name;

	private final Type type;

	/**
	 * Creates a new Bound Identifier declaration.
	 * 
	 * @param tag
	 *            the tag
	 * @param identifier
	 *            the identifier name
	 * @param Type
	 *            the type of the identifier
	 */
	SMTBoundIdentifierDecl(int tag, String name, Type type) {
		super(tag);
		this.name = name;
		this.type = type;
	}

	@Override
	public void toString(StringBuilder builder) {

		builder.append("(?");
		builder.append(name);
		builder.append(" ");
		builder.append(TypeEnvironment.getSMTAtomicExpressionFormat(type
				.toString()));
		builder.append(")");

	}
}
