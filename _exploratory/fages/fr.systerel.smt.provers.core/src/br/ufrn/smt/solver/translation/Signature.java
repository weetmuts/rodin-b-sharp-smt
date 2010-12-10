package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.ast.SMTFunDecl;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTPredDecl;
import fr.systerel.smt.provers.ast.SMTSort;

public class Signature {
	private final SMTLogic logic;

	private final List<SMTSort> sorts;

	private final List<SMTPredDecl> preds;

	private final List<SMTFunDecl> funs;

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
			final List<SMTPredDecl> preds, final List<SMTFunDecl> funs) {
		this.logic = new SMTLogic(logic);
		this.sorts = sorts;
		this.preds = preds;
		this.funs = funs;
		this.macros = new ArrayList<String>();
	}

	private static <T> void extraSection(final StringBuilder sb,
			final List<T> elements, final String sectionName,
			final String sep1, final String sep2) {
		String separator = sep1;
		sb.append(" :");
		sb.append(sectionName);
		sb.append(" (");
		for (final T element : elements) {
			sb.append(separator);
			sb.append(element.toString());
			separator = sep2;
		}
		sb.append(")\n");
	}

	private static void logicSection(final StringBuilder sb, final String logic) {
		sb.append(" :logic ");
		sb.append(logic);
		sb.append("\n");
	}

	private static void extrasortsSection(final StringBuilder sb,
			final List<SMTSort> sorts) {
		extraSection(sb, sorts, "extrasorts", "", " ");
	}

	private static void extrapredsSection(final StringBuilder sb,
			final List<SMTPredDecl> preds) {
		extraSection(sb, preds, "extrapreds", "", "\n              ");
	}

	private static void extrafunsSection(final StringBuilder sb,
			final List<SMTFunDecl> funs) {
		extraSection(sb, funs, "extrafuns", "", "\n             ");
	}

	// TODO put this into a Signature extending class that will be used by veriT
	// approach
	public static void extramacrosSection(final StringBuilder sb,
			final List<String> macros) {
		extraSection(sb, macros, "extramacros", "", "\n               ");
	}

	/**
	 * Gives a fresh name to a variable of which name contains the character
	 * '\''.
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
		logicSection(sb, this.logic.toString());
		if (!this.sorts.isEmpty()) {
			extrasortsSection(sb, this.sorts);
		}
		if (!this.preds.isEmpty()) {
			extrapredsSection(sb, this.preds);
		}
		if (!this.funs.isEmpty()) {
			extrafunsSection(sb, this.funs);
		}
		if (!this.macros.isEmpty()) {
			extramacrosSection(sb, this.macros);
		}
	}
}
