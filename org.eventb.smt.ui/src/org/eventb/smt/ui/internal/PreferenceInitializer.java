package org.eventb.smt.ui.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eventb.smt.ui.internal.preferences.configurations.EnablementStore;
import org.eventb.smt.ui.internal.provers.SMTProversUI;
import org.osgi.service.prefs.Preferences;

/**
 * Initialize the default values for this plug-in preferences.
 *
 * @author Laurent Voisin
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IScopeContext scope = DefaultScope.INSTANCE;
		final Preferences node = scope.getNode(SMTProversUI.PLUGIN_ID);
		EnablementStore.setDefault(node);
	}

}
