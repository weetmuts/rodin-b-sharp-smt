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

	public static SMTLIBVersion parseVersion(String name) {
		if (V1_2.name.equals(name)) {
			return V1_2;
		}
		return V2_0;
	}

	private final String name;

	private SMTLIBVersion(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
