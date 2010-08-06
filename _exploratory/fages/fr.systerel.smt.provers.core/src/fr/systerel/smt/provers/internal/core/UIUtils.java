/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * @author Fages-Tafanelli Yoann
 *         <p>
 *         This class contains some utility static methods that are used in this
 *         Event-B User interface plug-in.
 */
public class UIUtils {

	/**
	 * Opens an error dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message displayed
	 * @param title
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

	private static void syncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}

	static Shell getShell() {
		return SmtProversCore.getActiveWorkbenchShell();
	}

}
