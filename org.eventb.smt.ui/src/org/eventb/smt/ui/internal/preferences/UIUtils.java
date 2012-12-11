/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.NO_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.YES_LABEL;
import static org.eclipse.jface.dialogs.MessageDialog.QUESTION_WITH_CANCEL;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Fages-Tafanelli Yoann
 *         <p>
 *         This class contains some utility static methods that are used in this
 *         Event-B User interface plug-in.
 */
public class UIUtils {
	private static final String SMT_ERROR_TITLE = "SMT Error";
	private static final String SMT_WARNING_TITLE = "SMT Warning";
	private static final String SMT_SOLVER_REMOVAL = "SMT Solver Removal";

	/**
	 * Opens an error dialog to the user displaying the given message.
	 */
	public static void showError(final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				openError(getShell(), SMT_ERROR_TITLE, message);
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
		syncExec(new Runnable() {
			@Override
			public void run() {
				openWarning(getShell(), SMT_WARNING_TITLE, message);
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
				openInformation(getShell(), null, message);
			}
		});
	}

	// FIXME should be synchronised like other methods of this class ?
	public static int showQuestionWithCancel(final String message) {
		final MessageDialog dialog = new MessageDialog(getShell(),
				SMT_SOLVER_REMOVAL, null, message, QUESTION_WITH_CANCEL,
				new String[] { YES_LABEL, NO_LABEL, CANCEL_LABEL }, 0);
		return dialog.open();
	}

	private static void syncExec(final Runnable runnable) {
		final Display display = getWorkbench().getDisplay();
		display.syncExec(runnable);
	}

	static Shell getShell() {
		return getWorkbench().getModalDialogShellProvider().getShell();
	}

}
