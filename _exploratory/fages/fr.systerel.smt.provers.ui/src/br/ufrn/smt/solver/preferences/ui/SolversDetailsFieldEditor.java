/**
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 */
package br.ufrn.smt.solver.preferences.ui;

import static br.ufrn.smt.solver.preferences.ui.Messages.SMTPreferencePage2_MandatoryFieldsInSolverDetails;
import static org.eclipse.swt.SWT.FULL_SELECTION;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetails;

/**
 * This class is used to build the solver parameters table printed in the
 * preferences page. This table contains all the information set by the user
 * when he added a new SMT solver. This class also defines four buttons which
 * interact with the table:
 * <ul>
 * <li>The 'Add' button to add a new SMT solver configuration into the table.</li>
 * <li>The 'Edit' button to modify a previously added SMT solver configuration.</li>
 * <li>The 'Remove' button to remove an existing SMT solver configuration.</li>
 * <li>The 'Select' button to select the solver which will be used to discharge
 * a sequent using the SMT tactic.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SolverDetails</code> list, which is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>solversTableViewer</code> each time the list
 * <code>solversDetails</code> is modified, by calling the <code>refresh</code>
 * method.
 * 
 * @author guyot
 */
class SolversDetailsFieldEditor extends FieldEditor {
	/**
	 * This constant represents a click on the 'SELECT' button.
	 */
	private static final boolean SELECTION_REQUESTED = true;

	/**
	 * Labels
	 */
	private static final String ADD_LABEL = "Add...";
	private static final String REMOVE_LABEL = "Remove";
	private static final String EDIT_LABEL = "Edit...";
	private static final String SELECT_LABEL = "Select";
	private static final String SOLVER_ID_LABEL = "Solver ID";
	private static final String SOLVER_PATH_LABEL = "Solver path";
	private static final String SOLVER_ARGS_LABEL = "Solver arguments";
	private static final String V1_2_LABEL = "v1.2";
	private static final String V2_0_LABEL = "v2.0";
	private static final String SMT_LIB_LABEL = "SMT-LIB";
	private static final String BROWSE_LABEL = "Browse";
	private static final String OK_LABEL = "OK";
	private static final String CANCEL_LABEL = "Cancel";

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS = { SOLVER_ID_LABEL,
			SOLVER_PATH_LABEL, SOLVER_ARGS_LABEL, V1_2_LABEL, V2_0_LABEL };
	private static final int[] COLUMN_BOUNDS = { 70, 190, 150, 40, 40 };

	/**
	 * The button for adding a new solver to the table.
	 */
	private Button addButton;
	/**
	 * The button for removing the currently selected solver from the table.
	 */
	private Button removeButton;
	/**
	 * The button for editing the currently selected solver details.
	 */
	private Button editButton;
	/**
	 * The button for setting the currently selected solver as the solver to be
	 * used for SMT proofs.
	 */
	private Button selectButton;

	/**
	 * The top-level control for the field editor.
	 */
	private Composite top;
	/**
	 * The table showing the list of solvers details
	 */
	TableViewer solversTableViewer;
	/**
	 * The list of solvers details
	 */
	List<SolverDetails> solversDetails = new ArrayList<SolverDetails>();
	/**
	 * The index of the solver selected for SMT proofs
	 */
	int selectedSolverIndex = -1;

	/**
	 * Creates a new solvers details field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SolversDetailsFieldEditor(final String name, final String labelText,
			final Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * Creates a table viewer and configures it.
	 * 
	 * @param parent
	 *            the parent of the table viewer's control
	 */
	private static TableViewer createTableViewer(final Composite parent) {
		final TableViewer tableViewer = new TableViewer(parent, FULL_SELECTION);

		createColumns(tableViewer);
		tableViewer.setColumnProperties(COLUMNS_LABELS);
		tableViewer.setContentProvider(new SolversDetailsContentProvider());
		tableViewer.setLabelProvider(new SolversDetailsLabelProvider());

		return tableViewer;
	}

