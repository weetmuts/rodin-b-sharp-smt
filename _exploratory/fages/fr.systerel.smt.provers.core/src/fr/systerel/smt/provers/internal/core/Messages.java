package fr.systerel.smt.provers.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.internal.core"; //$NON-NLS-1$
	public static String SmtProversCall_veriT_path_not_defined;
	public static String SmtProversCall_SMT_file_does_not_exist;
	public static String force_error_invalid_forces;
	public static String SmtProversCall_Check_Smt_Preferences;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
