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

/**
 * This class represents a SMT-LIB annotation.
 */
public final class Annotation {

	/** The attribute. */
	private final String attribute;

	/** The value. */
	private final String value;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds an annotation.
	 * 
	 * @param attribute
	 *            the attribute identifier
	 * @param value
	 *            the user value
	 */
	public Annotation(String attribute, String value) {
	    this.attribute = attribute;
		this.value = value;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the attribute.
	 * 
	 * @return the attribute identifier
	 */
	public final String getAttribute() {
		return attribute;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the user value
	 */
	public final String getValue() {
		return value;
	}
	
	// ========================================================================
	// Other useful methods
	// ========================================================================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Annotation other = (Annotation) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
