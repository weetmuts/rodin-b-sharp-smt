package br.ufrn.smt.solver.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "br.ufrn.smt.solver.preferences.messages"; //$NON-NLS-1$
	public static String SMTPreferencePage2_MandatoryFieldsInSolverDetails;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
