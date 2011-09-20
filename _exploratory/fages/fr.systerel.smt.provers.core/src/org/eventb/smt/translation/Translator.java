/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.translation;

import java.util.HashMap;
import java.util.List;

import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTNode;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTLogic;

/**
 * This class is a translator from Event-B syntax to SMT-LIB syntax.
 */
public abstract class Translator implements ISimpleVisitor {
	protected HashMap<Predicate, SMTFormula> predMap = new HashMap<Predicate, SMTFormula>();

	/**
	 * typeMap is a map between Event-B types encountered during the translation
	 * process and SMT-LIB sorts assigned to them. This map is built using an
	 * SMT-LIB Signature that provides fresh type names.
	 */
	protected HashMap<Type, SMTSortSymbol> typeMap = new HashMap<Type, SMTSortSymbol>();
	/**
	 * varMap is a map between Event-B variable names encountered during the
	 * translation process and SMT-LIB symbol names assigned to them. This map
	 * is built using an SMT-LIB Signature that provides fresh type names.
	 */
	protected HashMap<String, SMTSymbol> varMap = new HashMap<String, SMTSymbol>();

	public static boolean DEBUG = false;
	public static boolean DEBUG_DETAILS = false;
	public static boolean DEV = false;

	protected SMTNode<?> smtNode;

	/**
	 * This is the translation method. An Event-B sequent is given to this
	 * method as hypotheses and goal. Must be called by a public static method.
	 */
	protected abstract SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method takes an Event-B type and returns the equivalent in SMT-LIB.
	 */
	protected abstract SMTSymbol translateTypeName(final Type type);

	/**
	 * Determines and returns the SMT-LIB logic to use in order to discharge the
	 * current sequent.
	 */
	protected abstract SMTLogic determineLogic(
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method extracts the type environment from the Event-B sequent and
	 * builds the SMT-LIB signature to use.
	 */
	protected abstract void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal);

	/**
	 * This method returns the current SMT node.
	 */
	protected SMTFormula getSMTFormula() {
		if (smtNode instanceof SMTFormula) {
			return (SMTFormula) smtNode;
		} else {
			throw new IllegalArgumentException(Messages.Translation_error);
		}
	}

	/**
	 * Clears the formula
	 */
	protected void clearFormula() {
		smtNode = null;
	}
}
