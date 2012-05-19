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

	/*
	 * The preferences are actually stored in the SMT core plug-in.
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				SMTCore.PLUGIN_ID);
	}

	@Override
	public void init(final IWorkbench workbench) {
		// Nothing to do
	}

	@Override
	protected void createFieldEditors() {
		addField(new SolverConfigsFieldEditor(getFieldEditorParent()));
	}

	@Override
	public boolean performOk() {
		updateAllSMTSolversTactic();
		updateAllSMTSolversProfile();
		return super.performOk();
	}

}
