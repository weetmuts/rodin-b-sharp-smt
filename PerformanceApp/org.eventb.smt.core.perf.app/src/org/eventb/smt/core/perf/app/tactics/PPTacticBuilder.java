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
package org.eventb.smt.core.perf.app.tactics;

import org.eventb.core.seqprover.IParameterSetting;

/**
 * For running the external prover PP from the Atelier B plug-in.
 * 
 * @author Laurent Voisin
 */
public class PPTacticBuilder extends XProverTacticBuilder {

	public PPTacticBuilder() {
		super("PP");
	}

	@Override
	protected String getParameterizerId() {
		return "com.clearsy.atelierb.provers.core.ppParam";
	}

	@Override
	protected String getTimeoutParamName() {
		return "timeout";
	}

	@Override
	protected void completeParameters(IParameterSetting setting) {
		// Nothing to add
	}

}
