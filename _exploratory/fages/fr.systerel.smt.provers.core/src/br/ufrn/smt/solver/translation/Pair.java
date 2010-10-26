/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

/**
 * This is a generic class for storing a pair of variables. It is much often
 * used in the classes Translator and LemmaTranslator, principally for helding a
 * variable and it's type or the original rodinXML operator (or operand) and
 * it's SMT format.
 * 
 * 
 * @author Vitor Alcântara de Almeida
 * 
 * @param <X>
 *            The first generic variable
 * @param <Y>
 *            The second generic variable
 */
public class Pair<X, Y> {

	/**
	 * The first generic variable
	 */
	private X key;
	/**
	 * The second generic variable
	 */
	private Y value;

	/**
	 * It initializes an object with the generic variables set as user
	 * 
	 * @param key
	 *            The first variable
	 * @param value
	 *            The second variable
	 */
	public Pair(X key, Y value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the first generic variable
	 * 
	 * @return The fist generic variable
	 */
	public X getKey() {
		return key;
	}

	/**
	 * Returns the second generic variable
	 * 
	 * @return The second generic variable
	 */
	public Y getValue() {
		return value;
	}

	/**
	 * It overrides the Object method toString. It returns the String in the
	 * following format: (first Variable).toString + " -> " + (second
	 * Variable).toString.
	 * 
	 * Ex: if the first var is "House" and the second is "45", it will show:
	 * 
	 * House + -> + 45
	 * 
	 * @return A String which shows the variable in the format above.
	 */
	@Override
	public String toString() {
		return key.toString() + " ->  " + value.toString();
	}

	/**
	 * This method compares two objects of this class. It compares the first
	 * variable of one object with the first of the other and do the same with
	 * the second variable. If both of the methods above return yes, then this
	 * method returns yes.
	 * 
	 * @param o
	 *            The Pair object for comparison.
	 * @return True if the first variable of the two pair objects are equal and
	 *         the second variable of the two pair objects are equal too, else
	 *         False.
	 */
	public boolean equals(Pair<X, Y> o) {
		if (this.getKey().equals(o.getKey())
				&& this.getValue().equals(o.getValue())) {
			return true;
		}
		return false;
	}

}
