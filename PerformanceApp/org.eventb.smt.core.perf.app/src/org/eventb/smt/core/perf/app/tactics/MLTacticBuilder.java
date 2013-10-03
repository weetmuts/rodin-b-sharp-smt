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

import static com.clearsy.atelierb.provers.core.AtbProversCore.ML_FORCE_0;
import static com.clearsy.atelierb.provers.core.AtbProversCore.ML_FORCE_1;

import org.eventb.core.seqprover.IParameterSetting;

/**
 * For running the external prover ML from the Atelier B plug-in.
 * 
 * @author Laurent Voisin
 */
public class MLTacticBuilder extends XProverTacticBuilder {

	public MLTacticBuilder() {
		super("ML");
	}

	@Override
	protected String getParameterizerId() {
		return "com.clearsy.atelierb.provers.core.mlParam";
	}

	@Override
	protected String getTimeoutParamName() {
		return "timeout";
	}

	@Override
	protected void completeParameters(IParameterSetting setting) {
		setting.setInt("forces", ML_FORCE_0 | ML_FORCE_1);
	}

}
