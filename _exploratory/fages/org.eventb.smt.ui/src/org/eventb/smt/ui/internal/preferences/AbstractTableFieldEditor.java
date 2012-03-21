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

import static org.eventb.smt.core.preferences.AbstractPreferences.getSMTPrefs;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eventb.smt.core.preferences.AbstractPreferences;

/**
 * This class is used to build the tables printed in the preference pages. This
 * class also defines three buttons which interact with the table:
 * <ul>
 * <li>The 'Add' button to add a new object into the table.</li>
 * <li>The 'Edit' button to modify a previously added object.</li>
 * <li>The 'Remove' button to remove an existing object.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SMTPreferences</code> instance, of which the object list is given as
 * input to the <code>TableViewer</code>. As a consequence, it is necessary to
 * update the <code>tableViewer</code> each time the list is modified, by
 * calling the <code>refresh</code> method.
 * 
 * @author guyot
 */
abstract class AbstractTableFieldEditor<T> extends FieldEditor {
	/**
	 * Labels
	 */
	protected static final String ADD_LABEL = "Add...";
	protected static final String REMOVE_LABEL = "Remove";
	protected static final String EDIT_LABEL = "Edit...";

	protected Composite buttonsGroup;

	/**
	 * The button for adding a new object to the table.
	 */
	protected Button addButton;
	/**
	 * The button for removing the currently selected object from the table.
	 */
	protected Button removeButton;
	/**
	 * The button for editing the currently selected object.
	 */
	protected Button editButton;

	/**
	 * The top-level control for the field editor.
	 */
	protected Composite top;
	/**
	 * The table showing the list of objects
	 */
	TableViewer tableViewer;

	AbstractPreferences smtPrefs;

	/**
	 * Creates a new table field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public AbstractTableFieldEditor(final String name, final String labelText,
			final Composite parent) {
		super(name, labelText, parent);
		smtPrefs = getSMTPrefs();
	}

	/**
	 * Creates a table viewer and configures it.
	 * 
	 * @param parent
	 *            the parent of the table viewer's control
	 */
	abstract protected void createTableViewer(final Composite parent);

	abstract protected String[] getColumnsLabel();

	abstract protected int[] getColumnsBounds();

	/**
	 * Creates the columns of the table viewer.
	 * 
	 * @param viewer
	 *            the table viewer
	 */
	protected void createColumns(final TableViewer viewer) {
		for (int i = 0; i < getColumnsLabel().length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer,
					SWT.NONE);
			column.getColumn().setText(getColumnsLabel()[i]);
			column.getColumn().setWidth(getColumnsBounds()[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * Remove the currently selected element from the list, refresh the table
	 * viewer, updates the index of the selected element and refresh the button
	 * states.
	 * 
	 * @param elementsTable
	 *            the table
	 */
	abstract void removeCurrentSelection(final Table elementsTable);

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
	abstract void selectionChanged();

	/**
	 * Updates the colors of the table.
	 */
	void updateConfigsTableColors() {
		final Color white = top.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final Color black = top.getDisplay().getSystemColor(SWT.COLOR_BLACK);

		final Table table = tableViewer.getTable();
		final TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setBackground(white);
			items[i].setForeground(black);
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
		createTableViewer(top);

		/**
		 * Configures the table
		 */
		final Table solversTable = tableViewer.getTable();
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
		buttonsGroup = new Composite(top, SWT.NONE);
		buttonsGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final GridLayout buttonsLayout = new GridLayout(1, false);
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 0;
		buttonsGroup.setLayout(buttonsLayout);
	}

	@Override
	abstract protected void doLoad();

	@Override
	abstract protected void doLoadDefault();

	/**
	 * FIXME this should not need to be overriden
	 * 
	 * Overriden because when called after performDefault, the following
	 * statement was executed :</ br>
	 * <code>preferenceStore.setToDefault(preferenceName);</code> which was
	 * causing the values of this field editor not to be saved.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#store()
	 */
	@Override
	public void store() {
		doStore();
	}

	@Override
	protected void doStore() {
		smtPrefs.save();
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