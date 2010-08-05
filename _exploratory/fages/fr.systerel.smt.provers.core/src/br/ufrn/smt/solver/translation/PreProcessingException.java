package br.ufrn.smt.solver.translation;

public class PreProcessingException extends RuntimeException {
	
	private static final long serialVersionUID = 7635224872741003448L;
	
	public PreProcessingException() {
			super("The SMT File was not well pre-processed in solver");
	}
	public PreProcessingException(String cause) {
		super(cause);
	
}

}
