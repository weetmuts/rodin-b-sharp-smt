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
package br.ufrn.smt.solver.translation;

import java.util.List;

import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.Predicate;

import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTNode;

/**
 * 
 */
public abstract class Translator implements ISimpleVisitor {
	protected final SMTFactory sf;
	protected SMTNode<?> smtNode;

	protected Translator() {
		this.sf = SMTFactory.getDefault();
	}

	protected Translator(SMTFactory sf) {
		this.sf = sf;
	}

	public abstract Benchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal);

	protected abstract Signature translateSignature(
			final List<Predicate> hypotheses, final Predicate goal);

	protected abstract Sequent translateSequent(final Signature signature,
			final List<Predicate> hypotheses, final Predicate goal);

	protected SMTFormula getSMTFormula() {
		if (this.smtNode instanceof SMTFormula) {
			return (SMTFormula) this.smtNode;
		} else {
			throw new IllegalArgumentException(
					Messages.Translation_error);
		}
	}
}
