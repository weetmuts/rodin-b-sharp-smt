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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Use the portable string mechanism provided by class Path for serialization.
 * <p>
 * Do not check whether the path points to an executable file. Doing this check
 * when deserializing might be too early (e.g., for bundled solvers). Moreover,
 * as the executability of a file is beyond the reach of the Rodin platform, it
 * will be needed to test it just before launching the binary.
 * </p>
 *
 * @author Laurent Voisin
 */
public class PathSerializer extends AttributeSerializer<IPath> {

	public PathSerializer(String key) {
		super(key);
	}

	@Override
	protected String serialize(IPath value) {
		return value.toPortableString();
	}

	@Override
	protected IPath deserialize(String image) {
		return Path.fromPortableString(image);
	}

}
