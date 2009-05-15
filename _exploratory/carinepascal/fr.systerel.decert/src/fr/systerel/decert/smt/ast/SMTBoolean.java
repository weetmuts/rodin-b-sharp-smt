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
package fr.systerel.decert.smt.ast;

/**
 * This class represents a boolean in SMT-LIB grammar.
 */
public final class SMTBoolean extends SMTBaseTerm {

	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private static final int firstTag = FIRST_BOOLEAN_TERM;
	
	/** The tags. */
	private static final String tags[] = {
		"TRUE",    
		"FALSE",   
	};

	/**
	 * Creates a new boolean.
	 * 
	 * @param tag
	 *            the tag
	 */
	SMTBoolean(int tag) {
		super(tags[tag - firstTag], tag);
		assert tag >= firstTag && tag < firstTag + tags.length;
	}
}
