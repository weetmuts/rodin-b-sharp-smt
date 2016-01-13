/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eventb.smt.core.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.SolverKind.CVC3;
import static org.eventb.smt.core.SolverKind.CVC4;
import static org.eventb.smt.core.SolverKind.MATHSAT5;
import static org.eventb.smt.core.SolverKind.OPENSMT;
import static org.eventb.smt.core.SolverKind.UNKNOWN;
import static org.eventb.smt.core.SolverKind.VERIT;
import static org.eventb.smt.core.SolverKind.Z3;

import java.util.EnumMap;
import java.util.List;

import org.eventb.smt.core.SolverKind;

/**
 * Represents the arguments that must be passed to a given solver to tell it the
 * SMT-LIB version in which the input benchmark is encoded.
 * 
 * @author Laurent Voisin
 */
public class SMTVersionArgs {

	/**
	 * Returns the additional arguments that must be passed to the solver of the
	 * given configuration.
	 * 
	 * @param config
	 *            some SMT configuration
	 * @return the additional arguments to pass to the solver
	 */
	public static List<String> getArgs(SMTConfiguration config) {
		final SolverKind kind = config.getKind();
		return args.get(kind);
	}

	private static final List<String> NONE = emptyList();

	private static final EnumMap<SolverKind, List<String>> args;

	static {
		args = new EnumMap<SolverKind, List<String>>(SolverKind.class);
		initialize(ALT_ERGO, NONE);
		initialize(CVC3, asList("-lang", "smt2"));
		initialize(CVC4, asList("--lang", "smt2"));
		initialize(MATHSAT5, NONE);
		initialize(OPENSMT, NONE);
		initialize(VERIT, asList("-i", "smtlib2"));
		initialize(Z3, asList("-smt2"));
		initialize(UNKNOWN, NONE);

		// Ensure map is complete
		assert args.size() == SolverKind.values().length;
	}

	private static void initialize(SolverKind kind, List<String> v2Args) {
		args.put(kind, v2Args);
	}

}
