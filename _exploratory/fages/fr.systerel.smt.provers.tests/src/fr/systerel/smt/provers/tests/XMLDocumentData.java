package fr.systerel.smt.provers.tests;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;

public class XMLDocumentData {

	public XMLDocumentData(String lemmaName, List<String> hypotheses,
			String goal, ITypeEnvironment te, String origin) {
		super();
		this.lemmaName = lemmaName;
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.te = te;
		this.origin = origin;
	}

	private String lemmaName;
	private List<String> hypotheses;
	private String goal;
	private ITypeEnvironment te;
	private String origin;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public ITypeEnvironment getTe() {
		return te;
	}

	public void setTe(ITypeEnvironment te) {
		this.te = te;
	}

	public String getLemmaName() {
		return lemmaName;
	}

	public void setLemmaName(String lemmaName) {
		this.lemmaName = lemmaName;
	}

	public List<String> getHypotheses() {
		return hypotheses;
	}

	public void setHypotheses(List<String> hypotheses) {
		this.hypotheses = hypotheses;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

}
