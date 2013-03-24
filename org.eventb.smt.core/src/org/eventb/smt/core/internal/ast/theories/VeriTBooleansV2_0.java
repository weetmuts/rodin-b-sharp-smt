/*******************************************************************************
 * Copyright (c) 2012 UFRN and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UFRN - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.theories;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.PREDEFINED;

import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;

public class VeriTBooleansV2_0 extends Theory implements IBoolSort {

	public final static SMTSortSymbol[] EMPTY_SORT = {};

	private static final String BOOLS_THEORY_NAME = "Bools";
	public static SMTPolymorphicSortSymbol POLYMORPHIC = new SMTPolymorphicSortSymbol(
			V2_0);

	private final static SMTSortSymbol BOOL_SORT = new SMTSortSymbol(
			SMTSymbol.BOOL, !PREDEFINED, V2_0);
	private static final SMTSortSymbol[] SORTS = { BOOL_SORT };

	private final static SMTFunctionSymbol TRUE = new SMTFunctionSymbol("TRUE",
			EMPTY_SORT, BOOL_SORT, !ASSOCIATIVE, !PREDEFINED, V2_0);

	private final static SMTFunctionSymbol FALSE = new SMTFunctionSymbol(
			"FALSE", EMPTY_SORT, BOOL_SORT, !ASSOCIATIVE, !PREDEFINED, V2_0);

	private final static SMTPredicateSymbol[] PREDICATES = {};

	private static SMTFunctionSymbol[] FUNCTIONS = { TRUE, FALSE };

	private static final VeriTBooleansV2_0 INSTANCE = new VeriTBooleansV2_0();

	private VeriTBooleansV2_0() {
		super(BOOLS_THEORY_NAME, SORTS, PREDICATES, FUNCTIONS);
	}

	public static VeriTBooleansV2_0 getInstance() {
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
