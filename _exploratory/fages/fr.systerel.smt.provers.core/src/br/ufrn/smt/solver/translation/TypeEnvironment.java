package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.ast.SMTIdentifier;
import fr.systerel.smt.provers.ast.commands.SMTDeclareFunCommand;

public class TypeEnvironment {

	private ArrayList<Predicate> hypotheses;

	private Predicate goal;

	private ArrayList<String> sorts = new ArrayList<String>();

	private Hashtable<String, String> funs = new Hashtable<String, String>();

	private ArrayList<SMTDeclareFunCommand> declarefuns = new ArrayList<SMTDeclareFunCommand>();

	private Hashtable<String, String> singleQuotVars = new Hashtable<String, String>();

	private Hashtable<String, String> preds = new Hashtable<String, String>();

	/**
	 * Getters
	 */
	public ArrayList<Predicate> getHypotheses() {
		return hypotheses;
	}

	public Predicate getGoal() {
		return goal;
	}

	public ArrayList<String> getSorts() {
		return sorts;
	}

	public ArrayList<SMTDeclareFunCommand> getDeclarefuns() {
		return declarefuns;
	}

	public Hashtable<String, String> getFuns() {
		return funs;
	}

	public Hashtable<String, String> getPreds() {
		return preds;
	}

	public Hashtable<String, String> getSingleQuotVars() {
		return singleQuotVars;
	}

	/**
	 * Constructor
	 */
	public TypeEnvironment(List<Predicate> hypotheses, Predicate goal) {
		this.hypotheses = new ArrayList<Predicate>(hypotheses);
		this.goal = goal;
	}

	public void getTypeEnvironment() {
		Hashtable<String, Type> typEnv = new Hashtable<String, Type>();
		FreeIdentifier[] freeVars = goal.getFreeIdentifiers();
		if (freeVars != null) {

			for (int i = 0; i < freeVars.length; i++) {
				String name = freeVars[i].getName();
				Type type = freeVars[i].getType();
				typEnv.put(name, type);
			}
		}

		BoundIdentifier[] boundVars = goal.getBoundIdentifiers();
		if (boundVars != null) {
			for (int i = 0; i < boundVars.length; i++) {
				String name = boundVars[i].toString();
				Type type = boundVars[i].getType();
				typEnv.put(name, type);
			}
		}

		for (int j = 0; j < hypotheses.size(); j++) {
			freeVars = hypotheses.get(j).getFreeIdentifiers();
			if (freeVars != null) {
				for (int i = 0; i < freeVars.length; i++) {
					String name = freeVars[i].getName();
					Type type = freeVars[i].getType();
					typEnv.put(name, type);
				}
			}

			boundVars = hypotheses.get(j).getBoundIdentifiers();
			if (boundVars != null) {
				for (int i = 0; i < boundVars.length; i++) {
					String name = boundVars[i].toString();
					Type type = boundVars[i].getType();
					typEnv.put(name, type);
				}
			}
		}
		parseTypeEnv(typEnv);
	}

	private void parseTypeEnv(Hashtable<String, Type> typenvironment) {
		Set<Entry<String, Type>> typeVars = typenvironment.entrySet();
		Iterator<Entry<String, Type>> iterator = typeVars.iterator();
		while (iterator.hasNext()) {

			Entry<String, Type> el = iterator.next();
			String varName = verifyQuotedVar(el.getKey(), typenvironment);
			Type varType = el.getValue();
			// Regra 4
			if (varName.equals(varType.toString())) {
				sorts.add(varType.toString());
			}
			// Regra 6
			else if (varType.getSource() != null) {
				String pair = "(Pair "
						+ getSMTAtomicExpressionFormat(varType.getSource()
								.toString())
						+ " "
						+ getSMTAtomicExpressionFormat(varType.getTarget()
								.toString() + ")");
				preds.put(varName, pair);
			} else if (varType.getBaseType() != null) {
				if (varName.equals(varType.getBaseType().toString())) {
					sorts.add(varName);
				} else {
					preds.put(varName, getSMTAtomicExpressionFormat(varType
							.getBaseType().toString()));
				}
			} else {
				funs.put(varName,
						getSMTAtomicExpressionFormat(varType.toString()));
				declarefuns.add(new SMTDeclareFunCommand(new SMTIdentifier(
						varName), new Type[] {}, varType));
			}
		}
	}

	private String verifyQuotedVar(String name,
			Hashtable<String, Type> typenvironment) {
		if (name.lastIndexOf('\'') > 0) {
			int countofQuots = name.length() - name.lastIndexOf('\'');

			String alternativeName = name.replaceAll("'", "_" + countofQuots
					+ "_");
			Type t = typenvironment.get(alternativeName);
			while (t != null) {
				++countofQuots;
				alternativeName = name + "_" + countofQuots;
				t = typenvironment.get(alternativeName);
			}
			this.singleQuotVars.put(name, alternativeName);
			return alternativeName;
		}
		return name;
	}

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
}
