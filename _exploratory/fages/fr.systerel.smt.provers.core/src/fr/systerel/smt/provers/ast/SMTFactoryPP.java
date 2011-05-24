/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * This class stores methods used to make SMT-LIB elements. This class is used
 * only in the PP translation approach
 */
public class SMTFactoryPP extends SMTFactory {
	private final static SMTFactory DEFAULT_INSTANCE = new SMTFactoryPP();

	/**
	 * returns the instance of {@link SMTFactoryPP}
	 * 
	 * @return the instance of {@link SMTFactoryPP}
	 */
	public static SMTFactory getInstance() {
		return DEFAULT_INSTANCE;
	}
}
