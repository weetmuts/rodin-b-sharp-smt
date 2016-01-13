/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol.MAPSTO;
import static org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.PREDEFINED;

import java.util.Set;

import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.internal.ast.macros.SMTMacroSymbol;
import org.eventb.smt.core.internal.ast.macros.SMTMacroTerm;
import org.eventb.smt.core.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPolymorphicSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * This class stores methods used to make extended SMT-LIB elements. This class
 * is used only in the VeriT translation approach
 */
final public class SMTFactoryVeriT extends SMTFactory {

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	public static SMTPolymorphicSortSymbol makePolymorphicSortSymbol(
			final String symbolName, SMTLIBVersion smtlibVersion) {
		return new SMTPolymorphicSortSymbol(symbolName, smtlibVersion);
	}

	/**
	 * An instance of the Factory
	 */
	private final static SMTFactoryVeriT DEFAULT_INSTANCE = new SMTFactoryVeriT();

	public static final SMTSortSymbol PAIR_SORT_V2_0 = makePolymorphicSortSymbol(
			"Pair", V2_0);

	public static final SMTPolymorphicSortSymbol FST_RETURN_SORT = makePolymorphicSortSymbol(
			FST_PAIR_SORT_NAME, V2_0);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = makePolymorphicSortSymbol(
			SND_PAIR_SORT_NAME, V2_0);

	private static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL_V2_0 = new SMTFunctionSymbol(
			MAPSTO, PAIR_ARG_SORTS, PAIR_SORT_V2_0, !ASSOCIATIVE, PREDEFINED,
			V2_0);

