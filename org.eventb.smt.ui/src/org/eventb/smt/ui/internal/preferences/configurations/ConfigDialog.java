/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.swt.SWT.APPLICATION_MODAL;
import static org.eclipse.swt.SWT.CANCEL;
import static org.eclipse.swt.SWT.DIALOG_TRIM;
import static org.eclipse.swt.SWT.DROP_DOWN;
import static org.eclipse.swt.SWT.READ_ONLY;
import static org.eclipse.swt.SWT.RESIZE;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eventb.smt.core.TranslationApproach.parseApproach;

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
import org.eventb.smt.core.TranslationApproach;
import org.eventb.smt.ui.internal.UIUtils;
import org.eventb.smt.ui.internal.preferences.solvers.SolverElement;

/**
 * This class is the dialog opened when the user wants to add or edit an
 * SMT-solver configuration.
 *
 * @author Yoann Guyot
 */
public class ConfigDialog extends Dialog {

	private static final String CONFIG_NAME_LABEL = "Name";
	private static final String SOLVER_NAME_LABEL = "Solver Name";
	private static final String SOLVER_ARGS_LABEL = "Arguments";
	private static final String TRANSLATOR_LABEL = "Translator";
	private static final String ENABLE_LABEL = "Enable";

	public static final boolean SHOW_ERRORS = true;

	int returnCode = 0;

	final ConfigModel model;
	final ConfigElement config;

	public ConfigDialog(final Shell parentShell, final ConfigModel model,
			final ConfigElement config) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		setText("Solver configuration");
		this.model = model;
		this.config = config;
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
		nameText.setText(config.name);
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		nameText.setLayoutData(data);

		/**
		 * Execution
		 */
		data = new GridData();
		data.horizontalSpan = 1;
		final Button executionCheckButton = new Button(shell, SWT.CHECK);
		executionCheckButton.setText(ENABLE_LABEL);
		executionCheckButton.setSelection(config.enabled);
		executionCheckButton.setLayoutData(data);

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
		for (final SolverElement solver : model.getSolverElements()) {
			solverCombo.add(solver.name);
		}
		solverCombo.setText(config.solverName);
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
		argsText.setText(config.args);
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		argsText.setLayoutData(data);

		/**
		 * Translation approach
		 */
		final Label translatorLabel = new Label(shell, SWT.NONE);
		translatorLabel.setText(TRANSLATOR_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		translatorLabel.setLayoutData(data);

		final Combo translatorCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final TranslationApproach translationApproach : TranslationApproach
				.values()) {
			translatorCombo.add(translationApproach.toString());
		}
		translatorCombo.setText(config.approach.toString());
		data = new GridData(FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		translatorCombo.setLayoutData(data);

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
				final String solverStr = solverCombo.getText();
				final StringBuilder errBuilder = new StringBuilder();
				if (name.isEmpty() || model.usedNames(config).contains(name)) {
					errBuilder
							.append("A unique non-empty config name is required.\n");
				}
				if (solverStr.isEmpty()) {
					errBuilder.append("A solver must be selected.\n");
				}
				if (errBuilder.length() != 0) {
					UIUtils.showError(errBuilder.toString());
				} else {
					config.name = name;
					config.enabled = executionCheckButton.getSelection();
					config.solverName = solverCombo.getText();
					config.args = argsText.getText();
					config.approach = parseApproach(translatorCombo.getText());
					model.update(config);
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
