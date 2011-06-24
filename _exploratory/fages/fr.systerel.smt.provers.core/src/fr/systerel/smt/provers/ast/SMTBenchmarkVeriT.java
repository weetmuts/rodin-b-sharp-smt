/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.systerel.smt.provers.ast.macros.SMTEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacroTerm;
import fr.systerel.smt.provers.ast.macros.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTPredefinedMacro;
import fr.systerel.smt.provers.ast.macros.SMTQuantifiedMacro;
import fr.systerel.smt.provers.ast.macros.SMTSetComprehensionMacro;

/**
 * This class is the benchmark resultant of the translation in the VeriT
 * approach
 */
public class SMTBenchmarkVeriT extends SMTBenchmark {
	/**
	 * Constructs a new benchmark.
	 * 
	 * @param lemmaName
	 *            the lema of the benchmark
	 * @param signature
	 *            the signature of the benchmark
	 * @param assumptions
	 *            the assumptions of the benchmark
	 * @param formula
	 *            the formula of the benchmark
	 */
	public SMTBenchmarkVeriT(final String lemmaName,
			final SMTSignatureVerit signature,
			final List<SMTFormula> assumptions, final SMTFormula formula) {
		super(lemmaName + "_vt", signature, assumptions, formula);
		comments.add("translated from Event-B with the VeriT approach of Rodin SMT Plugin");
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code var}.
	 * 
	 * @param var
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTVeriTTerm var,
			final Set<SMTSymbol> symbols) {
		symbols.add(var.getSort());
		symbols.add(var.getSymbol());
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code vff}.
	 * 
	 * @param vff
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTVeritFiniteFormula vff,
			final Set<SMTSymbol> symbols) {
		symbols.add(vff.getfArgument());
		symbols.add(vff.getkArgument());
		symbols.add(vff.getpArgument());
		symbols.add(vff.getFinitePred());

		final SMTTerm[] terms = vff.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code va}.
	 * 
	 * @param va
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTVeriTAtom va,
			final Set<SMTSymbol> symbols) {
		symbols.add(va.getMacroSymbol());

		final SMTTerm[] terms = va.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code vcf}.
	 * 
	 * @param vcf
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTVeritCardFormula vcf,
			final Set<SMTSymbol> symbols) {
		symbols.add(vcf.getCardSymbol());
		symbols.add(vcf.getfArgument());
		symbols.add(vcf.getkArgument());

		final SMTTerm[] terms = vcf.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Remove unused symbols from the benchmark
	 */
	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption, symbols);
		}
		getUsedSymbols(formula, symbols);
		final Set<SMTMacro> macros = ((SMTSignatureVerit) signature)
				.getMacros();
		for (final SMTMacro macro : macros) {
			getUsedSymbols(macro, symbols);
		}
		signature.removeUnusedSymbols(symbols);
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code mt}.
	 * 
	 * @param mt
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTMacroTerm mt,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : mt.getArgs()) {
			getUsedSymbols(term, symbols);
		}
		symbols.add(mt.getMacroSymbol());
		symbols.add(mt.getSort());
	}

	/**
	 * Adds to the parameter {@code symbols} the SMT symbols from {@code macro}.
	 * 
	 * @param macro
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTEnumMacro macro,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : macro.getTerms()) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code macro}.
	 * 
	 * @param macro
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTPairEnumMacro macro,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : macro.getTerms()) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code macro}.
	 * 
	 * @param macro
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTPredefinedMacro macro,
			final Set<SMTSymbol> symbols) {
		if (macro.usesPairFunctionAndSort()) {
			symbols.add(SMTFactoryVeriT.PAIR_SORT);
			symbols.add(SMTFactoryVeriT.PAIR_SYMBOL);
		}
		if (macro.usesFstAndSndFunctions()) {
			symbols.add(SMTFactoryVeriT.FST_SYMBOL);
			symbols.add(SMTFactoryVeriT.SND_SYMBOL);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code macro}.
	 * 
	 * @param macro
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTSetComprehensionMacro macro,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(macro.getExpression(), symbols);
		getUsedSymbols(macro.getFormula(), symbols);
	}

	private void getUsedSymbols(final SMTQuantifiedMacro macro,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(macro.getFormula(), symbols);
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code macro}.
	 * 
	 * @param macro
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTMacro macro,
			final Set<SMTSymbol> symbols) {
		if (macro instanceof SMTEnumMacro) {
			final SMTEnumMacro em = (SMTEnumMacro) macro;
			getUsedSymbols(em, symbols);
		} else if (macro instanceof SMTPairEnumMacro) {
			final SMTPairEnumMacro pem = (SMTPairEnumMacro) macro;
			getUsedSymbols(pem, symbols);
		} else if (macro instanceof SMTPredefinedMacro) {
			final SMTPredefinedMacro pm = (SMTPredefinedMacro) macro;
			getUsedSymbols(pm, symbols);
		} else if (macro instanceof SMTSetComprehensionMacro) {
			final SMTSetComprehensionMacro scm = (SMTSetComprehensionMacro) macro;
			getUsedSymbols(scm, symbols);

		} else if (macro instanceof SMTQuantifiedMacro) {

			final SMTQuantifiedMacro qm = (SMTQuantifiedMacro) macro;
			getUsedSymbols(qm, symbols);
		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The class of the macro is: "
					+ macro.getClass().toString() + ", which is not recognized");
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code term}.
	 * 
	 * @param term
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTTerm term,
			final Set<SMTSymbol> symbols) {
		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fa = (SMTFunApplication) term;
			getUsedSymbols(fa, symbols);

		} else if (term instanceof SMTNumeral) {
			final SMTNumeral num = (SMTNumeral) term;
			getUsedSymbols(num, symbols);

		} else if (term instanceof SMTVar) {
			final SMTVar var = (SMTVar) term;
			getUsedSymbols(var, symbols);

		} else if (term instanceof SMTVeriTTerm) {
			final SMTVeriTTerm vt = (SMTVeriTTerm) term;
			getUsedSymbols(vt, symbols);

		} else if (term instanceof SMTMacroTerm) {
			final SMTMacroTerm mt = (SMTMacroTerm) term;
			getUsedSymbols(mt, symbols);
		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code fa}.
	 * 
	 * @param fa
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTFunApplication fa,
			final Set<SMTSymbol> symbols) {
		symbols.add(fa.getSort());
		symbols.add(fa.getSymbol());
		final SMTTerm[] terms = fa.getArgs();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code atom}.
	 * 
	 * @param atom
	 * @param symbols
	 */
	private void getUsedSymbols(final SMTAtom atom, final Set<SMTSymbol> symbols) {
		symbols.add(atom.getPredicate());

		final SMTTerm[] terms = atom.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from
	 * {@code formula}.
	 * 
	 * @param smtformula
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTFormula smtformula,
			final Set<SMTSymbol> symbols) {
		if (smtformula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) smtformula;
			getUsedSymbols(atom, symbols);

		} else if (smtformula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) smtformula;
			getUsedSymbols(con, symbols);

		} else if (smtformula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) smtformula;
			getUsedSymbols(qf, symbols);

		} else if (smtformula instanceof SMTVeriTAtom) {
			final SMTVeriTAtom va = (SMTVeriTAtom) smtformula;
			getUsedSymbols(va, symbols);

		} else if (smtformula instanceof SMTVeritCardFormula) {
			final SMTVeritCardFormula vcf = (SMTVeritCardFormula) smtformula;
			getUsedSymbols(vcf, symbols);

		} else if (smtformula instanceof SMTVeritFiniteFormula) {
			final SMTVeritFiniteFormula vff = (SMTVeritFiniteFormula) smtformula;
			getUsedSymbols(vff, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ smtformula.getClass().toString());
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code con}.
	 * 
	 * @param con
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTConnectiveFormula con,
			final Set<SMTSymbol> symbols) {
		final SMTFormula[] formulas = con.getFormulas();
		for (final SMTFormula smtFormula : formulas) {
			getUsedSymbols(smtFormula, symbols);
		}
	}

	/**
	 * Adds to the parameter {@code symbols} the smt symbols from {@code qf}.
	 * 
	 * @param qf
	 * @param symbols
	 */
	protected void getUsedSymbols(final SMTQuantifiedFormula qf,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(qf.getFormula(), symbols);
	}
}
