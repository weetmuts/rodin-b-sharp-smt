/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
 * A SMT boolean.
 */
public final class SMTBoolean extends SMTToken {
	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private static final int firstTag = FIRST_BOOLEAN;
	
	/**
	 * <code>TRUE</code> is the tag for the boolean <code>true</code>.
	 * 
	 * @see SMTBoolean
	 */
	public final static int TRUE = FIRST_BOOLEAN + 0;
	
	/**
	 * <code>FALSE</code> is the tag for the boolean <code>false</code>.
	 * 
	 * @see SMTBoolean
	 */
	public final static int FALSE = FIRST_BOOLEAN + 1;
	
	/** The tags. */
	private static final String tags[] = {
		"true",    
		"false",   
	};
	
	// =========================================================================
	// Constructor
	// =========================================================================
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
