/**
 * 
 */
package br.ufrn.smt.solver.preferences.ui;

import static br.ufrn.smt.solver.preferences.ui.Messages.SMTPreferencePage2_MandatoryFieldsInSolverDetails;

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
 * @author guyot
 * 
 */
class SolversDetailsFieldEditor extends FieldEditor {
	private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	private static final int SOLVERS_HEIGHT_IN_CHARS = 10;
	private static final int SOLVERS_HEIGHT_IN_DLUS = SOLVERS_HEIGHT_IN_CHARS
			* VERTICAL_DIALOG_UNITS_PER_CHAR;

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

	private static final String[] PROPS = { SOLVER_ID_LABEL, SOLVER_PATH_LABEL,
			SOLVER_ARGS_LABEL, V1_2_LABEL, V2_0_LABEL };

	private static final int[] BOUNDS = { 70, 190, 150, 40, 40 };

	/**
	 * The button for adding a new solver to the list.
	 */
	private Button addButton;
	/**
	 * The button for removing the currently selected solver from the list.
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
	Composite top;
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
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public SolversDetailsFieldEditor(final String name, final String labelText,
			final Composite parent, final List<SolverDetails> solversDetails,
			final int selectedSolverIndex) {
		super(name, labelText, parent);
		this.solversDetails = solversDetails;
		this.selectedSolverIndex = selectedSolverIndex;
	}

	/**
	 * Creates a table viewer and configure it.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private TableViewer createTableViewer(final Composite parent) {
		final TableViewer tv = new TableViewer(parent, SWT.FULL_SELECTION);

		createColumns(tv);
		tv.setColumnProperties(PROPS);
		tv.setContentProvider(new SolversDetailsContentProvider());
		tv.setLabelProvider(new SolversDetailsLabelProvider());

		return tv;
	}

	private void createColumns(final TableViewer viewer) {

		for (int i = 0; i < PROPS.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer,
					SWT.NONE);
			column.getColumn().setText(PROPS[i]);
			column.getColumn().setWidth(BOUNDS[i]);
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

	void createSolverDetailsPage(final Composite parent,
			final boolean editMode, final String id, final String path,
			final String args, final boolean v1_2, final boolean v2_0) {
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
		solverIdTextLabel.setText(SOLVER_ID_LABEL);

		final Text solverIdText = new Text(compName, SWT.LEFT | SWT.BORDER);
		solverIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverIdText.setText(id);

		// Solver Path
		final Label solverPathTextLabel = new Label(compPath, SWT.LEFT);
		solverPathTextLabel.setText(SOLVER_PATH_LABEL);

		final Text solverPathText = new Text(compPath, SWT.LEFT | SWT.BORDER);
		solverPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverPathText.setEditable(false);
		solverPathText.setText(path);

		// Add button Browse
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

		// arguments
		final Label solverArgTextLabel = new Label(compArg, SWT.LEFT);
		solverArgTextLabel.setText(SOLVER_ARGS_LABEL);

		final Text solverArgsText = new Text(compArg, SWT.LEFT | SWT.BORDER);
		solverArgsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverArgsText.setEditable(true);
		solverArgsText.setText(args);

		// smt version
		final Label smtVersionLabel = new Label(compSmtVersion, SWT.LEFT);
		smtVersionLabel.setText(SMT_LIB_LABEL);

		final Button smt1_2_Button = new Button(compSmtVersion, SWT.CHECK);
		smt1_2_Button.setText(V1_2_LABEL);
		smt1_2_Button.setSelection(v1_2);

		final Button smt2_0_Button = new Button(compSmtVersion, SWT.CHECK);
		smt2_0_Button.setText(V2_0_LABEL);
		smt2_0_Button.setSelection(v2_0);

		// callbacks for the 2 buttons
		smt1_2_Button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				smt2_0_Button.setSelection(false);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		smt2_0_Button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				smt1_2_Button.setSelection(false);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// Add 2 buttons OK and Cancel
		final Button okButton = new Button(compOkCancel, SWT.PUSH);
		okButton.setText(OK_LABEL);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Table solversTable = solversTableViewer.getTable();
				// Check mandatory fields
				if (solverIdText.getText() == ""
						|| !smt1_2_Button.getSelection()
						&& !smt2_0_Button.getSelection()
						|| solverPathText.getText() == "") {
					// Message popup displayed when there is no defined
					// solver path or smt lib chosen or smt Id
					UIUtils.showError(SMTPreferencePage2_MandatoryFieldsInSolverDetails);
				} else {
					if (editMode) {
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
					} else {
						solversDetails.add(new SolverDetails(solverIdText
								.getText(), solverPathText.getText(),
								solverArgsText.getText(), smt1_2_Button
										.getSelection(), smt2_0_Button
										.getSelection()));
					}

					// save preferences
					solversTableViewer.setInput(solversDetails);

					// Update table with solver details
					solversTableViewer.refresh();

					if (solversTable.getItemCount() == 1) {
						// Change color of the selected row
						final Color blueColor = compOkCancel.getDisplay()
								.getSystemColor(SWT.COLOR_BLUE);
						final Color whiteColor = compOkCancel.getDisplay()
								.getSystemColor(SWT.COLOR_WHITE);
						final int i = 0;
						solversTable.setSelection(i);

						final TableItem[] items = solversTable.getItems();
						items[0].setBackground(blueColor);
						items[0].setForeground(whiteColor);

						solversTableViewer.refresh();
					}

					// Close the shell
					shell.close();
				}

			}

		});

		final Button cancelButton = new Button(compOkCancel, SWT.PUSH);
		cancelButton.setText(CANCEL_LABEL);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// Close the shell
				shell.close();
			}

		});

		// Open the shell
		shell.open();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the list.
	 */
	void selectionChanged() {
		final Table solversTable = solversTableViewer.getTable();
		final int selectionIndex = solversTable.getSelectionIndex();
		final boolean interactionEnabled = selectionIndex >= 0
				&& selectionIndex < solversTable.getItemCount();
		removeButton.setEnabled(interactionEnabled);
		editButton.setEnabled(interactionEnabled);
		selectButton.setEnabled(interactionEnabled);
	}

