/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTTerm;

/**
 * This class is a translator from Event-B syntax into SMT-LIB syntax.
 */
public abstract class TranslatorV1_2 extends Translator {
	/**
	 * Extracts the type environment of a Predicate needed to build an SMT-LIB
	 * benchmark's signature, that is, free identifiers and given types.
	 */
	private static void extractPredicateTypenv(
			final ITypeEnvironment typeEnvironment, final Predicate predicate) {
		for (FreeIdentifier id : predicate.getFreeIdentifiers()) {
			typeEnvironment.add(id);
		}
		for (GivenType type : predicate.getGivenTypes()) {
			typeEnvironment.addGivenSet(type.getName());
		}
	}

	/**
	 * Extracts the type environment of a Event-B sequent
	 */
	protected static ITypeEnvironment extractTypeEnvironment(
			final List<Predicate> hypotheses, final Predicate goal) {
		final FormulaFactory ff = FormulaFactory.getDefault(); // FIXME use real
																// one
		final ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (final Predicate hypothesis : hypotheses) {
			extractPredicateTypenv(typeEnvironment, hypothesis);
		}
		extractPredicateTypenv(typeEnvironment, goal);
		return typeEnvironment;
	}

	/**
	 * SMT translation of an Event-B formula that is a term in SMT-LIB V1.2
	 * language.
	 */
	protected SMTTerm smtTerm(Formula<?> formula) {
		formula.accept(this);
		if (smtNode instanceof SMTTerm) {
			return (SMTTerm) smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTTerm'.");
		}
	}

	/**
	 * SMT translation of an Event-B formula that is a formula in SMT-LIB V1.2
	 * language.
	 */
	protected SMTFormula smtFormula(Formula<?> formula) {
		formula.accept(this);
		if (smtNode instanceof SMTFormula) {
			return (SMTFormula) smtNode;
		} else {
			throw new IllegalArgumentException(
					"This node type should be 'SMTFormula'.");
		}
	}

	/**
	 * SMT translation of two Event-B formulas (left and right children of a
	 * node) which are terms in SMT-LIB V1.2 language.
	 */
	protected SMTTerm[] smtTerms(Formula<?> left, Formula<?> right) {
		return new SMTTerm[] { smtTerm(left), smtTerm(right) };
	}

	/**
	 * SMT translation of two Event-B formulas (left and right children of a
	 * node) which are formulas in SMT-LIB V1.2 language.
	 */
	protected SMTFormula[] smtFormulas(Formula<?> left, Formula<?> right) {
		return new SMTFormula[] { smtFormula(left), smtFormula(right) };
	}

	/**
	 * SMT translation of a set of Event-B formulas which are terms in SMT-LIB
	 * V1.2 language.
	 */
	protected SMTTerm[] smtTerms(Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTTerm[] smtTerms = new SMTTerm[length];
		for (int i = 0; i < length; i++) {
			smtTerms[i] = smtTerm(formulas[i]);
		}
		return smtTerms;
	}

	/**
	 * SMT translation of a set of Event-B formulas which are formulas in
	 * SMT-LIB V1.2 language.
	 */
	protected SMTFormula[] smtFormulas(Formula<?>... formulas) {
		final int length = formulas.length;
		final SMTFormula[] smtFormulas = new SMTFormula[length];
		for (int i = 0; i < length; i++) {
			smtFormulas[i] = smtFormula(formulas[i]);
		}
		return smtFormulas;
	}
}