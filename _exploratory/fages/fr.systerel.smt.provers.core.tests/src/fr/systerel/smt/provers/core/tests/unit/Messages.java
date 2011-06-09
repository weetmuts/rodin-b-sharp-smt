package fr.systerel.smt.provers.core.tests.unit;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.core.tests.unit.messages"; //$NON-NLS-1$

	public static String SMTLIB_Translation_Failed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
