package br.ufrn.smt.solver.translation;

/**
 * Enumeration used to describe what approach is being used *
 */
public enum SMTTranslationApproach {
	USING_PP("SMT translation using PP approach"), USING_VERIT(
			"SMT translation using VeriT approach");

	/**
	 * The name of the approach
	 */
	private String name;

	/**
	 * The constructor of the enum
	 * 
	 * @param name
	 *            the name used to the approach
	 */
	SMTTranslationApproach(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
