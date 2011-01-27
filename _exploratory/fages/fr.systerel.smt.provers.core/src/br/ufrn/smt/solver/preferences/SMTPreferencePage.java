/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT): Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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

public class SMTPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String SOLVER_ID = "Solver ID";

	public static final String SOLVER_PATH = "Solver path";

	public static final String SOLVER_ARGS = "Args";

	public static final String V1_2 = "v1.2";

	public static final String V2_0 = "v2.0";

	public static final String[] PROPS = { SOLVER_ID, SOLVER_PATH, SOLVER_ARGS,
			V1_2, V2_0 };

	public static final int[] BOUNDS = { 70, 190, 50, 35, 35 };

	private static final String preferencesName = "solverpreferences";

	/*****************************************/
	/* TO REMOVE WHEN PREPRO HAS DISAPPEARED */
	static boolean prepro;

	static String preproPath;
	/*****************************************/

	int selectedSolverIndex;

	protected TableViewer fTable;

	protected Control fTableControl;

	List<SolverDetail> fModel = new ArrayList<SolverDetail>();

	/**
	 * The name of the preference displayed in this preference page.
	 */
	String preferences = new String();

	public SMTPreferencePage() {
		initWithPreferences();
	}

	private void initWithPreferences() {
		setPreferenceStore(SmtProversCore.getDefault().getPreferenceStore());
		preferences = getPreferenceStore().getString(preferencesName);
		/*****************************************/
		/* TO REMOVE WHEN PREPRO HAS DISAPPEARED */
		prepro = getPreferenceStore().getBoolean("usingprepro");//$NON-NLS-1$
		preproPath = getPreferenceStore().getString("prepropath");//$NON-NLS-1$
		/*****************************************/
		fModel = SMTPreferencesStore.CreateModel(preferences);
		selectedSolverIndex = getPreferenceStore().getInt("solverindex");//$NON-NLS-1$
		setDescription("SMT-Solver Plugin Preference Page YFT"); //$NON-NLS-1$

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
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

		// Create the table viewer
		fTable = createTableViewer(comp);
		fTable.setInput(fModel);

		// Configure table
		final Table tableControl = fTable.getTable();
		tableControl.setHeaderVisible(true);
		tableControl.setLinesVisible(true);
		tableControl
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// get back the selected row if exists
		Color blue = comp.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		Color white = comp.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		if (selectedSolverIndex >= 0) {
			TableItem[] Item = fTable.getTable().getItems();
			Item[selectedSolverIndex].setBackground(blue);
			Item[selectedSolverIndex].setForeground(white);
		}

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
			@Override
			public void widgetSelected(SelectionEvent event) {
				createSolverDetailsPage(compButtons, false, "", "", "", false,
						false);
			}
		});

		final Button removeButton = new Button(compButtons, SWT.PUSH);
		removeButton.setText("Remove"); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				StructuredSelection sel = (StructuredSelection) fTable
						.getSelection();
				SolverDetail solverToRemove = (SolverDetail) sel
						.getFirstElement();
				fModel.remove(solverToRemove);

				// Check if the selected is being to be removed
				if (fTable.getTable().getSelectionIndex() == selectedSolverIndex) {
					// Clear selectedSolverIndex
					selectedSolverIndex = -1;
				}

				// Update table with solver details
				fTable.refresh();

				// save preferences
				preferences = SMTPreferencesStore.CreatePreferences(fModel);

			}
		});

		final Button editButton = new Button(compButtons, SWT.PUSH);
		editButton.setText("Edit..."); //$NON-NLS-1$
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				StructuredSelection sel = (StructuredSelection) fTable
						.getSelection();
				SolverDetail solverToEdit = (SolverDetail) sel
						.getFirstElement();
				if (solverToEdit != null) {
					createSolverDetailsPage(compButtons, true,
							solverToEdit.getId(), solverToEdit.getPath(),
							solverToEdit.getArgs(), solverToEdit.getsmtV1_2(),
							solverToEdit.getsmtV2_0());
				}
			}
		});

		final Button selectButton = new Button(compButtons, SWT.PUSH);
		selectButton.setText("Select"); //$NON-NLS-1$
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Change color of the selected row
				Color blueColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_BLUE);
				Color whiteColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_WHITE);
				Color blackColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_BLACK);

				// memorize the selected solver index
				selectedSolverIndex = fTable.getTable().getSelectionIndex();

				TableItem[] items = fTable.getTable().getItems();
				for (int i = 0; i < items.length; i++) {
					if (i == selectedSolverIndex) {
						items[i].setBackground(blueColor);
						items[i].setForeground(whiteColor);
					} else {
						items[i].setBackground(whiteColor);
						items[i].setForeground(blackColor);
					}
				}
			}
		});

		/*****************************************/
		/* TO REMOVE WHEN PREPRO HAS DISAPPEARED */
		// Create a new Composite with 1 column to dispose prepro option
		final Composite compPrepro = new Composite(comp, SWT.NONE);

		// Define 1 column for buttons
		final GridLayout layoutPrepro = new GridLayout(3, false);
		layoutButtons.marginHeight = 0;
		layoutButtons.marginWidth = 0;
		compPrepro.setLayout(layoutPrepro);

		// resize compButtons
		compPrepro.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Button prepro_Button = new Button(compPrepro, SWT.CHECK);
		prepro_Button.setText("prepro"); //$NON-NLS-1$
		prepro_Button.setSelection(prepro);

		// callbacks for the 2 buttons
		prepro_Button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				prepro = prepro_Button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Label solverPreproPathTextLabel = new Label(compPrepro, SWT.LEFT);
		solverPreproPathTextLabel.setText("prepro Path"); //$NON-NLS-1$

		final Text solverPreproPathText = new Text(compPrepro, SWT.LEFT
				| SWT.BORDER);
		solverPreproPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, true));
		solverPreproPathText.setEditable(false);
		solverPreproPathText.setText(preproPath);

		// Add button Browse
		Button browseButton = new Button(compPrepro, SWT.PUSH);
		browseButton.setText("Browse"); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				File f = new File(solverPreproPathText.getText());
				if (!f.exists()) {
					f = null;
				}
				File d = getFile(f);
				solverPreproPathText.setText(d.getPath());
				preproPath = solverPreproPathText.getText();
			}

		});
		/*********************************************/

		// Update table with solver details
		fTable.refresh();

		// pack everything
		fTable.getTable().pack();
		parent.pack();

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

		final TableViewer tv = new TableViewer(parent, SWT.FULL_SELECTION);

		createColumns(tv);
		tv.setColumnProperties(PROPS);
		tv.setContentProvider(new SolversDetailsContentProvider());
		tv.setLabelProvider(new SolversDetailsLabelProvider());

		return tv;
	}

	private void createColumns(TableViewer viewer) {

		for (int i = 0; i < PROPS.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(PROPS[i]);
			column.getColumn().setWidth(BOUNDS[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
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
	File getFile(File startingDirectory) {

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

	@Override
	public boolean performOk() {
		// Set preferences
		getPreferenceStore().putValue(preferencesName, preferences);
		getPreferenceStore().setValue("solverindex", selectedSolverIndex);
		/*****************************************/
		/* TO REMOVE WHEN PREPRO HAS DISAPPEARED */
		getPreferenceStore().setValue("usingprepro", prepro);
		getPreferenceStore().putValue("prepropath", preproPath);
		/*****************************************/
		return super.performOk();
	}

	void createSolverDetailsPage(Composite parent, final boolean editMode,
			final String id, final String path, final String args,
			final boolean v1_2, final boolean v2_0) {
		final Shell shell = new Shell(parent.getShell());
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

		compName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compArg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compSmtVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		compOkCancel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Solver Id
		final Label solverIdTextLabel = new Label(compName, SWT.LEFT);
		solverIdTextLabel.setText("Solver ID"); //$NON-NLS-1$

		final Text solverIdText = new Text(compName, SWT.LEFT | SWT.BORDER);
		solverIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverIdText.setText(id);

		// Solver Path
		final Label solverPathTextLabel = new Label(compPath, SWT.LEFT);
		solverPathTextLabel.setText("Solver Path"); //$NON-NLS-1$

		final Text solverPathText = new Text(compPath, SWT.LEFT | SWT.BORDER);
		solverPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverPathText.setEditable(false);
		solverPathText.setText(path);

		// Add button Browse
		Button browseButton = new Button(compPath, SWT.PUSH);
		browseButton.setText("Browse"); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				File f = new File(solverPathText.getText());
				if (!f.exists()) {
					f = null;
				}
				File d = getFile(f);
				solverPathText.setText(d.getPath());
			}

		});

		// arguments
		final Label solverArgTextLabel = new Label(compArg, SWT.LEFT);
		solverArgTextLabel.setText("Solver arguments"); //$NON-NLS-1$

		final Text solverArgsText = new Text(compArg, SWT.LEFT | SWT.BORDER);
		solverArgsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverArgsText.setEditable(true);
		solverArgsText.setText(args);

		// smt version
		final Label smtVersionLabel = new Label(compSmtVersion, SWT.LEFT);
		smtVersionLabel.setText("SMT-Lib"); //$NON-NLS-1$

		final Button smt1_2_Button = new Button(compSmtVersion, SWT.CHECK);
		smt1_2_Button.setText("1.2"); //$NON-NLS-1$
		smt1_2_Button.setSelection(v1_2);

		final Button smt2_0_Button = new Button(compSmtVersion, SWT.CHECK);
		smt2_0_Button.setText("2.0"); //$NON-NLS-1$
		smt2_0_Button.setSelection(v2_0);

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
			@Override
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
					if (editMode) {
						int indexToEdit = fTable.getTable().getSelectionIndex();
						fModel.get(indexToEdit).setId(solverIdText.getText());
						fModel.get(indexToEdit).setPath(
								solverPathText.getText());
						fModel.get(indexToEdit).setArgs(
								solverArgsText.getText());
						fModel.get(indexToEdit).setSmtV1_2(
								smt1_2_Button.getSelection());
						fModel.get(indexToEdit).setSmtV2_0(
								smt2_0_Button.getSelection());
					} else {
						fModel.add(new SolverDetail(solverIdText.getText(),
								solverPathText.getText(), solverArgsText
										.getText(), smt1_2_Button
										.getSelection(), smt2_0_Button
										.getSelection()));
					}

					// save preferences
					preferences = SMTPreferencesStore.CreatePreferences(fModel);

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
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Close the shell
				shell.close();
			}

		});

		// Open the shell
		shell.open();
	}

	public class SolverEditingSupport extends EditingSupport {
		private CellEditor editor;
		private int column;

		public SolverEditingSupport(ColumnViewer viewer, int column) {
			super(viewer);

			// Create the correct editor based on the column index
			switch (column) {
			case 2:
				editor = new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
				break;
			case 3:
				editor = new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
				break;
			default:
				editor = new TextCellEditor(((TableViewer) viewer).getTable());
			}
			this.column = column;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			SolverDetail solver = (SolverDetail) element;

			switch (this.column) {
			case 0:
				return solver.getId();
			case 1:
				return solver.getPath();
			case 2:
				return solver.getsmtV1_2();
			case 3:
				return solver.getsmtV2_0();
			default:
				break;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			SolverDetail solver = (SolverDetail) element;

			switch (this.column) {
			case 0:
				solver.setId(String.valueOf(value));
				break;
			case 1:
				solver.setPath(String.valueOf(value));
				break;
			case 2:
				solver.setSmtV1_2((Boolean) value);
				break;
			case 3:
				solver.setSmtV2_0((Boolean) value);
				break;
			default:
				break;
			}

			getViewer().update(element, null);
		}

	}

}
