/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.macros;

import java.util.HashSet;
import java.util.Set;

import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.ast.SMTSignatureV2_0Verit;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.SMTVar;
import org.eventb.smt.core.internal.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and creates macro
 * enumerations, which are used to translate in extension.
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public abstract class SMTMacroFactory {

	public final static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";

	public static SMTPolymorphicSortSymbol makePolymorphicSortSymbol(
			final String symbolName, SMTLIBVersion smtlibVersion) {
		return new SMTPolymorphicSortSymbol(symbolName, smtlibVersion);
	}

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	protected final Set<String> qSymbols = new HashSet<String>();

	protected static final SMTPredefinedMacro[] EMPTY_MACROS = {};


	/**
	 * Retrieves the name of the identifiers that have a question mark as a
	 * prefix.
	 * 
	 * @return the identifiers as defined above.
	 */
	public Set<String> getqSymbols() {
		return qSymbols;
	}

	/**
	 * Creates a macro of sets defined in extension which the elements are a
	 * mapping. An enumeration macro is of the form:
	 * 
	 * (macroName (lambda (x1 s1)(x2 s2) . (or (pair (= x1 t1a)(= x2 t1b) · · ·
	 * (= x1 tna)(= 2 tnb)))));
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param varName1
	 *            The first variable (in the example above: (x1 s1))
	 * @param terms
	 *            The terms of the set defined by extension (in the example
	 *            above: t1a, t1b ... tna,tnb)
	 * @param signature
	 *            The signature used to add predefined macros if necessary
	 * @return The macro of the set defined in extension
	 */
	public static SMTPairEnumMacro makePairEnumerationMacro(
			final String macroName, final SMTVarSymbol varName1,
			final SMTTerm[] terms, final SMTSignature signature) {
		((SMTSignatureV2_0Verit) signature).addPairSortAndFunction();
		return new SMTPairEnumMacro(macroName, varName1, terms, 1);
	}

	public static SMTEnumMacro makeEnumMacro(final SMTLIBVersion version,
			final String macroName, final SMTVarSymbol varName,
			final SMTTerm... terms) {
		return new SMTEnumMacro(version, macroName, varName, terms, 0);
	}

	/**
	 * Creates a macro from lambda expressions and sets defined in extension.
	 * 
	 * This macro created has the following form:
	 * <p>
	 * 
	 * (macroName (lambda (?y s) . (exists (?x1 s1) ... (?xn sn) (and (= ?y
	 * (E(?x1 ... ?xn))) P(?x1..?xn)))))
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param terms
	 *            The terms that contains the bound identifier declarations (in
	 *            the example above: (?x1 s1) ... (?xn sn))
	 * @param lambdaVar
	 *            The lambda variable with the same type as the expression (in
	 *            the example above: ?y)
	 * @param formula
	 * @param expression
	 * @param signature
	 * @return a new set comprehension macro
	 */
	public static SMTSetComprehensionMacro makeSetComprehensionMacro(
			final SMTLIBVersion smtLibVersion, final String macroName,
			final SMTTerm[] terms, final SMTVarSymbol lambdaVar,
			final SMTFormula formula, final SMTTerm expression,
			final SMTSignature signature) {
		((SMTSignatureV2_0Verit) signature).addPairSortAndFunction();
		final SMTVarSymbol[] qVars = new SMTVarSymbol[terms.length];
		for (int i = 0; i < terms.length; i++) {
			final SMTTerm term = terms[i];
			if (term instanceof SMTVar) {
				final SMTVar var = (SMTVar) term;
				qVars[i] = var.getSymbol();
			} else {
				throw new IllegalArgumentException(
						"The term should be an SMTVar");
			}
		}
		return new SMTSetComprehensionMacro(macroName, qVars, lambdaVar, formula, expression, 1);
	}

}
