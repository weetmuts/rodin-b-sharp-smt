package fr.systerel.smt.provers.core;

import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.xprover.XProverInput;

import fr.systerel.smt.provers.internal.core.ExternalSmt;


/**
 * The main plugin class for the Smt provers.
 */
public class SmtProversCore extends Plugin {
	/**
	 * The plug-in identifier 
	 */
	public static final String PLUGIN_ID = "fr.systerel.smt.provers.core"; 

	// The shared instance.
	private static SmtProversCore PLUGIN;
	
	/**
	 * Default delay for time-out of the Atelier B provers (value 30 seconds).
	 */
	public static long DEFAULT_DELAY = 30 * 1000;

	/**
	 * Returns a tactic for applying the Smt prover to a proof tree node.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * <pre>
	 *    externalSMT(forces, DEFAULT_DELAY)
	 * </pre>
	 * </p>
	 * 
	 * @param forces
	 *            a mask formed by the bitwise or of SMT forces to use
	 * @return a tactic for running SMT with the given forces
	 * @see #externalSMT(int, long)
	 */
	public static ITactic externalSMT(boolean restricted) {
		return BasicTactics.reasonerTac(
				new ExternalSmt(),
				new XProverInput(restricted,DEFAULT_DELAY));
	}
	
	/**
	 * Returns the single instance of the Smt Provers for Rodin core plug-in.
	 * 
	 * @return the single instance of the Smt Provers for Rodin core plug-in
	 */
	public static SmtProversCore getDefault() {
		return PLUGIN;
	}

	/**
	 * Creates the Smt Provers for Rodin core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public SmtProversCore() {
		super();
		PLUGIN = this;
	}

}
