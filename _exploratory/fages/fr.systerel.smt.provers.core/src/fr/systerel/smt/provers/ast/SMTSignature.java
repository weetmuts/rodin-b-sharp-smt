package fr.systerel.smt.provers.ast;

import java.util.HashSet;
import java.util.Set;

public abstract class SMTSignature {
	private final SMTLogic logic;

	protected final Set<String> symbols = new HashSet<String>();

	protected final Set<SMTSortSymbol> sorts = new HashSet<SMTSortSymbol>();

	protected final Set<SMTPredicateSymbol> preds = new HashSet<SMTPredicateSymbol>();

	protected final Set<SMTFunctionSymbol> funs = new HashSet<SMTFunctionSymbol>();

	public SMTSignature(final String logicName) {
		this.logic = new SMTLogic(logicName);
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
	public String freshVar(String name) {
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

			while (this.symbols.contains(freshName.toString())) {
				discrNumber = discrNumber + 1;

				patch.setLength(1);
				patch.append(discrNumber).append("_");

				freshName.setLength(0);
				freshName.append(name.replaceAll("'", patch.toString()));
			}
		}
		
		final String intermediateName = freshName.toString();

		/**
		 * Tries to put the symbol in symbols set.
		 */
		boolean successfullyAdded = this.symbols.add(intermediateName);
		/**
		 * If the set already contains this symbol
		 */
		while (!successfullyAdded) {
			/**
			 * Sets the buffer content to: name + "_" + i.
			 */
			freshName.setLength(intermediateName.length());
			freshName.append("_").append(i);

			/**
			 * Tries to put the symbol in symbols set.
			 */
			successfullyAdded = this.symbols.add(freshName.toString());

			i = i + 1;
		}

		return freshName.toString();
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	public SMTSortSymbol freshSort(final String name) {
		final String freshName = this.freshVar(name);
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
		this.funs.add(new SMTFunctionSymbol(name, SMTFactory.EMPTY_TAB, sort));
	}

	public void addPredicateSymbol(final String name, final String type) {
		// TODO must verify the given argument, and give a fresh name if needed
	}

	public void toString(StringBuilder sb) {
		this.logicSection(sb);
		this.extrasortsSection(sb);
		this.extrapredsSection(sb);
		this.extrafunsSection(sb);
	}
}
