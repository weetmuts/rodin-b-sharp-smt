/**
 * 
 */
package fr.systerel.smt.provers.internal.core;

/**
 * @author guyot
 *
 */
public class IllegalTagException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -33129241139929218L;
	
	private final int cause;

	public IllegalTagException(final int tag) {
		super();
		this.cause = tag;
	}

	@Override
	public String getMessage() {
		return "The given tag \'" + this.cause + "\' isn't a valid tag";
	}
}
