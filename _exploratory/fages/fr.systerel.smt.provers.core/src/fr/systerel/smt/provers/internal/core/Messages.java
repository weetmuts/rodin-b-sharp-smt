package fr.systerel.smt.provers.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.internal.core.messages"; //$NON-NLS-1$
	public static String force_error_invalid_forces;
	public static String SmtProversCall_19;
	public static String SmtProversCall_after_smt;
	public static String SmtProversCall_before_and_after;
	public static String SmtProversCall_cvc3;
	public static String SmtProversCall_execute_Trans;
	public static String SmtProversCall_lang_option;
	public static String SmtProversCall_no_defined_solver_path;
	public static String SmtProversCall_pre_smt;
	public static String SmtProversCall_prepro_path;
	public static String SmtProversCall_preprocessing_options;
	public static String SmtProversCall_preprocessor_path_not_defined;
	public static String SmtProversCall_print_simp_and_exit_option;
	public static String SmtProversCall_proof_and_show_file;
	public static String SmtProversCall_proof_and_showfile;
	public static String SmtProversCall_proof_only;
	public static String SmtProversCall_show_file_only;
	public static String SmtProversCall_smt_editor;
	public static String SmtProversCall_smt_option;
	public static String SmtProversCall_success;
	public static String SmtProversCall_translated_file_not_exists;
	public static String SmtProversCall_unsat;
	public static String SmtProversCall_unsig_prepro;
	public static String SmtProversCall_using_prepro;
	public static String SmtProversCall_veriT;
	public static String SmtProversCall_which_solver;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
