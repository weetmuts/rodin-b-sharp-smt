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

import java.util.Map;
import java.util.Map.Entry;

public class TranslationException extends Exception {

	private static final long serialVersionUID = -8016875587991618557L;

	private Map<String, String> causes;

	public TranslationException(Map<String, String> causes) {
		super();
		this.causes = causes;
	}

	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		for (final Entry<String, String> cause : causes.entrySet()) {
			sb.append("Formula: ");
			sb.append(cause.getKey());
			sb.append(", Reason: ");
			sb.append(cause.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
}
