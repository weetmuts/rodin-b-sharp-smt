/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * This is the interface for boolean sort
 */
public interface ISMTIntegerSort {

	/**
	 * returns the integer sort.
	 * 
	 * @return the integer sort.
	 */
	public SMTSortSymbol getIntegerSort();

	public SMTSortSymbol getPowerSetIntegerSort();
}
