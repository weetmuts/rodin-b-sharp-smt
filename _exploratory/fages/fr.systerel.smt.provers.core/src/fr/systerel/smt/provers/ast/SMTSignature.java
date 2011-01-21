package fr.systerel.smt.provers.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Here are the rules in SMT-LIB V1.2 that we need to implement in this class:
 * <ul>
 * <li>Explicit (ad-hoc) overloading of function or predicate symbols — by which
 * a symbol could have more than one rank — is allowed.</li>
 * <li>Every variable has a unique sort and no function symbol has distinct
 * ranks of the form <code>s1 ··· sn s</code> and <code>s1 ··· sn s</code>.</li>
 * <li>The sets <code>ΣS</code>, <code>ΣF</code> and <code>ΣP</code> of an
 * SMT-LIB signature are not required to be disjoint.</li>
 * <li>It is required for the set of attribute symbols to be disjoint from all
 * the other sets of the language.</li>
 * <li>The symbols <code>assumption</code>, <code>formula</code>,
 * <code>status</code>, <code>logic</code>, <code>extrasorts</code>,
 * <code>extrafuns</code>, <code>extrapreds</code>, <code>funs</code>,
 * <code>preds</code>, <code>axioms</code>, <code>sorts</code>,
 * <code>definition</code>, <code>theory</code>, <code>language</code>,
 * <code>extensions</code> and <code>notes</code> are reserved attribute
 * symbols.</li>
 * <li>Reserved symbols and keywords are: <code>=</code>, <code>and</code>,
 * <code>benchmark</code>, <code>distinct</code>, <code>exists</code>,
 * <code>false</code>, <code>flet</code>, <code>forall</code>,
 * <code>if_then_else</code>, <code>iff</code>, <code>implies</code>,
 * <code>ite</code>, <code>let</code>, <code>logic</code>, <code>not</code>,
 * <code>or</code>, <code>sat</code>, <code>theory</code>, <code>true</code>,
 * <code>unknown</code>, <code>unsat</code>, <code>xor</code>.</li>
 * </ul>
 */
// TODO Create two subclasses when hanging SMT-LIB 2.0: SMTSignatureV1_2 and
// SMTSignature2_0. This might be necessary if naming rules are not the same in
// the two versions of the language.
public abstract class SMTSignature {
	private final SMTLogic logic;

	private final static String NEW_SYMBOL_NAME = "NSYMB";
	private final static String NEW_SORT_NAME = "NSORT";
	private final static String MEMBERSHIP_PRED_NAME = "MS";

	protected final static String reservedSymbols[] = { "=", "and",
			"benchmark", "distinct", "exists", "false", "flet", "forall",
			"if_then_else", "iff", "implies", "ite", "let", "logic", "not",
			"or", "sat", "theory", "true", "unknown", "unsat", "xor" };

	protected final static String reservedAttributesSymbols[] = { "assumption",
			"formula", "status", "logic", "extrasorts", "extrafuns",
			"extrapreds", "funs", "preds", "axioms", "sorts", "definition",
			"theory", "language", "extensions", "notes" };

	protected final Set<String> attributeSymbols = new HashSet<String>(
			Arrays.asList(reservedAttributesSymbols));

	protected final Set<SMTSortSymbol> sorts = new HashSet<SMTSortSymbol>();

	protected final Set<SMTPredicateSymbol> preds = new HashSet<SMTPredicateSymbol>();

	protected final Set<SMTFunctionSymbol> funs = new HashSet<SMTFunctionSymbol>();

	public SMTSignature(final String logicName) {
		this.logic = new SMTLogic(logicName);
	}

