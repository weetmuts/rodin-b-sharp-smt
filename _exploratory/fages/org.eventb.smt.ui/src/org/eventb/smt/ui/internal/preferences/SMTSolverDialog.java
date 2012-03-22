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
import static org.eventb.smt.core.preferences.AbstractBundledSolverRegistry.getBundledSolverRegistry;
import static org.eventb.smt.core.preferences.AbstractSMTSolver.newSolver;
import static org.eventb.smt.core.provers.SolverKind.parseKind;
import static org.eventb.smt.ui.internal.preferences.UIUtils.showError;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
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
import org.eventb.smt.core.preferences.AbstractPreferences;
import org.eventb.smt.core.preferences.AbstractSMTSolver;
import org.eventb.smt.core.preferences.ExtensionLoadingException;
import org.eventb.smt.core.preferences.IRegistry;
import org.eventb.smt.core.provers.SolverKind;

/**
 * This class is the dialog opened when the user wants to add or edit an
 * SMT-solver configuration.
 * 
 * @author guyot
 */
public class SMTSolverDialog extends Dialog {
	private static final String SOLVER_ID_LABEL = "Solver ID";
	private static final String SOLVER_NAME_LABEL = "Name";
	private static final String SOLVER_KIND_LABEL = "Kind";
	private static final String SOLVER_PATH_LABEL = "Path";

	public static final boolean SHOW_ERRORS = true;

	int returnCode = 0;

	final AbstractPreferences smtPrefs;
	AbstractSMTSolver solver;

	public SMTSolverDialog(final Shell parentShell,
			final AbstractPreferences smtPrefs, final AbstractSMTSolver solver) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		this.smtPrefs = smtPrefs;
		if (solver != null) {
			this.solver = solver;
		} else {
			this.solver = newSolver();
		}
		setText("Solver integration");
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(4, true));
		GridData data;

		/**
		 * Configuration ID
		 */
		final Label idLabel = new Label(shell, SWT.NONE);
		idLabel.setText(SOLVER_ID_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		idLabel.setLayoutData(data);

		final Text idText = new Text(shell, SWT.BORDER);
		idText.setText(solver.getID());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		idText.setLayoutData(data);

		/**
		 * Configuration name
		 */
		final Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText(SOLVER_NAME_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		nameLabel.setLayoutData(data);

		final Text nameText = new Text(shell, SWT.BORDER);
		nameText.setText(solver.getName());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		nameText.setLayoutData(data);

		/**
		 * Solver
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
		solverPathText.setText(solver.getPath().toOSString());
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
				if (isValidPath(path, SHOW_ERRORS)) {
					solverPathText.setText(path);
				}
			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		browseButton.setLayoutData(data);

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
				final String name = nameText.getText();
				final String pathStr = solverPathText.getText();
				if (id.equals(solver.getID()) || smtPrefs.validId(id)) {
					if (isValidPath(pathStr, SHOW_ERRORS)) {
						final IPath path = new Path(pathStr);
						// TODO set the right name value
						solver = newSolver(id, name,
								parseKind(solverCombo.getText()), path);
						returnCode = OK;
						shell.close();
					}
				} else {
					final StringBuilder errBuilder = new StringBuilder();
					errBuilder
							.append("A solver ID and the solver path are required.\n");
					errBuilder.append("The solver ID must be unique.\n");
					StringBuilder errBuilder2 = new StringBuilder();
					try {
						final IRegistry<?> registry = getBundledSolverRegistry();
						errBuilder2
								.append("The following solver IDs are reserved:\n");
						for (final Object elem : registry.getMap().values()) {
							final AbstractSMTSolver bundledSolver = (AbstractSMTSolver) elem;
							errBuilder2.append("'");
							errBuilder2.append(bundledSolver.getID());
							errBuilder2.append("'\n");
						}
						errBuilder2.append("'.");
					} catch (InvalidRegistryObjectException iroe) {
						// TODO log the error
						errBuilder2 = new StringBuilder(
								"The specified solver ID is reserved.");
					} catch (ExtensionLoadingException e) {
						// TODO log the error
						errBuilder2 = new StringBuilder(
								"The specified solver ID is reserved.");
					}
					errBuilder.append(errBuilder2);
					UIUtils.showError(errBuilder.toString());
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

	public static boolean isValidPath(final String path,
			final boolean showErrors) {
		final StringBuilder error = new StringBuilder();
		if (AbstractPreferences.isPathValid(path, error)) {
			return true;
		} else {
			if (showErrors)
				showError(error.toString());
			return false;
		}
	}

	public AbstractSMTSolver getSolver() {
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