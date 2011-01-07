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
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSymbol;

/**
 * This class is a translator from Event-B syntax to SMT-LIB syntax.
 */
public abstract class Translator implements ISimpleVisitor {

	public static boolean DEBUG = false;

	protected final SMTFactory sf;
	protected SMTNode<?> smtNode;

	protected Translator() {
		this.sf = SMTFactory.getDefault();
	}

	protected Translator(SMTFactory sf) {
		this.sf = sf;
	}

	/**
	 * This is the translation method. An Event-B sequent is given to this
	 * method as hypotheses and goal. Must be called by a public static method.
	 */
	protected abstract SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method takes an Event-B type and returns the equivalent in SMT-LIB.
	 */
	protected abstract SMTSymbol translateTypeName(final SMTSignature signature, final Type type);

	/**
	 * This method extracts the type environment from the Event-B sequent and
	 * builds the SMT-LIB signature to use.
	 */
	protected abstract SMTSignature translateSignature(
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method returns the current SMT node.
	 */
	protected SMTFormula getSMTFormula() {
		if (this.smtNode instanceof SMTFormula) {
			return (SMTFormula) this.smtNode;
		} else {
			throw new IllegalArgumentException(Messages.Translation_error);
		}
	}
}
