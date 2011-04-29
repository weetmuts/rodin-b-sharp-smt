/**
 * 
 */
package fr.systerel.smt.provers.ast;

/**
 * @author vitor
 * 
 */
public class SMTFactoryPP extends SMTFactory {
	private final static SMTFactoryPP DEFAULT_INSTANCE = new SMTFactoryPP();

	public static SMTFactoryPP getInstance() {
		return DEFAULT_INSTANCE;
	}
}
