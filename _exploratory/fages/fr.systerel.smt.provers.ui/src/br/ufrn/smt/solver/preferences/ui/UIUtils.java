/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

/**
 * @author Fages-Tafanelli Yoann
 *         <p>
 *         This class contains some utility static methods that are used in this
 *         Event-B User interface plug-in.
 */
public class UIUtils {

	/**
	 * Opens an error dialog to the user displaying the given message.
	 */
	public static void showError(final String message) {
		final String title = "SMT error";
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(getShell(), title, message);
			}
		});
	}

	/**
	 * Opens a warning dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message displayed
	 * 
	 */
	public static void showWarning(final String message) {
		final String smtWarningTitle = "SMT warning";
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(getShell(), smtWarningTitle, message);
			}
		});
	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message
	 */
	public static void showInfo(final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), null, message);
			}
		});
	}

	private static void syncExec(final Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}

	static Shell getShell() {
		return SmtProversUIPlugin.getActiveWorkbenchShell();
	}

}
