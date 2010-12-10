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
	private static ITypeEnvironment extractTypeEnvironment(
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

	public static Signature translate(final List<Predicate> hypotheses,
			final Predicate goal) {
		return translate("UNKNOWN", hypotheses, goal);
	}

	public static Signature translate(final String logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		final List<SMTSort> sorts = new ArrayList<SMTSort>();
		final List<SMTPredDecl> preds = new ArrayList<SMTPredDecl>();
		final List<SMTFunDecl> funs = new ArrayList<SMTFunDecl>();
		final HashMap<String, String> singleQuotVars = new HashMap<String, String>();
		boolean insertPairDecl = false;

		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);

		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			final Type varType = iter.getType();

			if (varName.contains("\'")) {
				final String freshName = giveFreshVar(varName, typeEnvironment);
				singleQuotVars.put(varName, freshName);
			}

			if (varName.equals(varType.toString())) {
				sorts.add(new SMTSort(varType.toString()));
			}

			// Regra 6
			else if (varType.getSource() != null) {

				if (varType.getSource().getSource() != null
						|| varType.getSource().getBaseType() != null) {
					// TODO: Insert an Error message and abort, cartesian
					// product of cartesian product || cartesian product of
					// power type is not implemeted yet
					System.err
							.println("Cartesian product of cartesian product || Cartesian product of power type is not implemented yet");
				}

				final SMTSort sort1 = new SMTSort(
						getSMTAtomicExpressionFormat(varType.getSource()
								.toString()));
				final SMTSort sort2 = new SMTSort(
						getSMTAtomicExpressionFormat(varType.getTarget()
								.toString()));
				final SMTSort pair = new SMTSort("(Pair " + sort1.toString()
						+ " " + sort2.toString() + ")");
				preds.add(new SMTPredDecl(varName, pair));

				if (!insertPairDecl) {
					sorts.add(new SMTSort("(Pair 's 't)"));
				}
				if (!sorts.contains(sort1)) {
					sorts.add(sort1);
				}
				if (!sorts.contains(sort2)) {
					sorts.add(sort2);
				}
				insertPairDecl = true;

			} else if (varType.getBaseType() != null) {
				if (varName.equals(varType.getBaseType().toString())) {
					sorts.add(new SMTSort(varName));
				} else {
					preds.add(new SMTPredDecl(varName, new SMTSort(
							getSMTAtomicExpressionFormat(varType.getBaseType()
									.toString()))));
				}
			} else {
				funs.add(new SMTFunDecl(varName, new SMTSort(
						getSMTAtomicExpressionFormat(varType.toString()))));
			}
		}
		if (insertPairDecl) {
			funs.add(new SMTFunDecl("pair 's 't", new SMTSort("(Pair 's 't)")));
		}

		return new Signature(logic, sorts, preds, funs);
	}

	public Signature(final String logic, final List<SMTSort> sorts,
			final List<SMTPredDecl> preds, final List<SMTFunDecl> funs) {
		this.logic = new SMTLogic(logic);
		this.sorts = sorts;
		this.preds = preds;
		this.funs = funs;
		this.macros = new ArrayList<String>();
	}

	// TODO to be moved into the Translator used by veriT approach
	public static String getSMTAtomicExpressionFormat(String atomicExpression) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return "Int";
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else if (atomicExpression.equals("TRUE")) {
			return "true";
		} else if (atomicExpression.equals("FALSE")) {
			return "false";
		} else if (atomicExpression.equals("\u2205")) {
			return "emptyset";
		}
		return atomicExpression;
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
