/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

public class BooleanSerializer extends AttributeSerializer<Boolean> {

	public BooleanSerializer(String key) {
		super(key);
	}

	@Override
	protected String serialize(Boolean value) {
		return value.toString();
	}

	@Override
	protected Boolean deserialize(String image) {
		return Boolean.valueOf(image);
	}

}
