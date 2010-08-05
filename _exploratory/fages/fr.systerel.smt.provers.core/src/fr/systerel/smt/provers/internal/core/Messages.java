package fr.systerel.smt.provers.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.internal.core.messages"; //$NON-NLS-1$
	public static String force_error_invalid_forces;
	public static String SmtProversCall_no_defined_solver_path;
	public static String SmtProversCall_preprocessor_path_not_defined;
	public static String SmtProversCall_translated_file_not_exists;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
