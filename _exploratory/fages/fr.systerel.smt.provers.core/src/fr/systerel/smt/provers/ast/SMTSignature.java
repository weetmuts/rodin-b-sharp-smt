package fr.systerel.smt.provers.ast;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SMTSignature {
	private final SMTLogic logic;

	protected final Set<String> symbols = new HashSet<String>(); // TODO
																	// must
																	// implement
																	// SMT-LIB
																	// rules

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
	 * Gives a fresh identifier to a variable of which identifier contains the
	 * character '\''.
	 */
	public String giveFreshVar(final String name) {
		String freshVar = name;
		if (name.contains("\'")) {
			int discrNumber = name.length() - name.indexOf('\'');
			freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			while (this.symbols.contains(freshVar)) {
				discrNumber = discrNumber + 1;
				freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			}
		}
		return freshVar;
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	public SMTSortSymbol freshSort(final String name) {
		int i = 0;
		final StringBuilder buffer = new StringBuilder(name);

		/**
		 * Tries to put the symbol in symbols set.
		 */
		boolean successfullyAdded = this.symbols.add(name);
		/**
		 * If the set already contains this symbol
		 */
		while (!successfullyAdded) {
			/**
			 * Sets the buffer to contain name + "_" + i.
			 */
			buffer.setLength(name.length());
			buffer.append("_").append(i);

			/**
			 * Tries to put the symbol
			 */
			successfullyAdded = this.symbols.add(buffer.toString());

			i = i + 1;
		}

		final SMTSortSymbol freshSort = new SMTSortSymbol(buffer.toString());

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

	public void addFunctionSymbol() {
		// TODO must verify the given argument, and give a fresh name if needed
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
