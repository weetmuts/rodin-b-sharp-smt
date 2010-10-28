package br.ufrn.smt.solver.translation;

import java.util.Hashtable;
import java.util.List;

import fr.systerel.smt.provers.ast.commands.SMTDeclareFunCommand;

public class Signature {
	private final String logic;

	private List<String> sorts;

	private Hashtable<String, String> funs;

	private List<SMTDeclareFunCommand> declarefuns;

	private Hashtable<String, String> singleQuotVars;

	private Hashtable<String, String> preds;

	public Signature(final String logic, final List<String> sorts,
			final Hashtable<String, String> funs,
			final List<SMTDeclareFunCommand> declarefuns,
			final Hashtable<String, String> singleQuotVars,
			final Hashtable<String, String> preds) {
		this.logic = logic;
		this.sorts = sorts;
		this.funs = funs;
		this.declarefuns = declarefuns;
		this.singleQuotVars = singleQuotVars;
		this.preds = preds;
	}

	public Signature(final List<String> sorts,
			final Hashtable<String, String> funs,
			final List<SMTDeclareFunCommand> declarefuns,
			final Hashtable<String, String> singleQuotVars,
			final Hashtable<String, String> preds) {
		this("UNKNOWN", sorts, funs, declarefuns, singleQuotVars, preds);
	}

	/**
	 * Getters
	 */
	public String getLogic() {
		return this.logic;
	}

	public List<String> getSorts() {
		return this.sorts;
	}

	public List<SMTDeclareFunCommand> getDeclarefuns() {
		return this.declarefuns;
	}

	public Hashtable<String, String> getFuns() {
		return this.funs;
	}

	public Hashtable<String, String> getPreds() {
		return this.preds;
	}

	public Hashtable<String, String> getSingleQuotVars() {
		return this.singleQuotVars;
	}
}
