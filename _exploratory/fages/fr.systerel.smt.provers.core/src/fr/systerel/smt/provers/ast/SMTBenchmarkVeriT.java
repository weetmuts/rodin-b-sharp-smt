/**
 * 
 */
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTSymbol.BENCHMARK;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.systerel.smt.provers.ast.macros.SMTEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacroTerm;
import fr.systerel.smt.provers.ast.macros.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTPredefinedMacro;
import fr.systerel.smt.provers.ast.macros.SMTSetComprehensionMacro;

/**
 * @author vitor
 * 
 */
public class SMTBenchmarkVeriT extends SMTBenchmark {

	private final SMTSignatureVerit signature;

	public SMTBenchmarkVeriT(final String lemmaName,
			final SMTSignatureVerit signature,
			final List<SMTFormula> assumptions, final SMTFormula goal) {
		super(lemmaName, assumptions, goal);
		this.signature = signature;
	}

	@Override
	public SMTSignature getSignature() {
		return signature;
	}

	protected void getUsedSymbols(final SMTVeriTTerm var,
			final Set<SMTSymbol> symbols) {
		symbols.add(var.getSort());
		symbols.add(var.getSymbol());
	}

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

	private void getUsedSymbols(final SMTVeriTAtom va,
			final Set<SMTSymbol> symbols) {
		symbols.add(va.getPredicate());

		final SMTTerm[] terms = va.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

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

	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption, symbols);
		}
		getUsedSymbols(goal, symbols);

		final Set<SMTMacro> macros = signature.getMacros();
		for (final SMTMacro macro : macros) {
			getUsedSymbols(macro, symbols);
		}
		signature.removeUnusedSymbols(symbols);
	}

	private void getUsedSymbols(final SMTMacroTerm mt,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : mt.getArgs()) {
			getUsedSymbols(term, symbols);
		}
		symbols.add(mt.getMacroSymbol());
		symbols.add(mt.getSort());
	}

	private void getUsedSymbols(final SMTEnumMacro macro,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : macro.getTerms()) {
			getUsedSymbols(term, symbols);
		}
	}

	private void getUsedSymbols(final SMTPairEnumMacro macro,
			final Set<SMTSymbol> symbols) {
		for (final SMTTerm term : macro.getTerms()) {
			getUsedSymbols(term, symbols);
		}
	}

	private void getUsedSymbols(final SMTPredefinedMacro macro,
			final Set<SMTSymbol> symbols) {
		// Do nothing
	}

	private void getUsedSymbols(final SMTSetComprehensionMacro macro,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(macro.getExpression(), symbols);
		getUsedSymbols(macro.getFormula(), symbols);
	}

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
		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The class of the macro is: "
					+ macro.getClass().toString() + ", which is not recognized");
		}
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
	@Override
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		smtCmdOpening(sb, BENCHMARK, name);
		signature.toString(sb);
		sb.append("\n");
		assumptionsSection(sb);
		formulaSection(sb);
		sb.append(CPAR);
		pw.println(sb.toString());
	}

	protected void getUsedSymbols(final SMTTerm term,
			final Set<SMTSymbol> symbols) {
		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fa = (SMTFunApplication) term;
			getUsedSymbols(fa, symbols);

		} else if (term instanceof SMTITETerm) {
			final SMTITETerm ite = (SMTITETerm) term;
			getUsedSymbols(ite, symbols);

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

	protected void getUsedSymbols(final SMTITETerm ite,
			final Set<SMTSymbol> symbols) {
		symbols.add(ite.getSort());
		getUsedSymbols(ite.getFormula(), symbols);
		getUsedSymbols(ite.getfTerm(), symbols);
		getUsedSymbols(ite.gettTerm(), symbols);
	}

	protected void getUsedSymbols(final SMTFunApplication fa,
			final Set<SMTSymbol> symbols) {
		symbols.add(fa.getSort());
		symbols.add(fa.getSymbol());
		final SMTTerm[] terms = fa.getArgs();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	private void getUsedSymbols(final SMTAtom atom, final Set<SMTSymbol> symbols) {
		symbols.add(atom.getPredicate());

		final SMTTerm[] terms = atom.getTerms();
		for (final SMTTerm term : terms) {
			getUsedSymbols(term, symbols);
		}
	}

	protected void getUsedSymbols(final SMTFormula formula,
			final Set<SMTSymbol> symbols) {
		if (formula instanceof SMTAtom) {
			final SMTAtom atom = (SMTAtom) formula;
			getUsedSymbols(atom, symbols);

		} else if (formula instanceof SMTConnectiveFormula) {
			final SMTConnectiveFormula con = (SMTConnectiveFormula) formula;
			getUsedSymbols(con, symbols);

		} else if (formula instanceof SMTQuantifiedFormula) {
			final SMTQuantifiedFormula qf = (SMTQuantifiedFormula) formula;
			getUsedSymbols(qf, symbols);

		} else if (formula instanceof SMTVeriTAtom) {
			final SMTVeriTAtom va = (SMTVeriTAtom) formula;
			getUsedSymbols(va, symbols);

		} else if (formula instanceof SMTVeritCardFormula) {
			final SMTVeritCardFormula vcf = (SMTVeritCardFormula) formula;
			getUsedSymbols(vcf, symbols);

		} else if (formula instanceof SMTVeritFiniteFormula) {
			final SMTVeritFiniteFormula vff = (SMTVeritFiniteFormula) formula;
			getUsedSymbols(vff, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The formula is: "
					+ formula.getClass().toString());
		}
	}

	protected void getUsedSymbols(final SMTConnectiveFormula con,
			final Set<SMTSymbol> symbols) {
		final SMTFormula[] formulas = con.getFormulas();
		for (final SMTFormula formula : formulas) {
			getUsedSymbols(formula, symbols);
		}
	}

	protected void getUsedSymbols(final SMTQuantifiedFormula qf,
			final Set<SMTSymbol> symbols) {
		getUsedSymbols(qf.getFormula(), symbols);
	}

}
