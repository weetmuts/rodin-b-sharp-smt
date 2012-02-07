/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.translation;

import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.internal.ast.SMTBenchmark;

/**
 * Abstract method for encapsulating the translation result of an Event-B
 * sequent to SMT-LIB.
 * 
 * @author Systerel (yguyot)
 */
public abstract class TranslationResult {
	/**
	 * Returns <code>true</code> if the translation produced a trivial
	 * predicate, <code>false</code> otherwise.
	 */
	public abstract boolean isTrivial();

	/**
	 * Returns the trivial predicate produced while translating the original
	 * Event-B sequent.
	 */
	public abstract ITrackedPredicate getTrivialPredicate();

	/**
	 * Returns the SMT-LIB benchmark produced by translating the Event-B
	 * sequent.
	 */
	public abstract SMTBenchmark getSMTBenchmark();

}
