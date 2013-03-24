/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.acceptance.verit;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_VERIT;

import org.eventb.smt.tests.acceptance.AxiomsTestWithPPV1_2;

public class AxiomsTestWithVeriTPPV1_2 extends AxiomsTestWithPPV1_2 {

	public AxiomsTestWithVeriTPPV1_2() {
		super(BUNDLED_VERIT);
	}

}