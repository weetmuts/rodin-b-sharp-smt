/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *     Systerel (YFT) - Code simplification (unused methods etc ...)
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class executes the given command with arguments and returns a String
 * containing both input and error streams produced content
 */
public class Exec {

	public static String execProgram(String[] args) throws IOException {
		String ln;
		/**
		 * Executes the command with args in a new process
		 */
		final Process p = Runtime.getRuntime().exec(args);

		final BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		final BufferedReader bre = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		/**
		 * Reads input and error streams and writes content into the buffer to be
		 * returned
		 */
		String bufferedOut = "";
		while ((ln = br.readLine()) != null) {
			bufferedOut = bufferedOut + "\n" + ln;
		}
		while ((ln = bre.readLine()) != null) {
			bufferedOut = bufferedOut + "\n" + ln;
		}

		return bufferedOut;
	}
}
