/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.preferences.ui;

import static org.eventb.smt.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.preferences.SMTPreferences.VERIT_PATH_ID;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.smt.provers.ui.SmtProversUIPlugin;

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
		super(FieldEditorPreferencePage.FLAT);
		setPreferenceStore(SmtProversUIPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		final FieldEditor solversFieldEditor = new SolversDetailsFieldEditor(
				SOLVER_PREFERENCES_ID, SMT_SOLVERS_CONFIG_LABEL,
				getFieldEditorParent());
		addField(solversFieldEditor);

		final FileFieldEditor veriTBinaryBrowser = new FileFieldEditor(
				VERIT_PATH_ID, VERIT_PATH_LABEL, true, getFieldEditorParent());
		addField(veriTBinaryBrowser);

		final DirectoryFieldEditor translationDirectoryBrowser = new DirectoryFieldEditor(
				TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL,
				getFieldEditorParent());
		addField(translationDirectoryBrowser);
	}

	@Override
	public void init(final IWorkbench workbench) {
		// Do nothing
	}
}
