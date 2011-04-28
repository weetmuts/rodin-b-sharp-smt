package fr.systerel.smt.provers.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.osgi.framework.BundleContext;

import fr.systerel.smt.provers.internal.core.ExternalSmt;

/**
 * The activator class controls the plug-in life cycle
 */
public class SmtProversUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.smt.provers.ui"; //$NON-NLS-1$

	// The shared instance
	private static SmtProversUIPlugin plugin;

	/**
	 * Default delay for time-out of the Smt provers (value 30 seconds).
	 */
	public static long DEFAULT_DELAY = 30 * 1000;

	/**
	 * The constructor
	 */
	public SmtProversUIPlugin() {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		final IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	/**
	 * Returns a tactic for applying the Smt prover to a proof tree node.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * externalSMT(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic ExternalSmtTac(final boolean restricted) {
		return BasicTactics.reasonerTac(//
				new ExternalSmt(), //
				new XProverInput(restricted, DEFAULT_DELAY));
	}

	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SmtProversUIPlugin getDefault() {
		plugin.getPreferenceStore();
		return plugin;
	}

	public static IPreferenceStore getDefaultPreferenceStore() {
		return plugin.getPreferenceStore();
	}

}
