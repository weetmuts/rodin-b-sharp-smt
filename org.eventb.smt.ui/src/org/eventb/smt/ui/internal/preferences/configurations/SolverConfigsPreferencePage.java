/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eventb.smt.core.SMTCore.updateAllSMTSolversTactic;
import static org.eventb.smt.core.preferences.PreferenceManager.SOLVER_CONFIGS_ID;
import static org.eventb.smt.ui.internal.provers.SMTProversUI.updateAllSMTSolversProfile;

import org.eclipse.core.runtime.preferences.InstanceScope;
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
public class SolverConfigsPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private static final String SMT_SOLVER_CONFIGS_LABEL = "";
	private static final String SMT_SOLVER_CONFIGS_DESCRIPTION = "Customize SMT-solvers configurations...";

	private SolverConfigsFieldEditor configsFieldEditor;

	public SolverConfigsPreferencePage() {
		super(FLAT);
		setDescription(SMT_SOLVER_CONFIGS_DESCRIPTION);
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
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				SMTCore.PLUGIN_ID);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(doGetPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		configsFieldEditor = new SolverConfigsFieldEditor(SOLVER_CONFIGS_ID,
				SMT_SOLVER_CONFIGS_LABEL, getFieldEditorParent());
		addField(configsFieldEditor);
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && configsFieldEditor != null) {
			configsFieldEditor.refresh();
		}
		super.setVisible(visible);
	}

	@Override
	public boolean performOk() {
		updateAllSMTSolversTactic();
		updateAllSMTSolversProfile();
		return super.performOk();
	}
}
