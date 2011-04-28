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
		cause = tag;
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append("The given tag \'");
		sb.append(cause);
		sb.append("\' isn't a valid tag");
		return sb.toString();
	}
}
