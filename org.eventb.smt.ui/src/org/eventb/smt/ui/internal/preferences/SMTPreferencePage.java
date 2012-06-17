/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences;

import static org.eclipse.jface.resource.JFaceResources.getString;
import static org.eventb.smt.core.preferences.PreferenceManager.TRANSLATION_PATH_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.VERIT_PATH_ID;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_description;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_missingValue;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notADirectory;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notAbsolute;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notWritableDir;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_tmpDirLabel;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_tmpDirTooltip;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_veriTPathLabel;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_veriTPathTooltip;

import java.io.File;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.smt.core.SMTCore;

/**
 * Main preference page of the SMT core plug-in.
 *
 * @author Yoann Guyot
 * @author Laurent Voisin
 */
public class SMTPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	public void init(final IWorkbench workbench) {
		setDescription(MainPrefPage_description);
	}

	/*
	 * The preferences are actually stored in the SMT core plug-in.
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				SMTCore.PLUGIN_ID);
	}

	/*
	 * It is on purpose that getFieldEditorParent() is called for each field, it
	 * is mandated by the FieldEditor API. Do not share this code.
	 */
	@Override
	protected void createFieldEditors() {
		addField(new TempDirectoryEditor(getFieldEditorParent()));
		addField(new VeriTEditor(getFieldEditorParent()));
	}

	/*
	 * We want a directory field editor that validates the user input on every
	 * keystroke, because, otherwise, the "Apply" and "OK" button are not
	 * disabled when the field is edited directly by the user and is incorrect.
	 *
	 * However, the DirectoryFieldEditor does not allow this, unless we call the
	 * default constructor and provide all information afterwards. We therefore
	 * sub-class DirectoryFieldEditor, copy its constructor, just changing one
	 * line to set the validation strategy we want. This is a pity.
	 *
	 * Once this class has been created, it has been extended for additional
	 * purposes: - define the label string and tool tip - prevent empty strings
	 * - check that the directory is writable
	 */
	private static class TempDirectoryEditor extends DirectoryFieldEditor {

		public TempDirectoryEditor(Composite parent) {
			init(TRANSLATION_PATH_ID, MainPrefPage_tmpDirLabel);
			setErrorMessage(getString("DirectoryFieldEditor.errorMessage")); //$NON-NLS-1$
			setChangeButtonText(getString("openBrowse")); //$NON-NLS-1$
			setEmptyStringAllowed(false);
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
			getLabelControl().setToolTipText(MainPrefPage_tmpDirTooltip);
		}

		/*
		 * The implementation of this method in the super-classes does not
		 * fulfill all our needs, so we override it completely.
		 */
		@Override
		protected boolean checkState() {
			final String path = getTextControl().getText().trim();
			final String errorMessage = checkWritableDirectory(path);
			if (errorMessage != null) {
				showErrorMessage(errorMessage);
				return false;
			}
			clearErrorMessage();
			return true;
		}

		private String checkWritableDirectory(String path) {
			if (path.length() == 0) {
				return MainPrefPage_missingValue;
			}
			final File file = new File(path);
			if (!file.isDirectory()) {
				return MainPrefPage_notADirectory;
			}
			if (!file.isAbsolute()) {
				return MainPrefPage_notAbsolute;
			}
			if (!file.canWrite()) {
				return MainPrefPage_notWritableDir;
			}
			return null;
		}

		@Override
		protected void doStore() {
			getPreferenceStore().setValue(getPreferenceName(),
					getTextControl().getText().trim());
		}

	}

	private static class VeriTEditor extends ExecutableFileEditor {

		public VeriTEditor(Composite parent) {
			super(VERIT_PATH_ID, MainPrefPage_veriTPathLabel,
					MainPrefPage_veriTPathTooltip, parent);
			setEmptyStringAllowed(true);
		}

	}

}