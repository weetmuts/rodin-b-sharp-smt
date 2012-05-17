/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eventb.smt.ui.internal.Messages.SolverDialog_title;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.smt.core.preferences.ISMTSolver;

/**
 * Implement a dialog for editing an SMT solver definition (name, kind and
 * path). This class is responsible for displaying a dialog to the user together
 * with error messages. However, the bulk of editing the solver definition is
 * delegated to the @link {@link SolverEditor} class.
 *
 * @author Yoann Guyot
 * @author Laurent Voisin
 */
public class SolverDialog extends StatusDialog implements
		IPreferencePageContainer {

	private SolverEditor editor;

	public SolverDialog(final Shell parentShell, final ISMTSolver solver,
			final Set<String> usedNames) {
		super(parentShell);
		setTitle(SolverDialog_title);
		setImage(parentShell.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		setHelpAvailable(false);

		this.editor = new SolverEditor(parentShell, solver, usedNames);
		editor.setContainer(this);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/*
	 * The creation of the intermediate composite "area" is necessary to have
	 * proper margins and alignment.
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		editor.createControl(area);
		return area;
	}

	@Override
	protected void okPressed() {
		editor.performOk();
		super.okPressed();
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateButtons() {
		updateStatus();
	}

	@Override
	public void updateMessage() {
		updateStatus();
	}

	@Override
	public void updateTitle() {
		// Not relevant, do nothing
	}

	private void updateStatus() {
		final IStatus status;
		if (editor.isValid()) {
			status = Status.OK_STATUS;
		} else {
			final String msg = editor.getErrorMessage();
			status = new Status(IStatus.ERROR, "dummy", msg); //$NON-NLS-1$
		}
		updateStatus(status);
	}

	public ISMTSolver getSolver() {
		return editor.getSolver();
	}

}
