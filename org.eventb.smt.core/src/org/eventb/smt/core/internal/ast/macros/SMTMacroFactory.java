/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.macros;

import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.BCOMP;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.BINTER;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.BUNION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.CARD;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.CARTESIAN_PRODUCT;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.DOM;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.DOMAIN_RESTRICTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.DOMAIN_SUBSTRACTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.EMPTY;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.FCOMP;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.FINITE;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.ID;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.IN;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.INV;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.ISMAX;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.ISMIN;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.MAPSTO;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.NAT;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.NAT1;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.OVR;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.PARTIAL_FUNCTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.PARTIAL_INJECTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.PARTIAL_SURJECTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.PRED;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RANGE;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RANGE_INTEGER;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RANGE_RESTRICTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RANGE_SUBSTRACTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RELATION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.RELATIONAL_IMAGE;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.SETMINUS;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.SUBSET;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.SUBSETEQ;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.SUCC;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.SURJECTIVE_RELATION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_BIJECTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_FUNCTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_INJECTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_RELATION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_SURJECTION;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.TOTAL_SURJECTIVE_RELATION;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.BOOLS;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.INT;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.core.internal.ast.theories.VeritPredefinedTheoryV1_2.POLYMORPHIC;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;

import java.util.HashSet;
import java.util.Set;

import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.ast.SMTSignatureV1_2Verit;
import org.eventb.smt.core.internal.ast.SMTSignatureV2_0Verit;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.SMTVar;
import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;
import org.eventb.smt.core.internal.ast.theories.TheoryV1_2;
import org.eventb.smt.core.internal.ast.theories.VeriTBooleansV1_2;
import org.eventb.smt.core.internal.ast.theories.VeritPredefinedTheoryV1_2;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This class handles macros defined in the extended version of the SMT-LIB for
 * VeriT. It stores macro expressions, Macro Symbols and creates macro
 * enumerations, which are used to translate in extension.
 * 
 * @author vitor almeida
 * 
 */
public abstract class SMTMacroFactory {

	public final static SMTSortSymbol[] EMPTY_SORT = {};

	public static boolean IS_GENERIC_SORT = true;
	public static final String ENUM_PREFIX = "enum";

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	public static SMTPolymorphicSortSymbol makePolymorphicSortSymbol(
			final String symbolName, SMTLIBVersion smtlibVersion) {
		return new SMTPolymorphicSortSymbol(symbolName, smtlibVersion);
	}

	private static final SMTPolymorphicSortSymbol FST_RETURN_SORT = makePolymorphicSortSymbol(
			FST_PAIR_SORT_NAME, V1_2);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = makePolymorphicSortSymbol(
			SND_PAIR_SORT_NAME, V1_2);

