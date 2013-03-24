/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import org.eventb.smt.core.SMTLIBVersion;

/**
 * We use the regular mechanism provided by Java for translating an enumerated
 * value to a String.
 *
 * @author Laurent Voisin
 */
public class SMTLIBVersionSerializer extends AttributeSerializer<SMTLIBVersion> {

	public SMTLIBVersionSerializer(String key) {
		super(key);
	}

	@Override
	protected String serialize(SMTLIBVersion value) {
		return value.name();
	}

	@Override
	protected SMTLIBVersion deserialize(String image)
			throws IllegalArgumentException {
		return SMTLIBVersion.valueOf(image);
	}

}
