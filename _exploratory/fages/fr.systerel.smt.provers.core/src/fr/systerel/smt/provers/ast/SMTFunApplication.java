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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * @author guyot
 * 
 */
public class SMTFunApplication extends SMTTerm {
	final SMTFunctionSymbol symbol;
	final SMTTerm[] args;

	// TODO Assert that terms are as many as specified in function symbol rank
	// and that their sorts are the same (a method to put in SMTSignature and to
	// be
	// called by the makeFun method of the factory)
	public SMTFunApplication(final SMTFunctionSymbol symbol,
			final SMTTerm terms[]) {
		this.symbol = symbol;
		this.args = terms.clone();
		this.sort = symbol.getResultSort();
	}

	@Override
	public void toString(StringBuilder builder) {
		if (this.symbol.isConstant()) {
			builder.append(this.symbol.name);
		} else {
			builder.append(OPAR);
			builder.append(this.symbol.name);
			for (SMTTerm arg : this.args) {
				builder.append(SPACE);
				builder.append(arg);
			}
			builder.append(CPAR);
		}
	}
}
