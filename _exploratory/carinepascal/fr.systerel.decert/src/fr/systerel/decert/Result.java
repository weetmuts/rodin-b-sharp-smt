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
package fr.systerel.decert;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.ITypeCheckResult;

/**
 * This class encapsulates the results of the type-checking.
 * 
 */
public final class Result {

	/** The predicate. */
	private final LemmaPredicate predicate;

	/** The result. */
	private final ITypeCheckResult result;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a result.
	 * 
	 * @param predicate
	 *            the checked predicate.
	 * @param result
	 *            the type-checking result.
	 */
	public Result (LemmaPredicate predicate, ITypeCheckResult result) {
		this.predicate = predicate;
		this.result = result;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the predicate.
	 * 
	 * @return a predicate
	 */
	public final LemmaPredicate getLemmaPredicate() {
		return predicate;
	}

	/**
	 * Gets the result.
	 * 
	 * @return a result
	 */
	public final ITypeCheckResult getResult() {
		return result;
	}
	
	// =========================================================================
	// Useful methods
	// =========================================================================
	
	@Override
	public final String toString() {
		String s = "In " + predicate.toString() + ":\n";
		for (ASTProblem problem : result.getProblems())
			s = s + problem.toString() + "\n";
		return s;
	}

}