	/**
	 * This method is used to get the symbol names already in use from a set of
	 * SMT-LIB symbols
	 */
	private static Set<String> getSymbolNames(
			final Set<? extends SMTSymbol> symbols) {
		final Set<String> symbolNames = new HashSet<String>();
		for (final SMTSymbol symbol : symbols) {
			symbolNames.add(symbol.getName());
		}
		return symbolNames;
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

	protected static <T> void extraSection(final StringBuilder sb,
			final Set<T> elements, final String sectionName) {
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

	/**
	 * Gives a fresh symbol name. Implements SMT-LIB rules. If the symbol name
	 * contains "\'", it is replaced with "_" + i + "_", where i is an arbitrary
	 * number , incremented as much as needed. If the symbol name already exists
	 * in the symbols set, a new name is created, that is: original_name + "_" +
	 * i, where i is incremented as much as needed.
	 */
	// TODO check which prover needs the "\'" simplification, and document it
	// here
	private static String freshName(Set<String> symbols, String name) {
		int i = 0;
		final StringBuilder freshName = new StringBuilder(name);

		if (name.contains("\'")) {
			final StringBuilder patch = new StringBuilder();
			/**
			 * Arbitrary chosen initial number
			 */
			int discrNumber = name.length() - name.indexOf('\'');

			patch.append("_").append(discrNumber).append("_");

			freshName.setLength(0);
			freshName.append(name.replaceAll("'", patch.toString()));

			while (symbols.contains(freshName.toString())) {
				discrNumber = discrNumber + 1;

				patch.setLength(1);
				patch.append(discrNumber).append("_");

				freshName.setLength(0);
				freshName.append(name.replaceAll("'", patch.toString()));
			}
		}

		final String intermediateName = freshName.toString();
		/**
		 * If the set already contains this symbol
		 */
		while (symbols.contains(freshName.toString())) {
			/**
			 * Sets the buffer content to: name + "_" + i.
			 */
			freshName.setLength(intermediateName.length());
			freshName.append("_").append(i);

			i = i + 1;
		}

		return freshName.toString();
	}

	public SMTFunctionSymbol getFunctionSymbol(final String name,
			final SMTSortSymbol[] argSorts, final SMTSortSymbol resultSort) {
		for (SMTFunctionSymbol fun : this.funs) {
			if (fun.name.equals(name) && fun.hasRank(argSorts, resultSort)) {
				return fun;
			}
		}
		return null;
	}

	public SMTPredicateSymbol getPredicateSymbol(final String name,
			final SMTSortSymbol[] argSorts) {
		for (SMTPredicateSymbol pred : this.preds) {
			if (pred.name.equals(name) && pred.hasRank(argSorts)) {
				return pred;
			}
		}
		return null;
	}

	public SMTPredicateSymbol getMembershipPredicateSymbol(
			final SMTSortSymbol[] argSorts) {
		for (SMTPredicateSymbol pred : this.preds) {
			if (pred.isAMembershipPredicate() && pred.hasRank(argSorts)) {
				return pred;
			}
		}
		return null;
	}

	public String freshCstName(final String name) {
		if (Arrays.asList(reservedSymbols).contains(name)
				|| this.attributeSymbols.contains(name)) {
			return freshName(getSymbolNames(this.funs), NEW_SYMBOL_NAME);
		} else {
			return freshName(getSymbolNames(this.funs), name);
		}
	}

	public SMTSortSymbol freshSort() {
		return this.freshSort(NEW_SORT_NAME);
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	public SMTSortSymbol freshSort(final String name) {
		final String freshName = freshName(getSymbolNames(this.sorts), name);
		final SMTSortSymbol freshSort = new SMTSortSymbol(freshName);

		/**
		 * Tries to put the sort in sorts set.
		 */
		this.sorts.add(freshSort);

		return freshSort;
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

	public void addConstant(final String name, final SMTSortSymbol sort) {
		this.funs.add(new SMTFunctionSymbol(name, SMTFactory.EMPTY_SORT, sort));
	}

	public void addPredicateSymbol(final boolean isAMembershipPredicate,
			final String name, final SMTSortSymbol[] argSorts) {
		this.preds.add(new SMTPredicateSymbol(isAMembershipPredicate, name,
				argSorts));
	}

	public void addMembershipPredicateSymbol(final SMTSortSymbol[] argSorts) {
		this.addPredicateSymbol(true, MEMBERSHIP_PRED_NAME, argSorts);
	}

	public void toString(StringBuilder sb) {
		this.logicSection(sb);
		this.extrasortsSection(sb);
		this.extrapredsSection(sb);
		this.extrafunsSection(sb);
	}
}
