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

import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * A SMT command response.
 */
public abstract class SMTCommandResponse extends SMTNode<SMTCommandResponse> {

	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	final static int firstTag = FIRST_RESPONSE;

	/** The tags. */
	final static String[] tags = { "", };

	/**
	 * <code>GET_INFO</code> is the tag for the get-info command response.
	 * 
	 * @see SMTGetInfoResponse
	 */
	public final static int GET_INFO = FIRST_RESPONSE + 0;
	
	/**
	 * <code>CHECK_SAT</code> is the tag for the check-sat command response.
	 * 
	 * @see SMTCheckSatResponse
	 */
	public final static int CHECK_SAT = FIRST_RESPONSE + 1;
	
	/**
	 * <code>GET_ASSERTIONS</code> is the tag for the get-assertions command response.
	 * 
	 * @see SMTGetAssertionsResponse
	 */
	public final static int GET_ASSERTIONS = FIRST_RESPONSE + 2;
	
	/**
	 * <code>GET_PROOF</code> is the tag for the get-proof command response.
	 * 
	 * @see SMTGetProofResponse
	 */
	public final static int GET_PROOF = FIRST_RESPONSE + 3;
	
	/**
	 * <code>GET_UNSAT_CORE</code> is the tag for the get-unsat-core command response.
	 * 
	 * @see SMTGetUnsatCoreResponse
	 */
	public final static int GET_UNSAT_CORE = FIRST_RESPONSE + 4;
	
	/**
	 * <code>GET_VALUE</code> is the tag for the get-value command response.
	 * 
	 * @see SMTGetValueResponse
	 */
	public final static int GET_VALUE = FIRST_RESPONSE + 5;
	
	/**
	 * <code>GET_ASSIGNMENT</code> is the tag for the get-assignment command response.
	 * 
	 * @see SMTGetAssignmentResponse
	 */
	public final static int GET_ASSIGNMENT = FIRST_RESPONSE + 6;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Creates a new command response with the specified tag.
	 * 
	 * @param tag
	 *            node tag of this term
	 */
	public SMTCommandResponse(int tag) {
		super(tag);
		if (this.getTag() < firstTag || this.getTag() >= firstTag + tags.length) {
			throw new IllegalTagException(this.getTag());
		}
	}
}
