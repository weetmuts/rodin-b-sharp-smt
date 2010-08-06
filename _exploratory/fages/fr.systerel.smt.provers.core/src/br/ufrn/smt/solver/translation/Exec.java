/*******************************************************************************
 * Copyright (c) 2010 Systerel and V�tor Alc�ntara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     V�tor Alc�ntara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Exec extends Thread{
	
	String[] args = {""};
	
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
	
	public static void main(String[] args)
	{
		try {
			System.out.println(execProgram(args));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
	//System.out.println("returns:" + p.exitValue());
		return bufferedOut;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			execProgram(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
