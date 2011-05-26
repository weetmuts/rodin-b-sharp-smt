package br.ufrn.smt.solver.translation;

public enum SMTTranslationApproach {
	USING_PP("SMT translation using PP approach"),
	USING_VERIT("SMT translation using VeriT approach");

	private String name;

	SMTTranslationApproach(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
