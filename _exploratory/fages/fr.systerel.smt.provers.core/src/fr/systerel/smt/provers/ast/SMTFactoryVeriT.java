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

import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
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

	private static final SMTPolymorphicSortSymbol FST_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(FST_PAIR_SORT_NAME);

	private static final SMTPolymorphicSortSymbol SND_RETURN_SORT = SMTFactory
			.makePolymorphicSortSymbol(SND_PAIR_SORT_NAME);

	public static SMTSortSymbol[] PAIR_ARG_SORTS = { FST_RETURN_SORT,
			SND_RETURN_SORT };

	public static final SMTFunctionSymbol PAIR_SYMBOL = new SMTFunctionSymbol(
			MAPSTO, PAIR_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_ARG_SORTS);

	public static SMTSortSymbol[] PAIR_SORTS = { PAIR_SORT };

	public static final SMTFunctionSymbol FST_SYMBOL = new SMTFunctionSymbol(
			"fst", FST_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_SORTS);

	public static final SMTFunctionSymbol SND_SYMBOL = new SMTFunctionSymbol(
			"snd", SND_RETURN_SORT, !ASSOCIATIVE, !PREDEFINED, PAIR_SORTS);

	private boolean pairAxiomAdded = false;
	private boolean fstAndSndAxiomAdded = false;

	/**
	 * Adds the fst and snd functions, as well as their defining assumptions.
	 * They are added only once.
	 */
	public void addFstAndSndAuxiliarAssumptions(
			final Set<SMTFormula> additionalAssumptions,
			final SMTSignatureVerit signature) {
		if (!fstAndSndAxiomAdded) {
			additionalAssumptions.add(createFstAssumption(signature));
		}
		additionalAssumptions.add(createSndAssumption(signature));
		fstAndSndAxiomAdded = true;
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
	 * This method returns the translation of an Event-B String
	 * 
	 * @param atomicExpression
	 *            The event-B string
	 * @param signature
	 *            the signature used to get a new constant name or to add macros
	 *            to the signature (it's necessary to define a macro for some
	 *            Event-B strings)
	 */
	public static String getSMTAtomicExpressionFormat(
			final String atomicExpression, final SMTSignatureVerit signature) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return SMTSymbol.INT;
		} else if (atomicExpression.equals("BOOL")) {
			return SMTMacroSymbol.BOOL_SORT_VERIT;
		} else {
			return signature.freshSymbolName(atomicExpression);
		}
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
	public static SMTFormula createPairEqualityAxiom() {

		final SMTVarSymbol pSymbol1 = new SMTVarSymbol("t8", PAIR_SORT,
				!PREDEFINED);
		final SMTVarSymbol pSymbol2 = new SMTVarSymbol("t9", PAIR_SORT,
				!PREDEFINED);

		final SMTVar pairVar1 = new SMTVar(pSymbol1);
		final SMTVar pairVar2 = new SMTVar(pSymbol2);

		final SMTFormula equalsFormula = SMTFactory.makeEqual(pairVar1,
				pairVar2);

		final SMTFunApplication fstFunAppl1 = new SMTFunApplication(FST_SYMBOL,
				pairVar1);

		final SMTFunApplication fstFunAppl2 = new SMTFunApplication(FST_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula1 = SMTFactory.makeEqual(fstFunAppl1,
				fstFunAppl2);

		final SMTFunApplication sndFunAppl1 = new SMTFunApplication(SND_SYMBOL,
				pairVar1);

		final SMTFunApplication sndFunAppl2 = new SMTFunApplication(SND_SYMBOL,
				pairVar2);

		final SMTFormula subEqualsFormula2 = SMTFactory.makeEqual(sndFunAppl1,
				sndFunAppl2);

		final SMTFormula andFormula = SMTFactory.makeAnd(subEqualsFormula1,
				subEqualsFormula2);

		final SMTFormula impliesFormula = SMTFactory.makeImplies(andFormula,
				equalsFormula);

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						impliesFormula, pairVar1, pairVar2);

		return quantifiedFormula;
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
			final SMTTerm... args) {
		return new SMTVeriTAtom(macroSymbol, args);
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
			return new SMTVeriTTerm((SMTPredicateSymbol) smtSymbol);

		} else if (smtSymbol instanceof SMTFunctionSymbol) {
			return makeFunApplication((SMTFunctionSymbol) smtSymbol, signature);
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
						"VeriT translation does not accept equal operator under terms with different operators");
			} else {
				if (term instanceof SMTFunApplication) {
					final SMTFunApplication function = (SMTFunApplication) term;
					final SMTSortSymbol[] sortSymbols = new SMTSortSymbol[function.args.length];
					for (int j = 0; j < function.args.length; j++) {
						sortSymbols[j] = function.args[j].getSort();
					}
					final SMTPredicateSymbol predicateSymbol = new SMTPredicateSymbol(
							function.symbol.name, !PREDEFINED, sortSymbols);
					final SMTAtom atom = new SMTAtom(predicateSymbol);
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

	/**
	 * This method creates a function application.
	 * 
	 * @param operatorSymbol
	 *            the symbol of the function application
	 * @param args
	 *            the arguments of the application
	 * @param signature
	 *            the signature for checking the rank
	 * @return a new SMT term with the symbol and the arguments
	 */
	public SMTTerm makeVeriTTermOperatorApplication(
			final SMTFunctionSymbol operatorSymbol, final SMTTerm[] args,
			final SMTSignature signature) {
		signature.verifyFunctionSignature(operatorSymbol);
		return new SMTFunApplication(operatorSymbol, args);
	}

	/**
	 * This method creates and returns a new SMT sort symbol
	 * 
	 * @param name
	 *            the name of the sort
	 * @param signature
	 *            the signature used to create a fresh name for the symbol
	 * @return a new sort symbol
	 */
	public static SMTSortSymbol makeVeriTSortSymbol(final String name,
			final SMTSignatureVerit signature) {
		final String symbolName = getSMTAtomicExpressionFormat(name, signature);
		return new SMTSortSymbol(symbolName, false);
	}

	/**
	 * This method creates and returns a new SMT predicate symbol
	 * 
	 * @param name
	 *            the name of the predicate
	 * @param sort
	 *            the sort of the first argument of the predicate
	 * @return a new predicate symbol
	 */
	public SMTPredicateSymbol makeVeriTPredSymbol(final String name,
			final SMTSortSymbol sort) {
		return new SMTPredicateSymbol(name, !SMTSymbol.PREDEFINED, sort);
	}

	/**
	 * Given the elements (?e 's) , (?f 't), ?x , and (fun (pair ?e ?f)) ?x), it
	 * creates the final auxiliar assumption.
	 * 
	 * @param signature
	 *            the signature
	 * @param forallVarSymbol1
	 *            (?e 's)
	 * @param forallVarSymbol2
	 *            (?f 't)
	 * @param varSymbol1
	 *            ?x
	 * @param fstFunAppl
	 *            (fun (pair ?e ?f)) ?x)
	 * @return The formula that represents the fst or the snd auxiliar function
	 *         assumption
	 * 
	 * @see #createFstAssumption(SMTSignature)
	 * @see #createSndAssumption(SMTSignature)
	 */
	private static SMTFormula createAuxiliarAssumption(
			final SMTSignature signature, final SMTVarSymbol forallVarSymbol1,
			final SMTVarSymbol forallVarSymbol2, final SMTVar varSymbol1,
			final SMTFunApplication fstFunAppl) {
		final SMTFormula equalAtom = SMTFactory.makeAtom(
				new SMTPredicateSymbol.SMTEqual(
						SMTMacroFactory.POLYMORPHIC_PAIRS), signature,
				fstFunAppl, varSymbol1);

		final SMTFormula quantifiedFormula = SMTFactory
				.makeSMTQuantifiedFormula(SMTQuantifierSymbol.FORALL,
						equalAtom, forallVarSymbol1, forallVarSymbol2);

		return quantifiedFormula;
	}

	/**
	 * This method creates the auxiliar assumption:
	 * 
	 * <p>
	 * (forall (?e 's) (?f 't) (= (snd (pair ?e ?f)) ?f))
	 * </p>
	 * 
	 * <p>
	 * that defines the <code>snd</code> function
	 * </p>
	 * 
	 * @return The formula that represents the snd auxiliar function assumption
	 */
	public static SMTFormula createSndAssumption(final SMTSignature signature) {
		final SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(
				FST_PAIR_ARG_NAME, FST_RETURN_SORT, !PREDEFINED);
		final SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(
				SND_PAIR_ARG_NAME, SND_RETURN_SORT, !PREDEFINED);

		final SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		final SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		final SMTFunApplication pairFunAppl = new SMTFunApplication(
				PAIR_SYMBOL, varSymbol1, varSymbol2);

		final SMTFunApplication sndFunAppl = new SMTFunApplication(SND_SYMBOL,
				pairFunAppl);

		return createAuxiliarAssumption(signature, forallVarSymbol1,
				forallVarSymbol2, varSymbol2, sndFunAppl);
	}

	/**
	 * This method creates the auxiliar assumption:
	 * 
	 * <p>
	 * <code>(forall (?e 's) (?f 't) (= (fst (pair ?e ?f)) ?e))</code>
	 * </p>
	 * 
	 * <p>
	 * that defines the <code>fst</code> function
	 * </p>
	 * 
	 * @return The formula that represents the fst auxiliar function assumption
	 */
	public static SMTFormula createFstAssumption(final SMTSignature signature) {
		final SMTVarSymbol forallVarSymbol1 = new SMTVarSymbol(
				FST_PAIR_ARG_NAME, FST_RETURN_SORT, PREDEFINED);
		final SMTVarSymbol forallVarSymbol2 = new SMTVarSymbol(
				SND_PAIR_ARG_NAME, SND_RETURN_SORT, PREDEFINED);

		final SMTVar varSymbol1 = new SMTVar(forallVarSymbol1);
		final SMTVar varSymbol2 = new SMTVar(forallVarSymbol2);

		final SMTFunApplication pairFunAppl = new SMTFunApplication(
				PAIR_SYMBOL, varSymbol1, varSymbol2);

		final SMTFunApplication fstFunAppl = new SMTFunApplication(FST_SYMBOL,
				pairFunAppl);

		return createAuxiliarAssumption(signature, forallVarSymbol1,
				forallVarSymbol2, varSymbol1, fstFunAppl);
	}

}
