/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.translation;

import org.eventb.smt.translation.SMTLIBVersion;

/**
 * Known SMT-LIB language versions.
 */
public enum SMTLIBVersion {

	/**
	 * The original SMT-LIB language used by the SMT Plug-in
	 */
	V1_2,
	/**
	 * The SMT-LIB language used by the SMT Plug-in since TODO when done.
	 */
	V2_0;

	/**
	 * The latest language version supported by the AST library.
	 */
	public static SMTLIBVersion LATEST = latest();

	private static SMTLIBVersion latest() {
		final SMTLIBVersion[] values = values();
		return values[values.length - 1];
	}
}
