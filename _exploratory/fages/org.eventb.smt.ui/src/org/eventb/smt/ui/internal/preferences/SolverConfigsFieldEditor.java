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
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_REPLACE;
import static org.eventb.smt.core.preferences.PreferenceManager.freshConfigID;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.SolverConfigFactory.getEnabledColumnNumber;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
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
	 * Labels
	 */
	private static final String CONFIG_ID_LABEL = "ID";
	private static final String EXECUTION_LABEL = "Execution";
	private static final String CONFIG_NAME_LABEL = "Name";
	private static final String SOLVER_LABEL = "Solver";
	private static final String SOLVER_ARGS_LABEL = "Arguments";
	private static final String SMTLIB_LABEL = "SMT-LIB";
	private static final String IS_EDITABLE_LABEL = "Editable";

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS = { CONFIG_ID_LABEL,
			EXECUTION_LABEL, CONFIG_NAME_LABEL, SOLVER_LABEL,
			SOLVER_ARGS_LABEL, SMTLIB_LABEL, IS_EDITABLE_LABEL };
	private static final int[] COLUMN_BOUNDS = { 0, 80, 90, 90, 200, 70, 0 };

	static final int ENABLED_INDEX = 0;
	static final int DISABLED_INDEX = 1;
	static final String ENABLED = "enabled";
	static final String DISABLED = "disabled";
	static final String[] ENABLED_COMBO_VALUES = new String[2];
	static {
		ENABLED_COMBO_VALUES[ENABLED_INDEX] = ENABLED;
		ENABLED_COMBO_VALUES[DISABLED_INDEX] = DISABLED;
	}

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

		CellEditor[] editors = new CellEditor[COLUMNS_LABELS.length];

		// editors[getEnabledColumnNumber()] = new CheckboxCellEditor(
		// tableViewer.getTable());
		editors[getEnabledColumnNumber()] = new ComboBoxCellEditor(
				tableViewer.getTable(), ENABLED_COMBO_VALUES, SWT.READ_ONLY);

		tableViewer.setCellEditors(editors);

		tableViewer.setCellModifier(new ICellModifier() {

			@Override
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item)
					element = ((Item) element).getData();

				final ISolverConfig config = (ISolverConfig) element;
				if (property.equals(EXECUTION_LABEL)) {
					final int comboIndex = (Integer) value;
					final boolean enable = ENABLED_COMBO_VALUES[comboIndex]
							.equals(ENABLED);
					// config.setEnabled(enable);
					smtPrefs.getSolverConfig(config.getID()).setEnabled(enable);
					tableViewer.refresh();
				}
			}

			@Override
			public Object getValue(Object element, String property) {
				if (property.equals(EXECUTION_LABEL)) {
					final ISolverConfig config = (ISolverConfig) element;
					return config.isEnabled() ? ENABLED_INDEX : DISABLED_INDEX;
				}

				return null;
			}

			@Override
			public boolean canModify(Object element, String property) {
				return property.equals(EXECUTION_LABEL);
			}
		});
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
		selectionChanged();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the table.
	 */
	@Override
	void selectionChanged() {
		final Table configsTable = tableViewer.getTable();
		final TableItem[] selection = configsTable.getSelection();
		if (selection.length == 0) {
			removeButton.setEnabled(false);
			editButton.setEnabled(false);
		} else {
			final TableItem firstItem = configsTable.getSelection()[0];
			final String selectedConfigID = firstItem.getText();
			final Map<String, ISolverConfig> solverConfigs = smtPrefs
					.getSolverConfigs();
			final boolean validSelection = solverConfigs
					.containsKey(selectedConfigID);
			final boolean validEditableSelection = validSelection ? solverConfigs
					.get(selectedConfigID).isEditable() : false;
			removeButton.setEnabled(validEditableSelection);
			editButton.setEnabled(validEditableSelection);
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
				final String freshID = freshConfigID();
				if (freshID == null) {
					return;
				}

				final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
						buttonsGroup.getShell(), smtPrefs, newConfig(freshID));
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
							smtPrefs.addSolverConfig(
									solverConfigDialog.getSolverConfig(),
									FORCE_REPLACE);
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
		 * Packs everything.
		 */
		configsTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		smtPrefs = getPreferenceManager().getSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		smtPrefs = getPreferenceManager().getDefaultSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
		selectionChanged();
	}
}