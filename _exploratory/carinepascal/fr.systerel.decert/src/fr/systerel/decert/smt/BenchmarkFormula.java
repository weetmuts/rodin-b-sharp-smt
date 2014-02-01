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
package fr.systerel.decert.smt;

import fr.systerel.decert.LemmaPredicate;

/**
 * This class represents a SMT-LIB formula.
 */
public class BenchmarkFormula {

	/** The formula content. */
	final String content;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a benchmark formula from a lemma predicate.
	 * 
	 * @param visitor
	 *            the visitor to be used for the translation
	 * @param predicate
	 *            the predicate to be translated
	 */
	public BenchmarkFormula(final Visitor visitor, final LemmaPredicate predicate) {
        visitor.reset();
		predicate.getContent().accept(visitor);
		content = visitor.getSMTNode();
	}
	
	/**
	 * Builds a benchmark formula.
	 * 
	 * @param content
	 *            the formula content
	 */
	public BenchmarkFormula(final String content) {
		this.content = content;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the formula content.
	 * 
	 * @return a string representation of the formula
	 */
	public final String getContent() {
		return content;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================

	@Override
	public final String toString() {
		return content.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BenchmarkFormula other = (BenchmarkFormula) obj;
		if (!content.equals(other.content))
			return false;
		return true;
	}

}
