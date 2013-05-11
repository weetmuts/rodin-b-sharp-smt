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

import org.eventb.smt.core.IDescriptor;
import org.osgi.service.prefs.Preferences;

/**
 * Common implementation of a solver or configuration descriptor. Contains just
 * a name.
 *
 * @author Laurent Voisin
 */
public abstract class Descriptor implements IDescriptor {

	private static final StringSerializer NAME = new StringSerializer("name"); //$NON-NLS-1$

	private final String name;
	private final boolean bundled;

	public Descriptor(String name, boolean bundled) {
		this.name = name;
		this.bundled = bundled;
	}

	public Descriptor(Preferences node) {
		this.name = NAME.load(node);
		this.bundled = false;
	}

	public void serialize(Preferences node) {
		NAME.store(node, name);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean isBundled() {
		return bundled;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toStringQuoted(sb, name);
		toStringSep(sb);
		sb.append(bundled ? "bundled" : "user");
		toString(sb);
		return sb.toString();
	}

	protected abstract void toString(StringBuilder sb);

	protected static void toStringSep(StringBuilder sb) {
		sb.append(' ');
	}

	protected static void toStringQuoted(StringBuilder sb, String value) {
		sb.append('"');
		sb.append(value);
		sb.append('"');
	}

}
