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

import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.swt.SWT.APPLICATION_MODAL;
import static org.eclipse.swt.SWT.CANCEL;
import static org.eclipse.swt.SWT.DIALOG_TRIM;
import static org.eclipse.swt.SWT.DROP_DOWN;
import static org.eclipse.swt.SWT.READ_ONLY;
import static org.eclipse.swt.SWT.RESIZE;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eventb.smt.core.preferences.PreferenceManager.solverExists;
import static org.eventb.smt.core.preferences.SMTSolverFactory.newSolver;
import static org.eventb.smt.core.provers.SolverKind.parseKind;
import static org.eventb.smt.ui.internal.preferences.UIUtils.showError;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.PreferenceManager;
import org.eventb.smt.core.provers.SolverKind;

/**
 * This class is the dialog opened when the user wants to add or edit an
 * SMT-solver configuration.
 * 
 * @author guyot
 */
public class SMTSolverDialog extends Dialog {
	private static final String SOLVER_NAME_LABEL = "Name";
	private static final String SOLVER_KIND_LABEL = "Kind";
	private static final String SOLVER_PATH_LABEL = "Path";

	public static final boolean SHOW_ERRORS = true;

	int returnCode = 0;

	final IPreferences smtPrefs;
	ISMTSolver solver;

	public SMTSolverDialog(final Shell parentShell,
			final IPreferences smtPrefs, final ISMTSolver solver) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		this.smtPrefs = smtPrefs;
		this.solver = solver;
		setText("Solver integration");
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(4, true));
		GridData data;

		/**
		 * Solver name
		 */
		final Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText(SOLVER_NAME_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		nameLabel.setLayoutData(data);

		final Text nameText = new Text(shell, SWT.BORDER);
		nameText.setText(solver.getName());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		nameText.setLayoutData(data);

		/**
		 * Solver kind
		 */
		final Label solverLabel = new Label(shell, SWT.NONE);
		solverLabel.setText(SOLVER_KIND_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverLabel.setLayoutData(data);

		final Combo solverCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final SolverKind kind : SolverKind.values()) {
			solverCombo.add(kind.toString());
		}
		solverCombo.setText(solver.getKind().toString());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		solverCombo.setLayoutData(data);

		/**
		 * Solver path
		 */
		final Label solverPathLabel = new Label(shell, SWT.NONE);
		solverPathLabel.setText(SOLVER_PATH_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverPathLabel.setLayoutData(data);

		final Text solverPathText = new Text(shell, SWT.BORDER);
		solverPathText.setText(solver.getPath().toOSString());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		solverPathText.setLayoutData(data);

		final Button browseButton = new Button(shell, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dialog = new FileDialog(shell, getStyle());
				String path = dialog.open();
				if (isValidPath(path, SHOW_ERRORS)) {
					solverPathText.setText(path);
				}
			}
		});
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		browseButton.setLayoutData(data);

		/**
		 * OK Button
		 */
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("OK");
		data = new GridData(FILL_HORIZONTAL);
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final String name = nameText.getText();
				final String pathStr = solverPathText.getText();
				final StringBuilder errBuilder = new StringBuilder();
				if (name.isEmpty()
						|| (!name.equals(solver.getName()) && solverExists(name))) {
					errBuilder
							.append("A unique non-empty solver name is required.\n");
				}
				if (solver.isEditable() && !isValidPath(pathStr, !SHOW_ERRORS)) {
					errBuilder.append("A valid solver path is required.\n");
				}
				if (errBuilder.length() != 0) {
					UIUtils.showError(errBuilder.toString());
				} else {
					final IPath path = new Path(pathStr);
					solver = newSolver(solver.getID(), name,
							parseKind(solverCombo.getText()), path);
					returnCode = OK;
					shell.close();
				}
			}
		});

		/**
		 * Cancel Button
		 */
		final Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		data = new GridData(FILL_HORIZONTAL);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnCode = CANCEL;
				shell.close();
			}
		});

		shell.setDefaultButton(okButton);
	}

	public static boolean isValidPath(final String path,
			final boolean showErrors) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		final StringBuilder error = new StringBuilder();
		if (PreferenceManager.isValidPath(path, error)) {
			return true;
		} else {
			if (showErrors)
				showError(error.toString());
			return false;
		}
	}

	public ISMTSolver getSolver() {
		return solver;
	}

	public int open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnCode;
	}
}
