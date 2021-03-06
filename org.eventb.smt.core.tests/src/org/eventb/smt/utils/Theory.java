/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.utils;

import static org.eventb.smt.utils.Theory.TheoryLevel.L1;
import static org.eventb.smt.utils.Theory.TheoryLevel.L2;
import static org.eventb.smt.utils.Theory.TheoryLevel.L3;

import java.util.HashSet;
import java.util.Set;

/**
 * This class enumerates mathematical theories.
 */
public enum Theory {
	/**
	 * Constant LISTS
	 */
	LISTS("lists", L3),

	/**
	 * Constant ARRAYS
	 */
	ARRAYS("arrays", L3),

	/**
	 * Constant BASIC_SET
	 */
	BASIC_SET("basic_set", L1),

	/**
	 * Constant BASIC_RELATION
	 */
	BASIC_RELATION("basic_relation", L2),

	/**
	 * Constant FULL_SET_THEORY
	 */
	FULL_SET_THEORY("full_set_theory", L3),

	/**
	 * Constant INTEGER
	 */
	INTEGER("integer", L1),

	/**
	 * Constant LINEAR_ORDER_INT
	 */
	LINEAR_ORDER_INT("linear_order_int", L1),

	/**
	 * Constant LINEAR_ARITH
	 */
	LINEAR_ARITH("linear_arith", L1),

	/**
	 * Constant NONLINEAR_ARITH
	 */
	NONLINEAR_ARITH("nonlinear_arith", L3),

	/**
	 * Constant FULL_ARITH
	 */
	FULL_ARITH("full_arith", L3),

	/**
	 * Constant BOOLEAN
	 */
	BOOLEAN("boolean", L1),

	;

	public enum TheoryLevel {
		L1("Level 1"), L2("Level 2"), L3("Level 3");

		private final String name;

		private TheoryLevel(final String name) {
			this.name = name;
		}

		/**
		 * Gets the theory level name.
		 * 
		 * @return the name
		 */
		public final String getName() {
			return name;
		}

		/**
		 * Gets the theory level with the specified name.
		 * 
		 * @param name
		 *            the theory level name
		 * @return the theory level whose name is <tt>name</tt>
		 */
		public final static TheoryLevel fromName(final String name) {
			for (final TheoryLevel level : TheoryLevel.values())
				if (level.name.equals(name))
					return level;
			throw new IllegalArgumentException(name);
		}
	}

	/** The theory name. */
	private final String name;
	private final TheoryLevel level;

	private Theory(final String name, final TheoryLevel level) {
		this.name = name;
		this.level = level;
	}

	/**
	 * Gets the theory name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	public final TheoryLevel getLevel() {
		return level;
	}

	/**
	 * Gets the theory with the specified name.
	 * 
	 * @param name
	 *            the theory name
	 * @return the theory whose name is <tt>name</tt>
	 */
	public final static Theory fromName(final String name) {
		for (final Theory theory : Theory.values())
			if (theory.name.equals(name))
				return theory;
		throw new IllegalArgumentException(name);
	}

	/**
	 * Gets the theories with the specified names.
	 * 
	 * @param names
	 *            the theories names
	 * @return the theories whose name are <tt>names</tt>
	 */
	public final static Set<Theory> fromNames(final Set<String> names) {
		final Set<Theory> theories = new HashSet<Theory>();
		for (final String name : names) {
			theories.add(fromName(name));
		}
		return theories;
	}

	/**
	 * Gets the set of known theories of the specified level.
	 * 
	 * @param level
	 *            the theories level
	 * @return the theories whose level is <tt>level</tt>
	 */
	public final static Set<Theory> fromLevel(final TheoryLevel level) {
		final Set<Theory> theories = new HashSet<Theory>();
		for (final Theory theory : Theory.values())
			if (theory.level.equals(level))
				theories.add(theory);
		return theories;
	}

	/**
	 * Gets the level of a theories combo, that is :</ br> level 1 if the combo
	 * contains only theories of level 1</ br> level 2 if the combo contains
	 * only theories of level 1 or 2 and at least one theory of level 2 </ br>
	 * level 3 if the combo contains at least one theory of level 3.
	 * 
	 * @param theories
	 *            the theories combo
	 * @return the level of this combo
	 */
	public final static TheoryLevel getComboLevel(final Set<Theory> theories) {
		TheoryLevel level = L1;
		if (theories != null) {
			for (final Theory theory : theories) {
				if (theory.level.equals(L2)) {
					level = L2;
				} else if (theory.level.equals(L3)) {
					return L3;
				}
			}
		}
		return level;
	}
}
