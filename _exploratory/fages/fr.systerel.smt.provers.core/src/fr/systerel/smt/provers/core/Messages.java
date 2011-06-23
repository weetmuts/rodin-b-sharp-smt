package fr.systerel.smt.provers.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.core.messages"; //$NON-NLS-1$
	public static String SMTProversCore_NoSMTSolverSelected;
	public static String SMTProversCore_NoSMTSolverSet;
	public static String SMTProversCore_SMTSolverConfigError;
	public static String SMTProversCore_VeriTPathNotSet;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// do not instantiate
	}
}