	public static SMTSortSymbol PAIR_SORT = makePolymorphicSortSymbol(
			"(Pair 's 't)", V1_2);

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_ARG_SORTS, PAIR_SORT, false, !PREDEFINED, V1_2);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	public static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, false, !PREDEFINED, V1_2);

	public static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, false, !PREDEFINED, V1_2);

	public static final SMTSymbol[] PAIR_AND_FST_AND_SND_SYMBOLS = { PAIR_SORT,
			PAIR_SYMBOL, FST_SYMBOL, SND_SYMBOL };

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	protected final Set<String> qSymbols = new HashSet<String>();

	protected static SMTPredefinedMacro[] EMPTY_MACROS = {};

	public static SMTPolymorphicSortSymbol[] POLYMORPHIC_PAIRS = { POLYMORPHIC,
			POLYMORPHIC };
	public static SMTPolymorphicSortSymbol[] POLYMORPHICS = { POLYMORPHIC };
	private static SMTSortSymbol[] ISMIN_MAX_SORTS = {
			TheoryV1_2.Ints.getInt(), POLYMORPHIC };
	private static SMTSortSymbol[] FINITE_SORTS = {
			VeriTBooleansV1_2.getInstance().getBooleanSort(), POLYMORPHIC,
			TheoryV1_2.Ints.getInt(), TheoryV1_2.Ints.getInt() };

	private static SMTSortSymbol[] CARD_SORTS = { POLYMORPHIC,
			TheoryV1_2.Ints.getInt(), TheoryV1_2.Ints.getInt() };

	protected static final SMTMacroSymbol RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE, POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);

	protected static final SMTMacroSymbol BOOL_SET_SYMBOL = new SMTMacroSymbol(
			BOOLS, EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static final SMTMacroSymbol INTEGER_SYMBOL = new SMTMacroSymbol(
			INT, EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol CARD_SYMBOL = new SMTMacroSymbol(CARD,
			CARD_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol FINITE_SYMBOL = new SMTMacroSymbol(FINITE,
			FINITE_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol ISMAX_SYMBOL = new SMTMacroSymbol(ISMAX,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol ISMIN_SYMBOL = new SMTMacroSymbol(ISMIN,
			ISMIN_MAX_SORTS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol SETMINUS_SYMBOL = new SMTMacroSymbol(SETMINUS,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol RELATIONAL_IMAGE_SYMBOL = new SMTMacroSymbol(
			RELATIONAL_IMAGE, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol DOMAIN_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol DOMAIN_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			DOMAIN_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol CARTESIAN_PRODUCT_SYMBOL = new SMTMacroSymbol(
			CARTESIAN_PRODUCT, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol TOTAL_BIJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_BIJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol TOTAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	public static SMTMacroSymbol PARTIAL_SURJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_SURJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	public static SMTMacroSymbol TOTAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PARTIAL_INJECTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_INJECTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol DOM_SYMBOL = new SMTMacroSymbol(DOM,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol INVERSE_SYMBOL = new SMTMacroSymbol(INV,
			POLYMORPHICS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol NAT1_SYMBOL = new SMTMacroSymbol(NAT1,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol NAT_SYMBOL = new SMTMacroSymbol(NAT,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol ID_SYMBOL = new SMTMacroSymbol(ID,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol TOTAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			TOTAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PARTIAL_FUNCTION_SYMBOL = new SMTMacroSymbol(
			PARTIAL_FUNCTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol TOTAL_SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC,
			!PREDEFINED, V1_2);
	protected static SMTMacroSymbol SURJECTIVE_RELATION_SYMBOL = new SMTMacroSymbol(
			SURJECTIVE_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol TOTAL_RELATION_SYMBOL = new SMTMacroSymbol(
			TOTAL_RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol RELATION_SYMBOL = new SMTMacroSymbol(
			RELATION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol RANGE_RESTRICTION_SYMBOL = new SMTMacroSymbol(
			RANGE_RESTRICTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol RANGE_SUBSTRACTION_SYMBOL = new SMTMacroSymbol(
			RANGE_SUBSTRACTION, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED,
			V1_2);
	protected static SMTMacroSymbol INTEGER_RANGE_SYMBOL = new SMTMacroSymbol(
			RANGE_INTEGER, VeritPredefinedTheoryV1_2.getIntIntTab(),
			POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUBSETEQ_SYMBOL = new SMTMacroSymbol(
			SUBSETEQ, POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUBSET_SYMBOL = new SMTMacroSymbol(SUBSET,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol IN_SYMBOL = new SMTMacroSymbol(IN,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BUNION_SYMBOL = new SMTMacroSymbol(BUNION,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BINTER_SYMBOL = new SMTMacroSymbol(BINTER,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol FCOMP_SYMBOL = new SMTMacroSymbol(FCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol BCOMP_SYMBOL = new SMTMacroSymbol(BCOMP,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol REL_OVR_SYMBOL = new SMTMacroSymbol(OVR,
			POLYMORPHIC_PAIRS, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol EMPTYSET_SYMBOL = new SMTMacroSymbol(EMPTY,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol SUCC_SYMBOL = new SMTMacroSymbol(SUCC,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);
	protected static SMTMacroSymbol PRED_SYMBOL = new SMTMacroSymbol(PRED,
			EMPTY_SORT, POLYMORPHIC, !PREDEFINED, V1_2);

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
		if (signature instanceof SMTSignatureV1_2Verit) {
			((SMTSignatureV1_2Verit) signature).addPairSortAndFunction();
		} else {
			((SMTSignatureV2_0Verit) signature).addPairSortAndFunction();
		}
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
		if (signature instanceof SMTSignatureV1_2Verit) {
			((SMTSignatureV1_2Verit) signature).addPairSortAndFunction();
		} else {
			((SMTSignatureV2_0Verit) signature).addPairSortAndFunction();
		}
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
		return new SMTSetComprehensionMacro(smtLibVersion, macroName, qVars,
				lambdaVar, formula, expression, 1);
	}

	/**
	 * Adds a predefined macro and other macros on which it depends on the
	 * signature
	 * 
	 * @param operator
	 *            The operator that represents the predefined macro
	 * @param signature
	 *            The signature that will receive the macro
	 */
	public static void addPredefinedMacroInSignature(
			final SMTPredefinedMacro operator,
			final SMTSignatureV1_2Verit signature) {
		final SMTPredefinedMacro pmacro = operator;
		if (pmacro.usesPairFunctionAndSort()) {
			signature.addPairSortAndFunction();
		}
		if (pmacro.usesFstAndSndFunctions()) {
			signature.addFstAndSndAuxiliarFunctions();
		}
		for (final SMTPredefinedMacro macro : pmacro.getRequiredMacros()) {
			addPredefinedMacroInSignature(macro, signature);
			signature.addMacro(macro);
		}
		signature.addMacro(pmacro);
	}

	/**
	 * Creates and returns a macroSymbol.
	 * 
	 * @param macroName
	 *            The string representation of the macro
	 * @return a new macroSymbol
	 */
	public static SMTMacroSymbol makeMacroSymbol(final String macroName,
			final SMTSortSymbol sort) {
		return new SMTMacroSymbol(macroName, EMPTY_SORT, sort, false, V1_2);
	}
}
