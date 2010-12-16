package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSort;

public class Signature {
	private final SMTLogic logic;

	private final List<SMTSort> sorts;

	private final List<SMTPredicateSymbol> preds;

	private final List<SMTFunctionSymbol> funs;

	// TODO put this into a Signature extending class that will be used by veriT
	// approach
	private final List<String> macros;

	/**
	 * Extracts the type environment of a Event-B sequent
	 */
	public static ITypeEnvironment extractTypeEnvironment(
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

	public Signature(final String logic, final List<SMTSort> sorts,
			final List<SMTPredicateSymbol> preds,
			final List<SMTFunctionSymbol> funs) {
		this.logic = new SMTLogic(logic);
		this.sorts = sorts;
		this.preds = preds;
		this.funs = funs;
		this.macros = new ArrayList<String>();
	}

	private static String sectionIndentation(final String sectionName) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < sectionName.length(); i++) {
			sb.append(" ");
		}
		sb.append("    ");
		return sb.toString();
	}

	private static <T> void extraSection(final StringBuilder sb,
			final List<T> elements, final String sectionName) {
		final String eltSep = sectionIndentation(sectionName);
		String separator = "";
		sb.append(" :");
		sb.append(sectionName);
		sb.append(" (");
		for (final T element : elements) {
			sb.append(separator);
			sb.append(element);
			separator = eltSep;
		}
		sb.append(")\n");
	}

	private void logicSection(final StringBuilder sb) {
		sb.append(" :logic ");
		sb.append(this.logic);
		sb.append("\n");
	}

	/**
	 * One sort per line. May add a comment beside.
	 */
	private void extrasortsSection(final StringBuilder sb) {
		if (!this.sorts.isEmpty()) {
			extraSection(sb, this.sorts, "extrasorts");
		}
	}

	private void extrapredsSection(final StringBuilder sb) {
		if (!preds.isEmpty()) {
			extraSection(sb, this.preds, "extrapreds");
		}
	}

	private void extrafunsSection(final StringBuilder sb) {
		if (!funs.isEmpty()) {
			extraSection(sb, this.funs, "extrafuns");
		}
	}

	// TODO put this into a Signature extending class that will be used by veriT
	// approach
	public void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			extraSection(sb, this.macros, "extramacros");
		}
	}

	/**
	 * Gives a fresh identifier to a variable of which identifier contains the
	 * character '\''.
	 */
	public static String giveFreshVar(final String name,
			final ITypeEnvironment typeEnv) {
		int discrNumber = name.length() - name.indexOf('\'');

		String alternativeName = name.replaceAll("'", "_" + discrNumber + "_");
		Type t = typeEnv.getType(alternativeName);
		while (t != null) {
			discrNumber = discrNumber + 1;
			alternativeName = name.replaceAll("'", "_" + discrNumber + "_");
			t = typeEnv.getType(alternativeName);
		}
		return alternativeName;
	}

	public void toString(StringBuilder sb) {
		this.logicSection(sb);
		this.extrasortsSection(sb);
		this.extrapredsSection(sb);
		this.extrafunsSection(sb);
		this.extramacrosSection(sb);
	}
}
