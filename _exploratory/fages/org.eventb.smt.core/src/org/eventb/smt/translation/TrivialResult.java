/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.translation;

import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.ast.SMTBenchmark;

/**
 * Encapsulates the trivial predicate produced during the translation of an
 * Event-B sequent.
 * 
 * @author Systerel (yguyot)
 */
public class TrivialResult extends TranslationResult {
	/**
	 * The produced Event-B trivial predicate
	 */
	final private ITrackedPredicate trivialPredicate;

	public TrivialResult(final ITrackedPredicate trivialPredicate) {
		this.trivialPredicate = trivialPredicate;
	}

	@Override
	public boolean isTrivial() {
		return true;
	}

	@Override
	public ITrackedPredicate getTrivialPredicate() {
		return trivialPredicate;
	}

	@Override
	public SMTBenchmark getSMTBenchmark() {
		return null;
	}
}
