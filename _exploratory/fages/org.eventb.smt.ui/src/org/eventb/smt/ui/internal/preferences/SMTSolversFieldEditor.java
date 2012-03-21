/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences;

import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.core.preferences.AbstractPreferences.getDefaultSMTPrefs;
import static org.eventb.smt.core.preferences.AbstractPreferences.getSMTPrefs;

import java.util.Map;

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
import org.eventb.smt.core.preferences.AbstractSMTSolver;

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
class SMTSolversFieldEditor extends AbstractTableFieldEditor<AbstractSMTSolver> {
	/**
	 * Labels
	 */
	private static final String SOLVER_ID_LABEL = "ID";
	private static final String SOLVER_NAME_LABEL = "Name";
	private static final String SOLVER_KIND_LABEL = "Kind";
	private static final String SOLVER_PATH_LABEL = "Path";
	private static final String IS_EDITABLE_LABEL = "Editable";

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS = { SOLVER_ID_LABEL,
			SOLVER_NAME_LABEL, SOLVER_KIND_LABEL, SOLVER_PATH_LABEL,
			IS_EDITABLE_LABEL };
	private static final int[] COLUMN_BOUNDS = { 0, 70, 70, 250, 0 };

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
		tableViewer
				.setContentProvider(new ContentProvider<AbstractSMTSolver>());
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
	void removeCurrentSelection(final Table elementsTable) {
		final String solverToRemove = elementsTable.getSelection()[0].getText();
		smtPrefs.removeSMTSolver(solverToRemove);
		tableViewer.refresh();
		selectionChanged();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the table.
	 */
	@Override
	void selectionChanged() {
		final Table solversTable = tableViewer.getTable();
		final TableItem selection = solversTable.getSelection()[0];
		final String selectedSolverID = selection.getText();
		final Map<String, AbstractSMTSolver> solvers = smtPrefs.getSolvers();
		final boolean validSelection = solvers.containsKey(selectedSolverID);
		final boolean validEditableSelection = validSelection ? solvers.get(
				selectedSolverID).isEditable() : false;
		removeButton.setEnabled(validEditableSelection);
		editButton.setEnabled(validEditableSelection);
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
				final SMTSolverDialog solverDialog = new SMTSolverDialog(
						buttonsGroup.getShell(), smtPrefs, null);
				if (solverDialog.open() == Window.OK) {
					/**
					 * Creates a new <code>SMTSolver</code> object, and adds it
					 * to the list.
					 */
					smtPrefs.addSolver(solverDialog.getSolver());

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
				if (smtPrefs.getSolvers().containsKey(selectionID)) {
					final AbstractSMTSolver solverToEdit = smtPrefs
							.getSolvers().get(selectionID);
					if (solverToEdit != null) {
						final SMTSolverDialog solverDialog = new SMTSolverDialog(
								buttonsGroup.getShell(), smtPrefs, solverToEdit);
						if (solverDialog.open() == Window.OK) {
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
		solversTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		smtPrefs = getSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolvers());
		tableViewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		smtPrefs = getDefaultSMTPrefs();
		tableViewer.setInput(smtPrefs.getSolvers());
		tableViewer.refresh();
	}
}