	/**
	 * Creates the columns of the table viewer.
	 * 
	 * @param viewer
	 *            the table viewer
	 */
	private static void createColumns(final TableViewer viewer) {
		for (int i = 0; i < COLUMNS_LABELS.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer,
					SWT.NONE);
			column.getColumn().setText(COLUMNS_LABELS[i]);
			column.getColumn().setWidth(COLUMN_BOUNDS[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * FIXME This method must be removed when the method
	 * <code>createSolverDetailsPage</code> will be fixed by using a
	 * <code>FileFieldEditor</code>.
	 * 
	 * @param startingDirectory
	 *            the directory to open the dialog on.
	 * @return File The File the user selected or <code>null</code> if they do
	 *         not.
	 */
	File getFile(final Shell shell, final File startingDirectory) {
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.SHEET);
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

	/**
	 * This method opens a new shell containing the input fields the user must
	 * fill to add a new SMT solver to the table.
	 * 
	 * Currently, the <code>v1_2</code> and <code>v2_0</code> options are
	 * mutually exclusive.
	 * 
	 * FIXME This method may be refactored using <code>FieldEditor</code>
	 * objects instead of manually configured widgets.
	 * 
	 * @param parent
	 *            the parent of the shell's control
	 * @param editMode
	 *            is <code>true</code> if this method was called by the 'Edit'
	 *            button, and <code>false</code> if it was called by the 'Add'
	 *            button
	 * @param id
	 *            the name of the solver to add or edit
	 * @param path
	 *            the absolute path to the solver
	 * @param args
	 *            (optional) the arguments the solver must be called with
	 * @param v1_2
	 *            tells whether this solver configuration is meant to be used
	 *            with the version 1.2 of the SMT-LIB language
	 * @param v2_0
	 *            tells whether this solver configuration is meant to be used
	 *            with the version 2.0 of the SMT-LIB language
	 */
	void createSolverDetailsPage(final Composite parent,
			final boolean editMode, final String id, final String path,
			final String args, final boolean v1_2, final boolean v2_0) {
		final Shell shell = new Shell(parent.getShell());

		/**
		 * Sets the title depending on the configuration mode (edit or add).
		 */
		if (editMode) {
			final Table solversTable = solversTableViewer.getTable();
			final int selectionIndex = solversTable.getSelectionIndex();
			final String solverId = solversDetails.get(selectionIndex).getId();
			shell.setText("Edit " + solverId + " settings");
		} else {
			shell.setText("New solver settings");
		}

		/**
		 * Shell layout settings: one column, filled horizontally and
		 * vertically.
		 */
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		shell.setSize(400, 250);

		/**
		 * Field composites
		 */
		final Composite compName = new Composite(shell, SWT.NONE);
		final Composite compPath = new Composite(shell, SWT.NONE);
		final Composite compArg = new Composite(shell, SWT.NONE);
		final Composite compSmtVersion = new Composite(shell, SWT.NONE);
		final Composite compOkCancel = new Composite(shell, SWT.NONE);

		/**
		 * Field layouts
		 */
		compName.setLayout(new GridLayout(2, false));
		compPath.setLayout(new GridLayout(3, false));
		compArg.setLayout(new GridLayout(2, false));
		compSmtVersion.setLayout(new GridLayout(3, false));
		compOkCancel.setLayout(new GridLayout(2, false));

		/**
		 * Field grid data
		 */
		final GridData gridData = new GridData(SWT.DEFAULT, 30);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		compName.setLayoutData(gridData);
		compPath.setLayoutData(gridData);
		compArg.setLayoutData(gridData);
		compSmtVersion.setLayoutData(gridData);
		compOkCancel.setLayoutData(gridData);

		/**
		 * Creates the solver name field (label and text field).
		 */
		final Label solverIdTextLabel = new Label(compName, SWT.LEFT);
		solverIdTextLabel.setText(SOLVER_ID_LABEL);

		final Text solverIdText = new Text(compName, SWT.LEFT | SWT.BORDER);
		solverIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverIdText.setText(id);

		/**
		 * Creates the solver path field (label, text field and browse button).
		 */
		final Label solverPathTextLabel = new Label(compPath, SWT.LEFT);
		solverPathTextLabel.setText(SOLVER_PATH_LABEL);

		final Text solverPathText = new Text(compPath, SWT.LEFT | SWT.BORDER);
		solverPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverPathText.setEditable(false);
		solverPathText.setText(path);

		final Button browseButton = new Button(compPath, SWT.PUSH);
		browseButton.setText(BROWSE_LABEL);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				File f = new File(solverPathText.getText());
				if (!f.exists()) {
					f = null;
				}
				final File selected = getFile(shell, f);
				if (selected != null) {
					solverPathText.setText(selected.getPath());
				}
			}

		});

		/**
		 * Creates the solver arguments field (label and text field).
		 */
		final Label solverArgTextLabel = new Label(compArg, SWT.LEFT);
		solverArgTextLabel.setText(SOLVER_ARGS_LABEL);

		final Text solverArgsText = new Text(compArg, SWT.LEFT | SWT.BORDER);
		solverArgsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverArgsText.setEditable(true);
		solverArgsText.setText(args);

		/**
		 * Creates the SMT-LIB version fields (label, and mutually exclusive
		 * checkboxes).
		 */
		final Label smtVersionLabel = new Label(compSmtVersion, SWT.LEFT);
		smtVersionLabel.setText(SMT_LIB_LABEL);

		final Button smt1_2_Button = new Button(compSmtVersion, SWT.CHECK);
		smt1_2_Button.setText(V1_2_LABEL);
		smt1_2_Button.setSelection(v1_2);

		final Button smt2_0_Button = new Button(compSmtVersion, SWT.CHECK);
		smt2_0_Button.setText(V2_0_LABEL);
		smt2_0_Button.setSelection(v2_0);

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

		/**
		 * The 'OK' button, when pushed, saves the input as a new solver if they
		 * are valid. Otherwise, shows a pop-up error message.
		 */
		final Button okButton = new Button(compOkCancel, SWT.PUSH);
		okButton.setText(OK_LABEL);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Table solversTable = solversTableViewer.getTable();
				/**
				 * Checks mandatory fields
				 */
				/**
				 * If something is missing,
				 */
				if (solverIdText.getText() == ""
						|| !smt1_2_Button.getSelection()
						&& !smt2_0_Button.getSelection()
						|| solverPathText.getText() == "") {
					/**
					 * Displays a pop-up error message displayed.
					 */
					UIUtils.showError(SMTPreferencePage2_MandatoryFieldsInSolverDetails);
				} else {
					/**
					 * If in edition mode,
					 */
					if (editMode) {
						/**
						 * Retrieves previously set configuration from the list
						 * <code>solversDetails</code>.
						 */
						final int indexToEdit = solversTable
								.getSelectionIndex();
						solversDetails.get(indexToEdit).setId(
								solverIdText.getText());
						solversDetails.get(indexToEdit).setPath(
								solverPathText.getText());
						solversDetails.get(indexToEdit).setArgs(
								solverArgsText.getText());
						solversDetails.get(indexToEdit).setSmtV1_2(
								smt1_2_Button.getSelection());
						solversDetails.get(indexToEdit).setSmtV2_0(
								smt2_0_Button.getSelection());

						/**
						 * Refreshes the table viewer.
						 */
						solversTableViewer.refresh();
					} else {
						/**
						 * Creates a new <code>SolverDetails</code> object, and
						 * adds it to the list.
						 */
						solversDetails.add(new SolverDetails(solverIdText
								.getText(), solverPathText.getText(),
								solverArgsText.getText(), smt1_2_Button
										.getSelection(), smt2_0_Button
										.getSelection()));

						/**
						 * Refreshes the table viewer.
						 */
						solversTableViewer.refresh();
						/**
						 * setSelectedSolverIndex is called so that if the added solver
						 * was the first one to be added, it is automatically selected
						 * as the solver to be used for SMT proofs.
						 */
						setSelectedSolverIndex(!SELECTION_REQUESTED);
						selectionChanged();
					}
					/**
					 * Closes the shell.
					 */
					shell.close();
				}

			}

		});

		/**
		 * The 'Cancel' button
		 */
		final Button cancelButton = new Button(compOkCancel, SWT.PUSH);
		cancelButton.setText(CANCEL_LABEL);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				/**
				 * Closes the shell without saving anything.
				 */
				shell.close();
			}

		});

		/**
		 * Opens the shell
		 */
		shell.open();
	}

	/**
	 * Tells whether the current selection index is valid or not
	 * 
	 * @param index
	 *            the current selection index
	 * @param itemCount
	 *            the count of items in the solvers table
	 * @return whether the current selection index is valid or not
	 */
	boolean isValidIndex(final int index, final int itemCount) {
		return index >= 0 && index < itemCount;
	}

	/**
	 * Sets the buttons statuses depending on the selection in the list.
	 */
	void selectionChanged() {
		final Table solversTable = solversTableViewer.getTable();
		final boolean validSelectionIndex = isValidIndex(
				solversTable.getSelectionIndex(), solversTable.getItemCount());
		removeButton.setEnabled(validSelectionIndex);
		editButton.setEnabled(validSelectionIndex);
		selectButton.setEnabled(validSelectionIndex);
	}

	/**
	 * This method sets the selected solver index to the current selection in
	 * the solvers table if it was requested, or to a valid index. If there is
	 * only one solver in the table, it is selected. If there is no solver in
	 * the table, it is set to the default value (-1).
	 * 
	 * Then, the selected solver background color is set to BLUE.
	 */
	void setSelectedSolverIndex(final boolean selectionRequested) {
		final Table solversTable = solversTableViewer.getTable();
		final int itemCount = solversTable.getItemCount();

		/**
		 * If there is only one solver set in the table, it is selected for SMT
		 * proofs
		 */
		if (itemCount == 1) {
			selectedSolverIndex = 0;
		} else {
			final int selectionIndex = solversTable.getSelectionIndex();

			/**
			 * Else, if the 'SELECT' button was pushed, the current selection is
			 * selected for SMT proofs. Notice that if the 'SELECT' button has
			 * been pushed, it means that the current selection is valid.
			 */
			if (selectionRequested) {
				selectedSolverIndex = selectionIndex;
			} else {
				/**
				 * Else if the current selected solver is not valid...
				 */
				if (!isValidIndex(selectedSolverIndex, itemCount)) {
					/**
					 * if there is some solvers set in the table, the first one
					 * is selected for SMT proofs, else the selected solver
					 * index is set to -1.
					 */
					if (itemCount > 1) {
						selectedSolverIndex = 0;
					} else {
						selectedSolverIndex = -1;
					}
				}
			}
		}

		updateSolversTableColors();
	}

	/**
	 * Updates the colors of the solvers table, the selected solver background
	 * color is set to BLUE.
	 */
	void updateSolversTableColors() {
		final Color blue = top.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		final Color white = top.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final Color black = top.getDisplay().getSystemColor(SWT.COLOR_BLACK);

		final Table solversTable = solversTableViewer.getTable();
		final TableItem[] items = solversTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (i == selectedSolverIndex) {
				items[i].setBackground(blue);
				items[i].setForeground(white);
			} else {
				items[i].setBackground(white);
				items[i].setForeground(black);
			}
		}
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) top.getLayoutData()).horizontalSpan = numColumns;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		top = parent;

		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		top.setLayoutData(gridData);

		/**
		 * Creates the table viewer
		 */
		solversTableViewer = createTableViewer(top);
		solversTableViewer.setInput(solversDetails);

		/**
		 * Configures the table
		 */
		final Table solversTable = solversTableViewer.getTable();
		solversTable.setHeaderVisible(true);
		solversTable.setLinesVisible(true);
		solversTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/**
		 * Sets the previously selected row
		 */
		final Color blue = top.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		final Color white = top.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final int previouslySelectedSolverIndex = solversTable
				.getSelectionIndex();
		if (previouslySelectedSolverIndex >= 0
				&& previouslySelectedSolverIndex < solversTable.getItemCount()) {
			final TableItem[] Item = solversTable.getItems();
			Item[previouslySelectedSolverIndex].setBackground(blue);
			Item[previouslySelectedSolverIndex].setForeground(white);
		}

		/**
		 * Create a grid data that takes up the extra space in the dialog and
		 * spans both columns.
		 */
		solversTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectionChanged();
			}
		});

		/**
		 * Create a Composite for the buttons
		 */
		final Composite buttonsGroup = new Composite(top, SWT.NONE);
		buttonsGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final GridLayout buttonsLayout = new GridLayout(1, false);
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 0;
		buttonsGroup.setLayout(buttonsLayout);

		/**
		 * 'Add...' button
		 */
		addButton = new Button(buttonsGroup, SWT.PUSH);
		addButton.setText(ADD_LABEL);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				createSolverDetailsPage(buttonsGroup, false, "", "", "", false,
						false);
			}
		});
		final GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.widthHint = convertHorizontalDLUsToPixels(addButton,
				IDialogConstants.BUTTON_WIDTH);
		addButton.setLayoutData(addButtonData);

		/**
		 * 'Remove' button
		 */
		removeButton = new Button(buttonsGroup, SWT.PUSH);
		removeButton.setEnabled(false);
		removeButton.setText(REMOVE_LABEL);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final int indexToRemove = solversTable.getSelectionIndex();
				solversDetails.remove(indexToRemove);
				solversTableViewer.refresh();
				if (selectedSolverIndex > indexToRemove) {
					selectedSolverIndex--;
				} else if (selectedSolverIndex == indexToRemove) {
					if (solversDetails.size() > 0) {
						selectedSolverIndex = 0;
					} else {
						selectedSolverIndex = -1;
					}
				}

				/**
				 * setSelectedSolverIndex is called so that another solver is
				 * automatically selected
				 */
				setSelectedSolverIndex(!SELECTION_REQUESTED);
				selectionChanged();
			}
		});
		final GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.widthHint = convertHorizontalDLUsToPixels(
				removeButton, IDialogConstants.BUTTON_WIDTH);
		removeButton.setLayoutData(removeButtonData);

		/**
		 * 'Edit' button
		 */
		editButton = new Button(buttonsGroup, SWT.PUSH);
		editButton.setEnabled(false);
		editButton.setText(EDIT_LABEL);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final StructuredSelection selectedItem;
				selectedItem = (StructuredSelection) solversTableViewer
						.getSelection();
				final SolverDetails solverToEdit = (SolverDetails) selectedItem
						.getFirstElement();
				if (solverToEdit != null) {
					createSolverDetailsPage(buttonsGroup, true,
							solverToEdit.getId(), solverToEdit.getPath(),
							solverToEdit.getArgs(), solverToEdit.getsmtV1_2(),
							solverToEdit.getsmtV2_0());
				}
			}
		});
		final GridData editButtonData = new GridData(GridData.FILL_HORIZONTAL);
		editButtonData.widthHint = convertHorizontalDLUsToPixels(editButton,
				IDialogConstants.BUTTON_WIDTH);
		editButton.setLayoutData(editButtonData);

		/**
		 * 'select' button
		 */
		selectButton = new Button(buttonsGroup, SWT.PUSH);
		selectButton.setEnabled(false);
		selectButton.setText(SELECT_LABEL);
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				setSelectedSolverIndex(SELECTION_REQUESTED);
			}
		});
		final GridData selectButtonData = new GridData(GridData.FILL_HORIZONTAL);
		selectButtonData.widthHint = convertHorizontalDLUsToPixels(
				selectButton, IDialogConstants.BUTTON_WIDTH);
		selectButton.setLayoutData(selectButtonData);

		/**
		 * Update table with solver details
		 */
		solversTableViewer.refresh();

		/**
		 * pack everything
		 */
		solversTable.pack();
		parent.pack();
	}

	/**
	 * Initializes this field editor with the preference value from the
	 * preference store.
	 */
	@Override
	protected void doLoad() {
		final String preferences = getPreferenceStore().getString(
				getPreferenceName());
		solversDetails = SMTPreferences.parsePreferencesString(preferences);
		solversTableViewer.setInput(solversDetails);
		selectedSolverIndex = getPreferenceStore().getInt(
				SMTPreferences.SOLVER_INDEX_ID);
		updateSolversTableColors();
		solversTableViewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		final String defaultPreferences = getPreferenceStore()
				.getDefaultString(getPreferenceName());
		solversDetails = SMTPreferences
				.parsePreferencesString(defaultPreferences);
		solversTableViewer.setInput(solversDetails);
		selectedSolverIndex = getPreferenceStore().getInt(
				SMTPreferences.SOLVER_INDEX_ID);
		setSelectedSolverIndex(!SELECTION_REQUESTED);
		solversTableViewer.refresh();
	}

	@Override
	protected void doStore() {
		final String preferences = SolverDetails.toString(solversDetails);
		if (preferences != null) {
			getPreferenceStore().setValue(getPreferenceName(), preferences);
		}
		getPreferenceStore().setValue(SMTPreferences.SOLVER_INDEX_ID,
				selectedSolverIndex);
	}

	@Override
	public int getNumberOfControls() {
		/**
		 * 1 - The list of solvers 2 - The button composite
		 */
		return 2;
	}
}