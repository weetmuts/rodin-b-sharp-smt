/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.translation;

/**
 * Known SMT-LIB language versions.
 */
public enum SMTLIBVersion {

	/**
	 * The original SMT-LIB language used by the SMT Plug-in
	 */
	V1_2("V1.2"),
	/**
	 * The current SMT-LIB language used by the SMT Plug-in
	 */
	V2_0("V2.0");

	final String value;

	/**
	 * The latest language version supported by the AST library.
	 */
	public static SMTLIBVersion LATEST = latest();

	private static SMTLIBVersion latest() {
		final SMTLIBVersion[] values = values();
		return values[values.length - 1];
	}

	private SMTLIBVersion(final String value) {
		this.value = value;
	}

	public static SMTLIBVersion getVersion(final String value) {
		if (value.equals(V2_0.value)) {
			return V2_0;
		} else {
			return V1_2;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
