/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT): Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences.ui;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVER_PREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERIT_PATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERIT_PATH_ID;
import static br.ufrn.smt.solver.preferences.ui.Messages.SMTPreferencePage_SettingsDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetails;
import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

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
	private static final String SMT_SOLVERS_PARAM_LABEL = "SMT-Solvers parameters";
	private static final String VERIT_PATH_LABEL = "VeriT path";
	private static final String TRANSLATION_PATH_LABEL = "Temporary translation files path";

	static String setVeriTPath;
	static String setTranslationPath;

	/**
	 * The name of the preference displayed in this preference page.
	 */
	String preferences = new String();

	public SMTPreferencePage() {
		super(FieldEditorPreferencePage.FLAT);
		setPreferenceStore(SmtProversUIPlugin.getDefault().getPreferenceStore());
		preferences = getPreferenceStore().getString(SOLVER_PREFERENCES_ID);
		setDescription(SMTPreferencePage_SettingsDescription);
	}

	public static SMTPreferences getSMTPreferencesForPP()
			throws PatternSyntaxException, IllegalArgumentException {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVER_PREFERENCES_ID, DEFAULT_SOLVER_PREFERENCES,
				null);
		final String translationPath = preferencesService.getString(
				PREFERENCES_ID, TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH, null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX, null);
		return new SMTPreferences(translationPath, solverPreferences,
				solverIndex);
	}

	public static SMTPreferences getSMTPreferencesForVeriT()
			throws PatternSyntaxException, IllegalArgumentException {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVER_PREFERENCES_ID, DEFAULT_SOLVER_PREFERENCES,
				null);
		final String translationPath = preferencesService.getString(
				PREFERENCES_ID, TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH, null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX, null);
		final String veriTPath = preferencesService.getString(PREFERENCES_ID,
				VERIT_PATH_ID, DEFAULT_VERIT_PATH, null);
		return new SMTPreferences(translationPath, solverPreferences,
				solverIndex, veriTPath);
	}

	@Override
	protected void createFieldEditors() {
		List<SolverDetails> solverDetails = new ArrayList<SolverDetails>();
		try {
			solverDetails = SMTPreferences.parsePreferencesString(preferences);
		} catch (final PatternSyntaxException pse) {
			pse.printStackTrace(System.err);
			UIUtils.showError(pse.getMessage());
		}
		final int selectedSolverIndex = getPreferenceStore()
				.getInt(SOLVER_INDEX_ID);
		final FieldEditor solversFieldEditor = new SolversDetailsFieldEditor(
				SOLVER_PREFERENCES_ID, SMT_SOLVERS_PARAM_LABEL, getFieldEditorParent(),
				solverDetails, selectedSolverIndex);
		addField(solversFieldEditor);

		final FileFieldEditor veriTBinaryBrowser = new FileFieldEditor(
				VERIT_PATH_ID, VERIT_PATH_LABEL, true, getFieldEditorParent());
		addField(veriTBinaryBrowser);

		final DirectoryFieldEditor translationDirectoryBrowser = new DirectoryFieldEditor(
				TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL, getFieldEditorParent());
		addField(translationDirectoryBrowser);
	}

	@Override
	public void init(final IWorkbench workbench) {
		// Do nothing
	}
}