	private final static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT_V2_0 };

	public final static SMTFunctionSymbol FST_SYMBOLV_2_0 = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, !ASSOCIATIVE, PREDEFINED, V2_0);

	public final static SMTFunctionSymbol SND_SYMBOL_V2_0 = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, !ASSOCIATIVE, PREDEFINED, V2_0);

	private boolean pairAxiomAdded = false;

	public SMTTerm makeTrueConstant(final SMTFunctionSymbol trueSymbol) {
		return new SMTFunApplication(trueSymbol, new SMTTerm[] {});
	}

	public SMTTerm makeFalseConstant(final SMTFunctionSymbol falseSymbol) {
		return new SMTFunApplication(falseSymbol, new SMTTerm[] {});
	}

	/**
	 * Adds the pair equality axiom. It is added only once.
	 */
	public void addPairEqualityAxiomV2_0(
			final Set<SMTFormula> additionalAssumptions,
			final SMTSignatureV2_0Verit signature) {
		signature.addFstAndSndAuxiliarFunctions();
		if (!pairAxiomAdded) {
			additionalAssumptions.add(createPairEqualityAxiom());
			pairAxiomAdded = true;
		}
	}

	/**
	 * Returns the instance of the factory
	 * 
	 * @return the instance of the factory
	 */
	public static SMTFactoryVeriT getInstance() {
		return DEFAULT_INSTANCE;
	}

	public static SMTTerm makeMacroTerm(final SMTMacroSymbol macro,
			final SMTTerm... args) {
		return new SMTMacroTerm(macro, args);
	}

	public static SMTTerm makeITE(final SMTFormula arg1, final SMTTerm arg2,
			final SMTTerm arg3) {
		return new SMTITETerm(arg1, arg2, arg3);
	}

	/**
	 * Creates the pair equality axiom. It is defined as:
	 * 
	 * (forall (?p1 (Pair 's 't)) (?p2 (Pair 's 't)) (implies (= ?p1 ?p2) (and
	 * (= (fst ?p1) (fst ?p2)) (= (snd ?p1) (snd ?p2)))))
	 * 
	 * @return the pair equality axiom
	 */
	public SMTFormula createPairEqualityAxiom() {

		final SMTVarSymbol pSymbol1 = new SMTVarSymbol("t8", PAIR_SORT_V2_0,
				!PREDEFINED, V2_0);
		final SMTVarSymbol pSymbol2 = new SMTVarSymbol("t9", PAIR_SORT_V2_0,
				!PREDEFINED, V2_0);

		final SMTVar pairVar1 = new SMTVar(pSymbol1, V2_0);
		final SMTVar pairVar2 = new SMTVar(pSymbol2, V2_0);

		final SMTFormula equalsFormula = SMTFactory.makeEqual(new SMTTerm[] {
				pairVar1, pairVar2 }, V2_0);

		final SMTFunApplication fstFunAppl1 = new SMTFunApplication(
				FST_SYMBOLV_2_0, pairVar1);

		final SMTFunApplication fstFunAppl2 = new SMTFunApplication(
				FST_SYMBOLV_2_0, pairVar2);

		final SMTFormula subEqualsFormula1 = SMTFactory.makeEqual(
				new SMTTerm[] { fstFunAppl1, fstFunAppl2 }, V2_0);

		final SMTFunApplication sndFunAppl1 = new SMTFunApplication(
				SND_SYMBOL_V2_0, pairVar1);

		final SMTFunApplication sndFunAppl2 = new SMTFunApplication(
				SND_SYMBOL_V2_0, pairVar2);

		final SMTFormula subEqualsFormula2 = SMTFactory.makeEqual(
				new SMTTerm[] { sndFunAppl1, sndFunAppl2 }, V2_0);

		final SMTFormula andFormula = SMTFactory.makeAnd(new SMTFormula[] {
				subEqualsFormula1, subEqualsFormula2 }, V2_0);

		final SMTFormula impliesFormula = SMTFactory.makeImplies(
				new SMTFormula[] { andFormula, equalsFormula }, V2_0);

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						new SMTTerm[] { pairVar1, pairVar2 }, impliesFormula,
						V2_0);

		return quantifiedFormula;
	}

	public SMTFormula makeDefinitionOfElementsOfBooleanFormula_V2_0(
			final String boolVarName, final SMTSortSymbol boolSort,
			final SMTFunctionSymbol trueSymbol,
			final SMTFunctionSymbol falseSymbol) {

		final SMTTerm trueTerm = new SMTFunApplication(trueSymbol,
				new SMTTerm[] {});
		final SMTTerm falseTerm = new SMTFunApplication(falseSymbol,
				new SMTTerm[] {});
		final SMTVarSymbol boolvarSymbol = new SMTVarSymbol(boolVarName,
				boolSort, false, V2_0);
		final SMTVar boolVar = new SMTVar(boolvarSymbol, V2_0);

		final SMTFormula or = makeOr(
				new SMTFormula[] {
						makeEqual(new SMTTerm[] { boolVar, trueTerm }, V2_0),
						makeEqual(new SMTTerm[] { boolVar, falseTerm }, V2_0) },
				V2_0);

		final SMTFormula forall = makeSMTQuantifiedFormula(
				SMTQuantifierSymbol.FORALL,
				new SMTVarSymbol[] { boolvarSymbol }, or, V2_0);

		final SMTFormula not = makeNot(
				new SMTFormula[] { makeEqual(new SMTTerm[] { trueTerm,
						falseTerm }, V2_0) }, V2_0);

		final SMTFormula and = makeAnd(new SMTFormula[] { forall, not }, V2_0);

		return and;
	}

	/**
	 * Creates and return a pair sort symbol. The string representation of a
	 * pair sort symbol is:
	 * <p>
	 * (Pair A B)
	 * <p>
	 * where A and B are two SMT sort symbols
	 * 
	 * @param sourceSymbol
	 *            The first sort symbol
	 * @param targetSymbol
	 *            The second sort symbol
	 * @return A pair sort symbol
	 */
	public static SMTSortSymbol makePairSortSymbol(SMTLIBVersion smtlibVersion,
			final SMTSortSymbol sourceSymbol, final SMTSortSymbol targetSymbol) {
		// if (smtlibVersion == V1_2) {
		final StringBuffer sb = new StringBuffer();
		sb.append("(Pair ");
		sb.append(sourceSymbol.toString());
		sb.append(" ");
		sb.append(targetSymbol.toString());
		sb.append(")");
		return new SMTSortSymbol(sb.toString(), false, V2_0);
		// } else {
		// final StringBuffer sb = new StringBuffer();
		// sb.append("Pair");
		// return new SMTSortSymbol(sb.toString(), false, V2_0);
		// }

	}

	/**
	 * this method makes a VeriT term. The difference between normal terms and
	 * VeriT terms is that VeriT terms can accept predicate symbols instead of
	 * function symbols. This happens when predicates are used as arguments of
	 * macros, which in this case, the predicates is used with no arguments and
	 * macros are in the terms level.
	 * 
	 * @param smtSymbol
	 *            the symbol of the term
	 * @param signature
	 *            used to check the rank of the term
	 * @return a new SMT term with the symbol
	 */
	public SMTTerm makeVeriTConstantTerm(final SMTSymbol smtSymbol,
			final SMTSignature signature) {
		if (smtSymbol instanceof SMTPredicateSymbol) {
			return new SMTVeriTTerm((SMTPredicateSymbol) smtSymbol, signature);

		} else if (smtSymbol instanceof SMTFunctionSymbol) {
			return makeConstant((SMTFunctionSymbol) smtSymbol, signature);
		} else {
			throw new IllegalArgumentException(
					"In the translation for veriT extended SMT-LIB, the Symbol should be a function or a verit pred symbol");
		}
	}

	public static SMTFormula makeMacroAtom(SMTMacroSymbol macroSymbol,
			SMTTerm[] args, SMTSignatureV2_0Verit signature) {
		return new SMTVeriTAtom(macroSymbol, args, signature);
	}
}
