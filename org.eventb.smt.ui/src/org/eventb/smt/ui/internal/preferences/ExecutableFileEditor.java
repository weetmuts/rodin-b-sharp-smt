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

import static org.eventb.smt.ui.internal.Messages.MainPrefPage_missingValue;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notAFile;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notAbsolute;
import static org.eventb.smt.ui.internal.Messages.MainPrefPage_notExecutableFile;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for entering the absolute path to an executable file.
 *
 * @author Laurent Voisin
 */
public class ExecutableFileEditor extends FileFieldEditor {

	public ExecutableFileEditor(String name, String label, String toolTip,
			Composite parent) {
		super(name, label, true, VALIDATE_ON_KEY_STROKE, parent);
		getLabelControl().setToolTipText(toolTip);
	}

	/*
	 * The implementation of this method in the super-classes does not fulfill
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
			if (isEmptyStringAllowed()) {
				return null;
			}
			return MainPrefPage_missingValue;
		}
		final File file = new File(path);
		if (!file.isFile()) {
			return MainPrefPage_notAFile;
		}
		if (!file.isAbsolute()) {
			return MainPrefPage_notAbsolute;
		}
		if (!file.canExecute()) {
			return MainPrefPage_notExecutableFile;
		}
		return null;
	}

	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(),
				getTextControl().getText().trim());
	}

}