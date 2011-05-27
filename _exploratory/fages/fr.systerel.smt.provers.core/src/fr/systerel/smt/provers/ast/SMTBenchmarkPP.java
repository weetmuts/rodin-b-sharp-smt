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

/**
 * @author vitor
 * 
 */
public class SMTBenchmarkPP extends SMTBenchmark {

	private final SMTSignaturePP signature;

	public SMTBenchmarkPP(final String lemmaName,
			final SMTSignaturePP signature, final List<SMTFormula> assumptions,
			final SMTFormula goal) {
		super(lemmaName, assumptions, goal);
		this.signature = signature;
	}

	@Override
	public SMTSignature getSignature() {
		return signature;
	}

	public void removeUnusedSymbols() {
		final Set<SMTSymbol> symbols = new HashSet<SMTSymbol>();
		for (final SMTFormula assumption : assumptions) {
			getUsedSymbols(assumption, symbols);
		}
		getUsedSymbols(goal, symbols);

		signature.removeUnusedSymbols(symbols);
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

		} else if (term instanceof SMTNumeral) {
			final SMTNumeral num = (SMTNumeral) term;
			getUsedSymbols(num, symbols);

		} else if (term instanceof SMTVar) {
			final SMTVar var = (SMTVar) term;
			getUsedSymbols(var, symbols);

		} else {
			// This part should never be reached
			throw new IllegalArgumentException("The term is: "
					+ term.getClass().toString());
		}
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
