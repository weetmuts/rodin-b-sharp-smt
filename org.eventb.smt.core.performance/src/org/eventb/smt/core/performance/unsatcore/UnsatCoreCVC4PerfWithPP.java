/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.unsatcore;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_CVC4;;

public class UnsatCoreCVC4PerfWithPP extends UnsatCoreExtractionPerfWithPP {

	public UnsatCoreCVC4PerfWithPP() {
		super(BUNDLED_CVC4);
	}

}
