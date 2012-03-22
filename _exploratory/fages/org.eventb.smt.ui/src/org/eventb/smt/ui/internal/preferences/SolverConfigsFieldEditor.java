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

import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.core.preferences.PreferenceManager.DEFAULT_SELECTED_CONFIG;
import static org.eventb.smt.core.preferences.PreferenceManager.getDefaultSMTPrefs;
import static org.eventb.smt.core.preferences.PreferenceManager.getSMTPrefs;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * This class is used to build the solver configurations table printed in the
 * preferences page. This table contains all the information set by the user
 * when he added a new SMT solver configuration. This class also defines four
 * buttons which interact with the table:
 * <ul>
 * <li>The 'Add' button to add a new SMT solver configuration into the table.</li>
 * <li>The 'Edit' button to modify a previously added SMT solver configuration.</li>
 * <li>The 'Remove' button to remove an existing SMT solver configuration.</li>
 * <li>The 'Select' button to select the solver which will be used to discharge
 * a sequent using the SMT tactic.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SMTPreferences</code> instance, of which the
 * <code>SolverConfiguration</code> list is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>tableViewer</code> each time the list <code>solverConfigs</code> is
 * modified, by calling the <code>refresh</code> method.
 * 
 * @author guyot
 */
class SolverConfigsFieldEditor extends AbstractTableFieldEditor<ISolverConfig> {
	/**
	 * This constant represents a click on the 'SELECT' button.
	 */
	private static final boolean SELECTION_REQUESTED = true;

	/**
	 * Labels
	 */
	private static final String SELECT_LABEL = "Select";
	private static final String CONFIG_ID_LABEL = "ID";
	private static final String CONFIG_NAME_LABEL = "Name";
	private static final String SOLVER_LABEL = "Solver";
	private static final String SOLVER_ARGS_LABEL = "Arguments";
	private static final String SMTLIB_LABEL = "SMT-LIB";
	private static final String IS_EDITABLE_LABEL = "Editable";

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS = { CONFIG_ID_LABEL,
			CONFIG_NAME_LABEL, SOLVER_LABEL, SOLVER_ARGS_LABEL, SMTLIB_LABEL,
			IS_EDITABLE_LABEL };
	private static final int[] COLUMN_BOUNDS = { 0, 70, 70, 200, 50, 0 };

	/**
	 * The button for setting the currently selected solver as the solver to be
	 * used for SMT proofs.
	 */
	private Button selectButton;

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
	public SolverConfigsFieldEditor(final String name, final String labelText,
			final Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected String[] getColumnsLabel() {
		return COLUMNS_LABELS;
	}

	@Override
	protected int[] getColumnsBounds() {
		return COLUMN_BOUNDS;
	}

	@Override
	protected void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, FULL_SELECTION);

