/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vítor Alcântara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.util.ArrayList;

public class TranslationException extends Exception {

	private static final long serialVersionUID = -8016875587991618557L;
	
	private ArrayList<Pair<String,String>> causes;
	
	public TranslationException(ArrayList<Pair<String,String>> causes)
	{
		super();
		this.causes = causes;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < causes.size() ; i++)
		{	
			sb.append("Formula: " + causes.get(i).getKey() + ",Reason: " + causes.get(i).getValue() + "\n");
		}
		return sb.toString();
	}
	
	
	
}
