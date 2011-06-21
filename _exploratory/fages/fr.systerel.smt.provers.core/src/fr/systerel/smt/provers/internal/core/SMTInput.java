/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eventb.core.seqprover.xprover.XProverInput;

public class SMTInput extends XProverInput {

	// Forces to use in the mono-lemma prover
	final String forces;

	protected SMTInput(final String forces, final long timeOutDelay,
			final boolean restricted) {
		super(restricted, timeOutDelay);
		if (forces.length() == 0) {
			this.forces = null;
			return;
		}
		this.forces = forces;
	}

	protected SMTInput(final String forces, final long timeOutDelay) {
		this(forces, timeOutDelay, false);
	}

	public SMTInput(final int forces, final long timeOutDelay) {
		this(forcesToString(forces), timeOutDelay, false);
	}

	public SMTInput(final int forces, final long timeOutDelay,
			final boolean restricted) {
		this(forcesToString(forces), timeOutDelay, restricted);
	}

	public static String forcesToString(final int forces) {
		return null;

	}

	@Override
	public boolean hasError() {
		return forces == null || super.hasError();
	}

	@Override
	public String getError() {
		if (forces == null) {
			return Messages.force_error_invalid_forces;
		}
		return super.getError();
	}

}
