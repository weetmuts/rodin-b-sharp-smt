/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class is used to build the tables printed in the preference pages. This
 * class also defines four buttons which interact with the table:
 * <ul>
 * <li>The 'Add' button to add a new object into the table.</li>
 * <li>The 'Edit' button to modify a previously added object.</li>
 * <li>The 'Duplicate' button to duplicate an existing object.</li>
 * <li>The 'Remove' button to remove an existing object.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SMTPreferences</code> instance, of which the object list is given as
 * input to the <code>TableViewer</code>. As a consequence, it is necessary to
 * update the <code>tableViewer</code> each time the list is modified, by
 * calling the <code>refresh</code> method.
 * 
 * @author Yoann Guyot
 */
public abstract class AbstractTableFieldEditor<U, T extends AbstractElement<U>, M extends AbstractModel<U, T>>
		extends FieldEditor {

	/**
	 * Button labels
	 */
	private static final String ADD_LABEL = "Add...";
	private static final String EDIT_LABEL = "Edit...";
	private static final String DUPLICATE_LABEL = "Duplicate...";
	private static final String REMOVE_LABEL = "Remove";

	/**
	 * Column descriptors
	 */
	protected static interface ColumnDescriptor<C> {

		String name();

		String title();

		int width();

		String getLabel(C element);

	}

	protected final M model;

	// Main composite grouping the controls of the field editor
	private Composite main;

	// Table displaying the elements to edit
	private TableViewer tableViewer;

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
			final Composite parent, final M model) {
		super(name, labelText, parent);
		this.model = model;
	}

	/**
	 * @return the tableViewer
	 */
	protected final TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * @return a shell for parenting dialogs
	 */
	protected final Shell getShell() {
		return main.getShell();
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		final GridData layoutData = (GridData) main.getLayoutData();
		layoutData.horizontalSpan = numColumns;
	}

	@Override
	public final int getNumberOfControls() {
		return 1; // Only the main composite
	}

	@Override
	protected final void doFillIntoGrid(Composite parent, int numColumns) {
		/*
		 * By default, the parent expands only horizontally, but we also want to
		 * expand vertically, because of the table.
		 */
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		main = createMainComposite(parent, numColumns);
		createLabel(main);
		createTable(main);
		createButtons(main);
	}

	/*
	 * The purpose of the main composite is to layout other controls in exactly
	 * two columns, whatever the number of columns in the original field editor.
	 */
	private Composite createMainComposite(Composite parent, int numColumns) {
		final Composite result = new Composite(parent, SWT.NONE);
		result.setLayout(new GridLayout(2, false));
		final GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = numColumns;
		result.setLayoutData(layoutData);
		return result;
	}

	private void createLabel(final Composite parent) {
		final Label label = getLabelControl(parent);
		final GridData labelData = new GridData(SWT.FILL, SWT.TOP, true, false);
		labelData.horizontalSpan = 2;
		label.setLayoutData(labelData);
	}

	private void createTable(final Composite parent) {
		tableViewer = createTableViewer(parent);
		tableViewer.setContentProvider(new ContentProvider<M>());

		final Table table = tableViewer.getTable();
		createTableColumns(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	protected abstract TableViewer createTableViewer(Composite parent);

	private void createTableColumns(final Table table) {
		final ColumnDescriptor<T>[] columnDescs = getColumnDescriptors();
		for (final ColumnDescriptor<T> cd : columnDescs) {
			final TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(cd.title());
			tc.setWidth(cd.width());
			tc.setResizable(true);
			tc.setMoveable(true);
			new TableViewerColumn(tableViewer, tc);
		}
		final TableLabelProvider<T> labelProvider = new TableLabelProvider<T>(
				columnDescs);
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setColumnProperties(labelProvider.getColumnProperties());
	}

	protected abstract ColumnDescriptor<T>[] getColumnDescriptors();

	private void createButtons(final Composite parent) {
		final Composite buttonsGroup = new Composite(parent, SWT.NONE);
		final GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		buttonsGroup.setLayoutData(data);
		buttonsGroup.setLayout(new FillLayout(SWT.VERTICAL));

		new TableButtonController<T>(buttonsGroup, ADD_LABEL, tableViewer) {
			@Override
			protected boolean isEnabled() {
				return true;
			}

			@Override
			public void perform() {
				doAdd();
			}
		};
		final TableButtonController<T> edit = new TableButtonController<T>(
				buttonsGroup, EDIT_LABEL, tableViewer) {
			@Override
			protected boolean isEnabled() {
				return isEditable(selectedElement);
			}

			@Override
			public void perform() {
				doEdit(selectedElement);
			}
		};
		tableViewer.addDoubleClickListener(edit);

		if (canDuplicate()) {
			new TableButtonController<T>(buttonsGroup, DUPLICATE_LABEL,
					tableViewer) {
				@Override
				protected boolean isEnabled() {
					return selectedElement != null;
				}

				@Override
				public void perform() {
					doDuplicate(selectedElement);
				}
			};
		}
		new TableButtonController<T>(buttonsGroup, REMOVE_LABEL, tableViewer) {
			@Override
			protected boolean isEnabled() {
				return isEditable(selectedElement);
			}

			@Override
			public void perform() {
				doRemove(selectedElement);
			}
		};
	}

	protected abstract boolean canDuplicate();

	protected final boolean isEditable(T element) {
		return element != null && element.editable;
	}

	@Override
	protected final void doLoad() {
		/*
		 * The model was not yet available when creating the viewer (because the
		 * control is created by the constructor of the abstraction). We thus
		 * link the model and the viewer here.
		 */
		model.setViewer(getTableViewer());
		getTableViewer().setInput(model);
		model.load();
	}

	@Override
	protected final void doLoadDefault() {
		model.loadDefault();
	}

	// Action for Add button
	protected final void doAdd() {
		openEditor(model.newElement());
	}

	// Action for Edit button
	protected final void doEdit(final T element) {
		if (!isEditable(element)) {
			return;
		}
		openEditor(element);
	}

	// Action for Duplicate button
	protected final void doDuplicate(final T element) {
		if (element == null || !canDuplicate()) {
			return;
		}
		openEditor(model.clone(element));
	}

	// Action for Remove button
	protected final void doRemove(final T element) {
		if (!isEditable(element)) {
			return;
		}
		if (checkRemovePrecondition(element)) {
			model.remove(element);
		}
	}

	protected abstract void openEditor(T element);

	protected abstract boolean checkRemovePrecondition(T element);

	/*
	 * We need to override this method, because we do not handle defaults value
	 * in the same manner as the abstraction: we need to tell the core plug-in
	 * to forget about everything contributed by users.
	 */
	@Override
	public final void store() {
		doStore();
	}

	@Override
	protected final void doStore() {
		model.store();
	}

}