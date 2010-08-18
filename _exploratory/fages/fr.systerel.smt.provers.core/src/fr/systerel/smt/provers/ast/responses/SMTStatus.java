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
package fr.systerel.smt.provers.ast.responses;

import fr.systerel.smt.provers.ast.SMTToken;

/**
 * The status of an SMT script response.
 */
public final class SMTStatus extends SMTToken {
	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private static final int firstTag = FIRST_STATUS;
	
	/**
	 * <code>SAT</code> is the tag for the sat status.
	 * 
	 * @see SMTStatus
	 */
	public final static int SAT = FIRST_STATUS + 0;
	
	/**
	 * <code>UNSAT</code> is the tag for the unsat status.
	 * 
	 * @see SMTStatus
	 */
	public final static int UNSAT = FIRST_STATUS + 1;
	
	/**
	 * <code>UNKNOWN</code> is the tag for the unknown status.
	 * 
	 * @see SMTStatus
	 */
	public final static int UNKNOWN = FIRST_STATUS + 2;
	
	/** The tags. */
	private static final String tags[] = {
		"sat",    
		"unsat",
		"unknown",
	};
	
	// =========================================================================
	// Constructor
	// =========================================================================
	/**
	 * Creates a new status.
	 * 
	 * @param tag
	 *            the tag
	 */
	SMTStatus(int tag) {
		super(tags[tag - firstTag], tag);
		assert tag >= firstTag && tag < firstTag + tags.length;
	}
}
