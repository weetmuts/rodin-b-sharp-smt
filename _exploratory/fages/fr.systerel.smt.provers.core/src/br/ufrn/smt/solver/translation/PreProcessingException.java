/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vítor Alcântara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

public class PreProcessingException extends RuntimeException {
	
	private static final long serialVersionUID = 7635224872741003448L;
	
	public PreProcessingException() {
			super("The SMT File was not well pre-processed in solver");
	}
	public PreProcessingException(String cause) {
		super(cause);
	
}

}
