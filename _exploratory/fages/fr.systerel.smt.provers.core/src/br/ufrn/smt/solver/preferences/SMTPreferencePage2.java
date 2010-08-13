/*******************************************************************************
 * Copyright (c) 2010 Systerel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import fr.systerel.smt.provers.core.SmtProversCore;
import fr.systerel.smt.provers.internal.core.UIUtils;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class SMTPreferencePage2 extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String SOLVER_ID = "Solver ID";

	public static final String SOLVER_PATH = "Solver path";

	public static final String V1_2 = "v1.2";

	public static final String V2_0 = "v2.0";
	
	public static final String[] PROPS = { SOLVER_ID, SOLVER_PATH, V1_2, V2_0};

	protected TableViewer fTable;

	protected Control fTableControl;
	
	private List<SolverDetail> fModel= new ArrayList<SolverDetail>();

	/**
	 * The name of the preference displayed in this preference page.
	 */
	private String preferenceName;

	public SMTPreferencePage2() {
		setPreferenceStore(SmtProversCore.getDefault().getPreferenceStore());
		setDescription("SMT-Solver Plugin Preference Page YFT"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		return createTableAndButtons(parent);
	}

	/**
	 * Creates the table viewer and buttons and configure it.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private Control createTableAndButtons(final Composite parent) {

		// Create a new Composite with 2 columns
		final Composite comp = new Composite(parent, SWT.NONE);

		// resize comp
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Define 2 columns (1 for the table and 1 for buttons)
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		fTable = createTableViewer(comp);
		fTable.setInput(fModel);

		// Configure table
		final Table tableControl = fTable.getTable();
		tableControl.setHeaderVisible(true);
		tableControl.setLinesVisible(true);
		tableControl
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// Create a new Composite with 1 column to dispose buttons
		final Composite compButtons = new Composite(comp, SWT.NONE);

		// resize compButtons
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Define 1 column for buttons
		final GridLayout layoutButtons = new GridLayout(1, false);
		layoutButtons.marginHeight = 0;
		layoutButtons.marginWidth = 0;
		compButtons.setLayout(layoutButtons);

		// Add buttons
		final Button addButton = new Button(compButtons, SWT.PUSH);
		addButton.setText("Add..."); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				final Shell shell = new Shell(compButtons.getShell());
				shell.setLayout(new GridLayout(1, false));
				shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				shell.setSize(400, 200);

				final Composite compName = new Composite(shell, SWT.NONE);
				final Composite compPath = new Composite(shell, SWT.NONE);
				final Composite compArg = new Composite(shell, SWT.NONE);
				final Composite compSmtVersion = new Composite(shell, SWT.NONE);
				final Composite compOkCancel = new Composite(shell, SWT.NONE);

				compName.setLayout(new GridLayout(2, false));
				compPath.setLayout(new GridLayout(3, false));
				compArg.setLayout(new GridLayout(2, false));
				compSmtVersion.setLayout(new GridLayout(3, false));
				compOkCancel.setLayout(new GridLayout(2, false));

				compName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				compPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				compArg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				compSmtVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true));
				compOkCancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true));

				final Label solverIdTextLabel = new Label(compName, SWT.LEFT);
				solverIdTextLabel.setText("Solver ID"); //$NON-NLS-1$

				final Text solverIdText = new Text(compName, SWT.LEFT
						| SWT.BORDER);
				solverIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, true));

				final Label solverPathTextLabel = new Label(compPath, SWT.LEFT);
				solverPathTextLabel.setText("Solver Path"); //$NON-NLS-1$

				final Text solverPathText = new Text(compPath, SWT.LEFT
						| SWT.BORDER);
				solverPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, true));
				solverPathText.setEditable(false);

				// Add button Browse
				Button browseButton = new Button(compPath, SWT.PUSH);
				browseButton.setText("Browse"); //$NON-NLS-1$
				browseButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						File f = new File(solverPathText.getText());
						if (!f.exists()) {
							f = null;
						}
						File d = getFile(f);
						solverPathText.setText(d.getPath());
					}

				});

				final Label smtVersionLabel = new Label(compSmtVersion,
						SWT.LEFT);
				smtVersionLabel.setText("SMT-Lib"); //$NON-NLS-1$

				final Button smt1_2_Button = new Button(compSmtVersion,
						SWT.CHECK);
				smt1_2_Button.setText("1.2"); //$NON-NLS-1$

				final Button smt2_0_Button = new Button(compSmtVersion,
						SWT.CHECK);
				smt2_0_Button.setText("2.0"); //$NON-NLS-1$

				// callbacks for the 2 buttons
				smt1_2_Button.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						smt2_0_Button.setSelection(false);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub

					}
				});

				smt2_0_Button.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						smt1_2_Button.setSelection(false);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub

					}
				});

				// Add 2 buttons OK and Cancel
				Button okButton = new Button(compOkCancel, SWT.PUSH);
				okButton.setText("OK"); //$NON-NLS-1$
				okButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						// Check mandatory fields
						if (solverIdText.getText() == "" //$NON-NLS-1$
								|| (!smt1_2_Button.getSelection() && !smt2_0_Button
										.getSelection())
								|| solverPathText.getText() == "") { //$NON-NLS-1$
							// Message popup displayed when there is no defined
							// solver path or smt lib chosen or smt Id
							UIUtils.showError(br.ufrn.smt.solver.preferences.Messages.SMTPreferencePage2_MandatoryFieldsInSolverDetails);
						} else {
							fModel.add(new SolverDetail(
											solverIdText.getText(), 
											solverPathText.getText(), 
											smt1_2_Button.getSelection(), 
											smt2_0_Button.getSelection()));

							// Update table with solver details
							fTable.refresh();

							// Close the shell
							shell.close();
						}

					}

				});

				Button cancelButton = new Button(compOkCancel, SWT.PUSH);
				cancelButton.setText("Cancel"); //$NON-NLS-1$
				cancelButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						// Close the shell
						shell.close();
					}

				});

				// Open the shell
				shell.open();
			}
		});

		final Button removeButton = new Button(compButtons, SWT.PUSH);
		removeButton.setText("Remove"); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			}
		});

		final Button editButton = new Button(compButtons, SWT.PUSH);
		editButton.setText("Edit..."); //$NON-NLS-1$
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			}
		});

		// Update table with solver details
		fTable.refresh();

		// pack everything
		//fTable.getTable().pack();
		//parent.pack();

		// return the new created composite
		return comp;
	}

	/**
	 * Creates a table viewer and configure it.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected TableViewer createTableViewer(Composite parent) {

		String[] columnNames = new String[] {
				"Solver ID", "Solver Path", "V1.2", "V2.0" }; //$NON-NLS-1$ //$NON-NLS-2$
		final TableViewer tv = new TableViewer(parent, SWT.FULL_SELECTION);
		
		tv.setColumnProperties(columnNames);
		tv.setContentProvider(new SolversDetailsContentProvider());
		tv.setLabelProvider(new SolversDetailsLabelProvider());
		
		Table table = tv.getTable();
		TableColumn tc;
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText(columnNames[0]);
		tc.setWidth(90);
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText(columnNames[1]);
		tc.setWidth(200);
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText(columnNames[2]);
		tc.setWidth(40);
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText(columnNames[3]);
		tc.setWidth(40);

	    
	    CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new CheckboxCellEditor(table);
		editors[3] = new CheckboxCellEditor(table);
		
	    tv.setColumnProperties(PROPS);
	    tv.setCellModifier(new SolversDetailsCellModifier(tv));
	    tv.setCellEditors(editors);    
		
	    return tv;
	}


	/*
	 * Subclasses may override to specify a different style.
	 */
	protected int getListStyle() {
		int style = SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		return style;
	}

	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * @param startingDirectory
	 *            the directory to open the dialog on.
	 * @return File The File the user selected or <code>null</code> if they do
	 *         not.
	 */
	private File getFile(File startingDirectory) {

		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
		if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0) {
				return new File(file);
			}
		}

		return null;
	}

}
