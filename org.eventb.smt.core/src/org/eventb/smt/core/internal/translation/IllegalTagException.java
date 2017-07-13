/*******************************************************************************
 * Copyright (c) 2010, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.translation;

/**
 * @author Yoann Guyot
 * 
 */
public class IllegalTagException extends IllegalArgumentException {

	private static final long serialVersionUID = -33129241139929218L;

	private final int cause;

	public IllegalTagException(final int tag) {
		super();
		cause = tag;
	}

	@Override
	public String getMessage() {
		return "The given tag \'" + cause + "\' isn't a valid tag";
	}
}
