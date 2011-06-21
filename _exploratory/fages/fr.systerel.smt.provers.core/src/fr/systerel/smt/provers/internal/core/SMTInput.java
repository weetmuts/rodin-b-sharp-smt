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

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;

public class SMTInput extends XProverInput {

	private final String error;

	public final String sequentName;

	protected SMTInput(final IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		this.sequentName = reader.getDisplayName();
		this.error = validate();
	}

	public SMTInput(final boolean restricted, final long timeOutDelay,
			final String sequentName) {
		super(restricted, timeOutDelay);
		this.sequentName = sequentName;
		this.error = validate();
	}

	private String validate() {
		if (sequentName != null && !sequentName.equals("")) {
			return null;
		} else {
			return "Illegal sequent name";
		}
	}

	@Override
	public String getError() {
		return error != null ? error : super.getError();
	}

	@Override
	public boolean hasError() {
		return error != null || super.hasError();
	}

}
