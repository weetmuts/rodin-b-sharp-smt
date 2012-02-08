/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.preferences.ui;

import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.cvc3.core.Cvc3ProverCore.getCvc3Config;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.contains;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.getUsedIds;
import static org.eventb.smt.verit.core.VeriTProverCore.getVeriTConfig;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.smt.internal.preferences.SMTPreferences;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;

/**
 * This class is used to build the solver configurations table printed in the
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
 * <code>SMTSolverConfiguration</code> list, which is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>solversTableViewer</code> each time the list <code>solverConfigs</code>
 * is modified, by calling the <code>refresh</code> method. The index of the
 * selected solver for SMT proofs is stored locally in the field
 * <code>selectedSolverIndex</code>.
 * 
 * @author guyot
 */
class SMTSolverConfigurationsFieldEditor extends FieldEditor {
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
	private static final String CONFIG_ID_LABEL = "Config ID";
	private static final String SOLVER_LABEL = "Solver";
	private static final String SOLVER_PATH_LABEL = "Solver path";
	private static final String SOLVER_ARGS_LABEL = "Solver arguments";
	private static final String SMTLIB_LABEL = "SMT-LIB";

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS = { CONFIG_ID_LABEL,
			SOLVER_LABEL, SOLVER_PATH_LABEL, SOLVER_ARGS_LABEL, SMTLIB_LABEL };
	private static final int[] COLUMN_BOUNDS = { 70, 70, 190, 150, 50 };

	/**
	 * The button for adding a new solver to the table.
	 */
	private Button addButton;
	/**
	 * The button for removing the currently selected solver from the table.
	 */
	private Button removeButton;
	/**
	 * The button for editing the currently selected solver configuration.
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
	 * The table showing the list of solver configurations
	 */
	TableViewer solversTableViewer;
	/**
	 * The list of solver configurations
	 */
	List<SMTSolverConfiguration> solverConfigs = new ArrayList<SMTSolverConfiguration>();
	/**
	 * The index of the solver selected for SMT proofs
	 */
	int selectedSolverIndex = -1;

