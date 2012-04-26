/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.log;

import static org.eventb.smt.core.SMTCore.PLUGIN_ID;

import org.eclipse.core.runtime.Status;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTStatus extends Status {
	private SMTStatus(int severity, String pluginId, String message,
			Throwable exception) {
		super(severity, pluginId, message, exception);
	}

	public static SMTStatus smtError(final String message,
			final Throwable exception) {
		return new SMTStatus(ERROR, PLUGIN_ID, message, exception);
	}

	public static SMTStatus smtWarning(final String message) {
		return new SMTStatus(WARNING, PLUGIN_ID, message, null);
	}
}