		createColumns(tableViewer);
		tableViewer.setColumnProperties(getColumnsLabel());
		tableViewer.setContentProvider(new ContentProvider<ISolverConfig>());
		tableViewer.setLabelProvider(new SolverConfigsLabelProvider());
	}

	/**
	 * Remove the currently selected configuration from the list of solvers
	 * configurations, refresh the table viewer, updates the index of the
	 * selected configuration and refresh the button states.
	 * 
	 * @param solversTable
	 *            the solvers table
	 */
	@Override
	void removeCurrentSelection(final Table solversTable) {
		final String configToRemove = solversTable.getSelection()[0].getText();
		smtPrefs.removeSolverConfig(configToRemove);
		tableViewer.refresh();

		/**
		 * setSelectedConfigIndex is called so that another configuration is
		 * automatically selected
		 */
		setSelectedConfigIndex(!SELECTION_REQUESTED);
		selectionChanged();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the table.
	 */
	@Override
	void selectionChanged() {
		final Table configsTable = tableViewer.getTable();
		final TableItem selection = configsTable.getSelection()[0];
		final String selectedConfigID = selection.getText();
		final Map<String, ISolverConfig> solverConfigs = smtPrefs
				.getSolverConfigs();
		final boolean validSelection = solverConfigs
				.containsKey(selectedConfigID);
		final boolean validEditableSelection = validSelection ? solverConfigs
				.get(selectedConfigID).isEditable() : false;
		removeButton.setEnabled(validEditableSelection);
		editButton.setEnabled(validEditableSelection);
		selectButton.setEnabled(validSelection);
	}

	/**
	 * This method sets the selected configuration index to the current
	 * selection in the configurations table if it was requested, or to a valid
	 * index. If there is only one configuration in the table, it is selected.
	 * If there is no configuration in the table, it is set to the default value
	 * (-1).
	 * 
	 * Then, the selected configuration background color is set to BLUE.
	 */
	void setSelectedConfigIndex(final boolean selectionRequested) {
		final Table configsTable = tableViewer.getTable();
		final TableItem[] items = configsTable.getSelection();
		final String selectionID;
		if (selectionRequested && items.length > 0) {
			final TableItem selectedItem = items[0];
			selectionID = selectedItem.getText();
		} else {
			selectionID = DEFAULT_SELECTED_CONFIG;
		}

		/**
		 * If the 'SELECT' button was pushed, the current selection is selected
		 * for SMT proofs. Notice that if the 'SELECT' button has been pushed,
		 * it means that the current selection is valid.
		 */
		smtPrefs.setSelectedConfigID(selectionRequested, selectionID);

		updateConfigsTableColors();
	}

	/**
	 * Updates the colors of the configs table, the selected config background
	 * color is set to BLUE.
	 */
	@Override
	void updateConfigsTableColors() {
		final Color blue = top.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		final Color white = top.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final Color black = top.getDisplay().getSystemColor(SWT.COLOR_BLACK);

		final Table configsTable = tableViewer.getTable();
		for (TableItem item : configsTable.getItems()) {
			if (item.getText().equals(smtPrefs.getSelectedConfigID())) {
				item.setBackground(blue);
				item.setForeground(white);
			} else {
				item.setBackground(white);
				item.setForeground(black);
			}
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		final Table configsTable = tableViewer.getTable();

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
				final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
						buttonsGroup.getShell(), smtPrefs, null);
				if (solverConfigDialog.open() == Window.OK) {
					/**
					 * Creates a new <code>SolverConfiguration</code> object,
					 * and adds it to the list.
					 */
					smtPrefs.addSolverConfig(solverConfigDialog
							.getSolverConfig());

					/**
					 * Refreshes the table viewer.
					 */
					tableViewer.refresh();
					/**
					 * setSelectedConfigIndex is called so that if the added
					 * configuration was the first one to be added, it is
					 * automatically selected as the configuration to be used
					 * for SMT proofs.
					 */
					setSelectedConfigIndex(!SELECTION_REQUESTED);
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
				removeCurrentSelection(configsTable);
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
				final String selectionID = configsTable.getSelection()[0]
						.getText();
				if (smtPrefs.getSolverConfigs().containsKey(selectionID)) {
					final ISolverConfig configToEdit = smtPrefs
							.getSolverConfigs().get(selectionID);
					if (configToEdit != null) {
						final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
								buttonsGroup.getShell(), smtPrefs, configToEdit);
						if (solverConfigDialog.open() == Window.OK) {
							/**
							 * Refreshes the table viewer.
							 */
							tableViewer.refresh();
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
				 * When pushed, sets the currently selected configuration in the
				 * table as the selected configuration for SMT proofs.
				 */
				setSelectedConfigIndex(SELECTION_REQUESTED);
			}
		});
		final GridData selectButtonData = new GridData(GridData.FILL_HORIZONTAL);
		selectButtonData.widthHint = convertHorizontalDLUsToPixels(
				selectButton, IDialogConstants.BUTTON_WIDTH);
		selectButton.setLayoutData(selectButtonData);

		/**
		 * Packs everything.
		 */
		configsTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		smtPrefs = getSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
		setSelectedConfigIndex(!SELECTION_REQUESTED);
	}

	@Override
	protected void doLoadDefault() {
		smtPrefs = getDefaultSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
		setSelectedConfigIndex(!SELECTION_REQUESTED);
		selectionChanged();
	}
}