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

import static org.eventb.smt.core.preferences.AbstractPreferences.SOLVERS_ID;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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
public class SMTSolversPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private static final String SMT_SOLVERS_LABEL = "SMT-Solvers";

	public SMTSolversPreferencePage() {
		super(FLAT);
		setDescription(SMT_SOLVERS_LABEL);
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
		final FieldEditor solversFieldEditor = new SMTSolversFieldEditor(
				SOLVERS_ID, SMT_SOLVERS_LABEL, getFieldEditorParent());
		addField(solversFieldEditor);
	}
}
