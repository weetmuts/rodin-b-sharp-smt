/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import static org.eventb.smt.core.preferences.PreferenceManager.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.core.preferences.PreferenceManager.TRANSLATION_PATH_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.VERIT_PATH_ID;

import org.eventb.smt.core.preferences.ITranslationPreferences;

/**
 * @author Systerel (yguyot)
 * 
 */
public class TranslationPreferences extends AbstractPreferences implements
		ITranslationPreferences {

	private static final TranslationPreferences TRANSLATION_PREFS = new TranslationPreferences(
			!USE_DEFAULT_SCOPE);
	private static final TranslationPreferences DEFAULT_TRANSLATION_PREFS = new TranslationPreferences(
			USE_DEFAULT_SCOPE);

	public static final String DEFAULT_VERIT_PATH = ""; //$NON-NLS-1$

	private String translationPath;
	private String veriTPath;

	private TranslationPreferences(boolean useDefaultScope) {
		super(useDefaultScope);
	}

	public static TranslationPreferences getTranslationPrefs(
			final boolean reload) {
		TRANSLATION_PREFS.load(reload);
		return TRANSLATION_PREFS;
	}

	public static TranslationPreferences getDefaultTranslationPrefs(
			final boolean reload) {
		DEFAULT_TRANSLATION_PREFS.load(reload);
		return DEFAULT_TRANSLATION_PREFS;
	}

	@Override
	public void load(boolean reload) {
		if (loaded && !reload) {
			return;
		}
		translationPath = prefsNode.get(TRANSLATION_PATH_ID,
				DEFAULT_TRANSLATION_PATH);
		veriTPath = prefsNode.get(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
		loaded = true;
	}

	@Override
	public void loadDefault() {
		translationPath = getDefaultTranslationPrefs(!FORCE_RELOAD)
				.getTranslationPath();
		veriTPath = getDefaultTranslationPrefs(!FORCE_RELOAD).getVeriTPath();
	}

	@Override
	public void save() {
		prefsNode.put(TRANSLATION_PATH_ID, translationPath);
		prefsNode.put(VERIT_PATH_ID, veriTPath);
	}

	@Override
	public String getTranslationPath() {
		return translationPath;
	}

	@Override
	public String getVeriTPath() {
		return veriTPath;
	}

	/**
	 * @param translationPath
	 *            the translationPath to set
	 */
	@Override
	public void setTranslationPath(String translationPath) {
		this.translationPath = getValidPath(this.translationPath,
				translationPath, DEFAULT_TRANSLATION_PATH);
	}

	/**
	 * Sets veriT path to the path of the integrated veriT solver.
	 */
	@Override
	public void setVeriTPath(final String veriTPath) {
		this.veriTPath = getValidPath(this.veriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}
}
