/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.theories;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;

/**
 * This class represents SMT Theories
 */
public class SMTTheory {
	private final String name;
	private final SMTSortSymbol[] sorts;
	private final SMTPredicateSymbol[] predicates;
	private final SMTFunctionSymbol[] functions;

	protected SMTTheory(final String name, final SMTSortSymbol[] sorts,
			final SMTPredicateSymbol[] predicates,
			final SMTFunctionSymbol[] functions) {
		this.name = name;
		this.sorts = sorts.clone();
		this.predicates = predicates.clone();
		this.functions = functions.clone();
	}

	public String getName() {
		return name;
	}

	public List<SMTSortSymbol> getSorts() {
		return Arrays.asList(sorts);
	}

	public List<SMTPredicateSymbol> getPredicates() {
		return Arrays.asList(predicates);
	}

	public List<SMTFunctionSymbol> getFunctions() {
		return Arrays.asList(functions);
	}

	@Override
	public String toString() {
		return "SMTTheory [name=" + name + ", sorts=" + Arrays.toString(sorts)
				+ ", predicates=" + Arrays.toString(predicates)
				+ ", functions=" + Arrays.toString(functions) + "]";
	}
}
