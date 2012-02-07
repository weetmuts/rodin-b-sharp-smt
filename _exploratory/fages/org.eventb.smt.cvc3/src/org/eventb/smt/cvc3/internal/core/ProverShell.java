/*******************************************************************************
 * Copyright (c) 2012 Systerel. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.cvc3.internal.core;

import static org.eventb.smt.internal.translation.Translator.DEBUG;
import static org.eventb.smt.internal.translation.Translator.DEBUG_DETAILS;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.smt.cvc3.core.Cvc3ProverCore;
import org.osgi.framework.Bundle;

/**
 * @author Systerel (yguyot)
 */
public abstract class ProverShell {
	private static final String BUNDLE_NAME = Cvc3ProverCore.PLUGIN_ID;

	private static Bundle bundle;

	private static boolean toolsPresent;
	private static boolean cached;
	private static String cvc3Path;

	private static Bundle getBundle() {
		if (bundle == null) {
			bundle = Platform.getBundle(BUNDLE_NAME);
		}
		return bundle;
	}

	private static String extractFile(String localPathString, boolean exec) {
		final Bundle b = getBundle();
		final IPath localPath = new Path(localPathString);
		final IPath path = BundledFileExtractor.extractFile(b, localPath, exec);
		return path != null ? path.toOSString() : null;
	}

	private static synchronized void computeCache() {
		if (cached)
			return;
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				System.out.println("Computing tool path cache");
			}
		}
		cvc3Path = extractFile("$os$/cvc3", true);
		toolsPresent = cvc3Path != null;
		cached = true;
	}

	public static String getCvc3Path() {
		computeCache();
		if (!toolsPresent)
			return null;
		return cvc3Path;
	}
}
