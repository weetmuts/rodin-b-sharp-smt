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

import static org.eventb.smt.preferences.ui.Messages.SMTPreferencePage2_MandatoryFieldsInSolverDetails;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eventb.smt.preferences.SolverDetails;


/**
 * @author guyot
 * 
 */
public class SolverDetailsDialog extends Dialog {
	private static final String SOLVER_ID_LABEL = "Solver ID";
	private static final String SOLVER_PATH_LABEL = "Solver path";
	private static final String SOLVER_ARGS_LABEL = "Solver arguments";
	private static final String SMT_LIB_LABEL = "SMT-LIB";
	private static final String V1_2_LABEL = "v1.2";
	private static final String V2_0_LABEL = "v2.0";

	FieldEditorPreferencePage page;

	StringFieldEditor idEditor;
	FileFieldEditor pathEditor;
	StringFieldEditor argsEditor;
	Button smt1_2_Button;
	Button smt2_0_Button;

	SolverDetails solverDetails;

	public SolverDetailsDialog(final Shell parentShell,
			final SolverDetails solverDetails) {
		super(parentShell);
		if (solverDetails != null) {
			this.solverDetails = solverDetails;
		} else {
			this.solverDetails = new SolverDetails("", "", "", false, false);
		}
	}

	/**
	 * Centre the dialog in the preference page
	 */
	private static void centreShellInParentComposite(final Shell shell) {
		final Rectangle parentBounds = shell.getParent().getBounds();
		final Rectangle shellBounds = shell.getBounds();
		final int centerX = parentBounds.x
				+ (parentBounds.width - shellBounds.width) / 2;
		final int centerY = parentBounds.y
				+ (parentBounds.height - shellBounds.height) / 2;
		shell.setLocation(centerX, centerY);
	}

	public SolverDetails getSolverDetails() {
		return solverDetails;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Solver settings");
		shell.setSize(400, 260);
		centreShellInParentComposite(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		page = new FieldEditorPreferencePage(FieldEditorPreferencePage.GRID) {
			private void createVersionControl(Composite parentComposite) {
				/**
				 * Creates the SMT-LIB version fields (label, and mutually
				 * exclusive checkboxes).
				 */
				final Composite compSmtVersion = new Composite(parentComposite,
						SWT.NONE);
				compSmtVersion.setLayout(new GridLayout(3, false));
				final GridData gridData = new GridData(SWT.DEFAULT, 30);
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.verticalAlignment = SWT.FILL;
				compSmtVersion.setLayoutData(gridData);

				final Label smtVersionLabel = new Label(compSmtVersion,
						SWT.LEFT);
				smtVersionLabel.setText(SMT_LIB_LABEL);

				smt1_2_Button = new Button(compSmtVersion, SWT.CHECK);
				smt1_2_Button.setText(V1_2_LABEL);
				smt1_2_Button.setSelection(solverDetails.getsmtV1_2());

				smt2_0_Button = new Button(compSmtVersion, SWT.CHECK);
				smt2_0_Button.setText(V2_0_LABEL);
				smt2_0_Button.setSelection(solverDetails.getsmtV2_0());

				smt1_2_Button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						smt2_0_Button.setSelection(false);
					}

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						// Nothing to do.
					}
				});

				smt2_0_Button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						smt1_2_Button.setSelection(false);
					}

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						// Nothing to do.
					}
				});

				compSmtVersion.setVisible(true);
				compSmtVersion.pack();
			}

			@Override
			public void createControl(Composite parentComposite) {
				noDefaultAndApplyButton();
				super.createControl(parentComposite);
				createVersionControl(parentComposite);
			}

			@Override
			protected void createFieldEditors() {
				idEditor = new StringFieldEditor("", SOLVER_ID_LABEL,
						getFieldEditorParent());
				idEditor.setStringValue(solverDetails.getId());
				addField(idEditor);

				pathEditor = new FileFieldEditor("", SOLVER_PATH_LABEL,
						getFieldEditorParent());
				pathEditor.setStringValue(solverDetails.getPath());
				addField(pathEditor);

				argsEditor = new StringFieldEditor("", SOLVER_ARGS_LABEL,
						getFieldEditorParent());
				argsEditor.setStringValue(solverDetails.getArgs());
				addField(argsEditor);
			}
		};

		page.createControl(composite);
		final Control pageControl = page.getControl();
		pageControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		return pageControl;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			/**
			 * If something is missing,
			 */
			final String path = pathEditor.getStringValue();
			if (idEditor.getStringValue() == ""
					|| (!smt1_2_Button.getSelection() && !smt2_0_Button
							.getSelection()) || path == "") {
				/**
				 * Displays a pop-up error message displayed.
				 */
				UIUtils.showError(SMTPreferencePage2_MandatoryFieldsInSolverDetails);
				return;
			} else {
				final File pathFile = new File(path);
				try {
					if (pathFile.exists()) {
						if (pathFile.isFile()) {
							if (pathFile.canExecute()) {
								solverDetails.setId(idEditor.getStringValue());
								solverDetails.setPath(pathEditor
										.getStringValue());
								solverDetails.setArgs(argsEditor
										.getStringValue());
								solverDetails.setSmtV1_2(smt1_2_Button
										.getSelection());
								solverDetails.setSmtV2_0(smt2_0_Button
										.getSelection());
							} else {
								/**
								 * Displays a pop-up error message displayed.
								 */
								UIUtils.showError("Rodin cannot execute the indicated file.");
								return;
							}
						} else {
							/**
							 * Displays a pop-up error message displayed.
							 */
							UIUtils.showError("The indicated file is not a valid file.");
							return;
						}
					} else {
						/**
						 * Displays a pop-up error message displayed.
						 */
						UIUtils.showError("The indicated file does not exist.");
						return;
					}
				} catch (SecurityException se) {
					/**
					 * Displays a pop-up error message displayed.
					 */
					UIUtils.showError("Rodin cannot read or execute the indicated file.");
					return;
				}
			}
		}
		super.buttonPressed(buttonId);
	}
}
