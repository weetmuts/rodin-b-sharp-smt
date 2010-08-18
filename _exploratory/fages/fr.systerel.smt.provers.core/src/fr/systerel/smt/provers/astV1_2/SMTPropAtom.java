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

/**
 * This class represents a propositional atom in SMT-LIB grammar.
 */
public final class SMTPropAtom extends SMTFormula {

	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private static final int firstTag = FIRST_PROP_ATOM;

	/** The tags. */
	private static final String[] tags = { "true", "false" };

	/**
	 * Creates a new propositional atom.
	 * 
	 * @param tag
	 *            the tag
	 */
	SMTPropAtom(int tag) {
		super(tag);
		assert tag >= firstTag && tag < firstTag + tags.length;
	}

	@Override
	protected void toString(StringBuilder builder) {
		builder.append(tags[getTag() - firstTag]);
	}
}
