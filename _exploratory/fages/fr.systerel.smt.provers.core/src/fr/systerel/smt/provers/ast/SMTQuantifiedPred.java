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


/**
 * This class represents a bound identifier in SMT-LIB grammar.
 */
public final class SMTQuantifiedPred extends SMTFormula {
	
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private static final int firstTag = QUANTIFIER_SYMBOL;

	/** The tags. */
	private static final String[] tags = { "exists", "forall" };

	/** The members. */
	private final SMTTerm[] boundIdents;
	
	private final SMTFormula[] preds;
	
	
	/**
	 * Creates a new Bound Identifier declaration.
	 */
	SMTQuantifiedPred(int tag,SMTTerm[] boundIdents, SMTFormula[] preds) {
		super(tag);			
		this.boundIdents = boundIdents;
		this.preds = preds;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append("(");
		String sep = tags[getTag() - firstTag] + " ";
		for (SMTTerm child: boundIdents) {
			builder.append(sep);
			sep = " ";
			child.toString(builder);
		}
		
		for (SMTFormula pred : preds) {
			pred.toString(builder);
		}
		builder.append(")");
	
	}
}
