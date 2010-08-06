/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vítor Alcântara de Almeida - Creation
 *     Systerel (YFT) - Code simplification (unused methods etc ...)
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Exec {
	
	public static String execProgram(String [] args) throws IOException
	{
		String ln;
		Process p = Runtime.getRuntime().exec(args);
		BufferedReader br = new BufferedReader
		(
			new InputStreamReader
			(
				p.getInputStream()
			)
		);
		BufferedReader bre = new BufferedReader
		(
				new InputStreamReader
				(
					p.getErrorStream()
				)
			); 
		
		
		String bufferedOut = "";
		while((ln = br.readLine()) != null) 
		{
			bufferedOut = bufferedOut + "\n" + ln;
		}
		while((ln = bre.readLine()) != null) 
		{
			bufferedOut = bufferedOut + "\n" + ln;
		}

		return bufferedOut;
	}
}
