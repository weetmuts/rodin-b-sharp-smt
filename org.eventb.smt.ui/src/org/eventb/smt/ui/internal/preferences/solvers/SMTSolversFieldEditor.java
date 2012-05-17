/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_RELOAD;
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_REPLACE;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.SMTSolverFactory.EDITABLE_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.ID_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.KIND_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.NAME_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.PATH_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.newSolver;
import static org.eventb.smt.ui.internal.UIUtils.showQuestionWithCancel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;
import org.eventb.smt.ui.internal.preferences.AbstractTableFieldEditor;
import org.eventb.smt.ui.internal.preferences.ContentProvider;

/**
 * This class is used to build the solver table printed in the preferences page.
 * This table contains all the information set by the user when he added a new
 * SMT solver. This class also defines three buttons which interact with the
 * table:
 * <ul>
 * <li>The 'Add' button to add a new SMT solver into the table.</li>
 * <li>The 'Edit' button to modify a previously added SMT solver.</li>
 * <li>The 'Remove' button to remove an existing SMT solver.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SMTPreferences</code> instance, of which the <code>SMTSolver</code>
 * list is given as input to the <code>TableViewer</code>. As a consequence, it
 * is necessary to update the <code>tableViewer</code> each time the list
 * <code>solvers</code> is modified, by calling the <code>refresh</code> method.
 * 
 * @author guyot
 */
