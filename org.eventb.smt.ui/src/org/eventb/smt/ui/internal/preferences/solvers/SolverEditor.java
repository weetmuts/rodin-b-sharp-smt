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

import static org.eventb.smt.core.preferences.SMTSolverFactory.newSolver;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_kindLabel;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_kindToolTip;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_nameDuplicate;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_nameLabel;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_nameMissing;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_nameToolTip;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_pathLabel;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_pathToolTip;
import static org.eventb.smt.ui.internal.Messages.SolverDialog_title;

import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.ui.internal.preferences.ExecutableFileEditor;

/**
 * Implements the controls for editing the characteristics of an SMT solver.
 * <p>
 * In order to reuse implementations of {@link FieldEditor} we make this class a
 * preference page. However, it will never be displayed within the Eclipse
 * workbench preferences, but only in a specialized dialog.
 * </p>
 * <p>
 * Internally, this class creates a preference store for exchanging information
 * with the field editors it creates. This preference store is local to this
 * editor and is not persisted anywhere.
 * </p>
 *
 * @see SolverDialog
 * @author Laurent Voisin
 */
public class SolverEditor extends FieldEditorPreferencePage {

	// Keys in the internal preference store
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String KIND = "kind"; //$NON-NLS-1$
	private static final String PATH = "path"; //$NON-NLS-1$

	// A store for exchanging information with the field editors
	private final IPreferenceStore store;

	private final Set<String> usedNames;

	public SolverEditor(final Shell parentShell, final ISMTSolver solver,
			final Set<String> usedNames) {
		super(SolverDialog_title, FLAT);
		noDefaultAndApplyButton();
		store = new PreferenceStore();
		store.setValue(ID, solver.getID());
		store.setValue(NAME, solver.getName());
		store.setValue(KIND, solver.getKind().name());
		store.setValue(PATH, solver.getPath().toOSString());
		this.usedNames = usedNames;
		usedNames.remove(solver.getName());
	}

	public static String[][] kindNamesAndValues() {
		final String[][] namesAndValues = new String[SolverKind.values().length][];
		int idx = 0;
		for (final SolverKind kind : SolverKind.values()) {
			final String[] nameAndValue = { kind.toString(), kind.name() };
			namesAndValues[idx++] = nameAndValue;
		}
		return namesAndValues;
	}

	public ISMTSolver getSolver() {
		final String id = store.getString(ID);
		final String name = store.getString(NAME);
		final SolverKind kind = SolverKind.valueOf(store.getString(KIND));
		final Path path = new Path(store.getString(PATH));
		return newSolver(id, name, kind, path);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return store;
	}

	/*
	 * Need to fiddle with the layout of the top-level control of this page,
	 * because the default implementation does not resize with the window.
	 */
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		final Control control = getControl();
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		control.setLayoutData(gd);
	}

	@Override
	protected void createFieldEditors() {
		addField(new NameEditor(usedNames, getFieldEditorParent()));
		addField(new KindEditor(getFieldEditorParent()));
		addField(new PathEditor(getFieldEditorParent()));
	}

	private static class NameEditor extends StringFieldEditor {

		private final Set<String> usedNames;

		public NameEditor(final Set<String> usedNames, final Composite parent) {
			super(NAME, SolverDialog_nameLabel, UNLIMITED,
					VALIDATE_ON_KEY_STROKE, parent);
			getLabelControl().setToolTipText(SolverDialog_nameToolTip);
			setEmptyStringAllowed(false);
			this.usedNames = usedNames;
		}

		// Overridden to trim value string
		@Override
		protected void doStore() {
			getPreferenceStore().setValue(getPreferenceName(), getValue());
		}

		private String getValue() {
			return getTextControl().getText().trim();
		}

		@Override
		protected boolean checkState() {
			final String name = getValue();
			final String errorMessage = checkName(name);
			if (errorMessage != null) {
				showErrorMessage(errorMessage);
				return false;
			}
			clearErrorMessage();
			return true;
		}

		private String checkName(String name) {
			if (name.length() == 0) {
				return SolverDialog_nameMissing;
			}
			if (usedNames.contains(name)) {
				return SolverDialog_nameDuplicate;
			}
			return null;
		}

	}

	private static class KindEditor extends ComboFieldEditor {

		public KindEditor(final Composite parent) {
			super(KIND, SolverDialog_kindLabel, kindNamesAndValues(), parent);
			getLabelControl().setToolTipText(SolverDialog_kindToolTip);
		}

	}

	private static class PathEditor extends ExecutableFileEditor {

		public PathEditor(final Composite parent) {
			super(PATH, SolverDialog_pathLabel, SolverDialog_pathToolTip,
					parent);
			setEmptyStringAllowed(false);
		}
	}

}