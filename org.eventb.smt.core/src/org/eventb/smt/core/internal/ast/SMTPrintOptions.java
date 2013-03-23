/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

/**
 * Container class for specifying the options to use when printing a benchmark
 * to an external file which will be submitted to an SMT solver.
 * 
 * @author Laurent Voisin
 * @see SMTBenchmark#print(java.io.PrintWriter, SMTPrintOptions)
 */
public class SMTPrintOptions {

	/**
	 * Print SMT-LIB annotations and labels.
	 */
	public boolean printAnnotations;

	/**
	 * Print commands to activate unsat core extraction.
	 */
	public boolean printGetUnsatCoreCommands;

	/**
	 * Print additional commands to work around a soundness bug in the Z3
	 * solver.
	 */
	public boolean printZ3SpecificCommands;

}
