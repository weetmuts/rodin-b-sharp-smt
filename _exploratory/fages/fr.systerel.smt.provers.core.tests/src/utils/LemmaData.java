package utils;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;

public class LemmaData {

	public LemmaData(String lemmaName, List<String> hypotheses, String goal,
			ITypeEnvironment te, String origin, String comments,
			List<String> theories) {
		super();
		this.lemmaName = lemmaName;
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.te = te;
		this.origin = origin;
		this.comments = comments;
		this.theories = theories;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<String> getTheories() {
		return theories;
	}

	public void setTheories(List<String> theories) {
		this.theories = theories;
	}

	private String lemmaName;
	private List<String> hypotheses;
	private String goal;
	private ITypeEnvironment te;
	private String origin;
	private String comments;
	private List<String> theories;

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
