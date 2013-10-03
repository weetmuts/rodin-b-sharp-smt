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

public class SMTTacticBuilder extends XProverTacticBuilder {

	private final String configName;

	public SMTTacticBuilder(String configName) {
		super(configName);
		this.configName = configName;
	}

	@Override
	protected String getParameterizerId() {
		return "org.eventb.smt.core.SMTParam";
	}

	@Override
	protected String getTimeoutParamName() {
		return "timeOutDelay";
	}

	@Override
	protected void completeParameters(IParameterSetting setting) {
		setting.setString("configName", configName);
	}

}
