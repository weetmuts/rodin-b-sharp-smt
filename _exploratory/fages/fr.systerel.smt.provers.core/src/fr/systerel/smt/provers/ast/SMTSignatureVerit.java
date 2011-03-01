/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	private final Set<String> macros = new HashSet<String>();

	private final Map<String, String> singleQuotVars = new HashMap<String, String>();

	public SMTSignatureVerit(final SMTLogic logic) {
		super(logic);
	}

	private void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			//TODO
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		this.extramacrosSection(sb);
	}

	public void putSingleQuoteVar(final String varName, final String freshName) {
		this.singleQuotVars.put(varName, freshName);
	}

	public void addSort(final String sortName, final boolean predefined) {
		final SMTSortSymbol sort = new SMTSortSymbol(sortName, predefined);
		if (!this.sorts.contains(sort)) {
			this.sorts.add(sort);
		}
	}

	public void addPred(final String predName, final String argSortStrings[]) {
		final SMTSortSymbol[] argSorts = new SMTSortSymbol[argSortStrings.length];
		for (int i = 0; i < argSortStrings.length; i++) {
			argSorts[i] = new SMTSortSymbol(argSortStrings[i],
					!SMTSymbol.PREDEFINED);
		}
		this.preds.add(new SMTPredicateSymbol(predName, argSorts,
				!SMTSymbol.PREDEFINED));
	}

	public void addPairPred(final String predName, final String sortSymb1,
			final String sortSymb2) {
		final SMTSortSymbol sort1 = new SMTSortSymbol(sortSymb1,
				!SMTSymbol.PREDEFINED);
		final SMTSortSymbol sort2 = new SMTSortSymbol(sortSymb2,
				!SMTSymbol.PREDEFINED);
		final StringBuilder strSort = new StringBuilder();
		strSort.append("(Pair ");
		strSort.append(sort1.toString());
		strSort.append(" ");
		strSort.append(sort2.toString());
		strSort.append(")");
		final SMTSortSymbol[] pair = { new SMTSortSymbol(strSort.toString(),
				!SMTSymbol.PREDEFINED) };
		this.preds.add(new SMTPredicateSymbol(predName, pair,
				!SMTSymbol.PREDEFINED));
	}

	public void addFun(final String funName, final String argSorts[],
			final String resultSort) {
		final List<SMTSortSymbol> args = new ArrayList<SMTSortSymbol>();
		for (final String arg : argSorts) {
			args.add(new SMTSortSymbol(arg, !SMTSymbol.PREDEFINED));
		}
		this.funs.add(new SMTFunctionSymbol(funName, (SMTSortSymbol[]) args
				.toArray(),
				new SMTSortSymbol(resultSort, !SMTSymbol.PREDEFINED), false,
				!SMTSymbol.PREDEFINED));
	}

	public Set<SMTSortSymbol> getSorts() {
		return this.sorts;
	}
}