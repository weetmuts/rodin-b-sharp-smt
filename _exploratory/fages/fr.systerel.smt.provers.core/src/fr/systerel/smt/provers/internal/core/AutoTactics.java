/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;

import fr.systerel.smt.provers.core.SMTProversCore;

/**
 * This class file contains static classes that extend the autoTactics extension
 * point in the sequent prover
 * 
 * 
 * @author YFT
 * 
 */
public class AutoTactics {

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	// TODO add SMTAltErgoPP, SMTCVC3PP, SMTVeriTPP and SMTZ3PP etc.. with
	// appropriate preferences
	public static class SMTPP extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTProversCore.externalSMTThroughPP(null, true);
		}
	}

	public static class SMTVeriT extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTProversCore.externalSMTThroughVeriT(null, true);
		}
	}
}
