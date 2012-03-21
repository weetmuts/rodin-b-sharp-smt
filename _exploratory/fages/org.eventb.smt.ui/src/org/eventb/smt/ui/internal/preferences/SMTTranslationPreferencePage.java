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

import static org.eventb.smt.core.preferences.AbstractPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.core.preferences.AbstractPreferences.VERIT_PATH_ID;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.smt.core.SMTCore;

/**
 * This class contributes a preference page to the Preference dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field
 * support built into JFace that allows us to create a page that is small and
 * knows how to save, restore and apply itself.
 */
public class SMTTranslationPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private static final String SMT_TRANSLATION_SETTINGS_LABEL = "SMT translation settings";
	private static final String VERIT_PATH_LABEL = "VeriT path";
	private static final String TRANSLATION_PATH_LABEL = "Temporary translation files path";

	public SMTTranslationPreferencePage() {
		super(FLAT);
		setDescription(SMT_TRANSLATION_SETTINGS_LABEL);
	}

	/**
	 * This class is a directory field editor set to validate on key stroke.
	 * 
	 * @author Systerel (yguyot)
	 */
	private class ValidatedOnKeyStrokeDirFieldEd extends DirectoryFieldEditor {
		public ValidatedOnKeyStrokeDirFieldEd(String name, String label,
				Composite parent) {
			super();
			init(name, label);
			setErrorMessage(JFaceResources
					.getString("DirectoryFieldEditor.errorMessage"));
			setChangeButtonText(JFaceResources.getString("openBrowse"));
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
		}
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
				SMTCore.PLUGIN_ID);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(doGetPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		final FileFieldEditor veriTBinaryBrowser = new FileFieldEditor(
				VERIT_PATH_ID, VERIT_PATH_LABEL, true, getFieldEditorParent());
		addField(veriTBinaryBrowser);

		final ValidatedOnKeyStrokeDirFieldEd translationDirBrowser = new ValidatedOnKeyStrokeDirFieldEd(
				TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL,
				getFieldEditorParent());
		addField(translationDirBrowser);
	}
}
