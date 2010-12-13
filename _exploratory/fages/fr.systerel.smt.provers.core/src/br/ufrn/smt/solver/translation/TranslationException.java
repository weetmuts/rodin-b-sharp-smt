/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.util.ArrayList;

public class TranslationException extends Exception {

	private static final long serialVersionUID = -8016875587991618557L;

	private ArrayList<Pair<String, String>> causes;

	public TranslationException(ArrayList<Pair<String, String>> causes) {
		super();
		this.causes = causes;
	}

	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < causes.size(); i++) {
			sb.append("Formula: ");
			sb.append(causes.get(i).getKey());
			sb.append(", Reason: ");
			sb.append(causes.get(i).getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

}
