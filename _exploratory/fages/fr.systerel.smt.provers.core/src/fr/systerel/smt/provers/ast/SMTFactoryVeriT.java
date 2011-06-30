/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import static fr.systerel.smt.provers.ast.macros.SMTMacroSymbol.MAPSTO;

import java.util.Set;

import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.macros.SMTMacroTerm;

/**
 * This class stores methods used to make extended SMT-LIB elements. This class
 * is used only in the VeriT translation approach
 */
final public class SMTFactoryVeriT extends SMTFactory {

	public static final String SND_PAIR_ARG_NAME = "sndArg";
	public static final String FST_PAIR_ARG_NAME = "fstArg";
	private static final String FST_PAIR_SORT_NAME = "'s";
	private static final String SND_PAIR_SORT_NAME = "'t";

	/**
	 * An instance of the Factory
	 */
	private final static SMTFactoryVeriT DEFAULT_INSTANCE = new SMTFactoryVeriT();

	public static SMTSortSymbol PAIR_SORT = SMTFactory
			.makePolymorphicSortSymbol("(Pair 's 't)");

	public static final SMTPolymorphicSortSymbol FST_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(FST_PAIR_SORT_NAME);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(SND_PAIR_SORT_NAME);

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_ARG_SORTS, PAIR_SORT, !ASSOCIATIVE, !PREDEFINED);

	public final static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	public final static SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", PAIR_SORTS, FST_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED);

	public final static SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", PAIR_SORTS, SND_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED);

	private boolean pairAxiomAdded = false;

	public SMTTerm makeTrueConstant(final SMTFunctionSymbol trueSymbol) {
		return new SMTFunApplication(trueSymbol, new SMTTerm[] {});
	}

	public SMTTerm makeFalseConstant(final SMTFunctionSymbol falseSymbol) {
		return new SMTFunApplication(falseSymbol, new SMTTerm[] {});
	}

	public SMTTerm makeBoolConstant(final SMTFunctionSymbol boolsSet) {
		return new SMTFunApplication(boolsSet, new SMTTerm[] {});
	}

	/**
	 * Adds the pair equality axiom. It is added only once.
	 */
	public void addPairEqualityAxiom(
			final Set<SMTFormula> additionalAssumptions,
			final SMTSignatureVerit signature) {
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

	/**
	 * makes and returns a SMT formula which the operator is distinct.
	 * 
	 * @param args
	 *            The arguments of the formula
	 * @return a new SMT formula
	 */
	public static SMTFormula makeDistinct(final SMTTerm[] args) {
		return new SMTAtom(DISTINCT, args);
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

		final SMTVarSymbol pSymbol1 = new SMTVarSymbol("t8", PAIR_SORT,
				!PREDEFINED);
		final SMTVarSymbol pSymbol2 = new SMTVarSymbol("t9", PAIR_SORT,
				!PREDEFINED);

		final SMTVar pairVar1 = new SMTVar(pSymbol1);
		final SMTVar pairVar2 = new SMTVar(pSymbol2);

		final SMTFormula equalsFormula = SMTFactory.makeEqual(new SMTTerm[] {
				pairVar1, pairVar2 });

		final SMTFunApplication fstFunAppl1 = new SMTFunApplication(FST_SYMBOL,
				pairVar1);

		final SMTFunApplication fstFunAppl2 = new SMTFunApplication(FST_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula1 = SMTFactory
				.makeEqual(new SMTTerm[] { fstFunAppl1, fstFunAppl2 });

		final SMTFunApplication sndFunAppl1 = new SMTFunApplication(SND_SYMBOL,
				pairVar1);

		final SMTFunApplication sndFunAppl2 = new SMTFunApplication(SND_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula2 = SMTFactory
				.makeEqual(new SMTTerm[] { sndFunAppl1, sndFunAppl2 });

		final SMTFormula andFormula = SMTFactory.makeAnd(new SMTFormula[] {
				subEqualsFormula1, subEqualsFormula2 });

		final SMTFormula impliesFormula = SMTFactory
				.makeImplies(new SMTFormula[] { andFormula, equalsFormula });

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						new SMTTerm[] { pairVar1, pairVar2 }, impliesFormula);

		return quantifiedFormula;
	}

	public SMTFormula makeDefinitionOfElementsOfBooleanFormula(
			final String boolVarName, final SMTSortSymbol boolSort,
			final SMTFunctionSymbol trueSymbol,
			final SMTFunctionSymbol falseSymbol) {

		final SMTTerm trueTerm = new SMTFunApplication(trueSymbol,
				new SMTTerm[] {});
		final SMTTerm falseTerm = new SMTFunApplication(falseSymbol,
				new SMTTerm[] {});
		final SMTVarSymbol boolvarSymbol = new SMTVarSymbol(boolVarName,
				boolSort, false);
		final SMTVar boolVar = new SMTVar(boolvarSymbol);

		final SMTFormula or = makeOr(new SMTFormula[] {
				makeEqual(new SMTTerm[] { boolVar, trueTerm }),
				makeEqual(new SMTTerm[] { boolVar, falseTerm }) });

		final SMTFormula forall = makeSMTQuantifiedFormula(
				SMTQuantifierSymbol.FORALL,
				new SMTVarSymbol[] { boolvarSymbol }, or);

		final SMTFormula not = makeNot(new SMTFormula[] { makeEqual(new SMTTerm[] {
				trueTerm, falseTerm }) });

		final SMTFormula and = makeAnd(new SMTFormula[] { forall, not });

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
	public static SMTSortSymbol makePairSortSymbol(
			final SMTSortSymbol sourceSymbol, final SMTSortSymbol targetSymbol) {

		final StringBuffer sb = new StringBuffer();
		sb.append("(Pair ");
		sb.append(sourceSymbol.toString());
		sb.append(" ");
		sb.append(targetSymbol.toString());
		sb.append(")");
		return new SMTSortSymbol(sb.toString(), false);
	}

	/**
	 * This method creates and returns a macro atom.
	 * 
	 * @param macroSymbol
	 *            the symbol of the atom
	 * @param args
	 *            the args of the atom
	 * @return a macro atom
	 */
	public static SMTFormula makeMacroAtom(final SMTMacroSymbol macroSymbol,
			final SMTTerm[] args, final SMTSignatureVerit signature) {
		return new SMTVeriTAtom(macroSymbol, args, signature);
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

	/**
	 * This method converts verit SMT-TERM into formulas. These terms must be of
	 * sort Bool.
	 * 
	 * This method is used when the terms being compared are of Bool type. Then,
	 * the "=" operator is substituted by "iff" operator and the terms are
	 * converted into "veriT formulas".
	 * 
	 * @param terms
	 *            the terms
	 * @return the formulas from the terms
	 */
	public SMTFormula[] convertVeritTermsIntoFormulas(final SMTTerm... terms) {
		final SMTFormula[] formulas = new SMTFormula[terms.length];
		int i = 0;
		for (final SMTTerm term : terms) {
			if (!term
					.getSort()
					.toString()
					.equals(VeritPredefinedTheory.getInstance()
							.getBooleanSort().toString())) {
				throw new IllegalArgumentException(
						"VeriT translation does not accept equal operator under terms with different types");
			} else {
				if (term instanceof SMTFunApplication) {
					final SMTFunApplication function = (SMTFunApplication) term;
					final SMTSortSymbol[] sortSymbols = new SMTSortSymbol[function.args.length];
					for (int j = 0; j < function.args.length; j++) {
						// This code is not reachable because the terms that are
						// passed to formulas have no arguments in veriT
						// approach
						sortSymbols[j] = function.args[j].getSort();
					}
					final SMTPredicateSymbol predicateSymbol = new SMTPredicateSymbol(
							function.symbol.name, sortSymbols, !PREDEFINED);
					final SMTAtom atom = new SMTAtom(predicateSymbol,
							new SMTTerm[] {});
					formulas[i] = atom;
				} else {
					throw new IllegalArgumentException(
							"Conversion from terms to formula in VeriT shall happen only if all arguments of the terms are functions and their return types are boolean");
				}
			}
			++i;
		}
		return formulas;

	}
}
