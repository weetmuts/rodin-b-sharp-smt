/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

/**
 * This is the interface for boolean sort
 */
public interface ISMTBooleanSort {

	/**
	 * returns the boolean sort.
	 * 
	 * @return the boolean sort.
	 */
	public SMTSortSymbol getBooleanSort();
}