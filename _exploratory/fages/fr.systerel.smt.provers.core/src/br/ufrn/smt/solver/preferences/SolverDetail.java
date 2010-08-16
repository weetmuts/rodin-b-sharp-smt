package br.ufrn.smt.solver.preferences;

/**
 * This is a class to describe a solver detail
 * 
 */
public class SolverDetail {
	private String id;
	
	private String path;
	
	private String args;
	
	private boolean smtV1_2; 
	
	private boolean smtV2_0; 
	
	public String getId() {
		return id;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getArgs() {
		return args;
	}
	
	public boolean getsmtV1_2() {
		return smtV1_2;
	}
	
	public boolean getsmtV2_0() {
		return smtV2_0;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setArgs(String args) {
		this.args = args;
	}
	
	public void setSmtV1_2(boolean smtV1_2) {
		this.smtV1_2 = smtV1_2;
	}
	
	public void setSmtV2_0(boolean smtV2_0) {
		this.smtV2_0 = smtV2_0;
	}

	public SolverDetail(String id, String path, String args, boolean smtV1_2, boolean smtV2_0) {
		this.id = id;
		this.path = path;
		this.args = args;
		this.smtV1_2 = smtV1_2;
		this.smtV2_0 = smtV2_0;
	}
}
