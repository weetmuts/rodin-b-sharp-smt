package br.ufrn.smt.solver.translation;

import java.util.ArrayList;

import fr.systerel.smt.provers.ast.SMTFormula;

public class IdentifiersAndSMTStorage {

	ArrayList<String> boundIdentifiers = new ArrayList<String>();
	ArrayList<String> freeIdentifiers = new ArrayList<String>();
	SMTFormula smtFormula;

	public IdentifiersAndSMTStorage(SMTFormula smtFormula,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		this.smtFormula = smtFormula;
		this.boundIdentifiers = boundIdentifiers;
		this.freeIdentifiers = freeIdentifiers;
	}

	public ArrayList<String> getBoundIdentifiers() {
		return boundIdentifiers;
	}

	public void setBoundIdentifiers(ArrayList<String> boundIdentifiers) {
		this.boundIdentifiers = boundIdentifiers;
	}

	public ArrayList<String> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	public void setFreeIdentifiers(ArrayList<String> freeIdentifiers) {
		this.freeIdentifiers = freeIdentifiers;
	}

	public SMTFormula getSmtFormula() {
		return smtFormula;
	}

	public void setSmtFormula(SMTFormula smtFormula) {
		this.smtFormula = smtFormula;
	}

}
