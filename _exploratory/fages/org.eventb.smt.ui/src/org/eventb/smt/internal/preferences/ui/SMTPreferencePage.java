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

import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.smt.internal.provers.core.SMTProversCore;

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
public class SMTPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	private static final String SMT_SOLVERS_CONFIG_LABEL = "SMT-Solvers configurations";
	private static final String VERIT_PATH_LABEL = "VeriT path";
	private static final String TRANSLATION_PATH_LABEL = "Temporary translation files path";

	public SMTPreferencePage() {
		super(FLAT);
	}

	private class ValidatedOnKeyStrokeDirectoryFieldEditor extends
			DirectoryFieldEditor {
		public ValidatedOnKeyStrokeDirectoryFieldEditor(String name,
				String label, Composite parent) {
			super();
			init(name, label);
			setErrorMessage(JFaceResources
					.getString("DirectoryFieldEditor.errorMessage"));
			setChangeButtonText(JFaceResources.getString("openBrowse"));
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
		}
	}

	@Override
	protected void createFieldEditors() {
		final FieldEditor solversFieldEditor = new SMTSolverConfigurationsFieldEditor(
				SOLVER_PREFERENCES_ID, SMT_SOLVERS_CONFIG_LABEL,
				getFieldEditorParent());
		addField(solversFieldEditor);

		final FileFieldEditor veriTBinaryBrowser = new FileFieldEditor(
				VERIT_PATH_ID, VERIT_PATH_LABEL, true, getFieldEditorParent());
		addField(veriTBinaryBrowser);

		final ValidatedOnKeyStrokeDirectoryFieldEditor translationDirBrowser = new ValidatedOnKeyStrokeDirectoryFieldEditor(
				TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL,
				getFieldEditorParent());
		addField(translationDirBrowser);
	}

	/**
	 * Sets the preference store of this preference page. It is called when the
	 * preference store is currently <code>null</code>. The returned preference
	 * store is built over the core plug-in node (its ID) of the configuration
	 * scope, as in the <code>SMTPreference</code> class.
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(ConfigurationScope.INSTANCE,
				SMTProversCore.PLUGIN_ID);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(doGetPreferenceStore());
	}
}
