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
import static org.eventb.smt.core.preferences.PreferenceManager.configExists;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This class is the dialog opened when the user wants to add or edit an
 * SMT-solver configuration.
 * 
 * @author guyot
 */
public class SolverConfigDialog extends Dialog {
	private static final String CONFIG_NAME_LABEL = "Name";
	private static final String SOLVER_NAME_LABEL = "Solver Name";
	private static final String SOLVER_ARGS_LABEL = "Arguments";
	private static final String SMT_LIB_LABEL = "SMT-LIB";

	public static final boolean SHOW_ERRORS = true;

	int returnCode = 0;

	final IPreferences smtPrefs;
	ISolverConfig solverConfig;

	public SolverConfigDialog(final Shell parentShell,
			final IPreferences smtPrefs, final ISolverConfig solverConfig) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		this.smtPrefs = smtPrefs;
		this.solverConfig = solverConfig;
		setText("Solver configuration");
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(4, true));
		GridData data;

		/**
		 * Configuration name
		 */
		final Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText(CONFIG_NAME_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		nameLabel.setLayoutData(data);

		final Text nameText = new Text(shell, SWT.BORDER);
		nameText.setText(solverConfig.getName());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		nameText.setLayoutData(data);

		/**
		 * Solver
		 */
		final Label solverLabel = new Label(shell, SWT.NONE);
		solverLabel.setText(SOLVER_NAME_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverLabel.setLayoutData(data);

		final Combo solverCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final String key : smtPrefs.getSolvers().keySet()) {
			solverCombo.add(key);
		}
		solverCombo.setText(solverConfig.getSolverId());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		solverCombo.setLayoutData(data);

		/**
		 * Solver arguments
		 */
		final Label argsLabel = new Label(shell, SWT.NONE);
		argsLabel.setText(SOLVER_ARGS_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		argsLabel.setLayoutData(data);

		final Text argsText = new Text(shell, SWT.BORDER);
		argsText.setText(solverConfig.getArgs());
		data = new GridData(FILL_HORIZONTAL);
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
		smtlibCombo.setText(solverConfig.getSmtlibVersion().toString());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		smtlibCombo.setLayoutData(data);

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
				final String solver = solverCombo.getText();
				final StringBuilder errBuilder = new StringBuilder();
				if (name.isEmpty()
						|| (!name.equals(solverConfig.getName()) && configExists(name))) {
					errBuilder
							.append("A unique non-empty config name is required.\n");
				}
				if (solver.isEmpty()) {
					errBuilder.append("A solver must be selected.\n");
				}
				if (errBuilder.length() != 0) {
					UIUtils.showError(errBuilder.toString());
				} else {
					solverConfig = newConfig(solverConfig.getID(), name,
							solverCombo.getText(), argsText.getText(),
							parseVersion(smtlibCombo.getText()));
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

	public ISolverConfig getSolverConfig() {
		return solverConfig;
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