	/**
	 * Creates a new solver configurations field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SMTSolverConfigurationsFieldEditor(final String name,
			final String labelText, final Composite parent) {
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
		tableViewer
				.setContentProvider(new SMTSolverConfigurationsContentProvider());
		tableViewer
				.setLabelProvider(new SMTSolverConfigurationsLabelProvider());

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

	private void addSolverConfig(final SMTSolverConfiguration solverConfig) {
		if (!contains(solverConfigs, solverConfig)) {
			solverConfigs.add(solverConfig);
		}
	}

	/**
	 * Remove the currently selected solver from the list of solver
	 * configurations, refresh the table viewer, updates the index of the
	 * selected solver and refresh the button states.
	 * 
	 * @param solversTable
	 *            the solvers table
	 */
	void removeCurrentSelection(final Table solversTable) {
		final int indexToRemove = solversTable.getSelectionIndex();
		solverConfigs.remove(indexToRemove);
		solversTableViewer.refresh();
		if (selectedSolverIndex > indexToRemove) {
			selectedSolverIndex--;
		} else if (selectedSolverIndex == indexToRemove) {
			if (solverConfigs.size() > 0) {
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
	 * Sets the buttons statuses depending on the selection in the table.
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

		/**
		 * Sets the parent's layout data
		 */
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/**
		 * Sets the label
		 */
		final Label label = getLabelControl(top);
		final GridData labelData = new GridData();
		labelData.horizontalSpan = numColumns;
		label.setLayoutData(labelData);

		/**
		 * Creates the table viewer
		 */
		solversTableViewer = createTableViewer(top);

		/**
		 * Configures the table
		 */
		final Table solversTable = solversTableViewer.getTable();
		solversTable.setHeaderVisible(true);
		solversTable.setLinesVisible(true);
		solversTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
				/**
				 * When pushed, opens the solver configuration shell
				 */
				final SMTSolverConfigurationDialog solverConfigDialog = new SMTSolverConfigurationDialog(
						buttonsGroup.getShell(), null,
						getUsedIds(solverConfigs));
				if (solverConfigDialog.open() == Window.OK) {
					/**
					 * Creates a new <code>SMTSolverConfiguration</code> object,
					 * and adds it to the list.
					 */
					solverConfigs.add(solverConfigDialog.getSolverConfig());

					/**
					 * Refreshes the table viewer.
					 */
					solversTableViewer.refresh();
					/**
					 * setSelectedSolverIndex is called so that if the added
					 * solver was the first one to be added, it is automatically
					 * selected as the solver to be used for SMT proofs.
					 */
					setSelectedSolverIndex(!SELECTION_REQUESTED);
					selectionChanged();
				}
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
				/**
				 * When pushed, remove the current selection
				 */
				removeCurrentSelection(solversTable);
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
				/**
				 * When pushed, opens the configuration shell of the solver
				 * currently selected in the table.
				 */
				final int selectionIndex = solversTable.getSelectionIndex();
				if (isValidIndex(selectionIndex, solverConfigs.size())) {
					final SMTSolverConfiguration solverToEdit = solverConfigs
							.get(selectionIndex);
					if (solverToEdit != null) {
						final SMTSolverConfigurationDialog solverConfigDialog = new SMTSolverConfigurationDialog(
								buttonsGroup.getShell(), solverToEdit,
								getUsedIds(solverConfigs));
						if (solverConfigDialog.open() == Window.OK) {
							/**
							 * Refreshes the table viewer.
							 */
							solversTableViewer.refresh();
						}
					}
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
				/**
				 * When pushed, sets the currently selected solver in the table
				 * as the selected solver for SMT proofs.
				 */
				setSelectedSolverIndex(SELECTION_REQUESTED);
			}
		});
		final GridData selectButtonData = new GridData(GridData.FILL_HORIZONTAL);
		selectButtonData.widthHint = convertHorizontalDLUsToPixels(
				selectButton, IDialogConstants.BUTTON_WIDTH);
		selectButton.setLayoutData(selectButtonData);

		/**
		 * Packs everything.
		 */
		solversTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		final String preferences = getPreferenceStore().getString(
				getPreferenceName());
		solverConfigs = SMTPreferences.parsePreferencesString(preferences);
		addSolverConfig(getVeriTConfig());
		addSolverConfig(getCvc3Config());
		solversTableViewer.setInput(solverConfigs);
		solversTableViewer.refresh();
		selectedSolverIndex = getPreferenceStore().getInt(SOLVER_INDEX_ID);
		setSelectedSolverIndex(!SELECTION_REQUESTED);
	}

	@Override
	protected void doLoadDefault() {
		final String defaultPreferences = getPreferenceStore()
				.getDefaultString(getPreferenceName());
		solverConfigs = SMTPreferences
				.parsePreferencesString(defaultPreferences);
		addSolverConfig(getVeriTConfig());
		addSolverConfig(getCvc3Config());
		solversTableViewer.setInput(solverConfigs);
		solversTableViewer.refresh();
		selectedSolverIndex = getPreferenceStore().getInt(SOLVER_INDEX_ID);
		setSelectedSolverIndex(!SELECTION_REQUESTED);
		selectionChanged();
	}

	@Override
	protected void doStore() {
		final String preferences = SMTSolverConfiguration
				.toString(solverConfigs);
		if (preferences != null) {
			getPreferenceStore().setValue(getPreferenceName(), preferences);
		}
		getPreferenceStore().setValue(SOLVER_INDEX_ID, selectedSolverIndex);
	}

	@Override
	public int getNumberOfControls() {
		/**
		 * 1 - The table of solver configurations
		 * 
		 * 2 - The button composite
		 */
		return 2;
	}
}