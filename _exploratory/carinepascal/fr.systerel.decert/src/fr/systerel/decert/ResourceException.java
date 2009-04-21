/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert;

import java.io.IOException;

/**
 * Exception related to a resource.
 */
public class ResourceException extends IOException {

	private static final long serialVersionUID = 7374306941236489902L;

	public ResourceException(Throwable cause) {
		super(cause.getMessage());
		initCause(cause);
	}

	public ResourceException(String message) {
		super(message);
	}
}