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
 * Common class for SMT-LIB formulas built from connectives.
 */
public class SMTConnectiveFormula extends SMTFormula {
	
	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private final static int firstTag = FIRST_CONNECTIVE_FORMULA;
	
	/** The tags. */
	private final static String[] tags = {
		"not", 
		"implies",
		"if_then_else",
		"and",
		"or",
		"xor",
		"iff"
	};
	
	// =========================================================================
	// Variables
	// =========================================================================
	/** The children. */
	private final SMTFormula[] children;
	
	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a new connective formula with the specified tag.
	 * 
	 * @param tag node tag of this term
	 */
	SMTConnectiveFormula(int tag, SMTFormula[] children) {
		super(tag);
		this.children = children.clone();
		assert getTag() >= firstTag && getTag() < firstTag + tags.length;
		assert children != null;
		assert children.length >= 1;
	}
	
	// =========================================================================
	// Getters
	// =========================================================================
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return a list of children
	 */
	public SMTFormula[] getChildren() {
		return children.clone();
	}
	
	// =========================================================================
	// Other useful methods
	// =========================================================================
	
	@Override
	protected void toString(StringBuilder builder) {
        builder.append('(');
        String sep = tags[getTag() - firstTag] + " ";
		for (SMTFormula child: children) {
			builder.append(sep);
			sep = " ";
			child.toString(builder);
		}
		builder.append(')');
	}

}
