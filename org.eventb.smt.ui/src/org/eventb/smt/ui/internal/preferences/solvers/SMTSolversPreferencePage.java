/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eventb.smt.core.preferences.PreferenceManager.SOLVERS_ID;

import org.eclipse.core.runtime.preferences.InstanceScope;
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

	private static final String SMT_SOLVERS_LABEL = "";
	private static final String SMT_SOLVERS_DESCRIPTION = "Connect SMT-solvers to the platform...";

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
		setDescription(SMT_SOLVERS_DESCRIPTION);
	}

	@Override
	protected void createFieldEditors() {
		final FieldEditor solversFieldEditor = new SMTSolversFieldEditor(
				SOLVERS_ID, SMT_SOLVERS_LABEL, getFieldEditorParent());
		addField(solversFieldEditor);
	}
}
