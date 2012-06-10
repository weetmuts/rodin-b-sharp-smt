/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

public class StringSerializer extends AttributeSerializer<String> {

	public StringSerializer(String key) {
		super(key);
	}

	@Override
	protected String serialize(String value) {
		return value;
	}

	@Override
	protected String deserialize(String image) {
		return image;
	}

}
