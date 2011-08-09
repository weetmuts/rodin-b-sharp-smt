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

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_TRANSLATIONPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERITPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.TRANSLATIONPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERITPATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

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

	private static final String PREFERENCES_NAME = "solverpreferences";
	private static final String SOLVER_ID = "Solver ID";
	private static final String SOLVER_PATH = "Solver path";
	private static final String SOLVER_ARGS = "Solver arguments";
	private static final String V1_2 = "v1.2";
	private static final String V2_0 = "v2.0";
	private static final String TRANSLATION_PATH = "Temporary translation files path";
	private static final String VERIT_PATH = "VeriT path";
	private static final String SMT_LIB = "SMT-LIB";
	/**
	 * Button texts
	 */
	private static final String BROWSE = "Browse";
	private static final String OK = "OK";
	private static final String CANCEL = "Cancel";
	private static final String ADD = "Add...";
	private static final String REMOVE = "Remove";
	private static final String EDIT = "Edit...";
	private static final String SELECT = "Select";

	private static final String[] PROPS = { SOLVER_ID, SOLVER_PATH,
			SOLVER_ARGS, V1_2, V2_0 };

	private static final int[] BOUNDS = { 70, 190, 50, 35, 35 };

	TableViewer fTable;

	static String setTranslationPath;
	static String setVeriTPath;

	int selectedSolverIndex;

	List<SolverDetail> solverDetails = new ArrayList<SolverDetail>();

	/**
	 * The name of the preference displayed in this preference page.
	 */
	String preferences = new String();

	public SMTPreferencePage() {
		initWithPreferences();
	}

	private void initWithPreferences() {
		setDescription("SMT-Solver Plugin Preference Page");
		setPreferenceStore(SmtProversUIPlugin.getDefault().getPreferenceStore());
		preferences = getPreferenceStore().getString(PREFERENCES_NAME);
		setTranslationPath = getPreferenceStore().getString("TRANSLATIONPATH");
		setVeriTPath = getPreferenceStore().getString(VERITPATH);
		try {
			solverDetails = SMTPreferences.parsePreferencesString(preferences);
		} catch (final PatternSyntaxException pse) {
			pse.printStackTrace(System.err);
			UIUtils.showError(pse.getMessage());
		}
		selectedSolverIndex = getPreferenceStore().getInt(SOLVERINDEX);
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
		fTable.setInput(solverDetails);

		// Configure table
		final Table tableControl = fTable.getTable();
		tableControl.setHeaderVisible(true);
		tableControl.setLinesVisible(true);
		tableControl
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// get back the selected row if exists
		final Color blue = comp.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		final Color white = comp.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		if (selectedSolverIndex >= 0) {
			final TableItem[] Item = fTable.getTable().getItems();
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
		addButton.setText(ADD);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				createSolverDetailsPage(compButtons, false, "", "", "", false,
						false);
			}
		});

		final Button removeButton = new Button(compButtons, SWT.PUSH);
		removeButton.setText(REMOVE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final StructuredSelection sel = (StructuredSelection) fTable
						.getSelection();
				final SolverDetail solverToRemove = (SolverDetail) sel
						.getFirstElement();
				solverDetails.remove(solverToRemove);

				// Check if the selected is being to be removed
				if (fTable.getTable().getSelectionIndex() == selectedSolverIndex) {
					// Clear selectedSolverIndex
					selectedSolverIndex = -1;
				}

				// Update table with solver details
				fTable.refresh();

				// save preferences
				preferences = SolverDetail.toString(solverDetails);

			}
		});

		final Button editButton = new Button(compButtons, SWT.PUSH);
		editButton.setText(EDIT);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final StructuredSelection sel = (StructuredSelection) fTable
						.getSelection();
				final SolverDetail solverToEdit = (SolverDetail) sel
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
		selectButton.setText(SELECT);
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// Change color of the selected row
				final Color blueColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_BLUE);
				final Color whiteColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_WHITE);
				final Color blackColor = comp.getDisplay().getSystemColor(
						SWT.COLOR_BLACK);

				// memorize the selected solver index
				selectedSolverIndex = fTable.getTable().getSelectionIndex();

				final TableItem[] items = fTable.getTable().getItems();
				for (int i = 0; i < items.length; i++) {
					if (i == selectedSolverIndex) {
						items[i].setBackground(blueColor);
						items[i].setForeground(whiteColor);
					} else {
						items[i].setBackground(whiteColor);
						items[i].setForeground(blackColor);
					}
				}
				fTable.refresh();
			}
		});

		// Create a new Composite with 1 column to dispose path options
		final Composite compPaths = new Composite(comp, SWT.NONE);

		// Define 1 column for buttons
		final GridLayout layoutPaths = new GridLayout(3, false);
		layoutButtons.marginHeight = 0;
		layoutButtons.marginWidth = 0;
		compPaths.setLayout(layoutPaths);

		// resize compButtons
		compPaths.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// translation path
		final Label translationPathTextLabel = new Label(compPaths, SWT.LEFT);
		translationPathTextLabel.setText(TRANSLATION_PATH);

		final Text translationPathText = new Text(compPaths, SWT.LEFT
				| SWT.BORDER);
		translationPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, true));
		translationPathText.setEditable(false);
		translationPathText.setText(setTranslationPath);

		// Add button Browse
		final Button translationPathbrowseButton = new Button(compPaths,
				SWT.PUSH);
		translationPathbrowseButton.setText(BROWSE);
		translationPathbrowseButton
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						File f = new File(translationPathText.getText());
						if (!f.exists()) {
							f = null;
						}
						final File d = getFile(f);
						translationPathText.setText(d.getPath());
						setTranslationPath = translationPathText.getText();
					}

				});

		//veriT path
		final Label solverPreproPathTextLabel = new Label(compPaths, SWT.LEFT);
		solverPreproPathTextLabel.setText(VERIT_PATH);

		final Text solverPreproPathText = new Text(compPaths, SWT.LEFT
				| SWT.BORDER);
		solverPreproPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, true));
		solverPreproPathText.setEditable(false);
		solverPreproPathText.setText(setVeriTPath);

		// Add button Browse
		final Button veriTPathBrowseButton = new Button(compPaths, SWT.PUSH);
		veriTPathBrowseButton.setText(BROWSE);
		veriTPathBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				File f = new File(solverPreproPathText.getText());
				if (!f.exists()) {
					f = null;
				}
				final File d = getFile(f);
				solverPreproPathText.setText(d.getPath());
				setVeriTPath = solverPreproPathText.getText();
			}

		});

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
	File getFile(final File startingDirectory) {

		final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN
				| SWT.SHEET);
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
		solverIdTextLabel.setText(SOLVER_ID);

		final Text solverIdText = new Text(compName, SWT.LEFT | SWT.BORDER);
		solverIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverIdText.setText(id);

		// Solver Path
		final Label solverPathTextLabel = new Label(compPath, SWT.LEFT);
		solverPathTextLabel.setText(SOLVER_PATH);

		final Text solverPathText = new Text(compPath, SWT.LEFT | SWT.BORDER);
		solverPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverPathText.setEditable(false);
		solverPathText.setText(path);

		// Add button Browse
		final Button browseButton = new Button(compPath, SWT.PUSH);
		browseButton.setText(BROWSE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				File f = new File(solverPathText.getText());
				if (!f.exists()) {
					f = null;
				}
				final File d = getFile(f);
				solverPathText.setText(d.getPath());
			}

		});

		// arguments
		final Label solverArgTextLabel = new Label(compArg, SWT.LEFT);
		solverArgTextLabel.setText(SOLVER_ARGS);

		final Text solverArgsText = new Text(compArg, SWT.LEFT | SWT.BORDER);
		solverArgsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true));
		solverArgsText.setEditable(true);
		solverArgsText.setText(args);

		// smt version
		final Label smtVersionLabel = new Label(compSmtVersion, SWT.LEFT);
		smtVersionLabel.setText(SMT_LIB);

		final Button smt1_2_Button = new Button(compSmtVersion, SWT.CHECK);
		smt1_2_Button.setText(V1_2);
		smt1_2_Button.setSelection(v1_2);

		final Button smt2_0_Button = new Button(compSmtVersion, SWT.CHECK);
		smt2_0_Button.setText(V2_0);
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
		okButton.setText(OK);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// Check mandatory fields
				if (solverIdText.getText() == ""
						|| !smt1_2_Button.getSelection()
						&& !smt2_0_Button.getSelection()
						|| solverPathText.getText() == "") {
					// Message popup displayed when there is no defined
					// solver path or smt lib chosen or smt Id
					UIUtils.showError(br.ufrn.smt.solver.preferences.Messages.SMTPreferencePage2_MandatoryFieldsInSolverDetails);
				} else {
					if (editMode) {
						final int indexToEdit = fTable.getTable()
								.getSelectionIndex();
						solverDetails.get(indexToEdit).setId(
								solverIdText.getText());
						solverDetails.get(indexToEdit).setPath(
								solverPathText.getText());
						solverDetails.get(indexToEdit).setArgs(
								solverArgsText.getText());
						solverDetails.get(indexToEdit).setSmtV1_2(
								smt1_2_Button.getSelection());
						solverDetails.get(indexToEdit).setSmtV2_0(
								smt2_0_Button.getSelection());
					} else {
						solverDetails.add(new SolverDetail(solverIdText
								.getText(), solverPathText.getText(),
								solverArgsText.getText(), smt1_2_Button
										.getSelection(), smt2_0_Button
										.getSelection()));
					}

					// save preferences
					preferences = SolverDetail.toString(solverDetails);

					// Update table with solver details
					fTable.refresh();

					if (fTable.getTable().getItemCount() == 1) {
						// Change color of the selected row
						final Color blueColor = compOkCancel.getDisplay()
								.getSystemColor(SWT.COLOR_BLUE);
						final Color whiteColor = compOkCancel.getDisplay()
								.getSystemColor(SWT.COLOR_WHITE);
						final int i = 0;
						selectedSolverIndex = i;

						final TableItem[] items = fTable.getTable().getItems();
						items[0].setBackground(blueColor);
						items[0].setForeground(whiteColor);

						fTable.refresh();
					}

					// Close the shell
					shell.close();
				}

			}

		});

		final Button cancelButton = new Button(compOkCancel, SWT.PUSH);
		cancelButton.setText(CANCEL);
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

	public static SMTPreferences getSMTPreferencesForPP()
			throws PatternSyntaxException, IllegalArgumentException {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES,
				null);
		final String translationPath = preferencesService.getString(
				PREFERENCES_ID, TRANSLATIONPATH, DEFAULT_TRANSLATIONPATH, null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVERINDEX, DEFAULT_SOLVERINDEX, null);
		return new SMTPreferences(translationPath, solverPreferences, solverIndex);
	}

	public static SMTPreferences getSMTPreferencesForVeriT()
			throws PatternSyntaxException, IllegalArgumentException {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES,
				null);
		final String translationPath = preferencesService.getString(
				PREFERENCES_ID, TRANSLATIONPATH, DEFAULT_TRANSLATIONPATH, null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVERINDEX, DEFAULT_SOLVERINDEX, null);
		final String veriTPath = preferencesService.getString(PREFERENCES_ID,
				VERITPATH, DEFAULT_VERITPATH, null);
		return new SMTPreferences(translationPath, solverPreferences,
				solverIndex, veriTPath);
	}

	@Override
	protected Control createContents(final Composite parent) {
		return createTableAndButtons(parent);
	}

	@Override
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().putValue(PREFERENCES_NAME, preferences);
		getPreferenceStore().setValue(SOLVERINDEX, selectedSolverIndex);
		getPreferenceStore().putValue(VERITPATH, setVeriTPath);
		if (selectedSolverIndex < 0
				|| selectedSolverIndex >= solverDetails.size()) {
			UIUtils.showWarning(Messages.SmtProversCall_no_selected_solver);
		}
		return super.performOk();
	}
}
