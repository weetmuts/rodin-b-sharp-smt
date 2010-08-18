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


/**
 * The check-sat command response.
 */
public class SMTCheckSatResponse extends SMTCommandResponse {
	// =========================================================================
	// Variables
	// =========================================================================
	/** The status. */
	final SMTStatus status;
	
	// =========================================================================
	// Constructor
	// =========================================================================
	/**
	 * Creates a check-sat command response with the specified tag.
	 * 
	 * @param status response status
	 * @param tag node tag of this command
	 */
	SMTCheckSatResponse(SMTStatus status, int tag) {
		super(tag);
		this.status = status;
	}
	
	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		status.toString(builder);
	}

}
