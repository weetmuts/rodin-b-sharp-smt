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

import java.io.File;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
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

	private static final String SMT_TRANSLATION_SETTINGS_LABEL = "SMT translation settings:";
	private static final String VERIT_PATH_LABEL = "veriT path";
	private static final String VERIT_PATH_TOOLTIP = "Absolute path of the binary to use when translating through veriT";
	private static final String TRANSLATION_PATH_LABEL = "Temporary directory";
	private static final String TRANSLATION_PATH_TOOLTIP = "Absolute path of a directory where the plug-in will store its temporary files";

	@Override
	public void init(final IWorkbench workbench) {
		setDescription(SMT_TRANSLATION_SETTINGS_LABEL);
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
			init(TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL);
			setErrorMessage(getString("DirectoryFieldEditor.errorMessage"));
			setChangeButtonText(getString("openBrowse"));
			setEmptyStringAllowed(false);
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
			getLabelControl().setToolTipText(TRANSLATION_PATH_TOOLTIP);
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
				return "Value must be provided";
			}
			final File file = new File(path);
			if (!file.isDirectory()) {
				return "Value must be an existing directory";
			}
			if (!file.isAbsolute()) {
				return "Value must be an absolute path";
			}
			if (!file.canWrite()) {
				return "Directory must be writable";
			}
			return null;
		}

	}

	private static class VeriTEditor extends FileFieldEditor {

		public VeriTEditor(Composite parent) {
			super(VERIT_PATH_ID, VERIT_PATH_LABEL, true,
					VALIDATE_ON_KEY_STROKE, parent);
			getLabelControl().setToolTipText(VERIT_PATH_TOOLTIP);
		}

		/*
		 * We want either an empty path, or a path to an executable file. The
		 * implementation of this method in the super-classes does not fulfill
		 * all our needs, so we override it completely.
		 */
		@Override
		protected boolean checkState() {
			final String path = getTextControl().getText().trim();
			final String errorMessage = checkExecutableFile(path);
			if (errorMessage != null) {
				showErrorMessage(errorMessage);
				return false;
			}
			clearErrorMessage();
			return true;
		}

		private String checkExecutableFile(String path) {
			if (path.length() == 0) {
				return null;
			}
			final File file = new File(path);
			if (!file.isFile()) {
				return "Value must be an existing file";
			}
			if (!file.isAbsolute()) {
				return "Value must be an absolute path";
			}
			if (!file.canExecute()) {
				return "File must be executable";
			}
			return null;
		}

	}

}