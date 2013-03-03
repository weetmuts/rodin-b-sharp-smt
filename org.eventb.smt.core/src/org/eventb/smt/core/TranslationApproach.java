/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core;

/**
 * Enumeration describing which translation approach to use.
 */
public enum TranslationApproach {

	USING_PP("PP"), USING_VERIT("veriT");

	public static TranslationApproach parseApproach(final String value) {
		if (USING_VERIT.name.equals(value)) {
			return USING_VERIT;
		}
		return USING_PP;
	}

	private final String name;

	private TranslationApproach(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