	/**
	 * This method sets the selected solver index to the current selection in
	 * the solvers table if the selection is correct. If there is only one
	 * solver in the list, it is selected, whatever the selection is. Otherwise,
	 * it is set to the default value (-1).
	 * 
	 * Then the selected solver background color is set to BLUE.
	 * 
	 */
	void setSelectedSolverIndex() {
		final Table solversTable = solversTableViewer.getTable();
		final int itemCount = solversTable.getItemCount();
		final int selectionIndex = solversTable.getSelectionIndex();
		final boolean validSelectionIndex = selectionIndex >= 0 && selectionIndex < itemCount;
		if (itemCount == 1 || (itemCount > 1 && !validSelectionIndex)) {
			selectedSolverIndex = 0;
		} else if (validSelectionIndex) {
			selectedSolverIndex = selectionIndex;
		} else {
			selectedSolverIndex = -1;
		}

		final Color blue = top.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		final Color white = top.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final Color black = top.getDisplay().getSystemColor(SWT.COLOR_BLACK);

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

		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = numColumns;
		top.setLayoutData(gridData);

		final Label label = getLabelControl(top);
		final GridData labelData = new GridData();
		labelData.horizontalSpan = numColumns;
		label.setLayoutData(labelData);

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
		final GridData solversData = new GridData(GridData.FILL_HORIZONTAL);
		solversData.heightHint = convertVerticalDLUsToPixels(solversTable,
				SOLVERS_HEIGHT_IN_DLUS);
		solversData.horizontalSpan = numColumns;

		solversTable.setLayoutData(solversData);
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
				solversTableViewer.refresh();
				if (solversTable.getItemCount() == 1) {
					setSelectedSolverIndex();
				}
				selectionChanged();
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
				solversDetails.remove(solversTable.getSelectionIndex());
				solversTableViewer.refresh();
				setSelectedSolverIndex();
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
					solversTableViewer.refresh();
				}
			}
		});

		/**
		 * 'select' button
		 */
		selectButton = new Button(buttonsGroup, SWT.PUSH);
		selectButton.setEnabled(false);
		selectButton.setText(SELECT_LABEL);
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				setSelectedSolverIndex();
			}
		});

		// Update table with solver details
		solversTableViewer.refresh();

		// pack everything
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
				SMTPreferences.SOLVERINDEX);
		setSelectedSolverIndex();
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
				SMTPreferences.SOLVERINDEX);
		setSelectedSolverIndex();
		solversTableViewer.refresh();
	}

	@Override
	protected void doStore() {
		final String preferences = SolverDetails.toString(solversDetails);
		if (preferences != null) {
			getPreferenceStore().setValue(getPreferenceName(), preferences);
		}
		getPreferenceStore().setValue(SMTPreferences.SOLVERINDEX,
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