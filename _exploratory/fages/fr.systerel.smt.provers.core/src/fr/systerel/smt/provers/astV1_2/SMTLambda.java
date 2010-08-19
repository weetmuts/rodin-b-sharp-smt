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
package fr.systerel.smt.provers.astV1_2;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

import br.ufrn.smt.solver.translation.TypeEnvironment;

public final class SMTLambda extends SMTTerm {

	/** The right handside formula. */
	private final FreeIdentifier[] leftHandside ;
	
	/** The left handside formula. */
	private final SMTTerm[] rightHandSide;
	
	SMTLambda(int tag, FreeIdentifier[] leftHandside, SMTTerm[] rightHandSide) {
		super(tag);
		this.rightHandSide = rightHandSide;
		this.leftHandside = leftHandside;
		assert tag == SMTNode.LAMBDA;
		assert rightHandSide != null;
		assert leftHandside != null;
	}
	
	@Override
	protected void toString(StringBuilder builder) {
		final String sep = " ";
		builder.append('(');
		builder.append("lambda");
		for (FreeIdentifier identifier: leftHandside) {
			builder.append("(?");
			identifier.getName();
			builder.append(sep);
			builder.append(TypeEnvironment
					.getSMTAtomicExpressionFormat(identifier
							.getType().toString()) + ")");
		}
		
		for (SMTTerm predicate: rightHandSide) {
			predicate.toString(builder);
		}
	}
}