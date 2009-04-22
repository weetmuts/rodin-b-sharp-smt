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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;

/**
 * This class represents a predicate.
 */
public class LemmaPredicate {

	/**
	 * Determines whether this hypothesis is required or not. This field is
	 * <tt>true</tt> iff the hypothesis is actually required.
	 */
	final boolean required;

	/** The hypothesis content. */
	final Predicate content;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a lemma predicate.
	 * 
	 * @param ff
	 *            the formula factory to be used to parse the content
	 * @param content
	 *            the content to be parsed
	 * @param required
	 *            shall be <tt>true</tt> iff this hypothesis is actually
	 *            required
	 * @throws ParseException
	 *             if a problem occurred when parsing the content
	 */
	public LemmaPredicate(final FormulaFactory ff, final String content,
			final boolean required) throws ParseException {

		IParseResult result = ff.parsePredicate(content,
				LanguageVersion.V2, null);
		if (!result.hasProblem())
			this.content = result.getParsedPredicate();
		else {
			this.content = null;
			ParseException.throwIt(result,
					"A problem occurred when parsing the following predicate: "
							+ content);
		}

		this.required = required;
	}

	/**
	 * Builds a lemma predicate.
	 * 
	 * @param ff
	 *            the formula factory to be used to parse the content
	 * @param content
	 *            the content to be parsed
	 * @throws ParseException
	 *             if a problem occurred when parsing the content
	 */
	public LemmaPredicate(final FormulaFactory ff, final String content)
			throws ParseException {
		this(ff, content, false);
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the predicate content.
	 * 
	 * @return a <tt>Predicate</tt> object matching the predicate content
	 */
	public final Predicate getContent() {
		return content;
	}

	/**
	 * Determines whether this predicate is required or not.
	 * 
	 * @return <tt>true</tt> iff this predicate is actually required
	 */
	public final boolean isRequired() {
		return required;
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
		result = prime * result + (required ? 1231 : 1237);
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
		final LemmaPredicate other = (LemmaPredicate) obj;
		if (!content.equals(other.content))
			return false;
		if (required != other.required)
			return false;
		return true;
	}

}
