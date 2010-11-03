package br.ufrn.smt.solver.translation;

import java.util.HashMap;
import java.util.List;

import fr.systerel.smt.provers.ast.commands.SMTDeclareFunCommand;

public class Signature {
	private final String logic;

	private List<String> sorts;

	private HashMap<String, String> funs;

	private List<SMTDeclareFunCommand> declarefuns;

	private HashMap<String, String> singleQuotVars;

	private HashMap<String, String> preds;

	public Signature(final String logic, final List<String> sorts,
			final HashMap<String, String> funs,
			final List<SMTDeclareFunCommand> declarefuns,
			final HashMap<String, String> singleQuotVars,
			final HashMap<String, String> preds) {
		this.logic = logic;
		this.sorts = sorts;
		this.funs = funs;
		this.declarefuns = declarefuns;
		this.singleQuotVars = singleQuotVars;
		this.preds = preds;
	}

	public Signature(final List<String> sorts,
			final HashMap<String, String> funs,
			final List<SMTDeclareFunCommand> declarefuns,
			final HashMap<String, String> singleQuotVars,
			final HashMap<String, String> preds) {
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

	public HashMap<String, String> getFuns() {
		return this.funs;
	}

	public HashMap<String, String> getPreds() {
		return this.preds;
	}

	public HashMap<String, String> getSingleQuotVars() {
		return this.singleQuotVars;
	}

	public void addFun(final String name, final String type) {
		this.funs.put(name, type);
	}
}