class SMTSolversFieldEditor extends
		AbstractTableFieldEditor<ISMTSolversPreferences> {
	/**
	 * Labels
	 */
	private static final String ID_LABEL = "ID";
	private static final String NAME_LABEL = "Name";
	private static final String KIND_LABEL = "Kind";
	private static final String PATH_LABEL = "Path";
	private static final String EDITABLE_LABEL = "Editable";

	private static final int ID_COL_BOUND = 0;
	private static final int NAME_COL_BOUND = 100;
	private static final int KIND_COL_BOUND = 70;
	private static final int PATH_COL_BOUND = 300;
	private static final int EDITABLE_COL_BOUND = 0;

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS;
	static {
		ArrayList<String> columnsLabels = new ArrayList<String>();
		columnsLabels.add(ID_COL, ID_LABEL);
		columnsLabels.add(NAME_COL, NAME_LABEL);
		columnsLabels.add(KIND_COL, KIND_LABEL);
		columnsLabels.add(PATH_COL, PATH_LABEL);
		columnsLabels.add(EDITABLE_COL, EDITABLE_LABEL);
		COLUMNS_LABELS = new String[columnsLabels.size()];
		columnsLabels.toArray(COLUMNS_LABELS);
	}
	private static final Integer[] COLUMNS_BOUNDS;
	static {
		ArrayList<Integer> columnsBounds = new ArrayList<Integer>();
		columnsBounds.add(ID_COL, ID_COL_BOUND);
		columnsBounds.add(NAME_COL, NAME_COL_BOUND);
		columnsBounds.add(KIND_COL, KIND_COL_BOUND);
		columnsBounds.add(PATH_COL, PATH_COL_BOUND);
		columnsBounds.add(EDITABLE_COL, EDITABLE_COL_BOUND);
		COLUMNS_BOUNDS = new Integer[columnsBounds.size()];
		columnsBounds.toArray(COLUMNS_BOUNDS);
	}

	/**
	 * Creates a new solvers field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SMTSolversFieldEditor(final String name, final String labelText,
			final Composite parent) {
		super(name, labelText, parent, getPreferenceManager()
				.getSMTSolversPrefs());
	}

	@Override
	protected String[] getColumnsLabel() {
		return COLUMNS_LABELS;
	}

	@Override
	protected Integer[] getColumnsBounds() {
		return COLUMNS_BOUNDS;
	}

	@Override
	protected void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, FULL_SELECTION);

		createColumns(tableViewer);
		tableViewer.setColumnProperties(getColumnsLabel());
		tableViewer.setContentProvider(new ContentProvider<ISMTSolver>());
		tableViewer.setLabelProvider(new SMTSolversLabelProvider());
	}

	/**
	 * Remove the currently selected element from the list, refresh the table
	 * viewer, updates the selected element and refresh the button states.
	 * 
	 * @param elementsTable
	 *            the table
	 */
	@Override
	protected void removeCurrentSelection(final Table elementsTable) {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		final String solverToRemove = elementsTable.getSelection()[0].getText();
		final Set<ISolverConfig> relatedConfigs = configsPrefs
				.relatedConfigs(solverToRemove);
		if (!relatedConfigs.isEmpty()) {
			final StringBuilder msgBuilder = new StringBuilder();
			msgBuilder
					.append("One or several configurations are linked to the solver you intend to remove:\n");
			for (final ISolverConfig config : relatedConfigs) {
				msgBuilder.append(config.getName() + "\n");
			}
			msgBuilder.append("Do you want to remove them as well?");
			final int choice = showQuestionWithCancel(msgBuilder.toString());
			switch (choice) {
			case 0:
				/**
				 * Yes
				 */
				for (final ISolverConfig config : relatedConfigs) {
					configsPrefs.removeSolverConfig(config.getID());
					getSMTPrefs().remove(solverToRemove);
				}
				break;
			case 1:
				/**
				 * No
				 */
				getSMTPrefs().remove(solverToRemove);
				break;
			case 2:
				/**
				 * Cancel
				 */
				return;
			}
		} else {
			getSMTPrefs().remove(solverToRemove);
		}
		tableViewer.refresh();
		selectionChanged();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the table.
	 */
	@Override
	protected void selectionChanged() {
		final Table solversTable = tableViewer.getTable();
		final TableItem[] selection = solversTable.getSelection();
		if (selection.length == 0) {
			removeButton.setEnabled(false);
			editButton.setEnabled(false);
		} else {
			final TableItem firstItem = selection[0];
			final String selectedSolverID = firstItem.getText();
			final Map<String, ISMTSolver> solvers = smtPrefs.getSolvers();
			final boolean validSelection = solvers
					.containsKey(selectedSolverID);
			final boolean validEditableSelection = validSelection ? solvers
					.get(selectedSolverID).isEditable() : false;
			removeButton.setEnabled(validEditableSelection);
			editButton.setEnabled(validEditableSelection);
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		final Table solversTable = tableViewer.getTable();

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
				final ISMTSolver newSolver = newSolver();

				if (newSolver == null) {
					return;
				}

				final SMTSolverDialog solverDialog = new SMTSolverDialog(
						getButtonsGroup().getShell(), getSMTPrefs(),
						newSolver());
				if (solverDialog.open() == Window.OK) {
					/**
					 * Creates a new <code>SMTSolver</code> object, and adds it
					 * to the list.
					 */
					getSMTPrefs().add(solverDialog.getSolver());

					/**
					 * Refreshes the table viewer.
					 */
					getTableViewer().refresh();
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
				final String selectionID = solversTable.getSelection()[0]
						.getText();
				if (getSMTPrefs().getSolvers().containsKey(selectionID)) {
					final ISMTSolver solverToEdit = getSMTPrefs().getSolvers()
							.get(selectionID);
					if (solverToEdit != null) {
						final SMTSolverDialog solverDialog = new SMTSolverDialog(
								getButtonsGroup().getShell(), getSMTPrefs(),
								solverToEdit);
						if (solverDialog.open() == Window.OK) {
							getSMTPrefs().add(solverDialog.getSolver(),
									FORCE_REPLACE);
							/**
							 * Refreshes the table viewer.
							 */
							getTableViewer().refresh();
							selectionChanged();
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
		solversTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		smtPrefs = getPreferenceManager().getSMTSolversPrefs(FORCE_RELOAD);
		tableViewer.setInput(smtPrefs.getSolvers());
		tableViewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		smtPrefs.loadDefault();
		tableViewer.setInput(smtPrefs.getSolvers());
		tableViewer.refresh();
	}
}