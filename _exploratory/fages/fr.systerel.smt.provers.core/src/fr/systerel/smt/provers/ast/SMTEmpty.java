/**
 * 
 */
package fr.systerel.smt.provers.ast;

/**
 * @author guyot
 *
 */
public class SMTEmpty extends SMTNode<SMTEmpty> {

	public SMTEmpty() {
		super(SMTNode.NO_TAG);
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append("");
	}
}
