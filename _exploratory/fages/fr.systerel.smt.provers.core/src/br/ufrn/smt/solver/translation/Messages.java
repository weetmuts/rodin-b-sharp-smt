package br.ufrn.smt.solver.translation;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "br.ufrn.smt.solver.translation.messages"; //$NON-NLS-1$
	public static String PreProcessingException_error;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
