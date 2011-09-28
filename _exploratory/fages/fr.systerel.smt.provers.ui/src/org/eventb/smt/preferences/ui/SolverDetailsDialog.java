/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.preferences.ui;

import static org.eclipse.jface.window.Window.CANCEL;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.swt.SWT.APPLICATION_MODAL;
import static org.eclipse.swt.SWT.DIALOG_TRIM;
import static org.eclipse.swt.SWT.DROP_DOWN;
import static org.eclipse.swt.SWT.READ_ONLY;
import static org.eclipse.swt.SWT.RESIZE;
import static org.eventb.smt.provers.internal.core.SMTSolver.UNKNOWN;
import static org.eventb.smt.provers.internal.core.SMTSolver.getSolver;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.translation.SMTLIBVersion.getVersion;

import java.io.File;

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
import org.eventb.smt.preferences.SolverConfiguration;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.translation.SMTLIBVersion;

/**
 * @author guyot
 * 
 */
public class SolverDetailsDialog extends Dialog {
	private static final String SOLVER_ID_LABEL = "Solver ID";
	private static final String SOLVER_LABEL = "Solver";
	private static final String SOLVER_PATH_LABEL = "Solver path";
	private static final String SOLVER_ARGS_LABEL = "Solver arguments";
	private static final String SMT_LIB_LABEL = "SMT-LIB";

	int returnCode = 0;

	final SolverConfiguration solverDetails;

	public SolverDetailsDialog(final Shell parentShell,
			final SolverConfiguration solverDetails) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		if (solverDetails != null) {
			this.solverDetails = solverDetails;
		} else {
			this.solverDetails = new SolverConfiguration("", UNKNOWN, "", "", V1_2);
		}
		setText("Solver settings");
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(4, true));
		GridData data;

		/**
		 * Solver ID
		 */
		final Label idLabel = new Label(shell, SWT.NONE);
		idLabel.setText(SOLVER_ID_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		idLabel.setLayoutData(data);

		final Text idText = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		idText.setLayoutData(data);

		/**
		 * Solver
		 */
		final Label solverLabel = new Label(shell, SWT.NONE);
		solverLabel.setText(SOLVER_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverLabel.setLayoutData(data);

		final Combo solverCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final SMTSolver solver : SMTSolver.values()) {
			solverCombo.add(solver.toString());
		}
		data = new GridData(GridData.FILL_HORIZONTAL);
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
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		solverPathText.setLayoutData(data);

		final Button browseButton = new Button(shell, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dialog = new FileDialog(shell, getStyle());
				String path = dialog.open();
				if (validPath(path)) {
					solverPathText.setText(path);
				}
			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		browseButton.setLayoutData(data);

		/**
		 * Solver arguments
		 */
		final Label argsLabel = new Label(shell, SWT.NONE);
		argsLabel.setText(SOLVER_ARGS_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		argsLabel.setLayoutData(data);

		final Text argsText = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		argsText.setLayoutData(data);

		/**
		 * SMT-LIB version
		 */
		final Label smtlibLabel = new Label(shell, SWT.NONE);
		smtlibLabel.setText(SMT_LIB_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		smtlibLabel.setLayoutData(data);

		final Combo smtlibCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final SMTLIBVersion smtlibVersion : SMTLIBVersion.values()) {
			smtlibCombo.add(smtlibVersion.toString());
		}
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		smtlibCombo.setLayoutData(data);

		/**
		 * OK Button
		 */
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final String id = idText.getText();
				final String path = solverPathText.getText();
				if (!id.isEmpty()) {
					if (validPath(path)) {
						solverDetails.setId(id);
						solverDetails.setSolver(getSolver(solverCombo.getText()));
						solverDetails.setPath(path);
						solverDetails.setArgs(argsText.getText());
						solverDetails.setSmtlibVersion(getVersion(smtlibCombo
								.getText()));
						returnCode = OK;
						shell.close();
					}
				} else {
					UIUtils.showError("Please, fill the id and path fields.");
				}
			}
		});

		/**
		 * Cancel Button
		 */
		final Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
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

	boolean validPath(final String path) {
		if (path != null) {
			final File file = new File(path);
			try {
				if (file.exists()) {
					if (file.isFile()) {
						if (file.canExecute()) {
							return true;
						} else {
							UIUtils.showError("Rodin cannot execute the indicated file.");
							return false;
						}
					} else {
						UIUtils.showError("The indicated file is not a valid file.");
						return false;
					}
				} else {
					UIUtils.showError("The indicated file does not exist.");
					return false;
				}
			} catch (SecurityException se) {
				UIUtils.showError("Rodin cannot read or execute the indicated file.");
				return false;
			}
		} else {
			return false;
		}
	}

	public SolverConfiguration getSolverDetails() {
		return solverDetails;
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
