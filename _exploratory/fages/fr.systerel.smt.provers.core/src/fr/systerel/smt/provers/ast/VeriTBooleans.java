/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;

public class VeriTBooleans extends SMTTheory implements ISMTBooleanSort {

	private static final String BOOLS_THEORY_NAME = "Bools";

	private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
			SMTSymbol.BOOL, !PREDEFINED);
	private static final SMTSortSymbol[] SORTS = { BOOL_SORT };

	private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol("TRUE",
			SMTMacroFactory.EMPTY_SORT, BOOL_SORT, false, false);

	private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
			"FALSE", SMTMacroFactory.EMPTY_SORT, BOOL_SORT, false, false);

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