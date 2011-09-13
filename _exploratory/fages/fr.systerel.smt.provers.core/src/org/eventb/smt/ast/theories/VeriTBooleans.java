/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.theories;

import static org.eventb.smt.ast.SMTFactory.EMPTY_SORT;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;

import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;

public class VeriTBooleans extends SMTTheory implements ISMTBooleanSort {

	private static final String BOOLS_THEORY_NAME = "Bools";

	private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
			SMTSymbol.BOOL, !PREDEFINED, V1_2);
	private static final SMTSortSymbol[] SORTS = { BOOL_SORT };

	private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol("TRUE",
			EMPTY_SORT, BOOL_SORT, false, false, V1_2);

	private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
			"FALSE", EMPTY_SORT, BOOL_SORT, false, false, V1_2);

	private final static SMTPredicateSymbol[] PREDICATES = {};

	private static SMTFunctionSymbol[] FUNCTIONS = { TRUE, FALSE };

	private static final VeriTBooleans INSTANCE = new VeriTBooleans();

	private VeriTBooleans() {
		super(BOOLS_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	public static VeriTBooleans getInstance() {
		return INSTANCE;
	}

	public SMTFunctionSymbol getTrueConstant() {
		return TRUE;
	}

	public SMTFunctionSymbol getFalseConstant() {
		return FALSE;
	}

	@Override
	public SMTSortSymbol getBooleanSort() {
		return BOOL_SORT;
	}
}